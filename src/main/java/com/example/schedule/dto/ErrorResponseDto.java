package com.example.schedule.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;


@Getter
@AllArgsConstructor
public class ErrorResponseDto {
    private final String message;
    private final int status;
    private final LocalDateTime timestamp;
}
