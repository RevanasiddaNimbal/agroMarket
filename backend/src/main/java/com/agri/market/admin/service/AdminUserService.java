package com.agri.market.admin.service;

import com.agri.market.admin.dto.AdminUserDetailResponseDto;
import com.agri.market.admin.dto.AdminUserSearchRequestDto;
import com.agri.market.admin.dto.AdminUserStatusResponseDto;
import com.agri.market.admin.dto.AdminUserSummaryResponseDto;
import org.springframework.data.domain.Page;

public interface AdminUserService {

    Page<AdminUserSummaryResponseDto> searchUsers(
            AdminUserSearchRequestDto request
    );

    AdminUserDetailResponseDto getUserById(
            String userId
    );

    AdminUserStatusResponseDto activateUser(
            String userId
    );

    AdminUserStatusResponseDto deactivateUser(
            String userId
    );

    AdminUserStatusResponseDto lockUser(
            String userId
    );

    AdminUserStatusResponseDto unlockUser(
            String userId
    );
}