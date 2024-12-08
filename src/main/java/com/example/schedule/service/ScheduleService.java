package com.example.schedule.service;


import com.example.schedule.dto.PageResponseDto;
import com.example.schedule.dto.ScheduleRequestDto;
import com.example.schedule.dto.ScheduleResponseDto;

public interface ScheduleService {

    ScheduleResponseDto saveSchedule(ScheduleRequestDto dto);

    PageResponseDto findAllSchedule(ScheduleRequestDto dto);

    ScheduleResponseDto findScheduleById(Long id);

    ScheduleResponseDto updateToDoAndName(Long id, String name, String toDo, String password);

    void deleteSchedule(Long id, String password);
}
