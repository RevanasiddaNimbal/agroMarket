package com.agri.market.auth.controller;

import com.agri.market.common.exception.BusinessException;
import com.agri.market.common.exception.ErrorCode;
import com.agri.market.common.handler.ApplicationExceptionHandler;
import com.agri.market.password.controller.PasswordResetController;
import com.agri.market.password.dto.ForgotPasswordRequest;
import com.agri.market.password.dto.ResetPasswordRequest;
import com.agri.market.password.service.PasswordResetService;
import com.agri.market.validation.validator.EmailDomainValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("PasswordResetController")
class PasswordResetControllerTest {

    private final ObjectMapper objectMapper =
            new ObjectMapper();

    @Mock
    private PasswordResetService passwordResetService;

    @InjectMocks
    private PasswordResetController passwordResetController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {

        LocalValidatorFactoryBean validator =
                new LocalValidatorFactoryBean();

        validator.setConstraintValidatorFactory(
                new ConstraintValidatorFactory() {

                    @Override
                    public <T extends ConstraintValidator<?, ?>> T getInstance(
                            Class<T> key) {

                        try {

                            if (key.equals(EmailDomainValidator.class)) {

                                return key.cast(
                                        new EmailDomainValidator(
                                                List.of(
                                                        "10minutemail",
                                                        "20minutemail",
                                                        "mailinator",
                                                        "yopmail"
                                                )
                                        )
                                );
                            }

                            return key.getDeclaredConstructor()
                                    .newInstance();

                        } catch (Exception exception) {

                            throw new IllegalStateException(exception);
                        }
                    }

                    @Override
                    public void releaseInstance(
                            ConstraintValidator<?, ?> instance) {
                    }
                }
        );

        validator.afterPropertiesSet();


        mockMvc = MockMvcBuilders
                .standaloneSetup(passwordResetController)
                .setControllerAdvice(new ApplicationExceptionHandler())
                .setValidator(validator)
                .build();
    }


    @Nested
    @DisplayName("forgotPassword")
    class ForgotPasswordTests {

        @Test
        void shouldProcessPasswordResetRequest()
                throws Exception {

            ForgotPasswordRequest request =
                    new ForgotPasswordRequest(
                            "user@example.com"
                    );

            mockMvc.perform(
                            post("/api/v1/auth/forgot-password")
                                    .contentType(
                                            MediaType.APPLICATION_JSON
                                    )
                                    .content(
                                            objectMapper.writeValueAsString(
                                                    request
                                            )
                                    )
                    )
                    .andExpect(status().isOk());

            verify(passwordResetService)
                    .forgotPassword(any());
        }

        @Test
        void shouldRejectInvalidEmail()
                throws Exception {

            mockMvc.perform(
                            post("/api/v1/auth/forgot-password")
                                    .contentType(
                                            MediaType.APPLICATION_JSON
                                    )
                                    .content("""
                                            {
                                                "email": "invalid-email"
                                            }
                                            """)
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(
                            jsonPath("$.code")
                                    .value("VALIDATION_ERROR")
                    );

            verifyNoInteractions(passwordResetService);
        }

        @Test
        void shouldNotExposeUserInformation()
                throws Exception {

            mockMvc.perform(
                            post("/api/v1/auth/forgot-password")
                                    .contentType(
                                            MediaType.APPLICATION_JSON
                                    )
                                    .content("""
                                            {
                                                "email": "unknown@example.com"
                                            }
                                            """)
                    )
                    .andExpect(status().isOk());

            verify(passwordResetService)
                    .forgotPassword(any());
        }
    }


    @Nested
    @DisplayName("resetPassword")
    class ResetPasswordTests {

        @Test
        void shouldResetPassword()
                throws Exception {

            ResetPasswordRequest request =
                    new ResetPasswordRequest(
                            "valid-token",
                            "NewPassword@123",
                            "NewPassword@123"
                    );

            mockMvc.perform(
                            post("/api/v1/auth/reset-password")
                                    .contentType(
                                            MediaType.APPLICATION_JSON
                                    )
                                    .content(
                                            objectMapper.writeValueAsString(
                                                    request
                                            )
                                    )
                    )
                    .andExpect(status().isOk());

            verify(passwordResetService)
                    .resetPassword(any());
        }

        @Test
        void shouldRejectInvalidResetRequest()
                throws Exception {

            mockMvc.perform(
                            post("/api/v1/auth/reset-password")
                                    .contentType(
                                            MediaType.APPLICATION_JSON
                                    )
                                    .content("""
                                            {
                                                "token": "",
                                                "password": "",
                                                "confirmPassword": ""
                                            }
                                            """)
                    )
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(passwordResetService);
        }

        @Test
        void shouldHandleExpiredResetToken()
                throws Exception {

            doThrow(
                    new BusinessException(
                            ErrorCode.PASSWORD_RESET_TOKEN_EXPIRED
                    )
            ).when(passwordResetService)
                    .resetPassword(any());

            ResetPasswordRequest request =
                    new ResetPasswordRequest(
                            "expired-token",
                            "NewPassword@123",
                            "NewPassword@123"
                    );

            mockMvc.perform(
                            post("/api/v1/auth/reset-password")
                                    .contentType(
                                            MediaType.APPLICATION_JSON
                                    )
                                    .content(
                                            objectMapper.writeValueAsString(
                                                    request
                                            )
                                    )
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(
                            jsonPath("$.code")
                                    .value(
                                            "PASSWORD_RESET_TOKEN_EXPIRED"
                                    )
                    );
        }

        @Test
        void shouldHandleAlreadyUsedResetToken()
                throws Exception {

            doThrow(
                    new BusinessException(
                            ErrorCode.PASSWORD_RESET_TOKEN_ALREADY_USED
                    )
            ).when(passwordResetService)
                    .resetPassword(any());

            ResetPasswordRequest request =
                    new ResetPasswordRequest(
                            "used-token",
                            "NewPassword@123",
                            "NewPassword@123"
                    );

            mockMvc.perform(
                            post("/api/v1/auth/reset-password")
                                    .contentType(
                                            MediaType.APPLICATION_JSON
                                    )
                                    .content(
                                            objectMapper.writeValueAsString(
                                                    request
                                            )
                                    )
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(
                            jsonPath("$.code")
                                    .value(
                                            "PASSWORD_RESET_TOKEN_ALREADY_USED"
                                    )
                    );
        }
    }
}