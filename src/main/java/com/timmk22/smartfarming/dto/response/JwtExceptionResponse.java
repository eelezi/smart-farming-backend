package com.timmk22.smartfarming.dto.response;

public record JwtExceptionResponse(
        int status,
        String error,
        String message,
        String path
) {}