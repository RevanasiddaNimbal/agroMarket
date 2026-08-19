package com.agri.market.security.oauth2.service;

import com.agri.market.exception.BusinessException;
import com.agri.market.exception.ErrorCode;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OAuth2AuthenticationServiceImplTest {

    private static final String REGISTRATION_ID = "google";
    private static final String PROVIDER_USER_ID = "provider-user-id-123";
    private static final String EMAIL = "user@example.com";
    private static final String FULL_NAME = "John Doe";
    private static final String PROFILE_PICTURE_URL = "https://example.com/pic.jpg";

    @Mock
    private OAuthAccountRepository oauthAccountRepository;

    @Mock
    private OAuthStrategyFactory oauthStrategyFactory;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private OAuthProviderStrategy strategy;

    @Mock
    private OAuthProviderContext context;

    private OAuth2AuthenticationServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new OAuth2AuthenticationServiceImpl(
                oauthAccountRepository,
                oauthStrategyFactory,
                userRepository,
                roleRepository
        );
    }

    @Nested
    @DisplayName("authenticate - provider resolution")
    class ProviderResolutionTests {

        @Test
        @DisplayName("Should throw IllegalArgumentException for unknown registration id")
        void shouldThrowExceptionForUnknownRegistrationId() {
            assertThatThrownBy(() -> service.authenticate("unknown-provider", context))
                    .isInstanceOf(IllegalArgumentException.class);

            verifyNoInteractions(oauthStrategyFactory, oauthAccountRepository);
        }

        @Test
        @DisplayName("Should resolve provider case-insensitively from registration id")
        void shouldResolveProviderCaseInsensitively() {
            when(oauthStrategyFactory.getStrategy(OAuthProvider.GOOGLE)).thenReturn(strategy);
            when(strategy.getProviderUserId(context)).thenReturn(PROVIDER_USER_ID);

            OAuthAccount account = mock(OAuthAccount.class);
            User existingUser = mock(User.class);
            when(account.getUser()).thenReturn(existingUser);

            when(oauthAccountRepository.findByProviderAndProviderUserId(OAuthProvider.GOOGLE, PROVIDER_USER_ID))
                    .thenReturn(Optional.of(account));

            User result = service.authenticate("GoOgLe", context);

            assertThat(result).isEqualTo(existingUser);
            verify(oauthStrategyFactory).getStrategy(OAuthProvider.GOOGLE);
        }

        @Test
        @DisplayName("Should throw NullPointerException when registrationId is null")
        void shouldThrowExceptionWhenRegistrationIdIsNull() {
            assertThatThrownBy(() -> service.authenticate(null, context))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("authenticate - existing OAuth account")
    class ExistingOAuthAccountTests {

        @BeforeEach
        void stubStrategyResolution() {
            when(oauthStrategyFactory.getStrategy(OAuthProvider.GOOGLE)).thenReturn(strategy);
            when(strategy.getProviderUserId(context)).thenReturn(PROVIDER_USER_ID);
        }

        @Test
        @DisplayName("Should return existing user when OAuth account already linked")
        void shouldReturnExistingUserWhenAccountFound() {
            User existingUser = mock(User.class);
            OAuthAccount account = mock(OAuthAccount.class);
            when(account.getUser()).thenReturn(existingUser);

            when(oauthAccountRepository.findByProviderAndProviderUserId(OAuthProvider.GOOGLE, PROVIDER_USER_ID))
                    .thenReturn(Optional.of(account));

            User result = service.authenticate(REGISTRATION_ID, context);

            assertThat(result).isEqualTo(existingUser);
        }

        @Test
        @DisplayName("Should not touch userRepository or roleRepository when account already exists")
        void shouldNotTouchUserOrRoleRepositoryWhenAccountExists() {
            User existingUser = mock(User.class);
            OAuthAccount account = mock(OAuthAccount.class);
            when(account.getUser()).thenReturn(existingUser);

            when(oauthAccountRepository.findByProviderAndProviderUserId(OAuthProvider.GOOGLE, PROVIDER_USER_ID))
                    .thenReturn(Optional.of(account));

            service.authenticate(REGISTRATION_ID, context);

            verifyNoInteractions(userRepository, roleRepository);
        }

        @Test
        @DisplayName("Should not save a new OAuth account when one already exists")
        void shouldNotSaveNewAccountWhenAlreadyExists() {
            User existingUser = mock(User.class);
            OAuthAccount account = mock(OAuthAccount.class);
            when(account.getUser()).thenReturn(existingUser);

            when(oauthAccountRepository.findByProviderAndProviderUserId(OAuthProvider.GOOGLE, PROVIDER_USER_ID))
                    .thenReturn(Optional.of(account));

            service.authenticate(REGISTRATION_ID, context);

            verify(oauthAccountRepository, never()).save(any(OAuthAccount.class));
        }
    }

    @Nested
    @DisplayName("authenticate - missing email")
    class MissingEmailTests {

        @BeforeEach
        void stubNoExistingAccount() {
            when(oauthStrategyFactory.getStrategy(OAuthProvider.GOOGLE)).thenReturn(strategy);
            when(strategy.getProviderUserId(context)).thenReturn(PROVIDER_USER_ID);
            when(oauthAccountRepository.findByProviderAndProviderUserId(OAuthProvider.GOOGLE, PROVIDER_USER_ID))
                    .thenReturn(Optional.empty());
        }

        @Test
        @DisplayName("Should throw BusinessException with OAUTH_EMAIL_NOT_AVAILABLE when email is null")
        void shouldThrowExceptionWhenEmailIsNull() {
            when(strategy.getEmail(context)).thenReturn(null);

            assertThatThrownBy(() -> service.authenticate(REGISTRATION_ID, context))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.OAUTH_EMAIL_NOT_AVAILABLE);
        }

        @Test
        @DisplayName("Should throw BusinessException with OAUTH_EMAIL_NOT_AVAILABLE when email is blank")
        void shouldThrowExceptionWhenEmailIsBlank() {
            when(strategy.getEmail(context)).thenReturn("   ");

            assertThatThrownBy(() -> service.authenticate(REGISTRATION_ID, context))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.OAUTH_EMAIL_NOT_AVAILABLE);
        }

        @Test
        @DisplayName("Should not query userRepository when email is unavailable")
        void shouldNotQueryUserRepositoryWhenEmailUnavailable() {
            when(strategy.getEmail(context)).thenReturn(null);

            assertThatThrownBy(() -> service.authenticate(REGISTRATION_ID, context))
                    .isInstanceOf(BusinessException.class);

            verifyNoInteractions(userRepository);
        }
    }

    @Nested
    @DisplayName("authenticate - link existing user by email")
    class LinkExistingUserTests {

        @BeforeEach
        void stubNoExistingAccountWithEmail() {
            when(oauthStrategyFactory.getStrategy(OAuthProvider.GOOGLE)).thenReturn(strategy);
            when(strategy.getProviderUserId(context)).thenReturn(PROVIDER_USER_ID);
            when(oauthAccountRepository.findByProviderAndProviderUserId(OAuthProvider.GOOGLE, PROVIDER_USER_ID))
                    .thenReturn(Optional.empty());
            when(strategy.getEmail(context)).thenReturn(EMAIL);
        }

        @Test
        @DisplayName("Should link OAuth account to existing user found by email")
        void shouldLinkOAuthAccountToExistingUser() {
            User existingUser = mock(User.class);
            when(userRepository.findByEmailIgnoreCase(EMAIL)).thenReturn(Optional.of(existingUser));

            User result = service.authenticate(REGISTRATION_ID, context);

            assertThat(result).isEqualTo(existingUser);
        }

        @Test
        @DisplayName("Should save new OAuthAccount entity when linking existing user")
        void shouldSaveNewOAuthAccountWhenLinkingExistingUser() {
            User existingUser = mock(User.class);
            when(userRepository.findByEmailIgnoreCase(EMAIL)).thenReturn(Optional.of(existingUser));

            service.authenticate(REGISTRATION_ID, context);

            ArgumentCaptor<OAuthAccount> captor = ArgumentCaptor.forClass(OAuthAccount.class);
            verify(oauthAccountRepository, times(1)).save(captor.capture());

            OAuthAccount savedAccount = captor.getValue();
            assertThat(savedAccount.getProvider()).isEqualTo(OAuthProvider.GOOGLE);
            assertThat(savedAccount.getProviderUserId()).isEqualTo(PROVIDER_USER_ID);
            assertThat(savedAccount.getUser()).isEqualTo(existingUser);
        }

        @Test
        @DisplayName("Should not create a new user when linking existing user by email")
        void shouldNotCreateNewUserWhenLinkingExistingUser() {
            User existingUser = mock(User.class);
            when(userRepository.findByEmailIgnoreCase(EMAIL)).thenReturn(Optional.of(existingUser));

            service.authenticate(REGISTRATION_ID, context);

            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("Should not query roleRepository when linking existing user")
        void shouldNotQueryRoleRepositoryWhenLinkingExistingUser() {
            User existingUser = mock(User.class);
            when(userRepository.findByEmailIgnoreCase(EMAIL)).thenReturn(Optional.of(existingUser));

            service.authenticate(REGISTRATION_ID, context);

            verifyNoInteractions(roleRepository);
        }
    }

    @Nested
    @DisplayName("authenticate - create new OAuth user")
    class CreateNewUserTests {

        @BeforeEach
        void stubNoExistingAccountAndNoExistingUser() {
            when(oauthStrategyFactory.getStrategy(OAuthProvider.GOOGLE)).thenReturn(strategy);
            when(strategy.getProviderUserId(context)).thenReturn(PROVIDER_USER_ID);
            when(oauthAccountRepository.findByProviderAndProviderUserId(OAuthProvider.GOOGLE, PROVIDER_USER_ID))
                    .thenReturn(Optional.empty());
            when(strategy.getEmail(context)).thenReturn(EMAIL);
            when(userRepository.findByEmailIgnoreCase(EMAIL)).thenReturn(Optional.empty());
        }

        @Test
        @DisplayName("Should throw IllegalStateException when USER role is not found")
        void shouldThrowExceptionWhenUserRoleNotFound() {
            when(roleRepository.findByName(RoleName.USER.name())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.authenticate(REGISTRATION_ID, context))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("USER role not found");
        }

        @Test
        @DisplayName("Should not save user when USER role lookup fails")
        void shouldNotSaveUserWhenRoleLookupFails() {
            when(roleRepository.findByName(RoleName.USER.name())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.authenticate(REGISTRATION_ID, context))
                    .isInstanceOf(IllegalStateException.class);

            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("Should create and save new user with data from strategy when role exists")
        void shouldCreateAndSaveNewUser() {

            Role userRole = mock(Role.class);

            when(roleRepository.findByName(RoleName.USER.name()))
                    .thenReturn(Optional.of(userRole));

            when(strategy.getFullName(context))
                    .thenReturn(FULL_NAME);

            when(strategy.getProfilePictureUrl(context))
                    .thenReturn(PROFILE_PICTURE_URL);

            when(strategy.isEmailVerified(context))
                    .thenReturn(true);

            User savedUser = mock(User.class);

            when(userRepository.save(any(User.class)))
                    .thenReturn(savedUser);

            User result = service.authenticate(
                    REGISTRATION_ID,
                    context
            );

            assertThat(result)
                    .isEqualTo(savedUser);
        }

        @Test
        @DisplayName("Should populate new user builder fields correctly before saving")
        void shouldPopulateNewUserFieldsCorrectly() {
            Role userRole = mock(Role.class);
            when(roleRepository.findByName(RoleName.USER.name())).thenReturn(Optional.of(userRole));

            when(strategy.getFullName(context)).thenReturn(FULL_NAME);
            when(strategy.getProfilePictureUrl(context)).thenReturn(PROFILE_PICTURE_URL);
            when(strategy.isEmailVerified(context)).thenReturn(true);

            when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

            service.authenticate(REGISTRATION_ID, context);

            verify(userRepository).save(userCaptor.capture());

            User builtUser = userCaptor.getValue();
            assertThat(builtUser.getEmail()).isEqualTo(EMAIL);
            assertThat(builtUser.getFullName()).isEqualTo(FULL_NAME);
            assertThat(builtUser.getProfilePictureUrl()).isEqualTo(PROFILE_PICTURE_URL);
            assertThat(builtUser.isEmailVerified()).isTrue();
            assertThat(builtUser.isPhoneVerified()).isFalse();
            assertThat(builtUser.isCredentialsExpired()).isFalse();
            assertThat(builtUser.isEnabled()).isTrue();
            assertThat(builtUser.isAccountLocked()).isFalse();
            assertThat(builtUser.getPassword()).isNull();
            assertThat(builtUser.getPhoneNumber()).isNull();
            assertThat(builtUser.getPasswordChangedAt()).isNull();
            assertThat(builtUser.getRoles()).containsExactly(userRole);
        }

        @Test
        @DisplayName("Should save new OAuthAccount linked to newly created user")
        void shouldSaveOAuthAccountLinkedToNewUser() {
            Role userRole = mock(Role.class);
            when(roleRepository.findByName(RoleName.USER.name())).thenReturn(Optional.of(userRole));

            when(strategy.getFullName(context)).thenReturn(FULL_NAME);
            when(strategy.getProfilePictureUrl(context)).thenReturn(PROFILE_PICTURE_URL);
            when(strategy.isEmailVerified(context)).thenReturn(false);

            User savedUser = mock(User.class);
            when(userRepository.save(any(User.class))).thenReturn(savedUser);

            service.authenticate(REGISTRATION_ID, context);

            ArgumentCaptor<OAuthAccount> accountCaptor = ArgumentCaptor.forClass(OAuthAccount.class);
            verify(oauthAccountRepository, times(1)).save(accountCaptor.capture());

            OAuthAccount savedAccount = accountCaptor.getValue();
            assertThat(savedAccount.getProvider()).isEqualTo(OAuthProvider.GOOGLE);
            assertThat(savedAccount.getProviderUserId()).isEqualTo(PROVIDER_USER_ID);
            assertThat(savedAccount.getUser()).isEqualTo(savedUser);
        }

        @Test
        @DisplayName("Should set emailVerified false when strategy reports unverified email")
        void shouldSetEmailUnverifiedWhenStrategyReportsUnverified() {
            Role userRole = mock(Role.class);
            when(roleRepository.findByName(RoleName.USER.name())).thenReturn(Optional.of(userRole));

            when(strategy.getFullName(context)).thenReturn(FULL_NAME);
            when(strategy.getProfilePictureUrl(context)).thenReturn(PROFILE_PICTURE_URL);
            when(strategy.isEmailVerified(context)).thenReturn(false);

            when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

            service.authenticate(REGISTRATION_ID, context);

            verify(userRepository).save(userCaptor.capture());

            assertThat(userCaptor.getValue().isEmailVerified()).isFalse();
        }

        @Test
        @DisplayName("Should call strategy.getEmail twice, once for validation and once for user creation")
        void shouldCallStrategyGetEmailForValidationAndCreation() {
            Role userRole = mock(Role.class);
            when(roleRepository.findByName(RoleName.USER.name())).thenReturn(Optional.of(userRole));

            when(strategy.getFullName(context)).thenReturn(FULL_NAME);
            when(strategy.getProfilePictureUrl(context)).thenReturn(PROFILE_PICTURE_URL);
            when(strategy.isEmailVerified(context)).thenReturn(true);

            User savedUser = mock(User.class);
            when(userRepository.save(any(User.class))).thenReturn(savedUser);

            service.authenticate(REGISTRATION_ID, context);

            verify(strategy, times(2)).getEmail(context);
        }
    }

    @Nested
    @DisplayName("authenticate - strategy factory delegation")
    class StrategyFactoryDelegationTests {

        @Test
        @DisplayName("Should propagate exception when strategy factory does not support the provider")
        void shouldPropagateExceptionWhenFactoryThrows() {
            when(oauthStrategyFactory.getStrategy(OAuthProvider.GOOGLE))
                    .thenThrow(new IllegalArgumentException("Unsupported OAuth provider: GOOGLE"));

            assertThatThrownBy(() -> service.authenticate(REGISTRATION_ID, context))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Unsupported OAuth provider");

            verifyNoInteractions(oauthAccountRepository);
        }

        @Test
        @DisplayName("Should call strategy.getProviderUserId exactly once with given context")
        void shouldCallGetProviderUserIdExactlyOnce() {
            when(oauthStrategyFactory.getStrategy(OAuthProvider.GOOGLE)).thenReturn(strategy);
            when(strategy.getProviderUserId(context)).thenReturn(PROVIDER_USER_ID);

            User existingUser = mock(User.class);
            OAuthAccount account = mock(OAuthAccount.class);
            when(account.getUser()).thenReturn(existingUser);
            when(oauthAccountRepository.findByProviderAndProviderUserId(OAuthProvider.GOOGLE, PROVIDER_USER_ID))
                    .thenReturn(Optional.of(account));

            service.authenticate(REGISTRATION_ID, context);

            verify(strategy, times(1)).getProviderUserId(context);
        }
    }
}