package com.agri.market.email.repository;

import com.agri.market.email.entity.EmailVerificationToken;
import com.agri.market.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailVerificationTokenRepository
        extends JpaRepository<EmailVerificationToken, String> {

    Optional<EmailVerificationToken> findByTokenHash(String tokenHash);

    void deleteByUser(User user);
}