package com.agri.market.auth.controller;

import com.agri.market.auth.dto.AuthenticationResponse;
import com.agri.market.auth.dto.RegistrationRequest;
import com.agri.market.auth.dto.RegistrationResponse;
import com.agri.market.auth.service.AuthenticationService;
import com.agri.market.exception.BusinessException;
import com.agri.market.exception.ErrorCode;
import com.agri.market.handler.ApplicationExceptionHandler;
import com.agri.market.security.client.ClientInfoResolver;
import com.agri.market.support.AuthenticationRequestTestFactory;
import com.agri.market.support.RefreshTokenRequestTestFactory;
import com.agri.market.support.RegistrationRequestTestFactory;
import com.agri.market.support.UserTestFactory;
import com.agri.market.user.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorFactory;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
@DisplayName("AuthenticationController")
class AuthenticationControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Mock
    private ClientInfoResolver clientInfoResolver;

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private AuthenticationController authenticationController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {

        LocalValidatorFactoryBean validator =
                new LocalValidatorFactoryBean();

        validator.setConstraintValidatorFactory(
                new ConstraintValidatorFactory() {

                    @Override
                    public <T extends ConstraintValidator<?, ?>> T getInstance(
                            Class<T> key
                    ) {
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
                .standaloneSetup(authenticationController)
                .setControllerAdvice(
                        new ApplicationExceptionHandler()
                )
                .setValidator(validator)
                .build();
    }

    @AfterEach
    void tearDown() {
        org.springframework.security.core.context
                .SecurityContextHolder.clearContext();
    }

    @Nested
    @DisplayName("register")
    class RegisterTests {

        @Test
        void shouldReturnCreatedResponse() throws Exception {

            RegistrationResponse response =
                    RegistrationResponse.builder()
                            .message(
                                    "Registration successful. " +
                                            "Please verify your email address " +
                                            "before logging in."
                            )
                            .build();

            when(authenticationService.register(
                    any(RegistrationRequest.class),
                    any()
            )).thenReturn(response);

            mockMvc.perform(
                            post("/api/v1/auth/register")
                                    .header(
                                            "User-Agent",
                                            "Test Device"
                                    )
                                    .contentType(
                                            MediaType.APPLICATION_JSON
                                    )
                                    .content(
                                            objectMapper.writeValueAsString(
                                                    RegistrationRequestTestFactory
                                                            .validRequest()
                                            )
                                    )
                    )
                    .andExpect(status().isCreated())
                    .andExpect(
                            jsonPath("$.message")
                                    .value(
                                            "Registration successful. " +
                                                    "Please verify your email address " +
                                                    "before logging in."
                                    )
                    );

            verify(authenticationService)
                    .register(
                            any(RegistrationRequest.class),
                            any()
                    );
        }

        @Test
        void shouldRejectInvalidRegistrationBody()
                throws Exception {

            mockMvc.perform(
                            post("/api/v1/auth/register")
                                    .contentType(
                                            MediaType.APPLICATION_JSON
                                    )
                                    .content("""
                                            {
                                                "email": "bad-email",
                                                "password": "short"
                                            }
                                            """)
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(
                            jsonPath("$.code")
                                    .value("VALIDATION_ERROR")
                    )
                    .andExpect(
                            jsonPath("$.validationErrors")
                                    .isArray()
                    );

            verifyNoInteractions(authenticationService);
        }

        @Test
        void shouldRejectEmptyRequestBody()
                throws Exception {

            mockMvc.perform(
                            post("/api/v1/auth/register")
                                    .contentType(
                                            MediaType.APPLICATION_JSON
                                    )
                                    .content("{}")
                    )
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(authenticationService);
        }

        @Test
        void shouldHandleBusinessException()
                throws Exception {

            when(authenticationService.register(
                    any(),
                    any()
            )).thenThrow(
                    new BusinessException(
                            ErrorCode.EMAIL_ALREADY_EXISTS
                    )
            );

            mockMvc.perform(
                            post("/api/v1/auth/register")
                                    .contentType(
                                            MediaType.APPLICATION_JSON
                                    )
                                    .content(
                                            objectMapper.writeValueAsString(
                                                    RegistrationRequestTestFactory
                                                            .validRequest()
                                            )
                                    )
                    )
                    .andExpect(status().isConflict())
                    .andExpect(
                            jsonPath("$.code")
                                    .value("EMAIL_ALREADY_EXISTS")
                    );

            verify(authenticationService)
                    .register(any(), any());
        }
    }


    @Nested
    @DisplayName("login")
    class LoginTests {

        @Test
        void shouldAuthenticateAndReturnTokens()
                throws Exception {

            AuthenticationResponse response =
                    AuthenticationResponse.builder()
                            .accessToken("access-token")
                            .refreshToken("refresh-token")
                            .tokenType("Bearer")
                            .build();

            when(authenticationService.login(
                    any(),
                    any()
            )).thenReturn(response);

            mockMvc.perform(
                            post("/api/v1/auth/login")
                                    .header(
                                            "User-Agent",
                                            "Device A"
                                    )
                                    .contentType(
                                            MediaType.APPLICATION_JSON
                                    )
                                    .content(
                                            objectMapper.writeValueAsString(
                                                    AuthenticationRequestTestFactory
                                                            .validRequest()
                                            )
                                    )
                    )
                    .andExpect(status().isOk())
                    .andExpect(
                            jsonPath("$.access_token")
                                    .value("access-token")
                    )
                    .andExpect(
                            jsonPath("$.refresh_token")
                                    .value("refresh-token")
                    )
                    .andExpect(
                            jsonPath("$.token_type")
                                    .value("Bearer")
                    );

            verify(authenticationService)
                    .login(any(), any());
        }

        @Test
        void shouldRejectInvalidLoginRequest()
                throws Exception {

            mockMvc.perform(
                            post("/api/v1/auth/login")
                                    .contentType(
                                            MediaType.APPLICATION_JSON
                                    )
                                    .content("""
                                            {
                                                "email": "invalid",
                                                "password": ""
                                            }
                                            """)
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(
                            jsonPath("$.code")
                                    .value("VALIDATION_ERROR")
                    );

            verifyNoInteractions(authenticationService);
        }

        @Test
        void shouldHandleInvalidCredentials()
                throws Exception {

            when(authenticationService.login(
                    any(),
                    any()
            )).thenThrow(
                    new BusinessException(
                            ErrorCode.BAD_CREDENTIALS
                    )
            );

            mockMvc.perform(
                            post("/api/v1/auth/login")
                                    .contentType(
                                            MediaType.APPLICATION_JSON
                                    )
                                    .content(
                                            objectMapper.writeValueAsString(
                                                    AuthenticationRequestTestFactory
                                                            .validRequest()
                                            )
                                    )
                    )
                    .andExpect(status().isUnauthorized())
                    .andExpect(
                            jsonPath("$.code")
                                    .value("BAD_CREDENTIALS")
                    );
        }
    }

    @Nested
    @DisplayName("refreshToken")
    class RefreshTokenTests {

        @Test
        void shouldRefreshTokens()
                throws Exception {

            AuthenticationResponse response =
                    AuthenticationResponse.builder()
                            .accessToken("new-access")
                            .refreshToken("new-refresh")
                            .tokenType("Bearer")
                            .build();

            when(authenticationService.refreshToken(
                    any()
            )).thenReturn(response);

            mockMvc.perform(
                            post("/api/v1/auth/refresh-token")
                                    .contentType(
                                            MediaType.APPLICATION_JSON
                                    )
                                    .content(
                                            objectMapper.writeValueAsString(
                                                    RefreshTokenRequestTestFactory
                                                            .validRequest()
                                            )
                                    )
                    )
                    .andExpect(status().isOk())
                    .andExpect(
                            jsonPath("$.access_token")
                                    .value("new-access")
                    )
                    .andExpect(
                            jsonPath("$.refresh_token")
                                    .value("new-refresh")
                    );

            verify(authenticationService)
                    .refreshToken(any());
        }

        @Test
        void shouldRejectBlankRefreshToken()
                throws Exception {

            mockMvc.perform(
                            post("/api/v1/auth/refresh-token")
                                    .contentType(
                                            MediaType.APPLICATION_JSON
                                    )
                                    .content(
                                            "{\"refreshToken\":\"\"}"
                                    )
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(
                            jsonPath("$.code")
                                    .value("VALIDATION_ERROR")
                    );

            verifyNoInteractions(authenticationService);
        }

        @Test
        void shouldRejectInvalidRefreshToken()
                throws Exception {

            when(authenticationService.refreshToken(any()))
                    .thenThrow(
                            new BusinessException(
                                    ErrorCode.INVALID_REFRESH_TOKEN
                            )
                    );

            mockMvc.perform(
                            post("/api/v1/auth/refresh-token")
                                    .contentType(
                                            MediaType.APPLICATION_JSON
                                    )
                                    .content(
                                            objectMapper.writeValueAsString(
                                                    RefreshTokenRequestTestFactory
                                                            .validRequest()
                                            )
                                    )
                    )
                    .andExpect(status().isUnauthorized())
                    .andExpect(
                            jsonPath("$.code")
                                    .value("INVALID_REFRESH_TOKEN")
                    );
        }
    }


    @Nested
    @DisplayName("logout")
    class LogoutTests {

        @Test
        void shouldLogoutCurrentSession()
                throws Exception {

            mockMvc.perform(
                            post("/api/v1/auth/logout")
                                    .contentType(
                                            MediaType.APPLICATION_JSON
                                    )
                                    .content(
                                            objectMapper.writeValueAsString(
                                                    RefreshTokenRequestTestFactory
                                                            .validRequest()
                                            )
                                    )
                    )
                    .andExpect(status().isNoContent());

            verify(authenticationService)
                    .logout("refresh-token");
        }

        @Test
        void shouldRejectBlankRefreshToken()
                throws Exception {

            mockMvc.perform(
                            post("/api/v1/auth/logout")
                                    .contentType(
                                            MediaType.APPLICATION_JSON
                                    )
                                    .content(
                                            "{\"refreshToken\":\"\"}"
                                    )
                    )
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(authenticationService);
        }

        @Test
        void shouldHandleInvalidRefreshToken()
                throws Exception {

            doThrow(
                    new BusinessException(
                            ErrorCode.INVALID_REFRESH_TOKEN
                    )
            ).when(authenticationService)
                    .logout("refresh-token");

            mockMvc.perform(
                            post("/api/v1/auth/logout")
                                    .contentType(
                                            MediaType.APPLICATION_JSON
                                    )
                                    .content(
                                            objectMapper.writeValueAsString(
                                                    RefreshTokenRequestTestFactory
                                                            .validRequest()
                                            )
                                    )
                    )
                    .andExpect(status().isUnauthorized())
                    .andExpect(
                            jsonPath("$.code")
                                    .value("INVALID_REFRESH_TOKEN")
                    );
        }
    }

    @Nested
    @DisplayName("logoutAll")
    class LogoutAllTests {

        @Test
        void shouldLogoutAllSessionsForAuthenticatedUser() {

            User user = UserTestFactory.activeUser();

            authenticationController.logoutAll(user);

            verify(authenticationService)
                    .logoutAll("user-id");
        }

        @Test
        void shouldCallLogoutAllWithCorrectUserId() {

            User user = UserTestFactory.activeUser();

            authenticationController.logoutAll(user);

            ArgumentCaptor<String> captor =
                    ArgumentCaptor.forClass(String.class);

            verify(authenticationService)
                    .logoutAll(captor.capture());

            Assertions.assertEquals(
                    "user-id",
                    captor.getValue()
            );
        }
    }
}