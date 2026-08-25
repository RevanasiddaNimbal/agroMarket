package com.agri.market.address.service;

import com.agri.market.address.dto.AddressResponseDto;
import com.agri.market.address.dto.CreateAddressRequestDto;
import com.agri.market.address.dto.UpdateAddressRequestDto;
import com.agri.market.address.entity.Address;
import com.agri.market.address.entity.LocationType;
import com.agri.market.address.mapper.AddressMapper;
import com.agri.market.address.repository.AddressRepository;
import com.agri.market.common.exception.BusinessException;
import com.agri.market.user.entity.User;
import com.agri.market.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static com.agri.market.common.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final AddressMapper addressMapper;

    @Override
    @Transactional
    public AddressResponseDto createAddress(
            final CreateAddressRequestDto request,
            final String userEmail
    ) {
        final User user = findUserByEmail(userEmail);

        validateLocation(
                request.getLocationType(),
                request.getLatitude(),
                request.getLongitude()
        );

        final Address address = addressMapper.toEntity(request);

        address.setUser(user);

        if (request.isDefaultAddress()) {
            addressRepository.clearDefaultAddressByUserId(
                    user.getId()
            );
        }

        final Address savedAddress = addressRepository.save(address);

        log.info(
                "Address created successfully for user: {}",
                userEmail
        );

        return addressMapper.toResponse(savedAddress);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddressResponseDto> getUserAddresses(
            final String userEmail
    ) {
        final User user = findUserByEmail(userEmail);

        return addressRepository.findAllByUserId(user.getId())
                .stream()
                .map(addressMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AddressResponseDto getAddress(
            final String addressId,
            final String userEmail
    ) {
        final User user = findUserByEmail(userEmail);

        final Address address = findUserAddress(
                addressId,
                user.getId()
        );

        return addressMapper.toResponse(address);
    }

    @Override
    @Transactional
    public AddressResponseDto updateAddress(
            final String addressId,
            final UpdateAddressRequestDto request,
            final String userEmail
    ) {
        final User user = findUserByEmail(userEmail);

        final Address address = findUserAddress(
                addressId,
                user.getId()
        );

        addressMapper.updateEntity(
                address,
                request
        );

        normalizeAndValidateLocation(address);

        if (Boolean.TRUE.equals(request.getDefaultAddress())) {
            addressRepository.clearDefaultAddressByUserId(
                    user.getId()
            );

            address.setDefaultAddress(true);
        }

        log.info(
                "Address updated successfully. Address: {}, User: {}",
                addressId,
                userEmail
        );

        return addressMapper.toResponse(address);
    }

    @Override
    @Transactional
    public void deleteAddress(
            final String addressId,
            final String userEmail
    ) {
        final User user = findUserByEmail(userEmail);

        final Address address = findUserAddress(
                addressId,
                user.getId()
        );

        addressRepository.delete(address);

        log.info(
                "Address deleted successfully. Address: {}, User: {}",
                addressId,
                userEmail
        );
    }

    @Override
    @Transactional
    public void setDefaultAddress(
            final String addressId,
            final String userEmail
    ) {
        final User user = findUserByEmail(userEmail);

        final Address address = findUserAddress(
                addressId,
                user.getId()
        );

        if (address.isDefaultAddress()) {
            log.debug(
                    "Address is already default. Address: {}, User: {}",
                    addressId,
                    userEmail
            );
            return;
        }

        addressRepository.clearDefaultAddressByUserId(
                user.getId()
        );

        address.setDefaultAddress(true);

        log.info(
                "Default address changed successfully. Address: {}, User: {}",
                addressId,
                userEmail
        );
    }

    private User findUserByEmail(
            final String userEmail
    ) {
        return userRepository.findByEmailIgnoreCase(userEmail)
                .orElseThrow(() -> {
                    log.warn(
                            "User not found while accessing address: {}",
                            userEmail
                    );

                    return new BusinessException(
                            USER_NOT_FOUND
                    );
                });
    }

    private Address findUserAddress(
            final String addressId,
            final String userId
    ) {
        return addressRepository.findByIdAndUserId(
                        addressId,
                        userId
                )
                .orElseThrow(() -> {
                    log.warn(
                            "Address not found or does not belong to user. "
                                    + "Address: {}, User ID: {}",
                            addressId,
                            userId
                    );

                    return new BusinessException(
                            ADDRESS_NOT_FOUND
                    );
                });
    }

    private void validateLocation(
            final LocationType locationType,
            final BigDecimal latitude,
            final BigDecimal longitude
    ) {
        if (locationType == LocationType.MAP) {

            if (latitude == null || longitude == null) {
                throw new BusinessException(
                        ADDRESS_COORDINATES_REQUIRED
                );
            }

            return;
        }

        if (locationType == LocationType.MANUAL
                && (latitude != null || longitude != null)) {

            throw new BusinessException(
                    ADDRESS_COORDINATES_NOT_ALLOWED
            );
        }
    }

    private void normalizeAndValidateLocation(
            final Address address
    ) {
        if (address.getLocationType() == LocationType.MANUAL) {
            address.setLatitude(null);
            address.setLongitude(null);
            return;
        }

        if (address.getLocationType() == LocationType.MAP
                && (address.getLatitude() == null
                || address.getLongitude() == null)) {

            throw new BusinessException(
                    ADDRESS_COORDINATES_REQUIRED
            );
        }
    }
}