package com.example.schedule.repository;

import com.example.schedule.dto.ResponseDto;
import com.example.schedule.entity.Schedule;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ScheduleRepositoryImpl implements  ScheduleRepository{

    private final JdbcTemplate jdbcTemplate;

    public ScheduleRepositoryImpl(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public ResponseDto saveSchedule(Schedule schedule) {

        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        jdbcInsert.withTableName("schedule").usingGeneratedKeyColumns("id");

        Map<String, Object> parameters = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        parameters.put("name",schedule.getName());
        parameters.put("toDo",schedule.getToDo());
        parameters.put("password",schedule.getPassword());
        parameters.put("createdDate",now);
        parameters.put("modifiedDate",now);

        Number key = jdbcInsert.executeAndReturnKey(new MapSqlParameterSource(parameters));

        return new ResponseDto(key.longValue(), schedule.getToDo(), schedule.getName(), now);
    }

    @Override
    public List<ResponseDto> findAllSchedule(String name,String period, LocalDateTime startDate,LocalDateTime endDate) {

        if(!"custom".equals(period))
        {
            LocalDateTime now = LocalDateTime.now();
            endDate = now;
            switch(period){
                case "1hour" -> startDate = now.minusHours(1);
                case "1day" -> startDate = now.minusDays(1);
                case "1week" -> startDate = now.minusWeeks(1);
                case "1month" -> startDate = now.minusMonths(1);
                case "3months" -> startDate = now.minusMonths(3);
                case "6months" -> startDate = now.minusMonths(6);
                case "1year" ->startDate =  now.minusYears(1);
                default -> throw new IllegalArgumentException("Invalid period: " + period);
            }
        }
        else{
            endDate = endDate.plusDays(1);
        }
        return jdbcTemplate.query("SELECT * FROM schedule WHERE name = ? AND modifiedDate BETWEEN ? AND ?",scheduleRowMapper(),name,startDate,endDate);

    }

    @Override
    public Schedule findScheduleById(Long id) {

        List<Schedule> result = jdbcTemplate.query("SELECT * FROM schedule WHERE id = ?",scheduleRowMapperV2(),id);
        return result.stream().findAny().orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Override
    public int updateToDoAndName(Long id, String name, String toDo, String password) {
        LocalDateTime now = LocalDateTime.now();
        if(password.equals(jdbcTemplate.queryForObject("SELECT password FROM schedule WHERE id = ?",new Object[]{id}, String.class))){
            return jdbcTemplate.update("UPDATE schedule SET name = ?, toDo = ?, modifiedDate = ? WHERE id = ?",name,toDo,id,now);
        }
        else{
            return 0;
        }
    }

    @Override
    public int deleteSchedule(Long id, String password) {
        if(password.equals(jdbcTemplate.queryForObject("SELECT password FROM schedule WHERE id = ?",new Object[]{id}, String.class))){
            return jdbcTemplate.update("DELETE FROM schedule WHERE id = ? AND password = ?",id, password);
        }
        return 0;
    }


    private RowMapper<ResponseDto> scheduleRowMapper()  {

        return new RowMapper<>() {
            @Override
            public ResponseDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new ResponseDto(
                        rs.getLong("id"),
                        rs.getString("toDo"),
                        rs.getString("name"),
                        rs.getTimestamp("modifiedDate").toLocalDateTime()
                );
            }
        };
    }

    private RowMapper<Schedule> scheduleRowMapperV2() {

        return new RowMapper<>() {
            @Override
            public Schedule mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new Schedule(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("toDo"),
                        rs.getTimestamp("modifiedDate").toLocalDateTime()
                );
            }
        };
    }
}
