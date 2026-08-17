package com.agri.market.auth.entity;

import com.agri.market.common.BaseEntity;
import com.agri.market.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "refresh_token_sessions",
        indexes = {
                @Index(
                        name = "idx_refresh_session_user_id",
                        columnList = "user_id"
                ),
                @Index(
                        name = "idx_refresh_session_token_hash",
                        columnList = "token_hash"
                )
        }
)
public class RefreshTokenSession extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false
    )
    private User user;

    @Column(
            name = "token_hash",
            nullable = false,
            unique = true,
            length = 64
    )
    private String tokenHash;

    @Column(
            name = "device_name",
            nullable = false,
            length = 255
    )
    private String deviceName;

    @Column(
            name = "ip_address",
            length = 45
    )
    private String ipAddress;

    @Column(
            name = "expires_at",
            nullable = false
    )
    private LocalDateTime expiresAt;

    @Column(
            name = "revoked",
            nullable = false
    )
    private boolean revoked;

    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;
}