package com.example.schedule.dto;

import com.example.schedule.entity.Author;
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

    public ResponseDto(Schedule schedule, Author author){
        this.id = schedule.getId();
        this.toDo = schedule.getToDo();
        this.name = author.getName();
        this.modifiedDate = schedule.getModifiedDate();
    }

}
