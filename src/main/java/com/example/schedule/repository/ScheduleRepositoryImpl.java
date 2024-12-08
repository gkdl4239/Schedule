package com.example.schedule.repository;

import com.example.schedule.dto.PageResponseDto;
import com.example.schedule.dto.ScheduleResponseDto;
import com.example.schedule.exception.BadRequestException;
import com.example.schedule.exception.NotFoundException;
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
public class ScheduleRepositoryImpl implements ScheduleRepository {

    private final JdbcTemplate jdbcTemplate;

    public ScheduleRepositoryImpl(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public ScheduleResponseDto saveSchedule(String name, String email, String toDo, String password) {


        // 요청값으로 받은 이름과 이메일로 기존 작성자 정보 존재 유무 확인
        String checkAuthor = "SELECT id FROM author WHERE name = ? AND email = ?";
        Long authorId;

        // 작성자 id가 존재하면 id 값 받아옴 OR 존재하지 않으면 작성자 테이블에 레코드 생성
        try {
            authorId = jdbcTemplate.queryForObject(checkAuthor, Long.class, name, email);
        } catch (EmptyResultDataAccessException e) {
            SimpleJdbcInsert authorInsert = new SimpleJdbcInsert(jdbcTemplate)
                    .withTableName("author")
                    .usingGeneratedKeyColumns("id")
                    .usingColumns("name", "email");

            Map<String, Object> authorParams = new HashMap<>();
            authorParams.put("name", name);
            authorParams.put("email", email);

            Number generatedId = authorInsert.executeAndReturnKey(new MapSqlParameterSource(authorParams));
            authorId = generatedId.longValue();
        }

        // 구한 작성자 ID를 바탕으로 나머지 속성과 함께 일정 레코드도 테이블에 생성
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("schedule")
                .usingGeneratedKeyColumns("id")
                .usingColumns("toDo", "password", "author_id");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("toDo", toDo);
        parameters.put("password", password);
        parameters.put("author_id", authorId);

        Number key = jdbcInsert.executeAndReturnKey(new MapSqlParameterSource(parameters));

        return new ScheduleResponseDto(key.longValue(), toDo, name, LocalDateTime.now());
    }

    @Override
    public PageResponseDto<ScheduleResponseDto> findAllScheduleByAuthorId(Long id, String period, LocalDateTime startDate, LocalDateTime endDate, int size, int page) {

        // 작성자 및 일정 테이블을 조인하여 검색 ( 식별자, 수정일 통합 )
        StringBuilder sql = new StringBuilder("""
                SELECT s.id, s.toDo, s.modifiedDate, a.name, a.email
                FROM schedule AS s
                JOIN author AS a ON s.author_id = a.id
                """);

        List<Object> params = new ArrayList<>();

        buildWhereClause(sql, params, id, startDate, endDate);

        // 페이징을 위한 검색된 레코드 총 개수 연산
        StringBuilder countSql = new StringBuilder("""
                SELECT COUNT(*)
                FROM schedule AS s
                JOIN author AS a ON s.author_id = a.id
                """);

        // 정확한 총 개수 연산을 위한 앞서 실행한 쿼리문의 조건 복사
        if (sql.indexOf("WHERE") != -1) {
            countSql.append(sql.substring(sql.indexOf("WHERE")));
        }

        int totalElements = jdbcTemplate.queryForObject(countSql.toString(), Integer.class, params.toArray());

        // 페이지 당 사이즈와 현재 페이지 위치 구하기
        sql.append("ORDER BY s.modifiedDate DESC LIMIT ? OFFSET ?");
        params.add(size);
        params.add((page - 1) * size);


        List<ScheduleResponseDto> schedules = jdbcTemplate.query(sql.toString(), scheduleRowMapper(), params.toArray());

        int totalPages = (int) Math.ceil((double) totalElements / size);

        // 현재 페이지는 마지막 페이지 초과 불가
        if (page > totalPages) {
            page = Math.max(totalPages, 1);
        }

        return new PageResponseDto<>(schedules, page, size, totalPages, totalElements);
    }

    // 조건에 따른 sql 쿼리문 추가
    private void buildWhereClause(StringBuilder sql, List<Object> params, Long id,
                                  LocalDateTime startDate, LocalDateTime endDate) {

        // 필터가 1개라도 있어야 WHERE 문 시작
        if (id != null || endDate != null) {
            sql.append("WHERE ");
        }

        // 식별자 필터링
        if (id != null) {
            sql.append("s.author_id = ? ");
            params.add(id);
        }

        // 수정일 필터링
        if (startDate != null && endDate != null) {
            if (id != null) {
                sql.append("AND ");
            }
            sql.append("s.modifiedDate BETWEEN ? AND ? ");
            params.add(startDate);
            params.add(endDate);
        }
    }

    @Override
    public ScheduleResponseDto findScheduleById(Long id) {

        isValidInTable(id);

        String sql = "SELECT * FROM schedule AS s JOIN author as a ON s.author_id = a.id WHERE s.id = ?";
        List<ScheduleResponseDto> result = jdbcTemplate.query(sql, scheduleRowMapper(), id);
        return result
                .stream()
                .findAny()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Override
    public void updateToDoAndName(Long id, String name, String toDo, String password) {

        isValidInTable(id);

        // 비밀번호가 같으면 이름,할일 둘다 변경하거나 하나만 변경 가능
        if (password.equals(searchPassword(id))) {

            if (name != null) {
                int authorId = jdbcTemplate.queryForObject("SELECT author_id FROM schedule WHERE id = ?", new Object[]{id}, Integer.class);
                jdbcTemplate.update("UPDATE author SET name =? WHERE id = ?", name, authorId);
            }

            if (toDo != null) {
                jdbcTemplate.update("UPDATE schedule SET toDo = ? WHERE id = ?", toDo, id);
            }
        } else {
            throw new BadRequestException("비밀번호가 올바르지 않습니다 !");
        }
    }

    @Override
    public void deleteSchedule(Long id, String password) {

        isValidInTable(id);

        if (password.equals(searchPassword(id))) {
            jdbcTemplate.update("DELETE FROM schedule WHERE id = ? AND password = ?", id, password);
        } else {
            throw new BadRequestException("비밀번호가 올바르지 않습니다 !");
        }
    }


    private void isValidInTable(Long id) {

        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM schedule WHERE id = ?", new Object[]{id}, Integer.class);

        if (count == null || count == 0) {
            throw new NotFoundException("존재 하지 않는 글 입니다 !");
        }

    }

    private String searchPassword(Long id) {
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
