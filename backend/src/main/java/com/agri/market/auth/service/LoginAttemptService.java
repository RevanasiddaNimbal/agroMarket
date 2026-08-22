package com.agri.market.auth.service;

import com.agri.market.user.entity.User;

public interface LoginAttemptService {

    void validateLockStatus(User user);

    void recordFailedLogin(User user);

    void resetAfterSuccessfulLogin(User user);
}