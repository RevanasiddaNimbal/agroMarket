package com.agri.market.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RegistrationResponse {

    private final String message;
}