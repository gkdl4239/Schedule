package com.example.schedule.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import jakarta.validation.constraints.Email;
import lombok.Setter;

@Setter
@Getter
public class ScheduleRequestDto {

    private Long id;
    @NotNull
    @Size(max = 200, message = "최대 200자까지 입력 가능합니다.")
    private String toDo;
    @NotNull
    private String name;
    @Email(message = "유효한 이메일 형식이어야 합니다")
    private String email;
    @NotNull
    private String password;
    private String period;
    private final int page = 1;
    private final int size = 5;
    private String startDate;
    private String endDate;

}
