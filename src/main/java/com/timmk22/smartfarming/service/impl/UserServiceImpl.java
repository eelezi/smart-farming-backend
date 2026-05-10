package com.timmk22.smartfarming.service.impl;

import com.timmk22.smartfarming.dto.request.CreateUserRequest;
import com.timmk22.smartfarming.dto.request.LoginUserRequest;
import com.timmk22.smartfarming.dto.response.DisplayUserResponse;
import com.timmk22.smartfarming.dto.response.LoginResponse;
import com.timmk22.smartfarming.model.User;
import com.timmk22.smartfarming.repository.UserRepository;
import com.timmk22.smartfarming.security.JwtHelper;
import com.timmk22.smartfarming.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtHelper jwtHelper;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtHelper jwtHelper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtHelper = jwtHelper;
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public DisplayUserResponse register(CreateUserRequest dto) {
        if (userRepository.existsByEmail(dto.email())) {
            throw new IllegalArgumentException("Email already in use.");
        }
        User user = new User();
        user.setName(dto.name());
        user.setEmail(dto.email());
        user.setPassword(passwordEncoder.encode(dto.password()));
        User saved = userRepository.save(user);
        return new DisplayUserResponse(saved.getUserId(), saved.getName(), saved.getEmail());
    }

    @Override
    public LoginResponse login(LoginUserRequest dto) {
        User user = userRepository.findByEmail(dto.email())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));

        if (!passwordEncoder.matches(dto.password(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password.");
        }

        String token = jwtHelper.generateToken(user);
        return new LoginResponse(token, user.getUserId(), user.getName(), user.getEmail());
    }

    @Override
    public DisplayUserResponse getMe(User user) {
        return userRepository.findByEmail(user.getEmail())
                .map(u -> new DisplayUserResponse(u.getUserId(), u.getName(), u.getEmail()))
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
    }
}