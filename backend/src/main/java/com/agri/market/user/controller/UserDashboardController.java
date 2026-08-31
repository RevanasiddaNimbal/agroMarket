package com.agri.market.user.controller;

import com.agri.market.user.dto.UserDashboardResponseDto;
import com.agri.market.user.entity.User;
import com.agri.market.user.service.UserDashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/user/dashboard")
public class UserDashboardController {

    private final UserDashboardService userDashboardService;

    @Operation(
            summary = "Get user dashboard",
            description = "Returns buying and selling activity for the authenticated user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User dashboard fetched successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User is not authenticated"
            )
    })
    @GetMapping
    public ResponseEntity<UserDashboardResponseDto> getDashboard(
            @AuthenticationPrincipal final User user
    ) {

        log.info(
                "User dashboard request received. User: {}",
                user.getId()
        );

        return ResponseEntity.ok(
                userDashboardService.getDashboard(
                        user.getId()
                )
        );
    }
}