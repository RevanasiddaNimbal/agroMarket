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
class RefreshTokenSessionServiceTest {

    private final TokenHasher refreshTokenHasher = new TokenHasher();
    @Mock
    private RefreshTokenSessionRepository refreshTokenSessionRepository;
    @Mock
    private JwtService jwtService;
    private RefreshTokenSessionService refreshTokenSessionService;

    private User user;

    @BeforeEach
    void setUp() {
        user = UserTestFactory.activeUser();
        user.setId("user-id");
        refreshTokenSessionService = new RefreshTokenSessionService(
                refreshTokenSessionRepository,
                refreshTokenHasher,
                jwtService
        );
    }

    @Nested
    @DisplayName("createSession")
    class CreateSessionTests {

        @Test
        void shouldCreateSessionAndHashRefreshToken() {
            when(refreshTokenSessionRepository.findByUser_IdAndDeviceNameAndRevokedFalse("user-id", "Device A"))
                    .thenReturn(Optional.empty());
            when(jwtService.getRefreshTokenExpirationTime())
                    .thenReturn(LocalDateTime.now().plusDays(7));
            when(refreshTokenSessionRepository.save(any(RefreshTokenSession.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            RefreshTokenSession session = refreshTokenSessionService.createSession(
                    "refresh-token",
                    user,
                    ClientInfoTestFactory.deviceA()
            );

            ArgumentCaptor<RefreshTokenSession> captor =
                    ArgumentCaptor.forClass(RefreshTokenSession.class);
            verify(refreshTokenSessionRepository).save(captor.capture());
            RefreshTokenSession saved = captor.getValue();

            assertThat(saved.getUser()).isEqualTo(user);
            assertThat(saved.getTokenHash()).isEqualTo(refreshTokenHasher.hash("refresh-token"));
            assertThat(saved.getDeviceName()).isEqualTo("Device A");
            assertThat(saved.getIpAddress()).isEqualTo("10.0.0.1");
            assertThat(saved.isRevoked()).isFalse();
            assertThat(session).isSameAs(saved);
        }

        @Test
        void shouldRevokeExistingSessionForSameUserAndDeviceBeforeCreatingNewOne() {
            RefreshTokenSession existing = RefreshTokenSessionTestFactory.activeSession(
                    user,
                    "existing-hash",
                    "Device A"
            );
            when(refreshTokenSessionRepository.findByUser_IdAndDeviceNameAndRevokedFalse("user-id", "Device A"))
                    .thenReturn(Optional.of(existing));
            when(jwtService.getRefreshTokenExpirationTime())
                    .thenReturn(LocalDateTime.now().plusDays(7));
            when(refreshTokenSessionRepository.save(any(RefreshTokenSession.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            refreshTokenSessionService.createSession(
                    "refresh-token",
                    user,
                    ClientInfoTestFactory.deviceA()
            );

            ArgumentCaptor<RefreshTokenSession> captor =
                    ArgumentCaptor.forClass(RefreshTokenSession.class);
            verify(refreshTokenSessionRepository, times(2)).save(captor.capture());
            List<RefreshTokenSession> savedSessions = captor.getAllValues();

            assertThat(savedSessions.get(0).isRevoked()).isTrue();
            assertThat(savedSessions.get(1).getTokenHash())
                    .isEqualTo(refreshTokenHasher.hash("refresh-token"));
            assertThat(savedSessions.get(1).isRevoked()).isFalse();
        }

        @Test
        void shouldKeepSessionsSeparateAcrossDifferentDevices() {
            when(refreshTokenSessionRepository.findByUser_IdAndDeviceNameAndRevokedFalse("user-id", "Device A"))
                    .thenReturn(Optional.empty());
            when(refreshTokenSessionRepository.findByUser_IdAndDeviceNameAndRevokedFalse("user-id", "Device B"))
                    .thenReturn(Optional.empty());
            when(jwtService.getRefreshTokenExpirationTime())
                    .thenReturn(LocalDateTime.now().plusDays(7));
            when(refreshTokenSessionRepository.save(any(RefreshTokenSession.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            RefreshTokenSession first = refreshTokenSessionService.createSession(
                    "refresh-a",
                    user,
                    ClientInfoTestFactory.deviceA()
            );
            RefreshTokenSession second = refreshTokenSessionService.createSession(
                    "refresh-b",
                    user,
                    ClientInfoTestFactory.deviceB()
            );

            assertThat(first.isRevoked()).isFalse();
            assertThat(second.isRevoked()).isFalse();
            verify(refreshTokenSessionRepository, times(2)).save(any(RefreshTokenSession.class));
        }
    }

    @Nested
    @DisplayName("findValidSession")
    class FindValidSessionTests {

        @Test
        void shouldThrowWhenRefreshTokenIsNull() {
            assertThatThrownBy(() -> refreshTokenSessionService.findValidSession(null))
                    .isInstanceOf(NullPointerException.class);

            verifyNoInteractions(refreshTokenSessionRepository);
        }

        @Test
        void shouldReturnActiveSessionForValidToken() {
            RefreshTokenSession session = RefreshTokenSessionTestFactory.activeSession(
                    user,
                    refreshTokenHasher.hash("refresh-token"),
                    "Device A"
            );
            when(refreshTokenSessionRepository.findByTokenHash(refreshTokenHasher.hash("refresh-token")))
                    .thenReturn(Optional.of(session));

            RefreshTokenSession found = refreshTokenSessionService.findValidSession("refresh-token");

            assertThat(found).isSameAs(session);
        }

        @Test
        void shouldRejectUnknownToken() {
            when(refreshTokenSessionRepository.findByTokenHash(refreshTokenHasher.hash("refresh-token")))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> refreshTokenSessionService.findValidSession("refresh-token"))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(ex -> assertThat(((BusinessException) ex).getErrorCode()).isEqualTo(INVALID_REFRESH_TOKEN));
        }

        @Test
        void shouldRejectRevokedSession() {
            RefreshTokenSession session = RefreshTokenSessionTestFactory.session(
                    user,
                    refreshTokenHasher.hash("refresh-token"),
                    "Device A",
                    "10.0.0.1",
                    LocalDateTime.now().plusDays(7),
                    true
            );
            when(refreshTokenSessionRepository.findByTokenHash(refreshTokenHasher.hash("refresh-token")))
                    .thenReturn(Optional.of(session));

            assertThatThrownBy(() -> refreshTokenSessionService.findValidSession("refresh-token"))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(ex -> assertThat(((BusinessException) ex).getErrorCode()).isEqualTo(INVALID_REFRESH_TOKEN));
        }

        @Test
        void shouldRejectExpiredSession() {
            RefreshTokenSession session = RefreshTokenSessionTestFactory.session(
                    user,
                    refreshTokenHasher.hash("refresh-token"),
                    "Device A",
                    "10.0.0.1",
                    LocalDateTime.now().minusMinutes(1),
                    false
            );
            when(refreshTokenSessionRepository.findByTokenHash(refreshTokenHasher.hash("refresh-token")))
                    .thenReturn(Optional.of(session));

            assertThatThrownBy(() -> refreshTokenSessionService.findValidSession("refresh-token"))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(ex -> assertThat(((BusinessException) ex).getErrorCode()).isEqualTo(INVALID_REFRESH_TOKEN));
        }
    }

    @Nested
    @DisplayName("rotateSession")
    class RotateSessionTests {

        @Test
        void shouldUpdateTokenHashAndExpiration() {
            RefreshTokenSession session = RefreshTokenSessionTestFactory.activeSession(
                    user,
                    "old-hash",
                    "Device A"
            );
            when(jwtService.getRefreshTokenExpirationTime())
                    .thenReturn(LocalDateTime.now().plusDays(14));
            when(refreshTokenSessionRepository.save(any(RefreshTokenSession.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            refreshTokenSessionService.rotateSession(session, "new-refresh-token");

            assertThat(session.getTokenHash())
                    .isEqualTo(refreshTokenHasher.hash("new-refresh-token"));
            assertThat(session.isRevoked()).isFalse();
            assertThat(session.getRevokedAt()).isNull();
            verify(refreshTokenSessionRepository).save(session);
        }
    }

    @Nested
    @DisplayName("revokeSession")
    class RevokeSessionTests {

        @Test
        void shouldRevokeSessionAndSetRevokedAt() {
            RefreshTokenSession session = RefreshTokenSessionTestFactory.activeSession(
                    user,
                    "hash",
                    "Device A"
            );
            when(refreshTokenSessionRepository.save(any(RefreshTokenSession.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            refreshTokenSessionService.revokeSession(session);

            assertThat(session.isRevoked()).isTrue();
            assertThat(session.getRevokedAt()).isNotNull();
            verify(refreshTokenSessionRepository).save(session);
        }

        @Test
        void shouldRevokeSessionByRefreshToken() {
            RefreshTokenSession session = RefreshTokenSessionTestFactory.activeSession(
                    user,
                    refreshTokenHasher.hash("refresh-token"),
                    "Device A"
            );
            when(refreshTokenSessionRepository.findByTokenHash(refreshTokenHasher.hash("refresh-token")))
                    .thenReturn(Optional.of(session));
            when(refreshTokenSessionRepository.save(any(RefreshTokenSession.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            refreshTokenSessionService.revokeSession("refresh-token");

            assertThat(session.isRevoked()).isTrue();
            verify(refreshTokenSessionRepository).save(session);
        }
    }

    @Nested
    @DisplayName("revokeAllSessions")
    class RevokeAllSessionsTests {

        @Test
        void shouldRevokeEveryActiveSessionForUser() {
            RefreshTokenSession first = RefreshTokenSessionTestFactory.activeSession(
                    user,
                    "hash-1",
                    "Device A"
            );
            RefreshTokenSession second = RefreshTokenSessionTestFactory.activeSession(
                    user,
                    "hash-2",
                    "Device B"
            );
            when(refreshTokenSessionRepository.findAllByUser_IdAndRevokedFalse("user-id"))
                    .thenReturn(List.of(first, second));
            when(refreshTokenSessionRepository.save(any(RefreshTokenSession.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            refreshTokenSessionService.revokeAllSessions("user-id");

            assertThat(first.isRevoked()).isTrue();
            assertThat(second.isRevoked()).isTrue();
            verify(refreshTokenSessionRepository, times(2)).save(any(RefreshTokenSession.class));
        }

        @Test
        void shouldDoNothingWhenUserHasNoActiveSessions() {
            when(refreshTokenSessionRepository.findAllByUser_IdAndRevokedFalse("user-id"))
                    .thenReturn(List.of());

            refreshTokenSessionService.revokeAllSessions("user-id");

            verify(refreshTokenSessionRepository, never()).save(any());
        }
    }
}
