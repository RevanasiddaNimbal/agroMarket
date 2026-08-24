package com.agri.market.user.service;

import com.agri.market.common.exception.BusinessException;
import com.agri.market.user.dto.ChangePasswordRequestDto;
import com.agri.market.user.dto.ProfileUpdateRequestDto;
import com.agri.market.user.dto.SetPasswordRequestDto;
import com.agri.market.user.entity.User;
import com.agri.market.user.mapper.UserMapper;
import com.agri.market.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.agri.market.common.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(
            final String userEmail
    ) throws UsernameNotFoundException {

        return userRepository.findByEmailIgnoreCase(userEmail)
                .orElseThrow(() -> {
                    log.debug("User not found during authentication: {}", userEmail);
                    return new UsernameNotFoundException(
                            "User not found with email: " + userEmail
                    );
                });
    }

    @Override
    @Transactional
    public void updateProfileInfo(
            final ProfileUpdateRequestDto request,
            final String userEmail
    ) {
        final User user = findUserByEmail(userEmail);

        userMapper.updateUserFromProfileRequest(request, user);

        userRepository.save(user);

        log.info("Profile updated successfully for user: {}", userEmail);
    }

    @Override
    @Transactional
    public void setPassword(
            final SetPasswordRequestDto request,
            final String userEmail
    ) {
        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            log.warn(
                    "Password setup rejected because passwords do not match. User: {}",
                    userEmail
            );
            throw new BusinessException(PASSWORD_MISMATCH);
        }

        final User user = findUserByEmail(userEmail);

        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            log.warn(
                    "Password setup rejected because password is already set. User: {}",
                    userEmail
            );
            throw new BusinessException(PASSWORD_ALREADY_SET);
        }

        final LocalDateTime now = LocalDateTime.now();

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setPasswordChangedAt(now);
        user.setCredentialsExpired(false);
        user.setFailedLoginAttempts(0);
        user.setTemporaryLockedUntil(null);

        userRepository.save(user);
    }

    @Override
    @Transactional
    public void changePassword(
            final ChangePasswordRequestDto request,
            final String userEmail
    ) {
        if (request.getCurrentPassword()
                .equals(request.getNewPassword())) {

            log.warn(
                    "Password change rejected because new password "
                            + "matches current password for user: {}",
                    userEmail
            );

            throw new BusinessException(PASSWORD_MISMATCH);
        }

        final User user = findUserByEmail(userEmail);

        if (!passwordEncoder.matches(
                request.getCurrentPassword(),
                user.getPassword()
        )) {
            log.warn(
                    "Password change rejected due to invalid current password "
                            + "for user: {}",
                    userEmail
            );

            throw new BusinessException(INVALID_CURRENT_PASSWORD);
        }

        user.setPassword(
                passwordEncoder.encode(request.getNewPassword())
        );
        user.setPasswordChangedAt(LocalDateTime.now());
        user.setCredentialsExpired(false);
        user.setFailedLoginAttempts(0);
        user.setTemporaryLockedUntil(null);
        userRepository.save(user);

        log.info("Password changed successfully for user: {}", userEmail);
    }

    @Override
    @Transactional
    public void deactivateAccount(final String userEmail) {

        final User user = findUserByEmail(userEmail);

        if (!user.isEnabled()) {
            log.warn(
                    "Account deactivation rejected; account already "
                            + "deactivated for user: {}",
                    userEmail
            );

            throw new BusinessException(
                    USER_ALREADY_DEACTIVATED
            );
        }

        user.setEnabled(false);

        userRepository.save(user);

        log.info("User account deactivated successfully: {}", userEmail);
    }

    @Override
    @Transactional
    public void reactivateAccount(final String userEmail) {

        final User user = findUserByEmail(userEmail);

        if (user.isEnabled()) {
            log.warn(
                    "Account reactivation rejected; account already "
                            + "active for user: {}",
                    userEmail
            );

            throw new BusinessException(
                    USER_ALREADY_ACTIVATED,
                    user.getEmail()
            );
        }

        user.setEnabled(true);

        userRepository.save(user);

        log.info("User account reactivated successfully: {}", userEmail);
    }

    @Override
    public void deleteAccount(final String userId) {
        // So many required components are needed.
    }

    private User findUserByEmail(final String userEmail) {

        return userRepository.findByEmailIgnoreCase(userEmail)
                .orElseThrow(() -> {
                    log.warn("User not found: {}", userEmail);
                    return new BusinessException(
                            USER_NOT_FOUND
                    );
                });
    }
}