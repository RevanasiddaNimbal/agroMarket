package com.agri.market.support;

import com.agri.market.role.entity.Role;

public final class RoleTestFactory {

    private RoleTestFactory() {
    }

    public static Role userRole() {
        return role("ROLE_USER");
    }

    public static Role role(final String name) {
        return Role.builder()
                .name(name)
                .build();
    }
}

