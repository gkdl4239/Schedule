package com.example.schedule.repository;

import com.example.schedule.dto.ResponseDto;
import com.example.schedule.entity.Author;
import com.example.schedule.entity.Schedule;
import org.springframework.dao.EmptyResultDataAccessException;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ScheduleRepositoryImpl implements  ScheduleRepository {

    private final JdbcTemplate jdbcTemplate;

    public ScheduleRepositoryImpl(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public ResponseDto saveSchedule(Schedule schedule, Author author) {

        String checkAuthor = "SELECT id FROM author WHERE name = ? AND email = ?";
        Long authorId;
        try{
            authorId = jdbcTemplate.queryForObject(checkAuthor,Long.class,author.getName(),author.getEmail());
        }

        catch(EmptyResultDataAccessException e){
            SimpleJdbcInsert authorInsert = new SimpleJdbcInsert(jdbcTemplate)
                    .withTableName("author")
                    .usingGeneratedKeyColumns("id")
                    .usingColumns("name","email");

            Map<String, Object> authorParams = new HashMap<>();
            authorParams.put("name",author.getName());
            authorParams.put("email",author.getEmail());

            Number generatedId = authorInsert.executeAndReturnKey(new MapSqlParameterSource(authorParams));
            authorId = generatedId.longValue();
        }

        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("schedule")
                .usingGeneratedKeyColumns("id")
                .usingColumns("toDo","password","author_id");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("toDo", schedule.getToDo());
        parameters.put("password", schedule.getPassword());
        parameters.put("author_id", authorId);

        Number key = jdbcInsert.executeAndReturnKey(new MapSqlParameterSource(parameters));

        return new ResponseDto(key.longValue(), schedule.getToDo(), author.getName(), LocalDateTime.now());
    }

    @Override
    public List<ResponseDto> findAllScheduleByAuthorId(String name, String email, String period, LocalDateTime startDate, LocalDateTime endDate) {


        StringBuilder sql = new StringBuilder("SELECT s.id, s.toDo, s.modifiedDate, a.name, a.email " +
                "FROM schedule AS s JOIN author AS a " +
                "ON s.author_id = a.id WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if(name != null){
            sql.append("AND a.name = ? ");
            params.add(name);
        }

        if(email != null){
            sql.append("AND a.email = ? ");
            params.add(email);
        }

        if(startDate != null && endDate != null){
            sql.append("AND s.modifiedDate BETWEEN ? AND ? ");
            params.add(startDate);
            params.add(endDate);
        }

        sql.append("ORDER BY s.modifiedDate DESC");

        return jdbcTemplate.query(sql.toString(), scheduleRowMapper(), params.toArray());

    }

    @Override
    public ResponseDto findScheduleById(Long id) {

        String sql = "SELECT * FROM schedule AS s JOIN author as a ON s.author_id = a.id WHERE s.id = ?";
        List<ResponseDto> result = jdbcTemplate.query(sql, scheduleRowMapper(), id);
        return result
                .stream()
                .findAny()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Override
    public int updateToDoAndName(Long id, String name, String toDo, String password) {
        if (password.equals(jdbcTemplate.queryForObject("SELECT password FROM schedule WHERE id = ?", new Object[]{id}, String.class))) {
            int updatedName = 0;
            int updatedToDo = 0;
            if(name != null){
                int authorId = jdbcTemplate.queryForObject("SELECT author_id FROM schedule WHERE id = ?", new Object[]{id}, Integer.class);
                updatedName = jdbcTemplate.update("UPDATE author SET name =? WHERE id = ?",name,authorId);
            }

            if (toDo != null) {
                updatedToDo = jdbcTemplate.update("UPDATE schedule SET toDo = ? WHERE id = ?", toDo, id);
            }
            return updatedName > 0 || updatedToDo > 0 ? 1 : 0;
        } else {
            return 0;
        }
    }

    @Override
    public int deleteSchedule(Long id, String password) {
        if (password.equals(jdbcTemplate.queryForObject("SELECT password FROM schedule WHERE id = ?", new Object[]{id}, String.class))) {
            return jdbcTemplate.update("DELETE FROM schedule WHERE id = ? AND password = ?", id, password);
        }
        return 0;
    }


    private RowMapper<ResponseDto> scheduleRowMapper() {

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
}
