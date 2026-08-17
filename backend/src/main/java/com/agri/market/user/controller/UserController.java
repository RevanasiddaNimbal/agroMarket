package com.agri.market.user.controller;

import com.agri.market.user.dto.ChangePasswordRequestDto;
import com.agri.market.user.dto.ProfileUpdateRequestDto;
import com.agri.market.user.entity.User;
import com.agri.market.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(
        name = "Users",
        description = "User account and profile management APIs"
)
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "Update current user's profile",
            description = "Updates profile information of the currently authenticated user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Profile updated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid profile information"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Profile information conflicts with an existing user"
            )
    })
    @PatchMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateProfile(
            @Valid @RequestBody final ProfileUpdateRequestDto request,
            final Authentication authentication
    ) {
        userService.updateProfileInfo(
                request,
                getAuthenticatedUserEmail(authentication)
        );
    }

    @Operation(
            summary = "Change current user's password",
            description = "Changes the password of the currently authenticated user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Password changed successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid password or password confirmation"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required or current password is invalid"
            )
    })
    @PatchMapping("/me/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(
            @Valid @RequestBody final ChangePasswordRequestDto request,
            final Authentication authentication
    ) {
        userService.changePassword(
                request,
                getAuthenticatedUserEmail(authentication)
        );
    }

    @Operation(
            summary = "Deactivate current user's account",
            description = "Deactivates the account of the currently authenticated user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Account deactivated successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Account is already deactivated"
            )
    })
    @PatchMapping("/me/deactivate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivateAccount(
            final Authentication authentication
    ) {
        userService.deactivateAccount(
                getAuthenticatedUserEmail(authentication)
        );
    }

    @Operation(
            summary = "Reactivate current user's account",
            description = "Reactivates the account of the currently authenticated user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Account reactivated successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Account is already active"
            )
    })
    @PatchMapping("/me/reactivate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reactivateAccount(
            final Authentication authentication
    ) {
        userService.reactivateAccount(
                getAuthenticatedUserEmail(authentication)
        );
    }

    @Operation(
            summary = "Delete current user's account",
            description = "Permanently deletes the account of the currently authenticated user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Account deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User account not found"
            )
    })
    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAccount(
            final Authentication authentication
    ) {
        userService.deleteAccount(
                getAuthenticatedUserEmail(authentication)
        );
    }

    private String getAuthenticatedUserEmail(
            final Authentication authentication
    ) {
        final User user = (User) authentication.getPrincipal();
        return user.getEmail();
    }
}