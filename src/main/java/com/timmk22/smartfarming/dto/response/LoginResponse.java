package com.timmk22.smartfarming.dto.response;

public record LoginResponse(
        String token,
        String type,
        Long userId,
        String name,
        String email
) {
    public LoginResponse(String token, Long userId, String name, String email) {
        this(token, "Bearer", userId, name, email);
    }
}
