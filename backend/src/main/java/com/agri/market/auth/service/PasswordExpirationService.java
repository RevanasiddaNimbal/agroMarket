package com.agri.market.auth.service;

import com.agri.market.user.entity.User;

public interface PasswordExpirationService {
    boolean isPasswordExpired(User user);

    void validatePasswordPolicy(User user);

}
