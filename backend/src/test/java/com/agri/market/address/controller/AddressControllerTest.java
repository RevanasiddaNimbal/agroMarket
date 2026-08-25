package com.agri.market.address.controller;

import com.agri.market.address.dto.AddressResponseDto;
import com.agri.market.address.dto.CreateAddressRequestDto;
import com.agri.market.address.dto.UpdateAddressRequestDto;
import com.agri.market.address.service.AddressService;
import com.agri.market.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class AddressControllerTest {

    private final String userEmail =
            "user@example.com";
    private final String addressId =
            "address-123";
    @Mock
    private AddressService addressService;
    @Mock
    private Authentication authentication;
    @InjectMocks
    private AddressController addressController;
    private User user;
    private AddressResponseDto response;

    @BeforeEach
    void setUp() {

        user = User.builder()
                .email(userEmail)
                .build();

        response = AddressResponseDto.builder()
                .id(addressId)
                .addressLine1("Main Road")
                .city("Vijayapura")
                .state("Karnataka")
                .pincode("586101")
                .country("India")
                .defaultAddress(false)
                .build();

        given(authentication.getPrincipal())
                .willReturn(user);
    }

    @Nested
    class CreateAddressTests {

        @Test
        void shouldCreateAddress() {

            final CreateAddressRequestDto request =
                    CreateAddressRequestDto.builder()
                            .addressLine1("Main Road")
                            .city("Vijayapura")
                            .state("Karnataka")
                            .pincode("586101")
                            .country("India")
                            .defaultAddress(false)
                            .build();

            given(addressService.createAddress(
                    request,
                    userEmail
            )).willReturn(response);

            final AddressResponseDto result =
                    addressController.createAddress(
                            request,
                            authentication
                    );

            assertSame(response, result);

            then(addressService)
                    .should()
                    .createAddress(
                            request,
                            userEmail
                    );
        }
    }

    @Nested
    class GetUserAddressesTests {

        @Test
        void shouldReturnUserAddresses() {

            final AddressResponseDto secondResponse =
                    AddressResponseDto.builder()
                            .id("address-456")
                            .build();

            final List<AddressResponseDto> responses =
                    List.of(response, secondResponse);

            given(addressService.getUserAddresses(userEmail))
                    .willReturn(responses);

            final List<AddressResponseDto> result =
                    addressController.getUserAddresses(
                            authentication
                    );

            assertSame(responses, result);

            then(addressService)
                    .should()
                    .getUserAddresses(userEmail);
        }

        @Test
        void shouldReturnEmptyAddressList() {

            given(addressService.getUserAddresses(userEmail))
                    .willReturn(List.of());

            final List<AddressResponseDto> result =
                    addressController.getUserAddresses(
                            authentication
                    );

            assertNotNull(result);
            assertTrue(result.isEmpty());

            then(addressService)
                    .should()
                    .getUserAddresses(userEmail);
        }
    }

    @Nested
    class GetAddressTests {

        @Test
        void shouldReturnAddress() {

            given(addressService.getAddress(
                    addressId,
                    userEmail
            )).willReturn(response);

            final AddressResponseDto result =
                    addressController.getAddress(
                            addressId,
                            authentication
                    );

            assertSame(response, result);

            then(addressService)
                    .should()
                    .getAddress(
                            addressId,
                            userEmail
                    );
        }
    }

    @Nested
    class UpdateAddressTests {

        @Test
        void shouldUpdateAddress() {

            final UpdateAddressRequestDto request =
                    UpdateAddressRequestDto.builder()
                            .addressLine1("Updated Address")
                            .city("Updated City")
                            .build();

            given(addressService.updateAddress(
                    addressId,
                    request,
                    userEmail
            )).willReturn(response);

            final AddressResponseDto result =
                    addressController.updateAddress(
                            addressId,
                            request,
                            authentication
                    );

            assertSame(response, result);

            then(addressService)
                    .should()
                    .updateAddress(
                            addressId,
                            request,
                            userEmail
                    );
        }
    }

    @Nested
    class SetDefaultAddressTests {

        @Test
        void shouldSetDefaultAddress() {

            addressController.setDefaultAddress(
                    addressId,
                    authentication
            );

            then(addressService)
                    .should()
                    .setDefaultAddress(
                            addressId,
                            userEmail
                    );
        }
    }

    @Nested
    class DeleteAddressTests {

        @Test
        void shouldDeleteAddress() {

            addressController.deleteAddress(
                    addressId,
                    authentication
            );

            then(addressService)
                    .should()
                    .deleteAddress(
                            addressId,
                            userEmail
                    );
        }
    }

    @Nested
    class AuthenticationTests {

        @Test
        void shouldUseAuthenticatedUserEmail() {

            final CreateAddressRequestDto request =
                    CreateAddressRequestDto.builder()
                            .addressLine1("Main Road")
                            .build();

            given(addressService.createAddress(
                    request,
                    userEmail
            )).willReturn(response);

            addressController.createAddress(
                    request,
                    authentication
            );

            then(authentication)
                    .should()
                    .getPrincipal();

            then(addressService)
                    .should()
                    .createAddress(
                            request,
                            userEmail
                    );
        }
    }
}