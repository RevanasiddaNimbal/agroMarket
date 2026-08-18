package com.agri.market.initializer;

import com.agri.market.role.entity.Role;
import com.agri.market.role.entity.RoleName;
import com.agri.market.role.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.ApplicationArguments;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleInitializerTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private ApplicationArguments applicationArguments;

    private RoleInitializer roleInitializer;

    @BeforeEach
    void setUp() {
        roleInitializer = new RoleInitializer(roleRepository);
    }

    @Nested
    class RunTests {

        @Test
        void shouldCreateMissingRoles() {
            for (RoleName roleName : RoleName.values()) {
                when(roleRepository.existsByName(roleName.name()))
                        .thenReturn(false);
            }

            roleInitializer.run(applicationArguments);

            verify(roleRepository, times(RoleName.values().length))
                    .save(any(Role.class));
        }

        @Test
        void shouldNotCreateExistingRoles() {
            for (RoleName roleName : RoleName.values()) {
                when(roleRepository.existsByName(roleName.name()))
                        .thenReturn(true);
            }

            roleInitializer.run(applicationArguments);

            verify(roleRepository, never()).save(any(Role.class));
        }

        @Test
        void shouldCreateOnlyMissingRoles() {
            RoleName[] roles = RoleName.values();

            for (int i = 0; i < roles.length; i++) {
                when(roleRepository.existsByName(roles[i].name()))
                        .thenReturn(i % 2 == 0);
            }

            roleInitializer.run(applicationArguments);

            long expectedSaveCount = java.util.Arrays.stream(roles)
                    .filter(roleName ->
                            java.util.Arrays.asList(roles).indexOf(roleName) % 2 != 0)
                    .count();

            verify(roleRepository, times((int) expectedSaveCount))
                    .save(any(Role.class));
        }

        @Test
        void shouldCreateRoleWithCorrectName() {
            when(roleRepository.existsByName(RoleName.USER.name()))
                    .thenReturn(false);

            for (RoleName roleName : RoleName.values()) {
                if (roleName != RoleName.USER) {
                    when(roleRepository.existsByName(roleName.name()))
                            .thenReturn(true);
                }
            }

            roleInitializer.run(applicationArguments);

            ArgumentCaptor<Role> roleCaptor =
                    ArgumentCaptor.forClass(Role.class);

            verify(roleRepository).save(roleCaptor.capture());

            Role savedRole = roleCaptor.getValue();

            assertThat(savedRole.getName())
                    .isEqualTo(RoleName.USER.name());
        }

        @Test
        void shouldCreateRoleWithSystemAsCreatedBy() {
            when(roleRepository.existsByName(RoleName.USER.name()))
                    .thenReturn(false);

            for (RoleName roleName : RoleName.values()) {
                if (roleName != RoleName.USER) {
                    when(roleRepository.existsByName(roleName.name()))
                            .thenReturn(true);
                }
            }

            roleInitializer.run(applicationArguments);

            ArgumentCaptor<Role> roleCaptor =
                    ArgumentCaptor.forClass(Role.class);

            verify(roleRepository).save(roleCaptor.capture());

            assertThat(roleCaptor.getValue().getCreatedBy())
                    .isEqualTo("SYSTEM");
        }

        @Test
        void shouldCheckEveryAvailableRole() {
            for (RoleName roleName : RoleName.values()) {
                when(roleRepository.existsByName(roleName.name()))
                        .thenReturn(true);
            }

            roleInitializer.run(applicationArguments);

            for (RoleName roleName : RoleName.values()) {
                verify(roleRepository).existsByName(roleName.name());
            }
        }
    }

    @Nested
    class RunWithNoRolesTests {

        @Test
        void shouldNotCreateExistingRoles() {
            for (RoleName roleName : RoleName.values()) {
                when(roleRepository.existsByName(roleName.name()))
                        .thenReturn(true);
            }

            roleInitializer.run(applicationArguments);

            verify(roleRepository, never()).save(any(Role.class));
        }
    }
}