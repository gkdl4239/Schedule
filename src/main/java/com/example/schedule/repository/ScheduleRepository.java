package com.example.schedule.repository;

import com.example.schedule.dto.PageResponseDto;
import com.example.schedule.dto.ScheduleResponseDto;


import java.time.LocalDateTime;

public interface ScheduleRepository {

    ScheduleResponseDto saveSchedule(String name, String email, String toDo, String password);

    PageResponseDto findAllScheduleByAuthorId(Long id, String period, LocalDateTime startDate, LocalDateTime endDate, int size, int page);

    ScheduleResponseDto findScheduleById(Long id);

    void updateToDoAndName(Long id, String name, String toDo, String password);

    void deleteSchedule(Long id, String password);
}
