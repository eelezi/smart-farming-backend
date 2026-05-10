package com.timmk22.smartfarming.service;

import com.timmk22.smartfarming.dto.request.CreateUserRequest;
import com.timmk22.smartfarming.dto.request.LoginUserRequest;
import com.timmk22.smartfarming.dto.response.DisplayUserResponse;
import com.timmk22.smartfarming.dto.response.LoginResponse;
import com.timmk22.smartfarming.model.User;

import java.util.Optional;

public interface UserService {

    /**
     * Finds a user by their email address.
     *
     * @param email the email address to search for
     * @return an {@link Optional} containing the user if found, or empty if not
     */
    Optional<User> findByEmail(String email);

    /**
     * Registers a new user by saving their details to the database
     * with an encoded password.
     *
     * @param dto the registration request containing name, email, and password
     * @return the saved user as a {@link DisplayUserResponse}
     * @throws IllegalArgumentException if the email is already in use
     */
    DisplayUserResponse register(CreateUserRequest dto);

    /**
     * Authenticates a user with their email and password,
     * and returns a JWT token on success.
     *
     * @param dto the login request containing email and password
     * @return a {@link LoginResponse} containing the JWT token and user details
     * @throws IllegalArgumentException if the email or password is incorrect
     */
    LoginResponse login(LoginUserRequest dto);

    /**
     * Returns the profile of the currently authenticated user.
     *
     * @param user the authenticated user injected from the security context
     * @return the user's details as a {@link DisplayUserResponse}
     * @throws IllegalArgumentException if the user is not found
     */
    DisplayUserResponse getMe(User user);
}