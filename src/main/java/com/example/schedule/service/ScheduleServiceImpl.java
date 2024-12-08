package com.example.schedule.service;


import com.example.schedule.dto.PageResponseDto;
import com.example.schedule.dto.ScheduleRequestDto;
import com.example.schedule.dto.ScheduleResponseDto;
import com.example.schedule.exception.BadRequestException;
import com.example.schedule.repository.ScheduleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleRepository scheduleRepository;

    public ScheduleServiceImpl(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    @Override
    public ScheduleResponseDto saveSchedule(ScheduleRequestDto dto) {

        String name = dto.getName();
        String email = dto.getEmail();
        String toDo = dto.getToDo();
        String password = dto.getPassword();


        return scheduleRepository.saveSchedule(name, email, toDo, password);
    }


    @Override
    public PageResponseDto<ScheduleResponseDto> findAllSchedule(ScheduleRequestDto dto) {

        Long id = dto.getId();
        int page = dto.getPage();
        int size = dto.getSize();
        String period = dto.getPeriod();
        LocalDateTime startDate = dto.getStartDate();
        LocalDateTime endDate = dto.getEndDate();

        if (page <= 0 || size <= 0) {
            throw new BadRequestException("페이지와 사이즈는 1 이상이어야 합니다");
        }


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
        } else if (startDate != null && endDate != null) {
            endDate = endDate.plusDays(1);
        } else if (startDate != null) {
            endDate = LocalDateTime.now().plusDays(1);
        }


        return scheduleRepository.findAllScheduleByAuthorId(id, period, startDate, endDate, size, page);
    }

    @Override
    public ScheduleResponseDto findScheduleById(Long id) {
        return scheduleRepository.findScheduleById(id);
    }

    @Transactional
    @Override
    public ScheduleResponseDto updateToDoAndName(Long id, String name, String toDo, String password) {

        if (name == null && toDo == null) {
            throw new BadRequestException("이름이나 할일을 적어도 1개 기입하세요");
        }
        scheduleRepository.updateToDoAndName(id, name, toDo, password);
        return scheduleRepository.findScheduleById(id);
    }

    @Override
    public void deleteSchedule(Long id, String password) {

        scheduleRepository.deleteSchedule(id, password);
    }
}
