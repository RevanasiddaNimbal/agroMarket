package com.agri.market.handler;

import com.agri.market.exception.BusinessException;
import com.agri.market.exception.ErrorCode;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.core.MethodParameter;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ApplicationExceptionHandler")
class ApplicationExceptionHandlerTest {

    private final ApplicationExceptionHandler handler = new ApplicationExceptionHandler();

    @Test
    void shouldMapBusinessException() {
        var response = handler.handleBusinessException(new BusinessException(ErrorCode.USER_ALREADY_ACTIVATED, "user@mail.com"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo("USER_ALREADY_ACTIVATED");
    }

    @Test
    void shouldMapDisabledException() {
        var response = handler.handleDisabledException(new DisabledException("disabled"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody().getCode()).isEqualTo("ERR_USER_DISABLED");
    }

    @Test
    void shouldMapBadCredentialsException() {
        var response = handler.handleBadCredentialsException(new BadCredentialsException("bad"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody().getCode()).isEqualTo("BAD_CREDENTIALS");
    }

    @Test
    void shouldMapUsernameNotFoundException() {
        var response = handler.handleUsernameNotFoundException(new UsernameNotFoundException("missing"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getCode()).isEqualTo("USER_NOT_FOUND");
    }

    @Test
    void shouldMapAuthorizationDeniedException() {
        var response = handler.handleAuthorizationDeniedException(new AuthorizationDeniedException("denied"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody().getCode()).isEqualTo("ACCESS_DENIED");
    }

    @Test
    void shouldMapEntityNotFoundException() {
        var response = handler.handleEntityNotFoundException(new EntityNotFoundException("missing entity"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getCode()).isEqualTo("RESOURCE_NOT_FOUND");
    }

    @Test
    void shouldMapUnexpectedException() {
        var response = handler.handleUnexpectedException(new RuntimeException("boom"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().getCode()).isEqualTo("ERR_INTERNAL_SERVER_ERROR");
    }

    @Test
    void shouldMapValidationErrors() throws Exception {
        Method method = DummyController.class.getDeclaredMethod("handle", DummyRequest.class);
        MethodParameter parameter = new MethodParameter(method, 0);
        BindingResult bindingResult = new BeanPropertyBindingResult(new DummyRequest(), "dummyRequest");
        bindingResult.addError(new org.springframework.validation.FieldError(
                "dummyRequest",
                "name",
                "VALIDATION.NAME.BLANK"
        ));

        MethodArgumentNotValidException exception =
                new MethodArgumentNotValidException(parameter, bindingResult);

        var response = handler.handleValidationException(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getCode()).isEqualTo("VALIDATION_ERROR");
        assertThat(response.getBody().getValidationErrors()).hasSize(1);
        assertThat(response.getBody().getValidationErrors().get(0).getField()).isEqualTo("name");
    }

    private static class DummyController {
        @SuppressWarnings("unused")
        void handle(@Valid DummyRequest request) {
        }
    }

    private static class DummyRequest {
        @NotBlank
        private String name;

        @SuppressWarnings("unused")
        public String getName() {
            return name;
        }

        @SuppressWarnings("unused")
        public void setName(String name) {
            this.name = name;
        }
    }
}
