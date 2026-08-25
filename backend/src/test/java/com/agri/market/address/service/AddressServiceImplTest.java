package com.agri.market.address.service;

import com.agri.market.address.dto.AddressResponseDto;
import com.agri.market.address.dto.CreateAddressRequestDto;
import com.agri.market.address.dto.UpdateAddressRequestDto;
import com.agri.market.address.entity.Address;
import com.agri.market.address.entity.AddressType;
import com.agri.market.address.entity.LocationType;
import com.agri.market.address.mapper.AddressMapper;
import com.agri.market.address.repository.AddressRepository;
import com.agri.market.common.exception.BusinessException;
import com.agri.market.user.entity.User;
import com.agri.market.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.agri.market.common.exception.ErrorCode.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class AddressServiceImplTest {

    private final String userEmail =
            "user@example.com";
    private final String userId =
            "user-123";
    private final String addressId =
            "address-123";
    @Mock
    private AddressRepository addressRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AddressMapper addressMapper;
    @InjectMocks
    private AddressServiceImpl addressService;
    private User user;
    private Address address;
    private AddressResponseDto response;

    @BeforeEach
    void setUp() {

        user = User.builder()
                .id(userId)
                .email(userEmail)
                .build();

        address = Address.builder()
                .addressLine1("Main Road")
                .city("Vijayapura")
                .state("Karnataka")
                .pincode("586101")
                .country("India")
                .locationType(LocationType.MANUAL)
                .addressType(AddressType.HOME)
                .defaultAddress(false)
                .build();

        response = AddressResponseDto.builder()
                .addressLine1("Main Road")
                .city("Vijayapura")
                .state("Karnataka")
                .pincode("586101")
                .country("India")
                .locationType(LocationType.MANUAL)
                .addressType(AddressType.HOME)
                .defaultAddress(false)
                .build();
    }

    @Nested
    class CreateAddressTests {

        @Test
        void shouldCreateManualAddress() {

            final CreateAddressRequestDto request =
                    CreateAddressRequestDto.builder()
                            .addressLine1("Main Road")
                            .city("Vijayapura")
                            .state("Karnataka")
                            .pincode("586101")
                            .country("India")
                            .locationType(LocationType.MANUAL)
                            .addressType(AddressType.HOME)
                            .defaultAddress(false)
                            .build();

            given(userRepository.findByEmailIgnoreCase(userEmail))
                    .willReturn(Optional.of(user));

            given(addressMapper.toEntity(request))
                    .willReturn(address);

            given(addressRepository.save(address))
                    .willReturn(address);

            given(addressMapper.toResponse(address))
                    .willReturn(response);

            final AddressResponseDto result =
                    addressService.createAddress(
                            request,
                            userEmail
                    );

            assertSame(response, result);

            then(addressMapper)
                    .should()
                    .toEntity(request);

            then(addressRepository)
                    .should()
                    .save(address);

            then(addressMapper)
                    .should()
                    .toResponse(address);

            then(addressRepository)
                    .shouldHaveNoMoreInteractions();
        }

        @Test
        void shouldCreateMapAddress() {

            final BigDecimal latitude =
                    new BigDecimal("16.830170");

            final BigDecimal longitude =
                    new BigDecimal("75.710030");

            final CreateAddressRequestDto request =
                    CreateAddressRequestDto.builder()
                            .addressLine1("Farm Road")
                            .city("Vijayapura")
                            .state("Karnataka")
                            .pincode("586101")
                            .country("India")
                            .locationType(LocationType.MAP)
                            .latitude(latitude)
                            .longitude(longitude)
                            .addressType(AddressType.FARM)
                            .defaultAddress(false)
                            .build();

            address.setLocationType(LocationType.MAP);
            address.setLatitude(latitude);
            address.setLongitude(longitude);

            given(userRepository.findByEmailIgnoreCase(userEmail))
                    .willReturn(Optional.of(user));

            given(addressMapper.toEntity(request))
                    .willReturn(address);

            given(addressRepository.save(address))
                    .willReturn(address);

            given(addressMapper.toResponse(address))
                    .willReturn(response);

            final AddressResponseDto result =
                    addressService.createAddress(
                            request,
                            userEmail
                    );

            assertSame(response, result);

            then(addressRepository)
                    .should()
                    .save(address);
        }

        @Test
        void shouldRejectMapAddressWithoutLatitude() {

            final CreateAddressRequestDto request =
                    CreateAddressRequestDto.builder()
                            .addressLine1("Farm Road")
                            .locationType(LocationType.MAP)
                            .latitude(null)
                            .longitude(new BigDecimal("75.710030"))
                            .addressType(AddressType.FARM)
                            .defaultAddress(false)
                            .build();

            given(userRepository.findByEmailIgnoreCase(userEmail))
                    .willReturn(Optional.of(user));

            final BusinessException exception =
                    assertThrows(
                            BusinessException.class,
                            () -> addressService.createAddress(
                                    request,
                                    userEmail
                            )
                    );

            assertEquals(
                    ADDRESS_COORDINATES_REQUIRED,
                    exception.getErrorCode()
            );

            then(addressMapper)
                    .shouldHaveNoInteractions();

            then(addressRepository)
                    .shouldHaveNoInteractions();
        }

        @Test
        void shouldRejectMapAddressWithoutLongitude() {

            final CreateAddressRequestDto request =
                    CreateAddressRequestDto.builder()
                            .addressLine1("Farm Road")
                            .locationType(LocationType.MAP)
                            .latitude(new BigDecimal("16.830170"))
                            .longitude(null)
                            .addressType(AddressType.FARM)
                            .defaultAddress(false)
                            .build();

            given(userRepository.findByEmailIgnoreCase(userEmail))
                    .willReturn(Optional.of(user));

            final BusinessException exception =
                    assertThrows(
                            BusinessException.class,
                            () -> addressService.createAddress(
                                    request,
                                    userEmail
                            )
                    );

            assertEquals(
                    ADDRESS_COORDINATES_REQUIRED,
                    exception.getErrorCode()
            );

            then(addressMapper)
                    .shouldHaveNoInteractions();

            then(addressRepository)
                    .shouldHaveNoInteractions();
        }

        @Test
        void shouldRejectMapAddressWithoutCoordinates() {

            final CreateAddressRequestDto request =
                    CreateAddressRequestDto.builder()
                            .addressLine1("Farm Road")
                            .locationType(LocationType.MAP)
                            .latitude(null)
                            .longitude(null)
                            .addressType(AddressType.FARM)
                            .defaultAddress(false)
                            .build();

            given(userRepository.findByEmailIgnoreCase(userEmail))
                    .willReturn(Optional.of(user));

            final BusinessException exception =
                    assertThrows(
                            BusinessException.class,
                            () -> addressService.createAddress(
                                    request,
                                    userEmail
                            )
                    );

            assertEquals(
                    ADDRESS_COORDINATES_REQUIRED,
                    exception.getErrorCode()
            );
        }

        @Test
        void shouldRejectManualAddressWithLatitude() {

            final CreateAddressRequestDto request =
                    CreateAddressRequestDto.builder()
                            .addressLine1("Main Road")
                            .locationType(LocationType.MANUAL)
                            .latitude(new BigDecimal("16.830170"))
                            .longitude(null)
                            .addressType(AddressType.HOME)
                            .defaultAddress(false)
                            .build();

            given(userRepository.findByEmailIgnoreCase(userEmail))
                    .willReturn(Optional.of(user));

            final BusinessException exception =
                    assertThrows(
                            BusinessException.class,
                            () -> addressService.createAddress(
                                    request,
                                    userEmail
                            )
                    );

            assertEquals(
                    ADDRESS_COORDINATES_NOT_ALLOWED,
                    exception.getErrorCode()
            );
        }

        @Test
        void shouldRejectManualAddressWithLongitude() {

            final CreateAddressRequestDto request =
                    CreateAddressRequestDto.builder()
                            .addressLine1("Main Road")
                            .locationType(LocationType.MANUAL)
                            .latitude(null)
                            .longitude(new BigDecimal("75.710030"))
                            .addressType(AddressType.HOME)
                            .defaultAddress(false)
                            .build();

            given(userRepository.findByEmailIgnoreCase(userEmail))
                    .willReturn(Optional.of(user));

            final BusinessException exception =
                    assertThrows(
                            BusinessException.class,
                            () -> addressService.createAddress(
                                    request,
                                    userEmail
                            )
                    );

            assertEquals(
                    ADDRESS_COORDINATES_NOT_ALLOWED,
                    exception.getErrorCode()
            );
        }

        @Test
        void shouldRejectManualAddressWithCoordinates() {

            final CreateAddressRequestDto request =
                    CreateAddressRequestDto.builder()
                            .addressLine1("Main Road")
                            .locationType(LocationType.MANUAL)
                            .latitude(new BigDecimal("16.830170"))
                            .longitude(new BigDecimal("75.710030"))
                            .addressType(AddressType.HOME)
                            .defaultAddress(false)
                            .build();

            given(userRepository.findByEmailIgnoreCase(userEmail))
                    .willReturn(Optional.of(user));

            final BusinessException exception =
                    assertThrows(
                            BusinessException.class,
                            () -> addressService.createAddress(
                                    request,
                                    userEmail
                            )
                    );

            assertEquals(
                    ADDRESS_COORDINATES_NOT_ALLOWED,
                    exception.getErrorCode()
            );
        }

        @Test
        void shouldCreateAddressAsDefault() {

            final CreateAddressRequestDto request =
                    CreateAddressRequestDto.builder()
                            .addressLine1("Default Address")
                            .locationType(LocationType.MANUAL)
                            .addressType(AddressType.HOME)
                            .defaultAddress(true)
                            .build();

            address.setDefaultAddress(true);

            given(userRepository.findByEmailIgnoreCase(userEmail))
                    .willReturn(Optional.of(user));

            given(addressMapper.toEntity(request))
                    .willReturn(address);

            given(addressRepository.save(address))
                    .willReturn(address);

            given(addressMapper.toResponse(address))
                    .willReturn(response);

            addressService.createAddress(
                    request,
                    userEmail
            );

            then(addressRepository)
                    .should()
                    .clearDefaultAddressByUserId(userId);

            then(addressRepository)
                    .should()
                    .save(address);
        }

        @Test
        void shouldCreateAddressWithoutClearingDefaultAddress() {

            final CreateAddressRequestDto request =
                    CreateAddressRequestDto.builder()
                            .addressLine1("Secondary Address")
                            .locationType(LocationType.MANUAL)
                            .addressType(AddressType.OTHER)
                            .defaultAddress(false)
                            .build();

            given(userRepository.findByEmailIgnoreCase(userEmail))
                    .willReturn(Optional.of(user));

            given(addressMapper.toEntity(request))
                    .willReturn(address);

            given(addressRepository.save(address))
                    .willReturn(address);

            given(addressMapper.toResponse(address))
                    .willReturn(response);

            addressService.createAddress(
                    request,
                    userEmail
            );

            then(addressRepository)
                    .should(never())
                    .clearDefaultAddressByUserId(any());
        }

        @Test
        void shouldThrowWhenUserNotFound() {

            final CreateAddressRequestDto request =
                    CreateAddressRequestDto.builder()
                            .addressLine1("Main Road")
                            .locationType(LocationType.MANUAL)
                            .addressType(AddressType.HOME)
                            .defaultAddress(false)
                            .build();

            given(userRepository.findByEmailIgnoreCase(userEmail))
                    .willReturn(Optional.empty());

            final BusinessException exception =
                    assertThrows(
                            BusinessException.class,
                            () -> addressService.createAddress(
                                    request,
                                    userEmail
                            )
                    );

            assertEquals(
                    USER_NOT_FOUND,
                    exception.getErrorCode()
            );

            then(addressMapper)
                    .shouldHaveNoInteractions();

            then(addressRepository)
                    .shouldHaveNoInteractions();
        }
    }

    @Nested
    class GetUserAddressesTests {

        @Test
        void shouldReturnAllUserAddresses() {

            final Address secondAddress =
                    Address.builder()
                            .build();

            final AddressResponseDto secondResponse =
                    AddressResponseDto.builder()
                            .id("address-456")
                            .build();

            given(userRepository.findByEmailIgnoreCase(userEmail))
                    .willReturn(Optional.of(user));

            given(addressRepository.findAllByUserId(userId))
                    .willReturn(List.of(address, secondAddress));

            given(addressMapper.toResponse(address))
                    .willReturn(response);

            given(addressMapper.toResponse(secondAddress))
                    .willReturn(secondResponse);

            final List<AddressResponseDto> result =
                    addressService.getUserAddresses(userEmail);

            assertEquals(2, result.size());
            assertSame(response, result.get(0));
            assertSame(secondResponse, result.get(1));

            then(addressRepository)
                    .should()
                    .findAllByUserId(userId);

            then(addressMapper)
                    .should()
                    .toResponse(address);

            then(addressMapper)
                    .should()
                    .toResponse(secondAddress);
        }

        @Test
        void shouldReturnEmptyListWhenUserHasNoAddresses() {

            given(userRepository.findByEmailIgnoreCase(userEmail))
                    .willReturn(Optional.of(user));

            given(addressRepository.findAllByUserId(userId))
                    .willReturn(List.of());

            final List<AddressResponseDto> result =
                    addressService.getUserAddresses(userEmail);

            assertNotNull(result);
            assertTrue(result.isEmpty());

            then(addressMapper)
                    .shouldHaveNoInteractions();
        }

        @Test
        void shouldThrowWhenUserNotFound() {

            given(userRepository.findByEmailIgnoreCase(userEmail))
                    .willReturn(Optional.empty());

            final BusinessException exception =
                    assertThrows(
                            BusinessException.class,
                            () -> addressService.getUserAddresses(
                                    userEmail
                            )
                    );

            assertEquals(
                    USER_NOT_FOUND,
                    exception.getErrorCode()
            );

            then(addressRepository)
                    .shouldHaveNoInteractions();

            then(addressMapper)
                    .shouldHaveNoInteractions();
        }
    }

    @Nested
    class GetAddressTests {

        @Test
        void shouldReturnAddress() {

            given(userRepository.findByEmailIgnoreCase(userEmail))
                    .willReturn(Optional.of(user));

            given(addressRepository.findByIdAndUserId(
                    addressId,
                    userId
            )).willReturn(Optional.of(address));

            given(addressMapper.toResponse(address))
                    .willReturn(response);

            final AddressResponseDto result =
                    addressService.getAddress(
                            addressId,
                            userEmail
                    );

            assertSame(response, result);

            then(addressMapper)
                    .should()
                    .toResponse(address);
        }

        @Test
        void shouldThrowWhenAddressNotFound() {

            given(userRepository.findByEmailIgnoreCase(userEmail))
                    .willReturn(Optional.of(user));

            given(addressRepository.findByIdAndUserId(
                    addressId,
                    userId
            )).willReturn(Optional.empty());

            final BusinessException exception =
                    assertThrows(
                            BusinessException.class,
                            () -> addressService.getAddress(
                                    addressId,
                                    userEmail
                            )
                    );

            assertEquals(
                    ADDRESS_NOT_FOUND,
                    exception.getErrorCode()
            );

            then(addressMapper)
                    .shouldHaveNoInteractions();
        }

        @Test
        void shouldRejectAddressBelongingToAnotherUser() {

            given(userRepository.findByEmailIgnoreCase(userEmail))
                    .willReturn(Optional.of(user));

            given(addressRepository.findByIdAndUserId(
                    addressId,
                    userId
            )).willReturn(Optional.empty());

            assertThrows(
                    BusinessException.class,
                    () -> addressService.getAddress(
                            addressId,
                            userEmail
                    )
            );

            then(addressRepository)
                    .should()
                    .findByIdAndUserId(addressId, userId);
        }

        @Test
        void shouldThrowWhenUserNotFound() {

            given(userRepository.findByEmailIgnoreCase(userEmail))
                    .willReturn(Optional.empty());

            final BusinessException exception =
                    assertThrows(
                            BusinessException.class,
                            () -> addressService.getAddress(
                                    addressId,
                                    userEmail
                            )
                    );

            assertEquals(
                    USER_NOT_FOUND,
                    exception.getErrorCode()
            );

            then(addressRepository)
                    .shouldHaveNoInteractions();
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

            given(userRepository.findByEmailIgnoreCase(userEmail))
                    .willReturn(Optional.of(user));

            given(addressRepository.findByIdAndUserId(
                    addressId,
                    userId
            )).willReturn(Optional.of(address));

            given(addressMapper.toResponse(address))
                    .willReturn(response);

            final AddressResponseDto result =
                    addressService.updateAddress(
                            addressId,
                            request,
                            userEmail
                    );

            assertSame(response, result);

            then(addressMapper)
                    .should()
                    .updateEntity(address, request);

            then(addressMapper)
                    .should()
                    .toResponse(address);

            then(addressRepository)
                    .should(never())
                    .clearDefaultAddressByUserId(any());
        }

        @Test
        void shouldUpdateAddressAndMakeItDefault() {

            final UpdateAddressRequestDto request =
                    UpdateAddressRequestDto.builder()
                            .addressLine1("Updated Address")
                            .defaultAddress(true)
                            .build();

            given(userRepository.findByEmailIgnoreCase(userEmail))
                    .willReturn(Optional.of(user));

            given(addressRepository.findByIdAndUserId(
                    addressId,
                    userId
            )).willReturn(Optional.of(address));

            given(addressMapper.toResponse(address))
                    .willReturn(response);

            addressService.updateAddress(
                    addressId,
                    request,
                    userEmail
            );

            then(addressRepository)
                    .should()
                    .clearDefaultAddressByUserId(userId);

            assertTrue(address.isDefaultAddress());
        }

        @Test
        void shouldNotClearDefaultWhenDefaultIsFalse() {

            final UpdateAddressRequestDto request =
                    UpdateAddressRequestDto.builder()
                            .defaultAddress(false)
                            .build();

            given(userRepository.findByEmailIgnoreCase(userEmail))
                    .willReturn(Optional.of(user));

            given(addressRepository.findByIdAndUserId(
                    addressId,
                    userId
            )).willReturn(Optional.of(address));

            given(addressMapper.toResponse(address))
                    .willReturn(response);

            addressService.updateAddress(
                    addressId,
                    request,
                    userEmail
            );

            then(addressRepository)
                    .should(never())
                    .clearDefaultAddressByUserId(any());
        }

        @Test
        void shouldNormalizeManualAddressCoordinates() {

            address.setLocationType(LocationType.MANUAL);
            address.setLatitude(new BigDecimal("16.830170"));
            address.setLongitude(new BigDecimal("75.710030"));

            final UpdateAddressRequestDto request =
                    UpdateAddressRequestDto.builder()
                            .locationType(LocationType.MANUAL)
                            .build();

            given(userRepository.findByEmailIgnoreCase(userEmail))
                    .willReturn(Optional.of(user));

            given(addressRepository.findByIdAndUserId(
                    addressId,
                    userId
            )).willReturn(Optional.of(address));

            given(addressMapper.toResponse(address))
                    .willReturn(response);

            addressService.updateAddress(
                    addressId,
                    request,
                    userEmail
            );

            assertEquals(
                    LocationType.MANUAL,
                    address.getLocationType()
            );
            assertNull(address.getLatitude());
            assertNull(address.getLongitude());
        }

        @Test
        void shouldAllowMapAddressWithValidCoordinates() {

            final BigDecimal latitude =
                    new BigDecimal("16.830170");

            final BigDecimal longitude =
                    new BigDecimal("75.710030");

            final UpdateAddressRequestDto request =
                    UpdateAddressRequestDto.builder()
                            .locationType(LocationType.MAP)
                            .latitude(latitude)
                            .longitude(longitude)
                            .build();

            given(userRepository.findByEmailIgnoreCase(userEmail))
                    .willReturn(Optional.of(user));

            given(addressRepository.findByIdAndUserId(
                    addressId,
                    userId
            )).willReturn(Optional.of(address));

            willAnswer(invocation -> {
                address.setLocationType(LocationType.MAP);
                address.setLatitude(latitude);
                address.setLongitude(longitude);
                return null;
            }).given(addressMapper)
                    .updateEntity(address, request);

            given(addressMapper.toResponse(address))
                    .willReturn(response);

            addressService.updateAddress(
                    addressId,
                    request,
                    userEmail
            );

            assertEquals(LocationType.MAP, address.getLocationType());
            assertEquals(latitude, address.getLatitude());
            assertEquals(longitude, address.getLongitude());
        }

        @Test
        void shouldRejectMapAddressWithoutCoordinates() {

            final UpdateAddressRequestDto request =
                    UpdateAddressRequestDto.builder()
                            .locationType(LocationType.MAP)
                            .build();

            given(userRepository.findByEmailIgnoreCase(userEmail))
                    .willReturn(Optional.of(user));

            given(addressRepository.findByIdAndUserId(
                    addressId,
                    userId
            )).willReturn(Optional.of(address));

            willAnswer(invocation -> {
                address.setLocationType(LocationType.MAP);
                address.setLatitude(null);
                address.setLongitude(null);
                return null;
            }).given(addressMapper)
                    .updateEntity(address, request);

            final BusinessException exception =
                    assertThrows(
                            BusinessException.class,
                            () -> addressService.updateAddress(
                                    addressId,
                                    request,
                                    userEmail
                            )
                    );

            assertEquals(
                    ADDRESS_COORDINATES_REQUIRED,
                    exception.getErrorCode()
            );

            then(addressMapper)
                    .should(never())
                    .toResponse(address);
        }

        @Test
        void shouldThrowWhenAddressNotFound() {

            final UpdateAddressRequestDto request =
                    UpdateAddressRequestDto.builder()
                            .addressLine1("Updated")
                            .build();

            given(userRepository.findByEmailIgnoreCase(userEmail))
                    .willReturn(Optional.of(user));

            given(addressRepository.findByIdAndUserId(
                    addressId,
                    userId
            )).willReturn(Optional.empty());

            final BusinessException exception =
                    assertThrows(
                            BusinessException.class,
                            () -> addressService.updateAddress(
                                    addressId,
                                    request,
                                    userEmail
                            )
                    );

            assertEquals(
                    ADDRESS_NOT_FOUND,
                    exception.getErrorCode()
            );

            then(addressMapper)
                    .shouldHaveNoInteractions();
        }

        @Test
        void shouldThrowWhenUserNotFound() {

            final UpdateAddressRequestDto request =
                    UpdateAddressRequestDto.builder()
                            .addressLine1("Updated")
                            .build();

            given(userRepository.findByEmailIgnoreCase(userEmail))
                    .willReturn(Optional.empty());

            final BusinessException exception =
                    assertThrows(
                            BusinessException.class,
                            () -> addressService.updateAddress(
                                    addressId,
                                    request,
                                    userEmail
                            )
                    );

            assertEquals(
                    USER_NOT_FOUND,
                    exception.getErrorCode()
            );

            then(addressRepository)
                    .shouldHaveNoInteractions();

            then(addressMapper)
                    .shouldHaveNoInteractions();
        }
    }

    @Nested
    class DeleteAddressTests {

        @Test
        void shouldDeleteAddress() {

            given(userRepository.findByEmailIgnoreCase(userEmail))
                    .willReturn(Optional.of(user));

            given(addressRepository.findByIdAndUserId(
                    addressId,
                    userId
            )).willReturn(Optional.of(address));

            addressService.deleteAddress(
                    addressId,
                    userEmail
            );

            then(addressRepository)
                    .should()
                    .delete(address);
        }

        @Test
        void shouldThrowWhenAddressNotFound() {

            given(userRepository.findByEmailIgnoreCase(userEmail))
                    .willReturn(Optional.of(user));

            given(addressRepository.findByIdAndUserId(
                    addressId,
                    userId
            )).willReturn(Optional.empty());

            assertThrows(
                    BusinessException.class,
                    () -> addressService.deleteAddress(
                            addressId,
                            userEmail
                    )
            );

            then(addressRepository)
                    .should(never())
                    .delete(any());
        }

        @Test
        void shouldRejectAddressBelongingToAnotherUser() {

            given(userRepository.findByEmailIgnoreCase(userEmail))
                    .willReturn(Optional.of(user));

            given(addressRepository.findByIdAndUserId(
                    addressId,
                    userId
            )).willReturn(Optional.empty());

            final BusinessException exception =
                    assertThrows(
                            BusinessException.class,
                            () -> addressService.deleteAddress(
                                    addressId,
                                    userEmail
                            )
                    );

            assertEquals(
                    ADDRESS_NOT_FOUND,
                    exception.getErrorCode()
            );

            then(addressRepository)
                    .should(never())
                    .delete(any());
        }

        @Test
        void shouldThrowWhenUserNotFound() {

            given(userRepository.findByEmailIgnoreCase(userEmail))
                    .willReturn(Optional.empty());

            assertThrows(
                    BusinessException.class,
                    () -> addressService.deleteAddress(
                            addressId,
                            userEmail
                    )
            );

            then(addressRepository)
                    .shouldHaveNoInteractions();
        }
    }

    @Nested
    class SetDefaultAddressTests {

        @Test
        void shouldSetNonDefaultAddressAsDefault() {

            address.setDefaultAddress(false);

            given(userRepository.findByEmailIgnoreCase(userEmail))
                    .willReturn(Optional.of(user));

            given(addressRepository.findByIdAndUserId(
                    addressId,
                    userId
            )).willReturn(Optional.of(address));

            addressService.setDefaultAddress(
                    addressId,
                    userEmail
            );

            then(addressRepository)
                    .should()
                    .clearDefaultAddressByUserId(userId);

            assertTrue(address.isDefaultAddress());
        }

        @Test
        void shouldDoNothingWhenAddressIsAlreadyDefault() {

            address.setDefaultAddress(true);

            given(userRepository.findByEmailIgnoreCase(userEmail))
                    .willReturn(Optional.of(user));

            given(addressRepository.findByIdAndUserId(
                    addressId,
                    userId
            )).willReturn(Optional.of(address));

            addressService.setDefaultAddress(
                    addressId,
                    userEmail
            );

            then(addressRepository)
                    .should(never())
                    .clearDefaultAddressByUserId(any());

            assertTrue(address.isDefaultAddress());
        }

        @Test
        void shouldThrowWhenAddressNotFound() {

            given(userRepository.findByEmailIgnoreCase(userEmail))
                    .willReturn(Optional.of(user));

            given(addressRepository.findByIdAndUserId(
                    addressId,
                    userId
            )).willReturn(Optional.empty());

            final BusinessException exception =
                    assertThrows(
                            BusinessException.class,
                            () -> addressService.setDefaultAddress(
                                    addressId,
                                    userEmail
                            )
                    );

            assertEquals(
                    ADDRESS_NOT_FOUND,
                    exception.getErrorCode()
            );
        }

        @Test
        void shouldRejectAddressBelongingToAnotherUser() {

            given(userRepository.findByEmailIgnoreCase(userEmail))
                    .willReturn(Optional.of(user));

            given(addressRepository.findByIdAndUserId(
                    addressId,
                    userId
            )).willReturn(Optional.empty());

            assertThrows(
                    BusinessException.class,
                    () -> addressService.setDefaultAddress(
                            addressId,
                            userEmail
                    )
            );

            then(addressRepository)
                    .should(never())
                    .clearDefaultAddressByUserId(any());
        }

        @Test
        void shouldThrowWhenUserNotFound() {

            given(userRepository.findByEmailIgnoreCase(userEmail))
                    .willReturn(Optional.empty());

            final BusinessException exception =
                    assertThrows(
                            BusinessException.class,
                            () -> addressService.setDefaultAddress(
                                    addressId,
                                    userEmail
                            )
                    );

            assertEquals(
                    USER_NOT_FOUND,
                    exception.getErrorCode()
            );

            then(addressRepository)
                    .shouldHaveNoInteractions();
        }
    }
}