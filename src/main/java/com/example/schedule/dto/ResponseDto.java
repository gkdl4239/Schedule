package com.example.schedule.dto;

import com.example.schedule.entity.Schedule;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ResponseDto {
    private Long id;
    private String toDo;
    private String name;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedDate;

    public ResponseDto(Schedule calendar){
        this.id = calendar.getId();
        this.toDo = calendar.getToDo();
        this.name = calendar.getName();
        this.modifiedDate = calendar.getModifiedDate();
    }

}
