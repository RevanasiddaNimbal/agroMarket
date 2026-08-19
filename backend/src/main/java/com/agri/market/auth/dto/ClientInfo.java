package com.agri.market.auth.dto;

import lombok.Builder;

@Builder
public record ClientInfo(
        String deviceName,
        String ipAddress
) {

}
