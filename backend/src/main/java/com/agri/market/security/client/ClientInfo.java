package com.agri.market.security.client;

import lombok.Builder;

@Builder
public record ClientInfo(
        String deviceName,
        String ipAddress
) {

}
