package com.agri.market.address.service;

import com.agri.market.address.dto.AddressResponseDto;
import com.agri.market.address.dto.CreateAddressRequestDto;
import com.agri.market.address.dto.UpdateAddressRequestDto;

import java.util.List;

public interface AddressService {

    AddressResponseDto createAddress(
            CreateAddressRequestDto request,
            String userId
    );

    List<AddressResponseDto> getUserAddresses(
            String userId
    );

    AddressResponseDto getAddress(
            String addressId,
            String userId
    );

    AddressResponseDto updateAddress(
            String addressId,
            UpdateAddressRequestDto request,
            String userId
    );

    void deleteAddress(
            String addressId,
            String userId
    );

    void setDefaultAddress(
            String addressId,
            String userId
    );
}