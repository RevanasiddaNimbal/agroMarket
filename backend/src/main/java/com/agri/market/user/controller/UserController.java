package com.agri.market.user.controller;

import com.agri.market.user.dto.*;
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
            summary = "Update current user's full name",
            description = "Updates only the full name of the currently authenticated user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Full name updated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid full name"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            )
    })
    @PatchMapping("/me/profile/full-name")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateFullName(
            @Valid @RequestBody final UpdateFullNameRequestDto request,
            final Authentication authentication
    ) {

        userService.updateFullName(
                request,
                getAuthenticatedUserEmail(authentication)
        );
    }

    @Operation(
            summary = "Update current user's profile picture",
            description = "Updates only the profile picture of the currently authenticated user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Profile picture updated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid profile picture URL"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            )
    })
    @PatchMapping("/me/profile-picture")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateProfilePicture(
            @Valid @RequestBody final UpdateProfilePictureRequestDto request,
            final Authentication authentication
    ) {

        userService.updateProfilePicture(
                request,
                getAuthenticatedUserEmail(authentication)
        );
    }

    @Operation(
            summary = "Send phone verification OTP",
            description = "Sends an OTP to the phone number that the authenticated user wants to add or change."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "OTP sent successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid phone number"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Phone number already belongs to another user"
            ),
            @ApiResponse(
                    responseCode = "429",
                    description = "OTP resend/request rate limit exceeded"
            ),
            @ApiResponse(
                    responseCode = "503",
                    description = "SMS provider unavailable"
            )
    })
    @PostMapping("/me/phone/send-otp")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void sendPhoneOtp(
            @Valid @RequestBody final SendPhoneOtpRequestDto request,
            final Authentication authentication
    ) {

        userService.sendPhoneOtp(
                request,
                getAuthenticatedUserEmail(authentication)
        );
    }

    @Operation(
            summary = "Verify phone number OTP",
            description = "Verifies the OTP and updates the authenticated user's phone number."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Phone number verified successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid or expired OTP"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Phone number already belongs to another user"
            )
    })
    @PostMapping("/me/phone/verify-otp")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void verifyPhoneOtp(
            @Valid @RequestBody final VerifyPhoneOtpRequestDto request,
            final Authentication authentication
    ) {

        userService.verifyPhoneOtp(
                request,
                getAuthenticatedUserEmail(authentication)
        );
    }

    @Operation(
            summary = "Resend phone verification OTP",
            description = "Resends the OTP to the phone number being verified."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "OTP resent successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid phone number"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Phone number already belongs to another user"
            ),
            @ApiResponse(
                    responseCode = "429",
                    description = "OTP resend is not currently allowed"
            ),
            @ApiResponse(
                    responseCode = "503",
                    description = "SMS provider unavailable"
            )
    })
    @PostMapping("/me/phone/resend-otp")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void resendPhoneOtp(
            @Valid @RequestBody final ResendPhoneOtpRequestDto request,
            final Authentication authentication
    ) {

        userService.resendPhoneOtp(
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
            summary = "Set current user's password",
            description = "Sets a password for the currently authenticated user who does not have a password configured."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Password set successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid password or password confirmation"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "User already has a password configured"
            )
    })
    @PatchMapping("/me/set-password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void setPassword(
            @Valid @RequestBody final SetPasswordRequestDto request,
            final Authentication authentication
    ) {

        userService.setPassword(
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
            summary = "Get current user's profile",
            description = "Returns the complete profile information of the currently authenticated user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User profile retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User profile not found"
            )
    })
    @GetMapping("/me/profile")
    public UserProfileResponseDto getCurrentUserProfile(
            final Authentication authentication
    ) {

        return userService.getCurrentUserProfile(
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
    @DeleteMapping("/me/profile")
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

        final User user =
                (User) authentication.getPrincipal();

        return user.getEmail();
    }
}