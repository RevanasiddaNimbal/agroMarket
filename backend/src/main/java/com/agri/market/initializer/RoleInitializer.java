package com.agri.market.initializer;

import com.agri.market.role.entity.Role;
import com.agri.market.role.entity.RoleName;
import com.agri.market.role.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Order(1)
@RequiredArgsConstructor
public class RoleInitializer implements ApplicationRunner {

    private final RoleRepository roleRepository;

    private static final String SYSTEM = "SYSTEM";

    @Override
    @Transactional
    public void run(ApplicationArguments args) {

        for (RoleName roleName : RoleName.values()) {

            String roleNameValue = roleName.name();

            if (!roleRepository.existsByName(roleNameValue)) {

                Role role = Role.builder()
                        .name(roleNameValue)
                        .createdBy(SYSTEM)
                        .build();

                roleRepository.save(role);
            }
        }
    }
}