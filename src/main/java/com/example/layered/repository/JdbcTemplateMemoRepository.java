package com.example.layered.repository;

import com.example.layered.dto.MemoResponseDto;
import com.example.layered.entity.Memo;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Repository
public class JdbcTemplateMemoRepository implements MemoRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcTemplateMemoRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // 메모 생성하기
    @Override
    public MemoResponseDto saveMemo(Memo memo) {
        // INSERT Query 직접 작성하지 않아도 된다.
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        jdbcInsert.withTableName("memo").usingGeneratedKeyColumns("id");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("title", memo.getTitle());
        parameters.put("contents", memo.getContents());

        // 저장 후 생성된 key값 Number 타입으로 반환하는 메서드
        Number key = jdbcInsert.executeAndReturnKey(new MapSqlParameterSource(parameters));

        return new MemoResponseDto(key.longValue(), memo.getTitle(), memo.getContents());
    }

    // 메모 전체 조회
    @Override
    public List<MemoResponseDto> findAllMemos() {
        return jdbcTemplate.query("select * from memo", memoRowMapper());
    }

    // 메모 단건 조회
    @Override
    public Optional<Memo> findMemoById(Long id) {
        List<Memo> result = jdbcTemplate.query("select * from memo where id = ?", memoRowMapperV2(), id);
        return result.stream().findAny();
    }

    @Override
    public Memo findMemoByIdOrElseThrow(Long id) {
        List<Memo> result = jdbcTemplate.query("select * from memo where id = ?", memoRowMapperV2(), id);
        return result.stream().findAny().orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Does not exists id = " + id));
    }

    // 메모 전체 수정
    @Override
    public int updateMemo(Long id, String title, String contents) {

        return jdbcTemplate.update("update memo set title = ?, contents = ? where id = ?", title, contents, id);
    }

    // 메모 제목 수정
    @Override
    public int updateTitle(Long id, String title) {

        return jdbcTemplate.update("update memo set title = ? where id = ?", title, id);
    }

    @Override
    public int deleteMemo(Long id) {
        return jdbcTemplate.update("delete from memo where id = ?", id);
    }

    // 전체 조회 로직
    private RowMapper<MemoResponseDto> memoRowMapper() {

        return new RowMapper<MemoResponseDto>() {
            @Override
            public MemoResponseDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new MemoResponseDto(
                        rs.getLong("id"),
                        rs.getString("title"),
                        rs.getString("contents")
                );
            }
        };
    }

    // 단건 조회 로직
    private RowMapper<Memo> memoRowMapperV2() {
        return new RowMapper<Memo>() {
            @Override
            public Memo mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new Memo(
                        rs.getLong("id"),
                        rs.getString("title"),
                        rs.getString("contents")
                );
            }
        };
    }
}

