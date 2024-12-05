package com.example.schedule.service;


import com.example.schedule.dto.ScheduleRequestDto;
import com.example.schedule.dto.ScheduleResponseDto;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleService {

    ScheduleResponseDto saveSchedule(ScheduleRequestDto dto);

    List<ScheduleResponseDto> findAllSchedule(String name, String email, String period, LocalDateTime startDate, LocalDateTime endDate );

    ScheduleResponseDto findScheduleById(Long id);

    ScheduleResponseDto updateToDoAndName(Long id, String name, String toDo, String password);

    void deleteSchedule(Long id, String password);
}
