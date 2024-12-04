package com.example.schedule.service;


import com.example.schedule.dto.RequestDto;
import com.example.schedule.dto.ResponseDto;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleService {

    ResponseDto saveSchedule(RequestDto dto);

    List<ResponseDto> findAllSchedule(String name,String period, LocalDateTime startDate,LocalDateTime endDate );

    ResponseDto findScheduleById(Long id);

    ResponseDto updateToDoAndName(Long id, String name, String toDo, String password);

    void deleteSchedule(Long id, String password);
}
