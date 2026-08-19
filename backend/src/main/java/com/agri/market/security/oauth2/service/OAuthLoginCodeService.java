package com.agri.market.security.oauth2.service;

import com.agri.market.user.entity.User;

public interface OAuthLoginCodeService {

    String createCode(User user);

    User exchangeCode(String code);
}