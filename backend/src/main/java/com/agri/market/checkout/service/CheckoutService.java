package com.agri.market.checkout.service;

import com.agri.market.checkout.dto.CheckoutRequestDto;
import com.agri.market.checkout.dto.CheckoutResponseDto;

public interface CheckoutService {

    CheckoutResponseDto checkout(
            CheckoutRequestDto request,
            String userId
    );
}