package com.example.schedule.exception;

import com.example.schedule.dto.ErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;



@RestControllerAdvice
public class GlobalExceptionHandler {

    // 사용자 정의 예외
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponseDto> handleCustomException(CustomException e) {
        ErrorResponseDto errorResponse = new ErrorResponseDto(
                e.getMessage(), e.getStatus().value(), LocalDateTime.now()
        );

        return new ResponseEntity<>(errorResponse, e.getStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationException(MethodArgumentNotValidException e) {

        String message = e.getBindingResult().getFieldErrors().get(0).getDefaultMessage();

        ErrorResponseDto errorResponse = new ErrorResponseDto(
                message, e.getStatusCode().value(), LocalDateTime.now()
        );

        return new ResponseEntity<>(errorResponse, e.getStatusCode());
    }



    // 나머지 일반적인 예외
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleException(Exception e) {
        ErrorResponseDto errorResponse = new ErrorResponseDto(
                "서버 오류 ! ",
                500,
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
