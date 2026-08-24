package com.agri.market.support;

import com.agri.market.security.client.ClientInfo;

public final class ClientInfoTestFactory {

    private ClientInfoTestFactory() {
    }

    public static ClientInfo deviceA() {
        return new ClientInfo("Device A", "10.0.0.1");
    }

    public static ClientInfo deviceB() {
        return new ClientInfo("Device B", "10.0.0.2");
    }

    public static ClientInfo of(
            final String deviceName,
            final String ipAddress
    ) {
        return new ClientInfo(deviceName, ipAddress);
    }
}

