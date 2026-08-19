package com.agri.market.security.oauth2.repository;

import com.agri.market.security.oauth2.entity.OAuthAccount;
import com.agri.market.security.oauth2.entity.OAuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OAuthAccountRepository
        extends JpaRepository<OAuthAccount, String> {

    Optional<OAuthAccount> findByProviderAndProviderUserId(
            OAuthProvider provider,
            String providerUserId
    );

    Optional<OAuthAccount> findByUserIdAndProvider(
            String userId,
            OAuthProvider provider
    );
}