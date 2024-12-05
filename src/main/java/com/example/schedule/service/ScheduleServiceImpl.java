package com.example.schedule.service;


import com.example.schedule.dto.RequestDto;
import com.example.schedule.dto.ResponseDto;
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
    public ResponseDto saveSchedule(RequestDto dto) {

        Schedule schedule = new Schedule(dto.getToDo(),dto.getPassword());
        Author author = new Author(dto.getName(),dto.getEmail());

        return scheduleRepository.saveSchedule(schedule,author);
    }



    @Override
    public List<ResponseDto> findAllSchedule(String name,String period, LocalDateTime startDate,LocalDateTime endDate) {
        return scheduleRepository.findAllScheduleByAuthorId(name,period,startDate,endDate);
    }

    @Override
    public ResponseDto findScheduleById(Long id) {
        return scheduleRepository.findScheduleById(id);
    }

    @Transactional
    @Override
    public ResponseDto updateToDoAndName(Long id, String name, String toDo, String password) {

        if(name == null || toDo == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Both of name and todo  are required");
        }

        int updatedRow = scheduleRepository.updateToDoAndName(id,name,toDo,password);

        if(updatedRow == 0){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Does not exist id = "+id);
        }


//        Schedule schedule = scheduleRepository.findScheduleById(id);
//        return new ResponseDto(schedule);
        return null;
    }

    @Override
    public void deleteSchedule(Long id, String password) {

        int deletedRow = scheduleRepository.deleteSchedule(id, password);

        if(deletedRow == 0){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }
}
