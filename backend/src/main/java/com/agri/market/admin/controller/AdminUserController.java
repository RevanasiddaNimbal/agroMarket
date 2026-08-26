package com.agri.market.admin.controller;

import com.agri.market.admin.dto.AdminUserDetailResponseDto;
import com.agri.market.admin.dto.AdminUserSearchRequestDto;
import com.agri.market.admin.dto.AdminUserStatusResponseDto;
import com.agri.market.admin.dto.AdminUserSummaryResponseDto;
import com.agri.market.admin.service.AdminUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
@Tag(
        name = "Admin User Management",
        description = "Administrative APIs for managing users"
)
@SecurityRequirement(name = "bearerAuth")
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping
    @Operation(
            summary = "Search and list users",
            description = "Returns a paginated list of users with optional search and filters."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Users retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid search parameters"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Admin access required"
            )
    })
    public ResponseEntity<Page<AdminUserSummaryResponseDto>> searchUsers(
            @Valid @ModelAttribute final AdminUserSearchRequestDto request
    ) {
        return ResponseEntity.ok(
                adminUserService.searchUsers(request)
        );
    }

    @GetMapping("/{userId}")
    @Operation(
            summary = "Get user details",
            description = "Returns detailed non-sensitive information about a user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User details retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Admin access required"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found"
            )
    })
    public ResponseEntity<AdminUserDetailResponseDto> getUserById(
            @PathVariable final String userId
    ) {
        return ResponseEntity.ok(
                adminUserService.getUserById(userId)
        );
    }

    @PatchMapping("/{userId}/activate")
    @Operation(
            summary = "Activate user",
            description = "Activates a previously deactivated user account."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User activated successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Admin access required"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "User is already active"
            )
    })
    public ResponseEntity<AdminUserStatusResponseDto> activateUser(
            @PathVariable final String userId
    ) {
        return ResponseEntity.ok(
                adminUserService.activateUser(userId)
        );
    }

    @PatchMapping("/{userId}/deactivate")
    @Operation(
            summary = "Deactivate user",
            description = "Deactivates an active user account."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User deactivated successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Admin access required"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "User is already inactive"
            )
    })
    public ResponseEntity<AdminUserStatusResponseDto> deactivateUser(
            @PathVariable final String userId
    ) {
        return ResponseEntity.ok(
                adminUserService.deactivateUser(userId)
        );
    }

    @PatchMapping("/{userId}/lock")
    @Operation(
            summary = "Lock user account",
            description = "Permanently locks a user account using the existing accountLocked field."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User locked successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Admin access required"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "User is already locked"
            )
    })
    public ResponseEntity<AdminUserStatusResponseDto> lockUser(
            @PathVariable final String userId
    ) {
        return ResponseEntity.ok(
                adminUserService.lockUser(userId)
        );
    }

    @PatchMapping("/{userId}/unlock")
    @Operation(
            summary = "Unlock user account",
            description = "Unlocks a user account using the existing accountLocked field."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User unlocked successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Admin access required"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "User is already unlocked"
            )
    })
    public ResponseEntity<AdminUserStatusResponseDto> unlockUser(
            @PathVariable final String userId
    ) {
        return ResponseEntity.ok(
                adminUserService.unlockUser(userId)
        );
    }
}