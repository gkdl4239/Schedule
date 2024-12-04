package com.example.schedule.repository;

import com.example.schedule.dto.ResponseDto;
import com.example.schedule.entity.Schedule;


import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleRepository {

    ResponseDto saveSchedule(Schedule schedule);

    List<ResponseDto> findAllSchedule(String name,String period, LocalDateTime startDate,LocalDateTime endDate);

    Schedule findScheduleById(Long id);

    int updateToDoAndName(Long id, String name, String toDo, String password);

    int deleteSchedule(Long id, String password);
}
