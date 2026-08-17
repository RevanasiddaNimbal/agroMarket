package com.agri.market.auth.repository;

import com.agri.market.auth.entity.RefreshTokenSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenSessionRepository
        extends JpaRepository<RefreshTokenSession, String> {

    Optional<RefreshTokenSession> findByTokenHash(
            String tokenHash
    );

    List<RefreshTokenSession> findAllByUser_IdAndRevokedFalse(
            String userId
    );

    Optional<RefreshTokenSession>
    findByUser_IdAndDeviceNameAndRevokedFalse(
            String userId,
            String deviceName
    );
}