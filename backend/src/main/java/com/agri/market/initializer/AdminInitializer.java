package com.agri.market.initializer;

import com.agri.market.role.entity.Role;
import com.agri.market.role.entity.RoleName;
import com.agri.market.role.repository.RoleRepository;
import com.agri.market.user.entity.User;
import com.agri.market.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Order(2)
@RequiredArgsConstructor
public class AdminInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.initial-admin.email}")
    private String adminEmail;

    @Value("${app.initial-admin.password}")
    private String adminPassword;

    @Value("${app.initial-admin.phone}")
    private String adminPhone;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {

        if (userRepository.existsByEmailIgnoreCase(adminEmail)) {
            return;
        }

        Role adminRole = roleRepository
                .findByName(RoleName.ADMIN.name())
                .orElseThrow(() ->
                        new IllegalStateException(
                                "ADMIN role not found. " +
                                        "RoleInitializer may not have executed."
                        )
                );

        User admin = User.builder()
                .fullName("System Administrator")
                .email(adminEmail)
                .phoneNumber(adminPhone)
                .password(passwordEncoder.encode(adminPassword))
                .roles(List.of(adminRole))
                .emailVerified(true)
                .phoneVerified(true)
                .enabled(true)
                .accountLocked(false)
                .credentialsExpired(false)
                .build();

        userRepository.save(admin);
    }
}