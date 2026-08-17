package com.agri.market.user.controller;

import com.agri.market.handler.ApplicationExceptionHandler;
import com.agri.market.support.ChangePasswordRequestTestFactory;
import com.agri.market.support.ProfileUpdateRequestTestFactory;
import com.agri.market.support.UserTestFactory;
import com.agri.market.user.entity.User;
import com.agri.market.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserController")
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new ApplicationExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @AfterEach
    void tearDown() {
        org.springframework.security.core.context.SecurityContextHolder.clearContext();
    }

    @Nested
    @DisplayName("updateProfile")
    class UpdateProfileTests {

        @Test
        void shouldUpdateProfileForAuthenticatedUser() throws Exception {
            User user = UserTestFactory.activeUser();
            Principal principal = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

            mockMvc.perform(patch("/api/v1/users/me")
                            .principal(principal)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(ProfileUpdateRequestTestFactory.validRequest())))
                    .andExpect(status().isNoContent());

            verify(userService).updateProfileInfo(any(), eq("revanasidda@mail.com"));
        }

        @Test
        void shouldRejectInvalidProfilePayload() throws Exception {
            User user = UserTestFactory.activeUser();
            Principal principal = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

            mockMvc.perform(patch("/api/v1/users/me")
                            .principal(principal)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"fullName\":\"\"}"))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(userService);
        }
    }

    @Nested
    @DisplayName("changePassword")
    class ChangePasswordTests {

        @Test
        void shouldChangePasswordForAuthenticatedUser() throws Exception {
            User user = UserTestFactory.activeUser();
            Principal principal = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

            mockMvc.perform(patch("/api/v1/users/me/password")
                            .principal(principal)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(ChangePasswordRequestTestFactory.validRequest())))
                    .andExpect(status().isNoContent());

            verify(userService).changePassword(any(), eq("revanasidda@mail.com"));
        }
    }

    @Nested
    @DisplayName("deactivate/reactivate/delete")
    class AccountActionsTests {

        @Test
        void shouldDeactivateAccount() throws Exception {
            User user = UserTestFactory.activeUser();
            Principal principal = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

            mockMvc.perform(patch("/api/v1/users/me/deactivate").principal(principal))
                    .andExpect(status().isNoContent());

            verify(userService).deactivateAccount("revanasidda@mail.com");
        }

        @Test
        void shouldReactivateAccount() throws Exception {
            User user = UserTestFactory.activeUser();
            Principal principal = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

            mockMvc.perform(patch("/api/v1/users/me/reactivate").principal(principal))
                    .andExpect(status().isNoContent());

            verify(userService).reactivateAccount("revanasidda@mail.com");
        }

        @Test
        void shouldDeleteAccount() throws Exception {
            User user = UserTestFactory.activeUser();
            Principal principal = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

            mockMvc.perform(delete("/api/v1/users/me").principal(principal))
                    .andExpect(status().isNoContent());

            verify(userService).deleteAccount("revanasidda@mail.com");
        }
    }
}

