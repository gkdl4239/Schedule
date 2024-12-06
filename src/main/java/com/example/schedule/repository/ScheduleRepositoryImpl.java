package com.example.schedule.repository;

import com.example.schedule.dto.PageResponseDto;
import com.example.schedule.dto.ScheduleResponseDto;
import com.example.schedule.entity.Author;
import com.example.schedule.entity.Schedule;
import com.example.schedule.exception.BadRequestException;
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
    public ScheduleResponseDto saveSchedule(Schedule schedule, Author author) {

        String name = author.getName();
        String email = author.getEmail();
        String toDo = schedule.getToDo();
        String password = schedule.getPassword();



        String checkAuthor = "SELECT id FROM author WHERE name = ? AND email = ?";
        Long authorId;
        try{
            authorId = jdbcTemplate.queryForObject(checkAuthor,Long.class,name,email);
        }

        catch(EmptyResultDataAccessException e){
            SimpleJdbcInsert authorInsert = new SimpleJdbcInsert(jdbcTemplate)
                    .withTableName("author")
                    .usingGeneratedKeyColumns("id")
                    .usingColumns("name","email");

            Map<String, Object> authorParams = new HashMap<>();
            authorParams.put("name",name);
            authorParams.put("email",email);

            Number generatedId = authorInsert.executeAndReturnKey(new MapSqlParameterSource(authorParams));
            authorId = generatedId.longValue();
        }

        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("schedule")
                .usingGeneratedKeyColumns("id")
                .usingColumns("toDo","password","author_id");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("toDo", toDo);
        parameters.put("password", password);
        parameters.put("author_id", authorId);

        Number key = jdbcInsert.executeAndReturnKey(new MapSqlParameterSource(parameters));

        return new ScheduleResponseDto(key.longValue(), toDo, name, LocalDateTime.now());
    }

    @Override
    public PageResponseDto<ScheduleResponseDto> findAllScheduleByAuthorId(Long id, String period, LocalDateTime startDate, LocalDateTime endDate, int size, int page) {


        StringBuilder sql = new StringBuilder("""
                SELECT s.id, s.toDo, s.modifiedDate, a.name, a.email
                FROM schedule AS s
                JOIN author AS a ON s.author_id = a.id
                """);

        List<Object> params = new ArrayList<>();

        buildWhereClause(sql, params, id, startDate, endDate);

        StringBuilder countSql = new StringBuilder("""
            SELECT COUNT(*)
            FROM schedule AS s
            JOIN author AS a ON s.author_id = a.id
            """);

        if(sql.indexOf("WHERE") != -1) {
            countSql.append(sql.substring(sql.indexOf("WHERE")));
        }

        int totalElements = jdbcTemplate.queryForObject(countSql.toString(), Integer.class, params.toArray());

        sql.append("ORDER BY s.modifiedDate DESC LIMIT ? OFFSET ?");
        params.add(size);
        params.add((page-1)*size);


        List<ScheduleResponseDto> schedules = jdbcTemplate.query(sql.toString(), scheduleRowMapper(), params.toArray());




        int totalPages = (int) Math.ceil((double) totalElements / size);

        if (page > totalPages) {
            page = Math.max(totalPages, 1); // 초과 시 마지막 페이지로 설정 (최소 1페이지 보장)
        }

        return new PageResponseDto<>(schedules,page,size,totalPages,totalElements);
    }

    private void buildWhereClause(StringBuilder sql, List<Object> params, Long id,
                                  LocalDateTime startDate, LocalDateTime endDate) {
        if (id != null || endDate != null){
            sql.append("WHERE ");
        }
        if (id != null) {
            sql.append("s.author_id = ? ");
            params.add(id);
        }

        if (startDate != null && endDate != null) {
            if(id !=null){
                sql.append("AND ");
            }
            sql.append("s.modifiedDate BETWEEN ? AND ? ");
            params.add(startDate);
            params.add(endDate);
        }
    }

    @Override
    public ScheduleResponseDto findScheduleById(Long id) {

        int count = isValidInTable(id);

        if(count == 0){
            throw new BadRequestException("존재하지 않는 글입니다 !");
        }

        String sql = "SELECT * FROM schedule AS s JOIN author as a ON s.author_id = a.id WHERE s.id = ?";
        List<ScheduleResponseDto> result = jdbcTemplate.query(sql, scheduleRowMapper(), id);
        return result
                .stream()
                .findAny()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Override
    public void updateToDoAndName(Long id, String name, String toDo, String password) {

        int count = isValidInTable(id);

        if (count ==0){
            throw new BadRequestException("존재하지 않는 글입니다 !");
        }

        if (password.equals(searchPassword(id))) {

            if(name != null){
                int authorId = jdbcTemplate.queryForObject("SELECT author_id FROM schedule WHERE id = ?", new Object[]{id}, Integer.class);
                jdbcTemplate.update("UPDATE author SET name =? WHERE id = ?",name,authorId);
            }

            if (toDo != null) {
                jdbcTemplate.update("UPDATE schedule SET toDo = ? WHERE id = ?", toDo, id);
            }
        }

        throw new BadRequestException("비밀번호가 올바르지 않습니다 !");
    }

    @Override
    public void deleteSchedule(Long id, String password) {

        int count = isValidInTable(id);

        if (count == 0) {
            throw new BadRequestException("존재 하지 않는 글 입니다 !");
        }

        if (password.equals(searchPassword(id))) {
            jdbcTemplate.update("DELETE FROM schedule WHERE id = ? AND password = ?", id, password);
        }

        throw new BadRequestException("비밀번호가 올바르지 않습니다 !");
    }

    private int isValidInTable(Long id){
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM schedule WHERE id = ?", new Object[]{id}, Integer.class);
    }

    private String searchPassword(Long id){
        return jdbcTemplate.queryForObject("SELECT password FROM schedule WHERE id = ?", new Object[]{id}, String.class);
    }


    private RowMapper<ScheduleResponseDto> scheduleRowMapper() {

        return new RowMapper<>() {
            @Override
            public ScheduleResponseDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new ScheduleResponseDto(
                        rs.getLong("id"),
                        rs.getString("toDo"),
                        rs.getString("name"),
                        rs.getTimestamp("modifiedDate").toLocalDateTime()
                );
            }
        };
    }
}
