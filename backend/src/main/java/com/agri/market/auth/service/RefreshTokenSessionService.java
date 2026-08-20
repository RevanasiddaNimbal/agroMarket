package com.agri.market.auth.service;

import com.agri.market.auth.dto.ClientInfo;
import com.agri.market.auth.entity.RefreshTokenSession;
import com.agri.market.user.entity.User;

public interface RefreshTokenSessionService {

    RefreshTokenSession createSession(
            String refreshToken,
            User user,
            ClientInfo clientInfo
    );

    RefreshTokenSession findValidSession(
            String refreshToken
    );

    RefreshTokenSession rotateSession(
            RefreshTokenSession currentSession,
            String newRefreshToken
    );

    void revokeSession(
            RefreshTokenSession session
    );

    void revokeSession(
            String refreshToken
    );

    void revokeAllSessions(
            String userId
    );
}