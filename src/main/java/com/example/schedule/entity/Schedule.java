package com.example.schedule.entity;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Schedule {

    private Long id;
    private final String toDo;
    private final String password;
    private LocalDateTime modifiedDate;

    public Schedule(String toDo, String password) {
        this.toDo = toDo;
        this.password = password;
    }
}
