package com.timmk22.smartfarming.security;

import com.timmk22.smartfarming.model.User;
import com.timmk22.smartfarming.service.UserService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class JwtFilterTest {

    private final JwtHelper jwtHelper = mock(JwtHelper.class);
    private final UserService userService = mock(UserService.class);
    private final HandlerExceptionResolver handlerExceptionResolver = mock(HandlerExceptionResolver.class);

    private final JwtFilter jwtFilter = new JwtFilter(jwtHelper, userService, handlerExceptionResolver);

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Should continue filter chain when authorization header is missing")
    void shouldContinueFilterChainWhenAuthorizationHeaderIsMissing() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        jwtFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtHelper, userService, handlerExceptionResolver);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("Should continue filter chain when header does not start with Bearer")
    void shouldContinueFilterChainWhenHeaderDoesNotStartWithBearer() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(JwtConstants.HEADER, "Basic abc123");

        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        jwtFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtHelper, userService, handlerExceptionResolver);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("Should set authentication when token is valid")
    void shouldSetAuthenticationWhenTokenIsValid() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(JwtConstants.HEADER, JwtConstants.TOKEN_PREFIX + "valid-token");

        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        User user = new User();
        user.setUserId(1L);
        user.setName("Dimitar");
        user.setEmail("dimitar@test.com");
        user.setPassword("encoded-password");

        when(jwtHelper.extractUsername("valid-token")).thenReturn("dimitar@test.com");
        when(userService.findByEmail("dimitar@test.com")).thenReturn(Optional.of(user));
        when(jwtHelper.isValid("valid-token", user)).thenReturn(true);

        jwtFilter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).isEqualTo(user);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Should continue filter chain without authentication when username is null")
    void shouldContinueFilterChainWithoutAuthenticationWhenUsernameIsNull() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(JwtConstants.HEADER, JwtConstants.TOKEN_PREFIX + "token");

        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        when(jwtHelper.extractUsername("token")).thenReturn(null);

        jwtFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(userService, never()).findByEmail(anyString());
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("Should continue filter chain without authentication when user is not found")
    void shouldContinueFilterChainWithoutAuthenticationWhenUserIsNotFound() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(JwtConstants.HEADER, JwtConstants.TOKEN_PREFIX + "token");

        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        when(jwtHelper.extractUsername("token")).thenReturn("missing@test.com");
        when(userService.findByEmail("missing@test.com")).thenReturn(Optional.empty());

        jwtFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(jwtHelper, never()).isValid(anyString(), any());
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("Should continue filter chain without authentication when token is invalid")
    void shouldContinueFilterChainWithoutAuthenticationWhenTokenIsInvalid() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(JwtConstants.HEADER, JwtConstants.TOKEN_PREFIX + "invalid-token");

        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        User user = new User();
        user.setUserId(1L);
        user.setName("Dimitar");
        user.setEmail("dimitar@test.com");
        user.setPassword("encoded-password");

        when(jwtHelper.extractUsername("invalid-token")).thenReturn("dimitar@test.com");
        when(userService.findByEmail("dimitar@test.com")).thenReturn(Optional.of(user));
        when(jwtHelper.isValid("invalid-token", user)).thenReturn(false);

        jwtFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("Should resolve exception when jwt helper throws JwtException")
    void shouldResolveExceptionWhenJwtHelperThrowsJwtException() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(JwtConstants.HEADER, JwtConstants.TOKEN_PREFIX + "bad-token");

        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        JwtException jwtException = new JwtException("Bad token");
        when(jwtHelper.extractUsername("bad-token")).thenThrow(jwtException);

        jwtFilter.doFilter(request, response, filterChain);

        verify(handlerExceptionResolver).resolveException(request, response, null, jwtException);
        verify(filterChain, never()).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }
}