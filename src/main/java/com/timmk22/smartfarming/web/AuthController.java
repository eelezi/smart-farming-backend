package com.timmk22.smartfarming.web;

import com.timmk22.smartfarming.dto.request.CreateUserRequest;
import com.timmk22.smartfarming.dto.request.LoginUserRequest;
import com.timmk22.smartfarming.dto.response.DisplayUserResponse;
import com.timmk22.smartfarming.dto.response.LoginResponse;
import com.timmk22.smartfarming.model.User;
import com.timmk22.smartfarming.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<DisplayUserResponse> register(@Valid @RequestBody CreateUserRequest createUserDto) {
        return ResponseEntity.ok(userService.register(createUserDto));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginUserRequest loginUserDto) {
        return ResponseEntity.ok(userService.login(loginUserDto));
    }

    @GetMapping("/me")
    public ResponseEntity<DisplayUserResponse> getCurrentUser(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userService.getMe(user));
    }
}
