package com.example.schedule.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ErrorResponseDto {
    private String message;
    private int status;
    private LocalDateTime timestamp;

    public ErrorResponseDto(String message, int status, LocalDateTime timestamp){
        this.message = message;
        this.status = status;
        this.timestamp = timestamp;
    }
}
