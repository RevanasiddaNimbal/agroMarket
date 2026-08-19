package com.agri.market.security.oauth2.service;

import com.agri.market.exception.BusinessException;
import com.agri.market.security.jwt.TokenHasher;
import com.agri.market.security.oauth2.entity.OAuthLoginCode;
import com.agri.market.security.oauth2.repository.OAuthLoginCodeRepository;
import com.agri.market.security.token.TokenGenerator;
import com.agri.market.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.agri.market.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuthLoginCodeServiceImpl
        implements OAuthLoginCodeService {

    private final OAuthLoginCodeRepository oauthLoginCodeRepository;
    private final TokenGenerator tokenGenerator;
    private final TokenHasher tokenHasher;

    @Value("${app.security.oauth2.login-code-expiration-seconds:60}")
    private long loginCodeExpirationSeconds;

    @Override
    @Transactional
    public String createCode(
            final User user
    ) {

        final String code =
                tokenGenerator.generate();

        final LocalDateTime expiresAt =
                LocalDateTime.now()
                        .plusSeconds(loginCodeExpirationSeconds);

        final OAuthLoginCode loginCode =
                OAuthLoginCode.builder()
                        .code(tokenHasher.hash(code))
                        .user(user)
                        .expiresAt(expiresAt)
                        .used(false)
                        .build();

        oauthLoginCodeRepository.save(loginCode);

        log.debug(
                "OAuth login code created for user: {}",
                user.getId()
        );

        return code;
    }

    @Override
    @Transactional
    public User exchangeCode(
            final String code
    ) {

        log.debug(
                "OAuth login code exchange requested"
        );

        final OAuthLoginCode loginCode =
                oauthLoginCodeRepository
                        .findByCode(tokenHasher.hash(code))
                        .orElseThrow(() -> {

                            log.warn(
                                    "OAuth login code not found"
                            );

                            return new BusinessException(
                                    INVALID_OAUTH_LOGIN_CODE
                            );
                        });

        if (loginCode.isUsed()) {

            log.warn(
                    "OAuth login code has already been used"
            );

            throw new BusinessException(
                    OAUTH_LOGIN_CODE_ALREADY_USED
            );
        }

        if (loginCode.getExpiresAt()
                .isBefore(LocalDateTime.now())) {

            log.warn(
                    "OAuth login code has expired"
            );

            throw new BusinessException(
                    OAUTH_LOGIN_CODE_EXPIRED
            );
        }

        loginCode.setUsed(true);

        oauthLoginCodeRepository.save(loginCode);

        log.info(
                "OAuth login code exchanged successfully for user: {}",
                loginCode.getUser().getId()
        );

        return loginCode.getUser();
    }
}