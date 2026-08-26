package com.agri.market.admin.mapper;

import com.agri.market.address.entity.Address;
import com.agri.market.admin.dto.AdminUserDetailResponseDto;
import com.agri.market.admin.dto.AdminUserStatusResponseDto;
import com.agri.market.admin.dto.AdminUserSummaryResponseDto;
import com.agri.market.role.entity.Role;
import com.agri.market.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AdminUserMapperTest {

    private AdminUserMapper adminUserMapper;
    private User user;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime passwordChangedAt;

    @BeforeEach
    void setUp() {
        adminUserMapper = new AdminUserMapper();

        createdAt = LocalDateTime.of(2026, 8, 1, 10, 30);
        updatedAt = LocalDateTime.of(2026, 8, 20, 15, 45);
        passwordChangedAt = LocalDateTime.of(2026, 8, 15, 12, 0);

        user = new User();
        user.setId("user-123");
        user.setFullName("Test User");
        user.setEmail("test@example.com");
        user.setPhoneNumber("9876543210");
        user.setEmailVerified(true);
        user.setPhoneVerified(true);
        user.setProfilePictureUrl("https://example.com/profile.jpg");
        user.setEnabled(true);
        user.setAccountLocked(false);
        user.setCreatedAt(createdAt);
        user.setUpdatedAt(updatedAt);
        user.setPasswordChangedAt(passwordChangedAt);
        user.setCredentialsExpired(false);
        user.setPassword("$2a$10$encodedPassword");
    }

    @Nested
    class ToSummaryResponseDtoTests {

        @Test
        void shouldMapAllUserFieldsToSummaryResponse() {
            Role farmerRole = new Role();
            farmerRole.setName("FARMER");

            Role customerRole = new Role();
            customerRole.setName("CUSTOMER");

            user.setRoles(List.of(farmerRole, customerRole));

            AdminUserSummaryResponseDto result =
                    adminUserMapper.toSummaryResponseDto(user);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo("user-123");
            assertThat(result.getFullName()).isEqualTo("Test User");
            assertThat(result.getEmail()).isEqualTo("test@example.com");
            assertThat(result.getPhoneNumber()).isEqualTo("9876543210");
            assertThat(result.isEmailVerified()).isTrue();
            assertThat(result.isPhoneVerified()).isTrue();
            assertThat(result.isEnabled()).isTrue();
            assertThat(result.isAccountLocked()).isFalse();
            assertThat(result.getRoles())
                    .containsExactlyInAnyOrder("FARMER", "CUSTOMER");
            assertThat(result.getCreatedAt()).isEqualTo(createdAt);
            assertThat(result.getUpdatedAt()).isEqualTo(updatedAt);
        }

        @Test
        void shouldReturnEmptyRolesWhenRolesAreNull() {
            user.setRoles(null);

            AdminUserSummaryResponseDto result =
                    adminUserMapper.toSummaryResponseDto(user);

            assertThat(result).isNotNull();
            assertThat(result.getRoles())
                    .isNotNull()
                    .isEmpty();
        }

        @Test
        void shouldReturnEmptyRolesWhenRolesAreEmpty() {
            user.setRoles(List.of());

            AdminUserSummaryResponseDto result =
                    adminUserMapper.toSummaryResponseDto(user);

            assertThat(result).isNotNull();
            assertThat(result.getRoles())
                    .isNotNull()
                    .isEmpty();
        }

        @Test
        void shouldMapUnverifiedUserCorrectly() {
            user.setEmailVerified(false);
            user.setPhoneVerified(false);
            user.setEnabled(false);
            user.setAccountLocked(true);

            AdminUserSummaryResponseDto result =
                    adminUserMapper.toSummaryResponseDto(user);

            assertThat(result.isEmailVerified()).isFalse();
            assertThat(result.isPhoneVerified()).isFalse();
            assertThat(result.isEnabled()).isFalse();
            assertThat(result.isAccountLocked()).isTrue();
        }

        @Test
        void shouldMapNullOptionalUserFields() {
            user.setPhoneNumber(null);
            user.setCreatedAt(null);
            user.setUpdatedAt(null);
            user.setRoles(null);

            AdminUserSummaryResponseDto result =
                    adminUserMapper.toSummaryResponseDto(user);

            assertThat(result.getPhoneNumber()).isNull();
            assertThat(result.getCreatedAt()).isNull();
            assertThat(result.getUpdatedAt()).isNull();
            assertThat(result.getRoles()).isEmpty();
        }
    }

    @Nested
    class ToDetailResponseDtoTests {

        @Test
        void shouldMapAllUserFieldsToDetailResponse() {
            Role farmerRole = new Role();
            farmerRole.setName("FARMER");

            Role adminRole = new Role();
            adminRole.setName("ADMIN");

            user.setRoles(List.of(farmerRole, adminRole));
            user.setAddresses(List.of());

            AdminUserDetailResponseDto result =
                    adminUserMapper.toDetailResponseDto(user);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo("user-123");
            assertThat(result.getFullName()).isEqualTo("Test User");
            assertThat(result.getEmail()).isEqualTo("test@example.com");
            assertThat(result.getPhoneNumber()).isEqualTo("9876543210");
            assertThat(result.isEmailVerified()).isTrue();
            assertThat(result.isPhoneVerified()).isTrue();
            assertThat(result.getProfilePictureUrl())
                    .isEqualTo("https://example.com/profile.jpg");
            assertThat(result.isEnabled()).isTrue();
            assertThat(result.isAccountLocked()).isFalse();
            assertThat(result.getRoles())
                    .containsExactlyInAnyOrder("FARMER", "ADMIN");
            assertThat(result.getAddressCount()).isZero();
            assertThat(result.isHasPassword()).isTrue();
            assertThat(result.isCredentialsExpired()).isFalse();
            assertThat(result.getCreatedAt()).isEqualTo(createdAt);
            assertThat(result.getUpdatedAt()).isEqualTo(updatedAt);
            assertThat(result.getPasswordChangedAt())
                    .isEqualTo(passwordChangedAt);
        }

        @Test
        void shouldCalculateAddressCountCorrectly() {
            Address address1 = new Address();
            Address address2 = new Address();
            Address address3 = new Address();

            user.setAddresses(List.of(
                    address1,
                    address2,
                    address3
            ));

            AdminUserDetailResponseDto result =
                    adminUserMapper.toDetailResponseDto(user);

            assertThat(result.getAddressCount())
                    .isEqualTo(3);
        }

        @Test
        void shouldReturnZeroAddressCountWhenAddressesAreNull() {
            user.setAddresses(null);

            AdminUserDetailResponseDto result =
                    adminUserMapper.toDetailResponseDto(user);

            assertThat(result.getAddressCount())
                    .isZero();
        }

        @Test
        void shouldReturnZeroAddressCountWhenAddressesAreEmpty() {
            user.setAddresses(List.of());

            AdminUserDetailResponseDto result =
                    adminUserMapper.toDetailResponseDto(user);

            assertThat(result.getAddressCount())
                    .isZero();
        }

        @Test
        void shouldReturnTrueWhenUserHasPassword() {
            user.setPassword("$2a$10$encodedPassword");

            AdminUserDetailResponseDto result =
                    adminUserMapper.toDetailResponseDto(user);

            assertThat(result.isHasPassword())
                    .isTrue();
        }

        @Test
        void shouldReturnFalseWhenPasswordIsNull() {
            user.setPassword(null);

            AdminUserDetailResponseDto result =
                    adminUserMapper.toDetailResponseDto(user);

            assertThat(result.isHasPassword())
                    .isFalse();
        }

        @Test
        void shouldReturnFalseWhenPasswordIsBlank() {
            user.setPassword("   ");

            AdminUserDetailResponseDto result =
                    adminUserMapper.toDetailResponseDto(user);

            assertThat(result.isHasPassword())
                    .isFalse();
        }

        @Test
        void shouldReturnFalseWhenPasswordIsEmpty() {
            user.setPassword("");

            AdminUserDetailResponseDto result =
                    adminUserMapper.toDetailResponseDto(user);

            assertThat(result.isHasPassword())
                    .isFalse();
        }

        @Test
        void shouldMapCredentialsExpiredCorrectly() {
            user.setCredentialsExpired(true);

            AdminUserDetailResponseDto result =
                    adminUserMapper.toDetailResponseDto(user);

            assertThat(result.isCredentialsExpired())
                    .isTrue();
        }

        @Test
        void shouldMapCredentialsNotExpiredCorrectly() {
            user.setCredentialsExpired(false);

            AdminUserDetailResponseDto result =
                    adminUserMapper.toDetailResponseDto(user);

            assertThat(result.isCredentialsExpired())
                    .isFalse();
        }

        @Test
        void shouldReturnEmptyRolesWhenRolesAreNull() {
            user.setRoles(null);

            AdminUserDetailResponseDto result =
                    adminUserMapper.toDetailResponseDto(user);

            assertThat(result.getRoles())
                    .isNotNull()
                    .isEmpty();
        }

        @Test
        void shouldReturnEmptyRolesWhenRolesAreEmpty() {
            user.setRoles(List.of());

            AdminUserDetailResponseDto result =
                    adminUserMapper.toDetailResponseDto(user);

            assertThat(result.getRoles())
                    .isNotNull()
                    .isEmpty();
        }

        @Test
        void shouldMapPasswordChangedAtCorrectly() {
            user.setPasswordChangedAt(passwordChangedAt);

            AdminUserDetailResponseDto result =
                    adminUserMapper.toDetailResponseDto(user);

            assertThat(result.getPasswordChangedAt())
                    .isEqualTo(passwordChangedAt);
        }

        @Test
        void shouldMapNullPasswordChangedAtCorrectly() {
            user.setPasswordChangedAt(null);

            AdminUserDetailResponseDto result =
                    adminUserMapper.toDetailResponseDto(user);

            assertThat(result.getPasswordChangedAt())
                    .isNull();
        }

        @Test
        void shouldMapNullOptionalFieldsCorrectly() {
            user.setPhoneNumber(null);
            user.setProfilePictureUrl(null);
            user.setCreatedAt(null);
            user.setUpdatedAt(null);
            user.setPasswordChangedAt(null);
            user.setRoles(null);
            user.setAddresses(null);
            user.setPassword(null);

            AdminUserDetailResponseDto result =
                    adminUserMapper.toDetailResponseDto(user);

            assertThat(result.getPhoneNumber()).isNull();
            assertThat(result.getProfilePictureUrl()).isNull();
            assertThat(result.getCreatedAt()).isNull();
            assertThat(result.getUpdatedAt()).isNull();
            assertThat(result.getPasswordChangedAt()).isNull();
            assertThat(result.getRoles()).isEmpty();
            assertThat(result.getAddressCount()).isZero();
            assertThat(result.isHasPassword()).isFalse();
        }
    }

    @Nested
    class ToStatusResponseDtoTests {

        @Test
        void shouldMapStatusResponseCorrectly() {
            user.setEnabled(true);
            user.setAccountLocked(false);

            AdminUserStatusResponseDto result =
                    adminUserMapper.toStatusResponseDto(
                            user,
                            "User account activated successfully."
                    );

            assertThat(result).isNotNull();
            assertThat(result.getUserId())
                    .isEqualTo("user-123");
            assertThat(result.isEnabled())
                    .isTrue();
            assertThat(result.isAccountLocked())
                    .isFalse();
            assertThat(result.getMessage())
                    .isEqualTo("User account activated successfully.");
        }

        @Test
        void shouldMapLockedUserStatusCorrectly() {
            user.setEnabled(true);
            user.setAccountLocked(true);

            AdminUserStatusResponseDto result =
                    adminUserMapper.toStatusResponseDto(
                            user,
                            "User account locked successfully."
                    );

            assertThat(result.getUserId())
                    .isEqualTo("user-123");
            assertThat(result.isEnabled())
                    .isTrue();
            assertThat(result.isAccountLocked())
                    .isTrue();
            assertThat(result.getMessage())
                    .isEqualTo("User account locked successfully.");
        }

        @Test
        void shouldMapDisabledUserStatusCorrectly() {
            user.setEnabled(false);
            user.setAccountLocked(false);

            AdminUserStatusResponseDto result =
                    adminUserMapper.toStatusResponseDto(
                            user,
                            "User account deactivated successfully."
                    );

            assertThat(result.getUserId())
                    .isEqualTo("user-123");
            assertThat(result.isEnabled())
                    .isFalse();
            assertThat(result.isAccountLocked())
                    .isFalse();
            assertThat(result.getMessage())
                    .isEqualTo("User account deactivated successfully.");
        }

        @Test
        void shouldPreserveNullMessage() {
            AdminUserStatusResponseDto result =
                    adminUserMapper.toStatusResponseDto(
                            user,
                            null
                    );

            assertThat(result.getMessage())
                    .isNull();
        }

        @Test
        void shouldPreserveEmptyMessage() {
            AdminUserStatusResponseDto result =
                    adminUserMapper.toStatusResponseDto(
                            user,
                            ""
                    );

            assertThat(result.getMessage())
                    .isEmpty();
        }
    }
}