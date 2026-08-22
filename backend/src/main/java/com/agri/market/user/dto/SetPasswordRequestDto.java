package com.agri.market.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SetPasswordRequestDto {

    @NotBlank(message = "VALIDATION.SET_PASSWORD.NEW_PASSWORD.BLANK")
    @Size(
            min = 8,
            max = 72,
            message = "VALIDATION.SET_PASSWORD.NEW_PASSWORD.SIZE"
    )
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*\\W).*$",
            message = "VALIDATION.SET_PASSWORD.NEW_PASSWORD.WEAK"
    )
    @Schema(example = "New@Password123")
    private String newPassword;


    @NotBlank(message = "VALIDATION.SET_PASSWORD.CONFIRM_NEW_PASSWORD.BLANK")
    @Size(
            min = 8,
            max = 72,
            message = "VALIDATION.SET_PASSWORD.CONFIRM_NEW_PASSWORD.SIZE"
    )
    @Schema(example = "New@Password123")
    private String confirmNewPassword;
}