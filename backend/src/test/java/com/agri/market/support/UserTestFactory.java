package com.agri.market.support;

import com.agri.market.role.entity.Role;
import com.agri.market.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;

public final class UserTestFactory {

    private UserTestFactory() {
    }

    public static User activeUser() {
        return user(
                "user-id",
                "revanasidda@mail.com",
                "encoded-password",
                true,
                false
        );
    }

    public static User inactiveUser() {
        return user(
                "user-id",
                "revanasidda@mail.com",
                "encoded-password",
                false,
                false
        );
    }

    public static User lockedUser() {
        return user(
                "user-id",
                "revanasidda@mail.com",
                "encoded-password",
                true,
                true
        );
    }

    public static User user(
            final String id,
            final String email,
            final String password,
            final boolean enabled,
            final boolean accountLocked
    ) {
        final Role role = RoleTestFactory.userRole();

        final User user = User.builder()
                .id(id)
                .fullName("Revanasidda Nimbal")
                .email(email)
                .phoneNumber("+919876543210")
                .password(password)
                .enabled(enabled)
                .accountLocked(accountLocked)
                .emailVerified(true)
                .phoneVerified(true)
                .passwordChangedAt(LocalDateTime.now().minusDays(1))
                .roles(List.of(role))
                .build();

        user.setProfilePictureUrl("https://example.com/profile.jpg");
        return user;
    }
}

