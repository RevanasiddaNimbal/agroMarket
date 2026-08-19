package com.agri.market.security.client;

import com.agri.market.auth.dto.ClientInfo;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ClientInfoResolver")
class ClientInfoResolverTest {

    private final ClientInfoResolver clientInfoResolver =
            new ClientInfoResolver();
    @Mock
    private HttpServletRequest request;

    @Nested
    @DisplayName("resolve")
    class ResolveTests {

        @Test
        @DisplayName("should resolve device name and IP address")
        void shouldResolveDeviceNameAndIpAddress() {
            when(request.getHeader("User-Agent"))
                    .thenReturn("Mozilla/5.0");
            when(request.getHeader("X-Forwarded-For"))
                    .thenReturn(null);
            when(request.getRemoteAddr())
                    .thenReturn("192.168.1.10");

            ClientInfo result =
                    clientInfoResolver.resolve(request);

            assertEquals("Mozilla/5.0", result.deviceName());
            assertEquals("192.168.1.10", result.ipAddress());

            verify(request).getHeader("User-Agent");
            verify(request).getHeader("X-Forwarded-For");
            verify(request).getRemoteAddr();
        }

        @Test
        @DisplayName("should use first IP from X-Forwarded-For")
        void shouldUseFirstIpFromForwardedFor() {
            when(request.getHeader("User-Agent"))
                    .thenReturn("Mozilla/5.0");
            when(request.getHeader("X-Forwarded-For"))
                    .thenReturn("203.0.113.10, 198.51.100.20, 192.0.2.30");

            ClientInfo result =
                    clientInfoResolver.resolve(request);

            assertEquals("Mozilla/5.0", result.deviceName());
            assertEquals("203.0.113.10", result.ipAddress());

            verify(request).getHeader("X-Forwarded-For");
        }

        @Test
        @DisplayName("should trim first forwarded IP")
        void shouldTrimFirstForwardedIp() {
            when(request.getHeader("User-Agent"))
                    .thenReturn("Mozilla/5.0");
            when(request.getHeader("X-Forwarded-For"))
                    .thenReturn(" 203.0.113.10 , 198.51.100.20");

            ClientInfo result =
                    clientInfoResolver.resolve(request);

            assertEquals("203.0.113.10", result.ipAddress());
        }

        @Test
        @DisplayName("should use remote address when forwarded IP is null")
        void shouldUseRemoteAddressWhenForwardedIpIsNull() {
            when(request.getHeader("User-Agent"))
                    .thenReturn("Mozilla/5.0");
            when(request.getHeader("X-Forwarded-For"))
                    .thenReturn(null);
            when(request.getRemoteAddr())
                    .thenReturn("192.168.1.10");

            ClientInfo result =
                    clientInfoResolver.resolve(request);

            assertEquals("192.168.1.10", result.ipAddress());

            verify(request).getRemoteAddr();
        }

        @Test
        @DisplayName("should use remote address when forwarded IP is blank")
        void shouldUseRemoteAddressWhenForwardedIpIsBlank() {
            when(request.getHeader("User-Agent"))
                    .thenReturn("Mozilla/5.0");
            when(request.getHeader("X-Forwarded-For"))
                    .thenReturn("   ");
            when(request.getRemoteAddr())
                    .thenReturn("192.168.1.10");

            ClientInfo result =
                    clientInfoResolver.resolve(request);

            assertEquals("192.168.1.10", result.ipAddress());

            verify(request).getRemoteAddr();
        }

        @Test
        @DisplayName("should return Unknown device when user agent is null")
        void shouldReturnUnknownDeviceWhenUserAgentIsNull() {
            when(request.getHeader("User-Agent"))
                    .thenReturn(null);
            when(request.getHeader("X-Forwarded-For"))
                    .thenReturn(null);
            when(request.getRemoteAddr())
                    .thenReturn("192.168.1.10");

            ClientInfo result =
                    clientInfoResolver.resolve(request);

            assertEquals("Unknown", result.deviceName());
            assertEquals("192.168.1.10", result.ipAddress());
        }

        @Test
        @DisplayName("should return Unknown device when user agent is blank")
        void shouldReturnUnknownDeviceWhenUserAgentIsBlank() {
            when(request.getHeader("User-Agent"))
                    .thenReturn("   ");
            when(request.getHeader("X-Forwarded-For"))
                    .thenReturn(null);
            when(request.getRemoteAddr())
                    .thenReturn("192.168.1.10");

            ClientInfo result =
                    clientInfoResolver.resolve(request);

            assertEquals("Unknown", result.deviceName());
            assertEquals("192.168.1.10", result.ipAddress());
        }

        @Test
        @DisplayName("should return Unknown IP when remote address is null")
        void shouldReturnUnknownIpWhenRemoteAddressIsNull() {
            when(request.getHeader("User-Agent"))
                    .thenReturn("Mozilla/5.0");
            when(request.getHeader("X-Forwarded-For"))
                    .thenReturn(null);
            when(request.getRemoteAddr())
                    .thenReturn(null);

            ClientInfo result =
                    clientInfoResolver.resolve(request);

            assertEquals("Mozilla/5.0", result.deviceName());
            assertEquals("Unknown", result.ipAddress());
        }

        @Test
        @DisplayName("should preserve user agent exactly")
        void shouldPreserveUserAgentExactly() {
            String userAgent =
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64)";

            when(request.getHeader("User-Agent"))
                    .thenReturn(userAgent);
            when(request.getHeader("X-Forwarded-For"))
                    .thenReturn(null);
            when(request.getRemoteAddr())
                    .thenReturn("192.168.1.10");

            ClientInfo result =
                    clientInfoResolver.resolve(request);

            assertEquals(userAgent, result.deviceName());
        }

        @Test
        @DisplayName("should not access remote address when forwarded IP is available")
        void shouldNotAccessRemoteAddressWhenForwardedIpIsAvailable() {
            when(request.getHeader("User-Agent"))
                    .thenReturn("Mozilla/5.0");
            when(request.getHeader("X-Forwarded-For"))
                    .thenReturn("203.0.113.10");

            ClientInfo result =
                    clientInfoResolver.resolve(request);

            assertEquals("203.0.113.10", result.ipAddress());

            verify(request).getHeader("X-Forwarded-For");
        }
    }
}