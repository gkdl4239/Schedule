package com.example.schedule.controller;

import com.example.schedule.dto.RequestDto;
import com.example.schedule.dto.ResponseDto;
import com.example.schedule.entity.Schedule;
import com.example.schedule.service.ScheduleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService){
        this.scheduleService = scheduleService;
    }


    @PostMapping
    public ResponseEntity<ResponseDto> createSchedule(@RequestBody RequestDto dto){

        return new ResponseEntity<>(scheduleService.saveSchedule(dto),HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ResponseDto>> findAllSchedule(@RequestBody RequestDto dto){
        return new ResponseEntity<>(scheduleService.findAllSchedule(dto.getName(),dto.getPeriod(),dto.getStartDate(),dto.getEndDate()), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDto> findScheduleById(@PathVariable Long id) {
        return new ResponseEntity<>(scheduleService.findScheduleById(id),HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ResponseDto> updateToDoAndName(
            @PathVariable Long id,
            @RequestBody RequestDto dto
    ) {
        return new ResponseEntity<>(scheduleService.updateToDoAndName(id,dto.getName(),dto.getToDo(),dto.getPassword()),HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public void deleteSchedule(
            @PathVariable Long id,
            @RequestBody RequestDto dto
    ){
        scheduleService.deleteSchedule(id,dto.getPassword());
    }
}
