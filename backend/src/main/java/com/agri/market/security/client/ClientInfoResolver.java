package com.agri.market.security.client;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class ClientInfoResolver {

    private static final String FORWARDED_FOR_HEADER = "X-Forwarded-For";
    private static final String USER_AGENT_HEADER = "User-Agent";
    private static final String UNKNOWN = "Unknown";

    public ClientInfo resolve(
            final HttpServletRequest request
    ) {

        return new ClientInfo(
                resolveDeviceName(request),
                resolveIpAddress(request)
        );
    }

    private String resolveDeviceName(
            final HttpServletRequest request
    ) {

        final String userAgent =
                request.getHeader(USER_AGENT_HEADER);

        if (userAgent == null || userAgent.isBlank()) {
            return UNKNOWN;
        }

        return userAgent;
    }

    private String resolveIpAddress(
            final HttpServletRequest request
    ) {

        final String forwardedFor =
                request.getHeader(FORWARDED_FOR_HEADER);

        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }

        final String remoteAddress =
                request.getRemoteAddr();

        return remoteAddress != null
                ? remoteAddress
                : UNKNOWN;
    }
}