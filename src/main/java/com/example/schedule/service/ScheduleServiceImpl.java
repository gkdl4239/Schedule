package com.example.schedule.service;


import com.example.schedule.dto.PageResponseDto;
import com.example.schedule.dto.ScheduleRequestDto;
import com.example.schedule.dto.ScheduleResponseDto;
import com.example.schedule.entity.Author;
import com.example.schedule.entity.Schedule;
import com.example.schedule.repository.ScheduleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ScheduleServiceImpl implements ScheduleService{

    private final ScheduleRepository scheduleRepository;

    public ScheduleServiceImpl(ScheduleRepository scheduleRepository){
        this.scheduleRepository = scheduleRepository;
    }

    @Override
    public ScheduleResponseDto saveSchedule(ScheduleRequestDto dto) {

        Schedule schedule = new Schedule(dto.getToDo(),dto.getPassword());
        Author author = new Author(dto.getName(),dto.getEmail());

        return scheduleRepository.saveSchedule(schedule,author);
    }



    @Override
    public PageResponseDto<ScheduleResponseDto> findAllSchedule(String name, String email, String period, LocalDateTime startDate, LocalDateTime endDate, int size, int page) {



        if (!"custom".equals(period) && period != null) {
            LocalDateTime now = LocalDateTime.now();
            endDate = now;
            switch (period) {
                case "1hour" -> startDate = now.minusHours(1);
                case "1day" -> startDate = now.minusDays(1);
                case "1week" -> startDate = now.minusWeeks(1);
                case "1month" -> startDate = now.minusMonths(1);
                case "3months" -> startDate = now.minusMonths(3);
                case "6months" -> startDate = now.minusMonths(6);
                case "1year" -> startDate = now.minusYears(1);
                default -> throw new IllegalArgumentException("Invalid period: " + period);
            }
        } else if (startDate != null && endDate != null){
            endDate = endDate.plusDays(1);
        }


        return scheduleRepository.findAllScheduleByAuthorId(name,email,period,startDate,endDate, size, page);
    }

    @Override
    public ScheduleResponseDto findScheduleById(Long id) {
        return scheduleRepository.findScheduleById(id);
    }

    @Transactional
    @Override
    public ScheduleResponseDto updateToDoAndName(Long id, String name, String toDo, String password) {

        if(name == null && toDo == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"At least one field is required");
        }

        int updatedRow = scheduleRepository.updateToDoAndName(id,name,toDo,password);

        if(updatedRow == 0){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Does not exist id = "+id);
        }
        return scheduleRepository.findScheduleById(id);
    }

    @Override
    public void deleteSchedule(Long id, String password) {

        int deletedRow = scheduleRepository.deleteSchedule(id, password);

        if(deletedRow == 0){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }
}
