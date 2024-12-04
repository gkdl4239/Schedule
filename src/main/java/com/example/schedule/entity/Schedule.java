package com.example.schedule.entity;

import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class Schedule {

    private Long id;
    private String toDo;
    private String name;
    private String password;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public Schedule(String name, String toDo, String password) {
        this.name = name;
        this.toDo = toDo;
        this.password = password;
    }

    public Schedule(Long id,String name, String toDo, LocalDateTime modifiedDate) {
        this.id = id;
        this.name = name;
        this.toDo = toDo;

        this.modifiedDate = modifiedDate;
    }
}
