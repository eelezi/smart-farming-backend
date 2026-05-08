package com.timmk22.smartfarming.controller;

import com.timmk22.smartfarming.dto.request.CreateUserRequest;
import com.timmk22.smartfarming.dto.request.LoginUserRequest;
import com.timmk22.smartfarming.dto.response.DisplayUserResponse;
import com.timmk22.smartfarming.dto.response.LoginResponse;
import com.timmk22.smartfarming.service.UserService;
import com.timmk22.smartfarming.web.AuthController;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @Test
    void registerShouldReturnUserWhenRequestIsValid() {
        UserService userService = mock(UserService.class);
        AuthController controller = new AuthController(userService);

        CreateUserRequest request = new CreateUserRequest(
                "Dimitar",
                "dimitar@example.com",
                "password123"
        );

        DisplayUserResponse response = new DisplayUserResponse(
                1L,
                "Dimitar",
                "dimitar@example.com"
        );

        when(userService.register(request)).thenReturn(response);

        ResponseEntity<DisplayUserResponse> result = controller.register(request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().userId()).isEqualTo(1L);
        assertThat(result.getBody().name()).isEqualTo("Dimitar");
        assertThat(result.getBody().email()).isEqualTo("dimitar@example.com");

        verify(userService, times(1)).register(request);
    }

    @Test
    void loginShouldReturnTokenWhenCredentialsAreValid() {
        UserService userService = mock(UserService.class);
        AuthController controller = new AuthController(userService);

        LoginUserRequest request = new LoginUserRequest(
                "dimitar@example.com",
                "password123"
        );

        LoginResponse response = new LoginResponse(
                "jwt-token-123",
                1L,
                "Dimitar",
                "dimitar@example.com"
        );

        when(userService.login(request)).thenReturn(response);

        ResponseEntity<LoginResponse> result = controller.login(request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().token()).isEqualTo("jwt-token-123");
        assertThat(result.getBody().type()).isEqualTo("Bearer");
        assertThat(result.getBody().userId()).isEqualTo(1L);
        assertThat(result.getBody().name()).isEqualTo("Dimitar");
        assertThat(result.getBody().email()).isEqualTo("dimitar@example.com");

        verify(userService, times(1)).login(request);
    }

    @Test
    void loginShouldThrowExceptionWhenServiceRejectsCredentials() {
        UserService userService = mock(UserService.class);
        AuthController controller = new AuthController(userService);

        LoginUserRequest request = new LoginUserRequest(
                "dimitar@example.com",
                "wrongpassword"
        );

        when(userService.login(request))
                .thenThrow(new IllegalArgumentException("Invalid email or password."));

        org.junit.jupiter.api.Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> controller.login(request)
        );

        verify(userService, times(1)).login(request);
    }
}