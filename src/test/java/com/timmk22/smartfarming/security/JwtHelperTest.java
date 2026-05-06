package com.timmk22.smartfarming.security;

import com.timmk22.smartfarming.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class JwtHelperTest {

    private final JwtHelper jwtHelper = new JwtHelper();

    private User createUser() {
        User user = new User();
        user.setUserId(1L);
        user.setName("Dimitar");
        user.setEmail("dimitar@test.com");
        user.setPassword("encoded-password");
        return user;
    }

    @Test
    @DisplayName("Should generate token and extract username")
    void shouldGenerateTokenAndExtractUsername() {
        User user = createUser();

        String token = jwtHelper.generateToken(user);
        String username = jwtHelper.extractUsername(token);

        assertThat(token).isNotBlank();
        assertThat(username).isEqualTo("dimitar@test.com");
    }

    @Test
    @DisplayName("Should generate token with future expiration")
    void shouldGenerateTokenWithFutureExpiration() {
        User user = createUser();

        String token = jwtHelper.generateToken(user);
        Date expiration = jwtHelper.extractExpiration(token);

        assertThat(expiration).isAfter(new Date());
    }

    @Test
    @DisplayName("Should validate token for matching user")
    void shouldValidateTokenForMatchingUser() {
        User user = createUser();

        String token = jwtHelper.generateToken(user);
        boolean valid = jwtHelper.isValid(token, user);

        assertThat(valid).isTrue();
    }

    @Test
    @DisplayName("Should invalidate token for different user")
    void shouldInvalidateTokenForDifferentUser() {
        User originalUser = createUser();

        User differentUser = new User();
        differentUser.setUserId(2L);
        differentUser.setName("Other User");
        differentUser.setEmail("other@test.com");
        differentUser.setPassword("encoded-password");

        String token = jwtHelper.generateToken(originalUser);
        boolean valid = jwtHelper.isValid(token, differentUser);

        assertThat(valid).isFalse();
    }
}