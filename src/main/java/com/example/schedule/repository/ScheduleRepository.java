package com.example.schedule.repository;

import com.example.schedule.dto.PageResponseDto;
import com.example.schedule.dto.ScheduleResponseDto;
import com.example.schedule.entity.Author;
import com.example.schedule.entity.Schedule;


import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleRepository {

    ScheduleResponseDto saveSchedule(Schedule schedule, Author author);

    PageResponseDto<ScheduleResponseDto> findAllScheduleByAuthorId(String name, String email, String period, LocalDateTime startDate, LocalDateTime endDate, int size, int page);

    ScheduleResponseDto findScheduleById(Long id);

    int updateToDoAndName(Long id, String name, String toDo, String password);

    int deleteSchedule(Long id, String password);
}
