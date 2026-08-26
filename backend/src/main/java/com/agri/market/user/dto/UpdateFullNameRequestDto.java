package com.agri.market.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request for updating the authenticated user's full name")
public class UpdateFullNameRequestDto {

    @NotBlank(
            message = "VALIDATION.UPDATE_FULL_NAME.FULL_NAME.BLANK"
    )
    @Size(
            min = 2,
            max = 100,
            message = "VALIDATION.UPDATE_FULL_NAME.FULL_NAME.SIZE"
    )
    @Pattern(
            regexp = "^[\\p{L} '-]+$",
            message = "VALIDATION.UPDATE_FULL_NAME.FULL_NAME.PATTERN"
    )
    @Schema(
            description = "New full name of the user",
            example = "Revanasidda Nimbal",
            minLength = 2,
            maxLength = 100
    )
    private String fullName;
}