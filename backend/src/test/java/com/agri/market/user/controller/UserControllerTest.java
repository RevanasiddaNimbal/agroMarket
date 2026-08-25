package com.agri.market.user.controller;

import com.agri.market.common.handler.ApplicationExceptionHandler;
import com.agri.market.support.ChangePasswordRequestTestFactory;
import com.agri.market.support.ProfileUpdateRequestTestFactory;
import com.agri.market.support.UserTestFactory;
import com.agri.market.user.dto.UserProfileResponseDto;
import com.agri.market.user.entity.User;
import com.agri.market.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.security.Principal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserController")
class UserControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        final LocalValidatorFactoryBean validator =
                new LocalValidatorFactoryBean();

        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(userController)
                .setControllerAdvice(new ApplicationExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @AfterEach
    void tearDown() {
        org.springframework.security.core.context.SecurityContextHolder
                .clearContext();
    }

    @Nested
    @DisplayName("getCurrentUserProfile")
    class GetCurrentUserProfileTests {

        @Test
        void shouldReturnCurrentUserProfile() throws Exception {
            final User user = UserTestFactory.activeUser();

            final Principal principal =
                    new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            user.getAuthorities()
                    );

            final UserProfileResponseDto response =
                    UserProfileResponseDto.builder()
                            .id(user.getId())
                            .fullName(user.getFullName())
                            .email(user.getEmail())
                            .phoneNumber(user.getPhoneNumber())
                            .emailVerified(user.isEmailVerified())
                            .phoneVerified(user.isPhoneVerified())
                            .profilePictureUrl(user.getProfilePictureUrl())
                            .addresses(List.of())
                            .build();

            when(userService.getCurrentUserProfile(
                    "revanasidda@mail.com"
            )).thenReturn(response);

            mockMvc.perform(
                            get("/api/v1/users/me")
                                    .principal(principal)
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(user.getId()))
                    .andExpect(jsonPath("$.fullName")
                            .value(user.getFullName()))
                    .andExpect(jsonPath("$.email")
                            .value(user.getEmail()))
                    .andExpect(jsonPath("$.phoneNumber")
                            .value(user.getPhoneNumber()))
                    .andExpect(jsonPath("$.emailVerified")
                            .value(user.isEmailVerified()))
                    .andExpect(jsonPath("$.phoneVerified")
                            .value(user.isPhoneVerified()))
                    .andExpect(jsonPath("$.profilePictureUrl")
                            .value(user.getProfilePictureUrl()))
                    .andExpect(jsonPath("$.addresses").isArray());

            verify(userService)
                    .getCurrentUserProfile("revanasidda@mail.com");
        }

        @Test
        void shouldReturnEmptyAddressListWhenUserHasNoAddresses()
                throws Exception {

            final User user = UserTestFactory.activeUser();

            final Principal principal =
                    new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            user.getAuthorities()
                    );

            final UserProfileResponseDto response =
                    UserProfileResponseDto.builder()
                            .id(user.getId())
                            .fullName(user.getFullName())
                            .email(user.getEmail())
                            .phoneNumber(user.getPhoneNumber())
                            .emailVerified(user.isEmailVerified())
                            .phoneVerified(user.isPhoneVerified())
                            .profilePictureUrl(user.getProfilePictureUrl())
                            .addresses(List.of())
                            .build();

            when(userService.getCurrentUserProfile(
                    "revanasidda@mail.com"
            )).thenReturn(response);

            mockMvc.perform(
                            get("/api/v1/users/me")
                                    .principal(principal)
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.addresses").isArray())
                    .andExpect(jsonPath("$.addresses").isEmpty());
        }
    }

    @Nested
    @DisplayName("updateProfile")
    class UpdateProfileTests {

        @Test
        void shouldUpdateProfileForAuthenticatedUser()
                throws Exception {

            final User user = UserTestFactory.activeUser();

            final Principal principal =
                    new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            user.getAuthorities()
                    );

            mockMvc.perform(
                            patch("/api/v1/users/me")
                                    .principal(principal)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(
                                            objectMapper.writeValueAsString(
                                                    ProfileUpdateRequestTestFactory
                                                            .validRequest()
                                            )
                                    )
                    )
                    .andExpect(status().isNoContent());

            verify(userService)
                    .updateProfileInfo(
                            any(),
                            eq("revanasidda@mail.com")
                    );
        }

        @Test
        void shouldRejectInvalidProfilePayload()
                throws Exception {

            final User user = UserTestFactory.activeUser();

            final Principal principal =
                    new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            user.getAuthorities()
                    );

            mockMvc.perform(
                            patch("/api/v1/users/me")
                                    .principal(principal)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content("{\"fullName\":\"\"}")
                    )
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(userService);
        }
    }

    @Nested
    @DisplayName("changePassword")
    class ChangePasswordTests {

        @Test
        void shouldChangePasswordForAuthenticatedUser()
                throws Exception {

            final User user = UserTestFactory.activeUser();

            final Principal principal =
                    new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            user.getAuthorities()
                    );

            mockMvc.perform(
                            patch("/api/v1/users/me/password")
                                    .principal(principal)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(
                                            objectMapper.writeValueAsString(
                                                    ChangePasswordRequestTestFactory
                                                            .validRequest()
                                            )
                                    )
                    )
                    .andExpect(status().isNoContent());

            verify(userService)
                    .changePassword(
                            any(),
                            eq("revanasidda@mail.com")
                    );
        }
    }

    @Nested
    @DisplayName("deactivate/reactivate/delete")
    class AccountActionsTests {

        @Test
        void shouldDeactivateAccount() throws Exception {

            final User user = UserTestFactory.activeUser();

            final Principal principal =
                    new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            user.getAuthorities()
                    );

            mockMvc.perform(
                            patch("/api/v1/users/me/deactivate")
                                    .principal(principal)
                    )
                    .andExpect(status().isNoContent());

            verify(userService)
                    .deactivateAccount("revanasidda@mail.com");
        }

        @Test
        void shouldReactivateAccount() throws Exception {

            final User user = UserTestFactory.activeUser();

            final Principal principal =
                    new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            user.getAuthorities()
                    );

            mockMvc.perform(
                            patch("/api/v1/users/me/reactivate")
                                    .principal(principal)
                    )
                    .andExpect(status().isNoContent());

            verify(userService)
                    .reactivateAccount("revanasidda@mail.com");
        }

        @Test
        void shouldDeleteAccount() throws Exception {

            final User user = UserTestFactory.activeUser();

            final Principal principal =
                    new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            user.getAuthorities()
                    );

            mockMvc.perform(
                            delete("/api/v1/users/me")
                                    .principal(principal)
                    )
                    .andExpect(status().isNoContent());

            verify(userService)
                    .deleteAccount("revanasidda@mail.com");
        }
    }
}