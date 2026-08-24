package com.agri.market.auth.controller;

import com.agri.market.common.exception.BusinessException;
import com.agri.market.common.exception.ErrorCode;
import com.agri.market.common.handler.ApplicationExceptionHandler;
import com.agri.market.email.controller.EmailVerificationController;
import com.agri.market.email.dto.EmailVerificationRequest;
import com.agri.market.email.service.EmailVerificationService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("EmailVerificationController")
class EmailVerificationControllerTest {

    private final ObjectMapper objectMapper =
            new ObjectMapper();

    @Mock
    private EmailVerificationService emailVerificationService;

    @InjectMocks
    private EmailVerificationController emailVerificationController;

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
                            if (key.equals(
                                    com.agri.market.validation.validator
                                            .EmailDomainValidator.class
                            )) {
                                return key.cast(
                                        new com.agri.market.validation.validator
                                                .EmailDomainValidator(
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
                            ConstraintValidator<?, ?> instance
                    ) {
                    }
                }
        );

        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(emailVerificationController)
                .setControllerAdvice(
                        new ApplicationExceptionHandler()
                )
                .setValidator(validator)
                .build();
    }


    @Nested
    @DisplayName("verifyEmail")
    class VerifyEmailTests {

        @Test
        void shouldVerifyEmail()
                throws Exception {

            mockMvc.perform(
                            get("/api/v1/auth/verify-email")
                                    .param(
                                            "token",
                                            "valid-token"
                                    )
                    )
                    .andExpect(status().isOk());

            verify(emailVerificationService)
                    .verifyEmail("valid-token");
        }

        @Test
        void shouldRejectMissingToken()
                throws Exception {

            mockMvc.perform(
                            get("/api/v1/auth/verify-email")
                    )
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(emailVerificationService);
        }

        @Test
        void shouldHandleInvalidVerificationToken()
                throws Exception {

            doThrow(
                    new BusinessException(
                            ErrorCode.INVALID_VERIFICATION_TOKEN
                    )
            ).when(emailVerificationService)
                    .verifyEmail("invalid-token");

            mockMvc.perform(
                            get("/api/v1/auth/verify-email")
                                    .param(
                                            "token",
                                            "invalid-token"
                                    )
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(
                            jsonPath("$.code")
                                    .value(
                                            "INVALID_VERIFICATION_TOKEN"
                                    )
                    );
        }

        @Test
        void shouldHandleExpiredVerificationToken()
                throws Exception {

            doThrow(
                    new BusinessException(
                            ErrorCode.VERIFICATION_TOKEN_EXPIRED
                    )
            ).when(emailVerificationService)
                    .verifyEmail("expired-token");

            mockMvc.perform(
                            get("/api/v1/auth/verify-email")
                                    .param(
                                            "token",
                                            "expired-token"
                                    )
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(
                            jsonPath("$.code")
                                    .value(
                                            "VERIFICATION_TOKEN_EXPIRED"
                                    )
                    );
        }

        @Test
        void shouldHandleAlreadyUsedToken()
                throws Exception {

            doThrow(
                    new BusinessException(
                            ErrorCode.VERIFICATION_TOKEN_ALREADY_USED
                    )
            ).when(emailVerificationService)
                    .verifyEmail("used-token");

            mockMvc.perform(
                            get("/api/v1/auth/verify-email")
                                    .param(
                                            "token",
                                            "used-token"
                                    )
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(
                            jsonPath("$.code")
                                    .value(
                                            "VERIFICATION_TOKEN_ALREADY_USED"
                                    )
                    );
        }
    }


    @Nested
    @DisplayName("resendVerificationEmail")
    class ResendVerificationEmailTests {

        @Test
        void shouldResendVerificationEmail()
                throws Exception {

            EmailVerificationRequest request =
                    new EmailVerificationRequest(
                            "user@example.com"
                    );

            mockMvc.perform(
                            post(
                                    "/api/v1/auth/" +
                                            "resend-verification-email"
                            )
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

            verify(emailVerificationService)
                    .resendVerificationEmail(any());
        }

        @Test
        void shouldRejectInvalidEmail()
                throws Exception {

            mockMvc.perform(
                            post(
                                    "/api/v1/auth/" +
                                            "resend-verification-email"
                            )
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

            verifyNoInteractions(emailVerificationService);
        }

        @Test
        void shouldProcessUnknownEmailWithoutExposingAccount()
                throws Exception {

            doNothing()
                    .when(emailVerificationService)
                    .resendVerificationEmail(any());

            EmailVerificationRequest request =
                    new EmailVerificationRequest(
                            "unknown@example.com"
                    );

            mockMvc.perform(
                            post(
                                    "/api/v1/auth/" +
                                            "resend-verification-email"
                            )
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

            verify(emailVerificationService)
                    .resendVerificationEmail(any());
        }
    }
}