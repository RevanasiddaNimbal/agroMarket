package com.agri.market.admin.controller;

import com.agri.market.admin.dto.AdminDashboardResponseDto;
import com.agri.market.admin.service.AdminDashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/admin/dashboard")
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    @Operation(
            summary = "Get admin dashboard",
            description = "Returns the overall system summary for the authenticated admin."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Admin dashboard fetched successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User is not authenticated"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "User does not have admin access"
            )
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<AdminDashboardResponseDto> getDashboard() {

        log.info("Admin dashboard request received");

        return ResponseEntity.ok(
                adminDashboardService.getDashboard()
        );
    }
}