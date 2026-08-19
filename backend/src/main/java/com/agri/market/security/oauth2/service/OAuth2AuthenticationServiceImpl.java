package com.agri.market.security.oauth2.service;

import com.agri.market.exception.BusinessException;
import com.agri.market.role.entity.Role;
import com.agri.market.role.entity.RoleName;
import com.agri.market.role.repository.RoleRepository;
import com.agri.market.security.oauth2.entity.OAuthAccount;
import com.agri.market.security.oauth2.entity.OAuthProvider;
import com.agri.market.security.oauth2.factory.OAuthStrategyFactory;
import com.agri.market.security.oauth2.model.OAuthProviderContext;
import com.agri.market.security.oauth2.repository.OAuthAccountRepository;
import com.agri.market.security.oauth2.strategy.OAuthProviderStrategy;
import com.agri.market.user.entity.User;
import com.agri.market.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.agri.market.exception.ErrorCode.OAUTH_EMAIL_NOT_AVAILABLE;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuth2AuthenticationServiceImpl
        implements OAuth2AuthenticationService {

    private final OAuthAccountRepository oauthAccountRepository;
    private final OAuthStrategyFactory oauthStrategyFactory;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public User authenticate(
            final String registrationId,
            final OAuthProviderContext context
    ) {

        log.info(
                "OAuth authentication started for provider: {}",
                registrationId
        );

        final OAuthProvider provider =
                OAuthProvider.valueOf(
                        registrationId.toUpperCase()
                );

        final OAuthProviderStrategy strategy =
                oauthStrategyFactory.getStrategy(provider);

        final String providerUserId =
                strategy.getProviderUserId(
                        context
                );

        return oauthAccountRepository
                .findByProviderAndProviderUserId(
                        provider,
                        providerUserId
                )
                .map(account -> {

                    log.info(
                            "Existing OAuth account found for provider: {}",
                            provider
                    );

                    return account.getUser();
                })
                .orElseGet(() -> {

                    log.info(
                            "OAuth account not found for provider: {}. " +
                                    "Processing account linking or creation",
                            provider
                    );

                    return findOrCreateUser(
                            provider,
                            providerUserId,
                            strategy,
                            context
                    );
                });
    }

    private User findOrCreateUser(
            final OAuthProvider provider,
            final String providerUserId,
            final OAuthProviderStrategy strategy,
            final OAuthProviderContext context
    ) {

        final String email =
                strategy.getEmail(
                        context
                );

        if (email == null || email.isBlank()) {

            log.warn(
                    "OAuth authentication failed because email was not " +
                            "provided by provider: {}",
                    provider
            );

            throw new BusinessException(
                    OAUTH_EMAIL_NOT_AVAILABLE
            );
        }

        final User user =
                userRepository
                        .findByEmailIgnoreCase(email)
                        .orElse(null);

        if (user != null) {

            log.info(
                    "Existing user found for OAuth provider: {}. " +
                            "Linking OAuth account",
                    provider
            );

            return linkOAuthAccount(
                    user,
                    provider,
                    providerUserId
            );
        }

        log.info(
                "No existing user found for OAuth provider: {}. " +
                        "Creating new user",
                provider
        );

        return createOAuthUser(
                provider,
                providerUserId,
                strategy,
                context
        );
    }

    private User linkOAuthAccount(
            final User user,
            final OAuthProvider provider,
            final String providerUserId
    ) {

        final OAuthAccount oauthAccount =
                OAuthAccount.builder()
                        .provider(provider)
                        .providerUserId(providerUserId)
                        .user(user)
                        .build();

        oauthAccountRepository.save(oauthAccount);

        log.info(
                "OAuth account linked successfully for provider: {}",
                provider
        );

        return user;
    }

    private User createOAuthUser(
            final OAuthProvider provider,
            final String providerUserId,
            final OAuthProviderStrategy strategy,
            final OAuthProviderContext context
    ) {

        final String email =
                strategy.getEmail(
                        context
                );

        final String fullName =
                strategy.getFullName(
                        context
                );

        final String profilePictureUrl =
                strategy.getProfilePictureUrl(
                        context
                );

        final boolean emailVerified =
                strategy.isEmailVerified(
                        context
                );

        final Role userRole =
                roleRepository
                        .findByName(RoleName.USER.name())
                        .orElseThrow(() -> {

                            log.error(
                                    "USER role not found while creating " +
                                            "OAuth user"
                            );

                            return new IllegalStateException(
                                    "USER role not found"
                            );
                        });

        final User user =
                User.builder()
                        .fullName(fullName)
                        .email(email)
                        .phoneNumber(null)
                        .password(null)
                        .emailVerified(emailVerified)
                        .phoneVerified(false)
                        .credentialsExpired(false)
                        .passwordChangedAt(null)
                        .enabled(true)
                        .accountLocked(false)
                        .profilePictureUrl(profilePictureUrl)
                        .roles(List.of(userRole))
                        .build();

        final User savedUser =
                userRepository.save(user);

        log.info(
                "New OAuth user created successfully for provider: {}",
                provider
        );

        final OAuthAccount oauthAccount =
                OAuthAccount.builder()
                        .provider(provider)
                        .providerUserId(providerUserId)
                        .user(savedUser)
                        .build();

        oauthAccountRepository.save(oauthAccount);

        log.info(
                "OAuth account created successfully for provider: {}",
                provider
        );

        return savedUser;
    }
}