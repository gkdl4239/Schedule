package com.example.schedule.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import jakarta.validation.constraints.Email;

import java.time.LocalDateTime;

@Getter
public class ScheduleRequestDto {

    @Max(value = 200, message = "최대 200자까지 입력 가능합니다.")
    private String toDo;

    private String name;

    @Email(message = "유효한 이메일 형식이어야 합니다")
    private String email;

    @NotNull
    private String password;

    private String period;
    private int page = 1;
    private int size = 5;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDate;
}
