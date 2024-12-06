package com.example.schedule.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ErrorResponseDto {
    private final String message;
    private final int status;
    private final LocalDateTime timestamp;

    public ErrorResponseDto(String message, int status, LocalDateTime timestamp){
        this.message = message;
        this.status = status;
        this.timestamp = timestamp;
    }
}
