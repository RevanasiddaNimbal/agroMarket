package com.agri.market.security.oauth2.repository;

import com.agri.market.security.oauth2.entity.OAuthLoginCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OAuthLoginCodeRepository
        extends JpaRepository<OAuthLoginCode, String> {

    Optional<OAuthLoginCode> findByCode(String code);
}