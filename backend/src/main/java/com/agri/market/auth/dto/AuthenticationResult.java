package com.agri.market.auth.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResult {
    private String accessToken;

    private String refreshToken;

    private String tokenType;
    
    private boolean hasPassword;
}