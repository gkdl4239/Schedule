package com.example.schedule.entity;

import lombok.Getter;

@Getter
public class Author {

    private Long id;
    private final String name;
    private final String email;

    public Author(String name, String email){
        this.name = name;
        this.email = email;
    }
}
