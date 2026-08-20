package com.agri.market.auth.service;

import com.agri.market.auth.entity.RefreshTokenSession;
import com.agri.market.auth.repository.RefreshTokenSessionRepository;
import com.agri.market.exception.BusinessException;
import com.agri.market.security.jwt.JwtService;
import com.agri.market.security.jwt.TokenHasher;
import com.agri.market.support.ClientInfoTestFactory;
import com.agri.market.support.RefreshTokenSessionTestFactory;
import com.agri.market.support.UserTestFactory;
import com.agri.market.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.agri.market.exception.ErrorCode.INVALID_REFRESH_TOKEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RefreshTokenSessionService")
class RefreshTokenSessionServiceImplTest {

    private final TokenHasher refreshTokenHasher = new TokenHasher();

    @Mock
    private RefreshTokenSessionRepository refreshTokenSessionRepository;

    @Mock
    private JwtService jwtService;

    private RefreshTokenSessionService refreshTokenSessionServiceImpl;

    private User user;

    @BeforeEach
    void setUp() {
        user = UserTestFactory.activeUser();
        user.setId("user-id");

        refreshTokenSessionServiceImpl =
                new RefreshTokenSessionServiceImpl(
                        refreshTokenSessionRepository,
                        refreshTokenHasher,
                        jwtService
                );
    }

    @Nested
    @DisplayName("createSession")
    class CreateSessionTests {

        @Test
        void shouldCreateSessionWithHashedRefreshTokenAndClientInformation() {

            LocalDateTime expiration =
                    LocalDateTime.now().plusDays(7);

            when(
                    refreshTokenSessionRepository
                            .findByUser_IdAndDeviceNameAndRevokedFalse(
                                    "user-id",
                                    "Device A"
                            )
            ).thenReturn(Optional.empty());

            when(jwtService.getRefreshTokenExpirationTime())
                    .thenReturn(expiration);

            when(refreshTokenSessionRepository.save(
                    any(RefreshTokenSession.class)
            )).thenAnswer(invocation -> invocation.getArgument(0));

            RefreshTokenSession result =
                    refreshTokenSessionServiceImpl.createSession(
                            "refresh-token",
                            user,
                            ClientInfoTestFactory.deviceA()
                    );

            ArgumentCaptor<RefreshTokenSession> captor =
                    ArgumentCaptor.forClass(
                            RefreshTokenSession.class
                    );

            verify(refreshTokenSessionRepository)
                    .save(captor.capture());

            RefreshTokenSession saved =
                    captor.getValue();

            assertThat(result)
                    .isSameAs(saved);

            assertThat(saved.getUser())
                    .isEqualTo(user);

            assertThat(saved.getTokenHash())
                    .isEqualTo(
                            refreshTokenHasher.hash(
                                    "refresh-token"
                            )
                    );

            assertThat(saved.getDeviceName())
                    .isEqualTo("Device A");

            assertThat(saved.getIpAddress())
                    .isEqualTo("10.0.0.1");

            assertThat(saved.getExpiresAt())
                    .isEqualTo(expiration);

            assertThat(saved.isRevoked())
                    .isFalse();

            assertThat(saved.getRevokedAt())
                    .isNull();
        }

        @Test
        void shouldRevokeExistingDeviceSessionBeforeCreatingNewSession() {

            RefreshTokenSession existing =
                    RefreshTokenSessionTestFactory.activeSession(
                            user,
                            "existing-hash",
                            "Device A"
                    );

            when(
                    refreshTokenSessionRepository
                            .findByUser_IdAndDeviceNameAndRevokedFalse(
                                    "user-id",
                                    "Device A"
                            )
            ).thenReturn(Optional.of(existing));

            when(jwtService.getRefreshTokenExpirationTime())
                    .thenReturn(
                            LocalDateTime.now().plusDays(7)
                    );

            when(refreshTokenSessionRepository.save(
                    any(RefreshTokenSession.class)
            )).thenAnswer(invocation -> invocation.getArgument(0));

            refreshTokenSessionServiceImpl.createSession(
                    "refresh-token",
                    user,
                    ClientInfoTestFactory.deviceA()
            );

            ArgumentCaptor<RefreshTokenSession> captor =
                    ArgumentCaptor.forClass(
                            RefreshTokenSession.class
                    );

            verify(refreshTokenSessionRepository, times(2))
                    .save(captor.capture());

            List<RefreshTokenSession> savedSessions =
                    captor.getAllValues();

            RefreshTokenSession revokedSession =
                    savedSessions.get(0);

            RefreshTokenSession newSession =
                    savedSessions.get(1);

            assertThat(revokedSession)
                    .isSameAs(existing);

            assertThat(revokedSession.isRevoked())
                    .isTrue();

            assertThat(revokedSession.getRevokedAt())
                    .isNotNull();

            assertThat(newSession)
                    .isNotSameAs(existing);

            assertThat(newSession.getTokenHash())
                    .isEqualTo(
                            refreshTokenHasher.hash(
                                    "refresh-token"
                            )
                    );

            assertThat(newSession.isRevoked())
                    .isFalse();
        }

        @Test
        void shouldNotRevokeSessionsFromDifferentDevices() {

            when(
                    refreshTokenSessionRepository
                            .findByUser_IdAndDeviceNameAndRevokedFalse(
                                    "user-id",
                                    "Device A"
                            )
            ).thenReturn(Optional.empty());

            when(
                    refreshTokenSessionRepository
                            .findByUser_IdAndDeviceNameAndRevokedFalse(
                                    "user-id",
                                    "Device B"
                            )
            ).thenReturn(Optional.empty());

            when(jwtService.getRefreshTokenExpirationTime())
                    .thenReturn(
                            LocalDateTime.now().plusDays(7)
                    );

            when(refreshTokenSessionRepository.save(
                    any(RefreshTokenSession.class)
            )).thenAnswer(invocation -> invocation.getArgument(0));

            RefreshTokenSession first =
                    refreshTokenSessionServiceImpl.createSession(
                            "refresh-a",
                            user,
                            ClientInfoTestFactory.deviceA()
                    );

            RefreshTokenSession second =
                    refreshTokenSessionServiceImpl.createSession(
                            "refresh-b",
                            user,
                            ClientInfoTestFactory.deviceB()
                    );

            assertThat(first.isRevoked())
                    .isFalse();

            assertThat(second.isRevoked())
                    .isFalse();

            verify(refreshTokenSessionRepository, times(2))
                    .save(any(RefreshTokenSession.class));
        }
    }

    @Nested
    @DisplayName("findValidSession")
    class FindValidSessionTests {

        @Test
        void shouldReturnActiveSessionForValidToken() {

            RefreshTokenSession session =
                    RefreshTokenSessionTestFactory.activeSession(
                            user,
                            refreshTokenHasher.hash(
                                    "refresh-token"
                            ),
                            "Device A"
                    );

            when(
                    refreshTokenSessionRepository.findByTokenHash(
                            refreshTokenHasher.hash(
                                    "refresh-token"
                            )
                    )
            ).thenReturn(Optional.of(session));

            RefreshTokenSession result =
                    refreshTokenSessionServiceImpl.findValidSession(
                            "refresh-token"
                    );

            assertThat(result)
                    .isSameAs(session);

            verify(refreshTokenSessionRepository)
                    .findByTokenHash(
                            refreshTokenHasher.hash(
                                    "refresh-token"
                            )
                    );
        }

        @Test
        void shouldRejectUnknownRefreshToken() {

            when(
                    refreshTokenSessionRepository.findByTokenHash(
                            refreshTokenHasher.hash(
                                    "refresh-token"
                            )
                    )
            ).thenReturn(Optional.empty());

            assertThatThrownBy(
                    () -> refreshTokenSessionServiceImpl
                            .findValidSession("refresh-token")
            )
                    .isInstanceOf(BusinessException.class)
                    .satisfies(exception ->
                            assertThat(
                                    ((BusinessException) exception)
                                            .getErrorCode()
                            ).isEqualTo(
                                    INVALID_REFRESH_TOKEN
                            )
                    );
        }

        @Test
        void shouldRejectRevokedSessionAndRevokeAllActiveSessions() {

            RefreshTokenSession revokedSession =
                    RefreshTokenSessionTestFactory.session(
                            user,
                            refreshTokenHasher.hash(
                                    "refresh-token"
                            ),
                            "Device A",
                            "10.0.0.1",
                            LocalDateTime.now().plusDays(7),
                            true
                    );

            RefreshTokenSession activeSession =
                    RefreshTokenSessionTestFactory.activeSession(
                            user,
                            "another-hash",
                            "Device B"
                    );

            when(
                    refreshTokenSessionRepository.findByTokenHash(
                            refreshTokenHasher.hash(
                                    "refresh-token"
                            )
                    )
            ).thenReturn(Optional.of(revokedSession));

            when(
                    refreshTokenSessionRepository
                            .findAllByUser_IdAndRevokedFalse(
                                    "user-id"
                            )
            ).thenReturn(
                    List.of(activeSession)
            );

            assertThatThrownBy(
                    () -> refreshTokenSessionServiceImpl
                            .findValidSession("refresh-token")
            )
                    .isInstanceOf(BusinessException.class)
                    .satisfies(exception ->
                            assertThat(
                                    ((BusinessException) exception)
                                            .getErrorCode()
                            ).isEqualTo(
                                    INVALID_REFRESH_TOKEN
                            )
                    );

            assertThat(activeSession.isRevoked())
                    .isTrue();

            assertThat(activeSession.getRevokedAt())
                    .isNotNull();

            verify(refreshTokenSessionRepository)
                    .saveAll(
                            List.of(activeSession)
                    );
        }

        @Test
        void shouldRejectExpiredSession() {

            RefreshTokenSession session =
                    RefreshTokenSessionTestFactory.session(
                            user,
                            refreshTokenHasher.hash(
                                    "refresh-token"
                            ),
                            "Device A",
                            "10.0.0.1",
                            LocalDateTime.now().minusMinutes(1),
                            false
                    );

            when(
                    refreshTokenSessionRepository.findByTokenHash(
                            refreshTokenHasher.hash(
                                    "refresh-token"
                            )
                    )
            ).thenReturn(Optional.of(session));

            assertThatThrownBy(
                    () -> refreshTokenSessionServiceImpl
                            .findValidSession("refresh-token")
            )
                    .isInstanceOf(BusinessException.class)
                    .satisfies(exception ->
                            assertThat(
                                    ((BusinessException) exception)
                                            .getErrorCode()
                            ).isEqualTo(
                                    INVALID_REFRESH_TOKEN
                            )
                    );

            verify(
                    refreshTokenSessionRepository,
                    never()
            ).saveAll(any());
        }
    }

    @Nested
    @DisplayName("rotateSession")
    class RotateSessionTests {

        @Test
        void shouldRevokeOldSessionAndCreateNewSession() {

            RefreshTokenSession currentSession =
                    RefreshTokenSessionTestFactory.activeSession(
                            user,
                            "old-hash",
                            "Device A"
                    );

            LocalDateTime expiration =
                    LocalDateTime.now().plusDays(14);

            when(jwtService.getRefreshTokenExpirationTime())
                    .thenReturn(expiration);

            when(refreshTokenSessionRepository.save(
                    any(RefreshTokenSession.class)
            )).thenAnswer(invocation -> invocation.getArgument(0));

            RefreshTokenSession result =
                    refreshTokenSessionServiceImpl.rotateSession(
                            currentSession,
                            "new-refresh-token"
                    );

            ArgumentCaptor<RefreshTokenSession> captor =
                    ArgumentCaptor.forClass(
                            RefreshTokenSession.class
                    );

            verify(refreshTokenSessionRepository, times(2))
                    .save(captor.capture());

            List<RefreshTokenSession> savedSessions =
                    captor.getAllValues();

            RefreshTokenSession oldSession =
                    savedSessions.get(0);

            RefreshTokenSession newSession =
                    savedSessions.get(1);

            assertThat(oldSession)
                    .isSameAs(currentSession);

            assertThat(oldSession.isRevoked())
                    .isTrue();

            assertThat(oldSession.getRevokedAt())
                    .isNotNull();

            assertThat(oldSession.getTokenHash())
                    .isEqualTo("old-hash");

            assertThat(newSession)
                    .isNotSameAs(currentSession);

            assertThat(newSession.getUser())
                    .isEqualTo(user);

            assertThat(newSession.getTokenHash())
                    .isEqualTo(
                            refreshTokenHasher.hash(
                                    "new-refresh-token"
                            )
                    );

            assertThat(newSession.getDeviceName())
                    .isEqualTo("Device A");

            assertThat(newSession.getIpAddress())
                    .isEqualTo(
                            currentSession.getIpAddress()
                    );

            assertThat(newSession.getExpiresAt())
                    .isEqualTo(expiration);

            assertThat(newSession.isRevoked())
                    .isFalse();

            assertThat(newSession.getRevokedAt())
                    .isNull();

            assertThat(result)
                    .isSameAs(newSession);
        }

        @Test
        void shouldSaveOldSessionBeforeSavingNewSession() {

            RefreshTokenSession currentSession =
                    RefreshTokenSessionTestFactory.activeSession(
                            user,
                            "old-hash",
                            "Device A"
                    );

            when(jwtService.getRefreshTokenExpirationTime())
                    .thenReturn(
                            LocalDateTime.now().plusDays(14)
                    );

            when(refreshTokenSessionRepository.save(
                    any(RefreshTokenSession.class)
            )).thenAnswer(invocation -> invocation.getArgument(0));

            refreshTokenSessionServiceImpl.rotateSession(
                    currentSession,
                    "new-refresh-token"
            );

            ArgumentCaptor<RefreshTokenSession> captor =
                    ArgumentCaptor.forClass(
                            RefreshTokenSession.class
                    );

            verify(refreshTokenSessionRepository, times(2))
                    .save(captor.capture());

            List<RefreshTokenSession> savedSessions =
                    captor.getAllValues();

            assertThat(savedSessions.get(0))
                    .isSameAs(currentSession);

            assertThat(savedSessions.get(0).isRevoked())
                    .isTrue();

            assertThat(savedSessions.get(1))
                    .isNotSameAs(currentSession);

            assertThat(savedSessions.get(1).isRevoked())
                    .isFalse();
        }
    }

    @Nested
    @DisplayName("revokeSession")
    class RevokeSessionTests {

        @Test
        void shouldRevokeActiveSession() {

            RefreshTokenSession session =
                    RefreshTokenSessionTestFactory.activeSession(
                            user,
                            "hash",
                            "Device A"
                    );

            when(refreshTokenSessionRepository.save(
                    any(RefreshTokenSession.class)
            )).thenAnswer(invocation -> invocation.getArgument(0));

            refreshTokenSessionServiceImpl.revokeSession(session);

            assertThat(session.isRevoked())
                    .isTrue();

            assertThat(session.getRevokedAt())
                    .isNotNull();

            verify(refreshTokenSessionRepository)
                    .save(session);
        }

        @Test
        void shouldNotSaveAlreadyRevokedSession() {

            RefreshTokenSession session =
                    RefreshTokenSessionTestFactory.session(
                            user,
                            "hash",
                            "Device A",
                            "10.0.0.1",
                            LocalDateTime.now().plusDays(7),
                            true
                    );

            refreshTokenSessionServiceImpl.revokeSession(session);

            verify(
                    refreshTokenSessionRepository,
                    never()
            ).save(any(RefreshTokenSession.class));
        }

        @Test
        void shouldRevokeSessionByRefreshToken() {

            RefreshTokenSession session =
                    RefreshTokenSessionTestFactory.activeSession(
                            user,
                            refreshTokenHasher.hash(
                                    "refresh-token"
                            ),
                            "Device A"
                    );

            when(
                    refreshTokenSessionRepository.findByTokenHash(
                            refreshTokenHasher.hash(
                                    "refresh-token"
                            )
                    )
            ).thenReturn(Optional.of(session));

            when(refreshTokenSessionRepository.save(
                    any(RefreshTokenSession.class)
            )).thenAnswer(invocation -> invocation.getArgument(0));

            refreshTokenSessionServiceImpl.revokeSession(
                    "refresh-token"
            );

            assertThat(session.isRevoked())
                    .isTrue();

            assertThat(session.getRevokedAt())
                    .isNotNull();

            verify(refreshTokenSessionRepository)
                    .save(session);
        }

        @Test
        void shouldRejectUnknownRefreshTokenWhenRevoking() {

            when(
                    refreshTokenSessionRepository.findByTokenHash(
                            refreshTokenHasher.hash(
                                    "refresh-token"
                            )
                    )
            ).thenReturn(Optional.empty());

            assertThatThrownBy(
                    () -> refreshTokenSessionServiceImpl
                            .revokeSession("refresh-token")
            )
                    .isInstanceOf(BusinessException.class)
                    .satisfies(exception ->
                            assertThat(
                                    ((BusinessException) exception)
                                            .getErrorCode()
                            ).isEqualTo(
                                    INVALID_REFRESH_TOKEN
                            )
                    );

            verify(
                    refreshTokenSessionRepository,
                    never()
            ).save(any(RefreshTokenSession.class));
        }
    }

    @Nested
    @DisplayName("revokeAllSessions")
    class RevokeAllSessionsTests {

        @Test
        void shouldRevokeEveryActiveSessionForUser() {

            RefreshTokenSession first =
                    RefreshTokenSessionTestFactory.activeSession(
                            user,
                            "hash-1",
                            "Device A"
                    );

            RefreshTokenSession second =
                    RefreshTokenSessionTestFactory.activeSession(
                            user,
                            "hash-2",
                            "Device B"
                    );

            when(
                    refreshTokenSessionRepository
                            .findAllByUser_IdAndRevokedFalse(
                                    "user-id"
                            )
            ).thenReturn(
                    List.of(first, second)
            );

            refreshTokenSessionServiceImpl.revokeAllSessions(
                    "user-id"
            );

            assertThat(first.isRevoked())
                    .isTrue();

            assertThat(second.isRevoked())
                    .isTrue();

            assertThat(first.getRevokedAt())
                    .isNotNull();

            assertThat(second.getRevokedAt())
                    .isNotNull();

            verify(refreshTokenSessionRepository)
                    .saveAll(
                            List.of(first, second)
                    );
        }

        @Test
        void shouldSetSameRevocationTimeForAllSessions() {

            RefreshTokenSession first =
                    RefreshTokenSessionTestFactory.activeSession(
                            user,
                            "hash-1",
                            "Device A"
                    );

            RefreshTokenSession second =
                    RefreshTokenSessionTestFactory.activeSession(
                            user,
                            "hash-2",
                            "Device B"
                    );

            when(
                    refreshTokenSessionRepository
                            .findAllByUser_IdAndRevokedFalse(
                                    "user-id"
                            )
            ).thenReturn(
                    List.of(first, second)
            );

            refreshTokenSessionServiceImpl.revokeAllSessions(
                    "user-id"
            );

            assertThat(first.getRevokedAt())
                    .isEqualTo(second.getRevokedAt());

            assertThat(first.getRevokedAt())
                    .isNotNull();
        }

        @Test
        void shouldDoNothingWhenUserHasNoActiveSessions() {

            when(
                    refreshTokenSessionRepository
                            .findAllByUser_IdAndRevokedFalse(
                                    "user-id"
                            )
            ).thenReturn(List.of());

            refreshTokenSessionServiceImpl.revokeAllSessions(
                    "user-id"
            );

            verify(
                    refreshTokenSessionRepository,
                    never()
            ).saveAll(any());

            verify(
                    refreshTokenSessionRepository,
                    never()
            ).save(any(RefreshTokenSession.class));
        }
    }
}