package com.agri.market.sms.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Normalized response returned by an SMS provider")
public class SmsProviderResponse {

    @Schema(
            description = "Indicates whether the SMS provider operation was successful",
            example = "true"
    )
    private boolean success;

    @Schema(
            description = "Provider operation message",
            example = "OTP sent successfully"
    )
    private String message;

    @Schema(
            description = "Provider request identifier when available",
            example = "64f7c8a1-1234-4567-8901"
    )
    private String requestId;
}