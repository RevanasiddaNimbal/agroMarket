package com.agri.market.user.service;

import com.agri.market.user.dto.UserDashboardResponseDto;

public interface UserDashboardService {

    UserDashboardResponseDto getDashboard(
            String userId
    );
}