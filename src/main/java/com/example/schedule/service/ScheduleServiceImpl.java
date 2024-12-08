package com.example.schedule.service;


import com.example.schedule.dto.PageResponseDto;
import com.example.schedule.dto.ScheduleRequestDto;
import com.example.schedule.dto.ScheduleResponseDto;
import com.example.schedule.exception.BadRequestException;
import com.example.schedule.repository.ScheduleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

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
    public PageResponseDto findAllSchedule(ScheduleRequestDto dto) {

        Long id = dto.getId();
        int page = dto.getPage();
        int size = dto.getSize();
        String period = dto.getPeriod();
        String start = dto.getStartDate();
        String end = dto.getEndDate();

        // String 으로 받아온 시작일과 종료일 LocalDateTime으로 변환하여 사용
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime startDate;
        LocalDateTime endDate;

        if (page <= 0 || size <= 0) {
            throw new BadRequestException("페이지와 사이즈는 1 이상이어야 합니다");
        }

        if(!(start == null && end == null) && (start == null || end == null)){
            throw new BadRequestException("시작일과 종료일 중 하나만 입력할 수 없습니다!");
        }

        if(start != null && period != null){
            throw new BadRequestException("기간이나 날짜선택 중 하나만 입력하세요!");
        }

        // 시작일, 종료일 null 확인
        if(start == null){
            startDate = null;
            endDate = null;
        }
        // 시작일 + 00:00:00 , 종료일 + 11:59:59
        else{
            startDate = LocalDate.parse(start, formatter).atStartOfDay();
            endDate= LocalDate.parse(end, formatter).atTime(LocalTime.MAX);
        }

        // 기간 선택 시 로직
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
                default -> throw new IllegalArgumentException("유효하지 않은 기간입니다 : " + period);
            }
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
            throw new BadRequestException("수정할 이름 이나 할일을 적어도 1개 기입하세요");
        }
        scheduleRepository.updateToDoAndName(id, name, toDo, password);
        return scheduleRepository.findScheduleById(id);
    }

    @Override
    public void deleteSchedule(Long id, String password) {

        scheduleRepository.deleteSchedule(id, password);
    }
}
