package com.agri.market.user.service;

import com.agri.market.common.exception.BusinessException;
import com.agri.market.sms.service.SmsService;
import com.agri.market.user.dto.*;
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
    private final SmsService smsService;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(
            final String userEmail
    ) throws UsernameNotFoundException {

        return userRepository.findByEmailIgnoreCase(userEmail)
                .orElseThrow(() -> {
                    log.debug(
                            "User not found during authentication: {}",
                            userEmail
                    );

                    return new UsernameNotFoundException(
                            "User not found with email: " + userEmail
                    );
                });
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponseDto getCurrentUserProfile(
            final String userEmail
    ) {

        final User user = findUserByEmail(userEmail);

        return userMapper.toUserProfileResponseDto(user);
    }

    @Override
    @Transactional
    public void updateFullName(
            final UpdateFullNameRequestDto request,
            final String userEmail
    ) {

        final User user = findUserByEmail(userEmail);

        final String newFullName =
                request.getFullName().trim();

        if (user.getFullName().equals(newFullName)) {
            return;
        }

        user.setFullName(newFullName);

        userRepository.save(user);

        log.info(
                "Full name updated successfully for user: {}",
                userEmail
        );
    }

    @Override
    @Transactional
    public void updateProfilePicture(
            final UpdateProfilePictureRequestDto request,
            final String userEmail
    ) {

        final User user = findUserByEmail(userEmail);

        final String profilePictureUrl =
                request.getProfilePictureUrl().trim();

        if (profilePictureUrl.equals(user.getProfilePictureUrl())) {
            return;
        }

        user.setProfilePictureUrl(profilePictureUrl);

        userRepository.save(user);

        log.info(
                "Profile picture updated successfully for user: {}",
                userEmail
        );
    }

    @Override
    @Transactional
    public void sendPhoneOtp(
            final SendPhoneOtpRequestDto request,
            final String userEmail
    ) {

        final User user = findUserByEmail(userEmail);

        final String phoneNumber =
                request.getPhoneNumber().trim();

        validatePhoneNumberChange(
                user,
                phoneNumber
        );

        smsService.sendOtp(phoneNumber);

        log.info(
                "Phone verification OTP sent successfully for user: {}",
                userEmail
        );
    }

    @Override
    @Transactional
    public void verifyPhoneOtp(
            final VerifyPhoneOtpRequestDto request,
            final String userEmail
    ) {

        final User user = findUserByEmail(userEmail);

        final String phoneNumber =
                request.getPhoneNumber().trim();

        final String otp =
                request.getOtp().trim();

        validatePhoneNumberChange(
                user,
                phoneNumber
        );

        smsService.verifyOtp(
                phoneNumber,
                otp
        );

        user.setPhoneNumber(phoneNumber);
        user.setPhoneVerified(true);

        userRepository.save(user);

        log.info(
                "Phone number verified and updated successfully for user: {}",
                userEmail
        );
    }

    @Override
    @Transactional
    public void resendPhoneOtp(
            final ResendPhoneOtpRequestDto request,
            final String userEmail
    ) {

        final User user = findUserByEmail(userEmail);

        final String phoneNumber =
                request.getPhoneNumber().trim();

        validatePhoneNumberChange(
                user,
                phoneNumber
        );

        smsService.resendOtp(phoneNumber);

        log.info(
                "Phone verification OTP resent successfully for user: {}",
                userEmail
        );
    }

    @Override
    @Transactional
    public void setPassword(
            final SetPasswordRequestDto request,
            final String userEmail
    ) {

        if (!request.getNewPassword().equals(
                request.getConfirmNewPassword()
        )) {

            log.warn(
                    "Password setup rejected because passwords do not match. User: {}",
                    userEmail
            );

            throw new BusinessException(
                    PASSWORD_MISMATCH
            );
        }

        final User user = findUserByEmail(userEmail);

        if (user.getPassword() != null
                && !user.getPassword().isBlank()) {

            log.warn(
                    "Password setup rejected because password is already set. User: {}",
                    userEmail
            );

            throw new BusinessException(
                    PASSWORD_ALREADY_SET
            );
        }

        final LocalDateTime now =
                LocalDateTime.now();

        user.setPassword(
                passwordEncoder.encode(
                        request.getNewPassword()
                )
        );

        user.setPasswordChangedAt(now);
        user.setCredentialsExpired(false);
        user.setFailedLoginAttempts(0);
        user.setTemporaryLockedUntil(null);

        userRepository.save(user);

        log.info(
                "Password set successfully for user: {}",
                userEmail
        );
    }

    @Override
    @Transactional
    public void changePassword(
            final ChangePasswordRequestDto request,
            final String userEmail
    ) {

        if (request.getCurrentPassword().equals(
                request.getNewPassword()
        )) {

            log.warn(
                    "Password change rejected because new password matches current password for user: {}",
                    userEmail
            );

            throw new BusinessException(
                    PASSWORD_MISMATCH
            );
        }

        if (!request.getNewPassword().equals(
                request.getConfirmNewPassword()
        )) {

            log.warn(
                    "Password change rejected because passwords do not match. User: {}",
                    userEmail
            );

            throw new BusinessException(
                    PASSWORD_MISMATCH
            );
        }

        final User user = findUserByEmail(userEmail);

        if (user.getPassword() == null
                || user.getPassword().isBlank()) {

            log.warn(
                    "Password change rejected because password is not set. User: {}",
                    userEmail
            );

            throw new BusinessException(
                    PASSWORD_LOGIN_NOT_AVAILABLE
            );
        }

        if (!passwordEncoder.matches(
                request.getCurrentPassword(),
                user.getPassword()
        )) {

            log.warn(
                    "Password change rejected due to invalid current password for user: {}",
                    userEmail
            );

            throw new BusinessException(
                    INVALID_CURRENT_PASSWORD
            );
        }

        user.setPassword(
                passwordEncoder.encode(
                        request.getNewPassword()
                )
        );

        user.setPasswordChangedAt(
                LocalDateTime.now()
        );

        user.setCredentialsExpired(false);
        user.setFailedLoginAttempts(0);
        user.setTemporaryLockedUntil(null);

        userRepository.save(user);

        log.info(
                "Password changed successfully for user: {}",
                userEmail
        );
    }

    @Override
    @Transactional
    public void deactivateAccount(
            final String userEmail
    ) {

        final User user =
                findUserByEmail(userEmail);

        if (!user.isEnabled()) {

            log.warn(
                    "Account deactivation rejected; account already deactivated for user: {}",
                    userEmail
            );

            throw new BusinessException(
                    USER_ALREADY_DEACTIVATED
            );
        }

        user.setEnabled(false);

        userRepository.save(user);

        log.info(
                "User account deactivated successfully: {}",
                userEmail
        );
    }

    @Override
    @Transactional
    public void reactivateAccount(
            final String userEmail
    ) {

        final User user =
                findUserByEmail(userEmail);

        if (user.isEnabled()) {

            log.warn(
                    "Account reactivation rejected; account already active for user: {}",
                    userEmail
            );

            throw new BusinessException(
                    USER_ALREADY_ACTIVATED,
                    user.getEmail()
            );
        }

        user.setEnabled(true);

        userRepository.save(user);

        log.info(
                "User account reactivated successfully: {}",
                userEmail
        );
    }

    @Override
    @Transactional
    public void deleteAccount(
            final String userEmail
    ) {

        final User user =
                findUserByEmail(userEmail);

        userRepository.delete(user);

        log.info(
                "User account deleted successfully: {}",
                userEmail
        );
    }

    private void validatePhoneNumberChange(
            final User user,
            final String phoneNumber
    ) {

        if (phoneNumber.equals(user.getPhoneNumber())
                && user.isPhoneVerified()) {

            log.warn(
                    "Phone number is already verified for user: {}",
                    user.getEmail()
            );

            throw new BusinessException(
                    PHONE_OTP_ALREADY_VERIFIED
            );
        }

        userRepository.findByPhoneNumber(phoneNumber)
                .ifPresent(existingUser -> {

                    if (!existingUser.getId().equals(
                            user.getId()
                    )) {

                        log.warn(
                                "Phone number already exists for another user: {}",
                                user.getEmail()
                        );

                        throw new BusinessException(
                                PHONE_ALREADY_EXISTS
                        );
                    }
                });
    }

    private User findUserByEmail(
            final String userEmail
    ) {

        return userRepository.findByEmailIgnoreCase(userEmail)
                .orElseThrow(() -> {

                    log.warn(
                            "User not found: {}",
                            userEmail
                    );

                    return new BusinessException(
                            USER_NOT_FOUND
                    );
                });
    }
}