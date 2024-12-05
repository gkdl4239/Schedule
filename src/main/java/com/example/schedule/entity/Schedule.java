package com.example.schedule.entity;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Schedule {

    private Long id;
    private String toDo;
    private String password;
    private LocalDateTime modifiedDate;

    public Schedule(String toDo, String password) {
        this.toDo = toDo;
        this.password = password;
    }

    public Schedule(Long id, String toDo, LocalDateTime modifiedDate) {
        this.id = id;
        this.toDo = toDo;
        this.modifiedDate = modifiedDate;
    }
}
