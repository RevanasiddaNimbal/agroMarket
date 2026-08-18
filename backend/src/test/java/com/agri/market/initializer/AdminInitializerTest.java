package com.agri.market.initializer;

import com.agri.market.role.entity.Role;
import com.agri.market.role.entity.RoleName;
import com.agri.market.role.repository.RoleRepository;
import com.agri.market.user.entity.User;
import com.agri.market.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.ApplicationArguments;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminInitializerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ApplicationArguments applicationArguments;

    private AdminInitializer adminInitializer;

    private static final String ADMIN_EMAIL = "admin@agrimarket.com";
    private static final String ADMIN_PASSWORD = "Admin@123";
    private static final String ADMIN_PHONE = "9876543210";
    private static final String ENCODED_PASSWORD = "encoded-password";

    @BeforeEach
    void setUp() {
        adminInitializer = new AdminInitializer(
                userRepository,
                roleRepository,
                passwordEncoder
        );

        setField(adminInitializer, "adminEmail", ADMIN_EMAIL);
        setField(adminInitializer, "adminPassword", ADMIN_PASSWORD);
        setField(adminInitializer, "adminPhone", ADMIN_PHONE);
    }

    @Nested
    class RunTests {

        @Test
        void shouldDoNothingWhenAdminAlreadyExists() {
            when(userRepository.existsByEmailIgnoreCase(ADMIN_EMAIL))
                    .thenReturn(true);

            adminInitializer.run(applicationArguments);

            verify(userRepository)
                    .existsByEmailIgnoreCase(ADMIN_EMAIL);

            verifyNoMoreInteractions(userRepository);
            verifyNoInteractions(roleRepository);
            verifyNoInteractions(passwordEncoder);
        }

        @Test
        void shouldCreateAdminWhenAdminDoesNotExist() {
            Role adminRole = createAdminRole();

            when(userRepository.existsByEmailIgnoreCase(ADMIN_EMAIL))
                    .thenReturn(false);

            when(roleRepository.findByName(RoleName.ADMIN.name()))
                    .thenReturn(Optional.of(adminRole));

            when(passwordEncoder.encode(ADMIN_PASSWORD))
                    .thenReturn(ENCODED_PASSWORD);

            adminInitializer.run(applicationArguments);

            verify(userRepository)
                    .existsByEmailIgnoreCase(ADMIN_EMAIL);

            verify(roleRepository)
                    .findByName(RoleName.ADMIN.name());

            verify(passwordEncoder)
                    .encode(ADMIN_PASSWORD);

            verify(userRepository)
                    .save(any(User.class));
        }

        @Test
        void shouldThrowExceptionWhenAdminRoleDoesNotExist() {
            when(userRepository.existsByEmailIgnoreCase(ADMIN_EMAIL))
                    .thenReturn(false);

            when(roleRepository.findByName(RoleName.ADMIN.name()))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() ->
                    adminInitializer.run(applicationArguments))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage(
                            "ADMIN role not found. " +
                                    "RoleInitializer may not have executed."
                    );

            verify(roleRepository)
                    .findByName(RoleName.ADMIN.name());

            verify(userRepository, never())
                    .save(any(User.class));

            verifyNoInteractions(passwordEncoder);
        }

        @Test
        void shouldEncodeAdminPassword() {
            Role adminRole = createAdminRole();

            when(userRepository.existsByEmailIgnoreCase(ADMIN_EMAIL))
                    .thenReturn(false);

            when(roleRepository.findByName(RoleName.ADMIN.name()))
                    .thenReturn(Optional.of(adminRole));

            when(passwordEncoder.encode(ADMIN_PASSWORD))
                    .thenReturn(ENCODED_PASSWORD);

            adminInitializer.run(applicationArguments);

            verify(passwordEncoder)
                    .encode(ADMIN_PASSWORD);
        }

        @Test
        void shouldSaveAdminWithCorrectDetails() {
            Role adminRole = createAdminRole();

            when(userRepository.existsByEmailIgnoreCase(ADMIN_EMAIL))
                    .thenReturn(false);

            when(roleRepository.findByName(RoleName.ADMIN.name()))
                    .thenReturn(Optional.of(adminRole));

            when(passwordEncoder.encode(ADMIN_PASSWORD))
                    .thenReturn(ENCODED_PASSWORD);

            adminInitializer.run(applicationArguments);

            ArgumentCaptor<User> userCaptor =
                    ArgumentCaptor.forClass(User.class);

            verify(userRepository)
                    .save(userCaptor.capture());

            User savedAdmin = userCaptor.getValue();

            assertThat(savedAdmin.getFullName())
                    .isEqualTo("System Administrator");

            assertThat(savedAdmin.getEmail())
                    .isEqualTo(ADMIN_EMAIL);

            assertThat(savedAdmin.getPhoneNumber())
                    .isEqualTo(ADMIN_PHONE);

            assertThat(savedAdmin.getPassword())
                    .isEqualTo(ENCODED_PASSWORD);

            assertThat(savedAdmin.getRoles())
                    .containsExactly(adminRole);

            assertThat(savedAdmin.isEmailVerified())
                    .isTrue();

            assertThat(savedAdmin.isPhoneVerified())
                    .isTrue();

            assertThat(savedAdmin.isEnabled())
                    .isTrue();

            assertThat(savedAdmin.isAccountLocked())
                    .isFalse();

            assertThat(savedAdmin.isCredentialsExpired())
                    .isFalse();
        }

        @Test
        void shouldFindAdminRoleUsingAdminRoleName() {
            Role adminRole = createAdminRole();

            when(userRepository.existsByEmailIgnoreCase(ADMIN_EMAIL))
                    .thenReturn(false);

            when(roleRepository.findByName(RoleName.ADMIN.name()))
                    .thenReturn(Optional.of(adminRole));

            when(passwordEncoder.encode(ADMIN_PASSWORD))
                    .thenReturn(ENCODED_PASSWORD);

            adminInitializer.run(applicationArguments);

            verify(roleRepository)
                    .findByName(RoleName.ADMIN.name());
        }

        @Test
        void shouldNotCreateAdminWhenEmailAlreadyExists() {
            when(userRepository.existsByEmailIgnoreCase(ADMIN_EMAIL))
                    .thenReturn(true);

            adminInitializer.run(applicationArguments);

            verify(userRepository, never())
                    .save(any(User.class));

            verifyNoInteractions(roleRepository);
            verifyNoInteractions(passwordEncoder);
        }
    }

    private Role createAdminRole() {
        return Role.builder()
                .name(RoleName.ADMIN.name())
                .build();
    }

    private void setField(
            Object target,
            String fieldName,
            String value
    ) {
        try {
            var field = target.getClass()
                    .getDeclaredField(fieldName);

            field.setAccessible(true);
            field.set(target, value);

        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException(
                    "Failed to set test field: " + fieldName,
                    exception
            );
        }
    }
}