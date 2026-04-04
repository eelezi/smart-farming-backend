package com.timmk22.smartfarming.dto.response;

public record DisplayUserResponse(
        Long userId,
        String name,
        String email
) {}
