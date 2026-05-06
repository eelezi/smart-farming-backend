package com.timmk22.smartfarming.service;

import com.timmk22.smartfarming.dto.request.CreateUserRequest;
import com.timmk22.smartfarming.dto.request.LoginUserRequest;
import com.timmk22.smartfarming.dto.response.DisplayUserResponse;
import com.timmk22.smartfarming.dto.response.LoginResponse;
import com.timmk22.smartfarming.model.User;
import com.timmk22.smartfarming.repository.UserRepository;
import com.timmk22.smartfarming.security.JwtHelper;
import com.timmk22.smartfarming.service.impl.UserServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtHelper jwtHelper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @DisplayName("Should register user successfully")
    void shouldRegisterUserSuccessfully() {
        CreateUserRequest request = new CreateUserRequest("Dimitar", "dimitar@test.com", "123456");

        User savedUser = new User();
        savedUser.setUserId(1L);
        savedUser.setName("Dimitar");
        savedUser.setEmail("dimitar@test.com");
        savedUser.setPassword("encoded-password");

        when(userRepository.existsByEmail("dimitar@test.com")).thenReturn(false);
        when(passwordEncoder.encode("123456")).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        DisplayUserResponse result = userService.register(request);

        assertThat(result.userId()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Dimitar");
        assertThat(result.email()).isEqualTo("dimitar@test.com");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertThat(userCaptor.getValue().getPassword()).isEqualTo("encoded-password");
    }

    @Test
    @DisplayName("Should throw when email already exists on register")
    void shouldThrowWhenEmailAlreadyExistsOnRegister() {
        CreateUserRequest request = new CreateUserRequest("Dimitar", "dimitar@test.com", "123456");

        when(userRepository.existsByEmail("dimitar@test.com")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> userService.register(request)
        );

        assertThat(ex.getMessage()).isEqualTo("Email already in use.");
        verify(userRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    @DisplayName("Should login successfully with valid credentials")
    void shouldLoginSuccessfullyWithValidCredentials() {
        LoginUserRequest request = new LoginUserRequest("dimitar@test.com", "123456");

        User user = new User();
        user.setUserId(1L);
        user.setName("Dimitar");
        user.setEmail("dimitar@test.com");
        user.setPassword("encoded-password");

        when(userRepository.findByEmail("dimitar@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("123456", "encoded-password")).thenReturn(true);
        when(jwtHelper.generateToken(user)).thenReturn("jwt-token");

        LoginResponse result = userService.login(request);

        assertThat(result.token()).isEqualTo("jwt-token");
        assertThat(result.userId()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Dimitar");
        assertThat(result.email()).isEqualTo("dimitar@test.com");
    }

    @Test
    @DisplayName("Should throw when login email is not found")
    void shouldThrowWhenLoginEmailIsNotFound() {
        LoginUserRequest request = new LoginUserRequest("missing@test.com", "123456");

        when(userRepository.findByEmail("missing@test.com")).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> userService.login(request)
        );

        assertThat(ex.getMessage()).isEqualTo("Invalid email or password.");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtHelper, never()).generateToken(any());
    }

    @Test
    @DisplayName("Should throw when login password is invalid")
    void shouldThrowWhenLoginPasswordIsInvalid() {
        LoginUserRequest request = new LoginUserRequest("dimitar@test.com", "wrong-password");

        User user = new User();
        user.setUserId(1L);
        user.setName("Dimitar");
        user.setEmail("dimitar@test.com");
        user.setPassword("encoded-password");

        when(userRepository.findByEmail("dimitar@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong-password", "encoded-password")).thenReturn(false);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> userService.login(request)
        );

        assertThat(ex.getMessage()).isEqualTo("Invalid email or password.");
        verify(jwtHelper, never()).generateToken(any());
    }

    @Test
    @DisplayName("Should return user by email in findByEmail")
    void shouldReturnUserByEmailInFindByEmail() {
        User user = new User();
        user.setUserId(1L);
        user.setName("Dimitar");
        user.setEmail("dimitar@test.com");

        when(userRepository.findByEmail("dimitar@test.com")).thenReturn(Optional.of(user));

        Optional<User> result = userService.findByEmail("dimitar@test.com");

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("dimitar@test.com");
    }

    @Test
    @DisplayName("Should return current user in getMe")
    void shouldReturnCurrentUserInGetMe() {
        User authenticatedUser = new User();
        authenticatedUser.setEmail("dimitar@test.com");

        User storedUser = new User();
        storedUser.setUserId(1L);
        storedUser.setName("Dimitar");
        storedUser.setEmail("dimitar@test.com");

        when(userRepository.findByEmail("dimitar@test.com")).thenReturn(Optional.of(storedUser));

        DisplayUserResponse result = userService.getMe(authenticatedUser);

        assertThat(result.userId()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Dimitar");
        assertThat(result.email()).isEqualTo("dimitar@test.com");
    }

    @Test
    @DisplayName("Should throw when current user is not found in getMe")
    void shouldThrowWhenCurrentUserIsNotFoundInGetMe() {
        User authenticatedUser = new User();
        authenticatedUser.setEmail("missing@test.com");

        when(userRepository.findByEmail("missing@test.com")).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> userService.getMe(authenticatedUser)
        );

        assertThat(ex.getMessage()).isEqualTo("User not found.");
    }
}