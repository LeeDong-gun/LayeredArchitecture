package com.example.layered.service;

import com.example.layered.dto.MemoRequestDto;
import com.example.layered.dto.MemoResponseDto;
import com.example.layered.entity.Memo;
import com.example.layered.repository.MemoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

/**
 * Annotation @Service는 @Component와 같다, Spring Bean으로 등록한다는 뜻.
 * Spring Bean으로 등록되면 다른 클래스에서 주입하여 사용할 수 있다.
 * 명시적으로 Service Layer 라는것을 나타낸다.
 * 비지니스 로직을 수행한다.
 */
@Service
public class MemoServiceImpl implements MemoService {

    private final MemoRepository memoRepository;

    public MemoServiceImpl(MemoRepository memoRepository) {
        this.memoRepository = memoRepository;
    }


    // 메모 생성 반환
    @Override
    public MemoResponseDto saveMemo(MemoRequestDto dto) {

        // 요청받은 데이터로 MEMO 객체 생성 ID 없음
        Memo memo = new Memo(dto.getTitle(), dto.getContents());

        // DB 저장
        return memoRepository.saveMemo(memo);
    }

    // 모든 조회 반환
    @Override
    public List<MemoResponseDto> findAllMemos() {

        List<MemoResponseDto> allMemos = memoRepository.findAllMemos();

        return allMemos;
    }

    // 단건 조회 반환
    @Override
    public MemoResponseDto findMemoById(Long id) {

        Memo memo = memoRepository.findMemoByIdOrElseThrow(id);

        return new MemoResponseDto(memo);
    }

    // 메모 전체 수정 반환
    @Transactional
    @Override
    public MemoResponseDto updateMemo(Long id, String title, String contents) {

        if (title == null || contents== null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The title and content are required values.");
        }

        int updateRow = memoRepository.updateMemo(id, title, contents);

        if (updateRow == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Does not exist id = " + id);
        }

        Memo memo = memoRepository.findMemoByIdOrElseThrow(id);

        return new MemoResponseDto(memo);
    }

    // 메모 제목 수정 반환
    @Transactional
    @Override
    public MemoResponseDto updateTitle(Long id, String title, String contents) {

        if (title == null || contents != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The title and content are required values.");
        }

        int updateRow = memoRepository.updateTitle(id, title);

        if (updateRow == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Does not exist id = " + id);
        }

        Memo memo = memoRepository.findMemoByIdOrElseThrow(id);

        return new MemoResponseDto(memo);
    }

    // 메모 삭제 요청
    @Override
    public void deleteMemo(Long id) {

        int deleteMemo = memoRepository.deleteMemo(id);

        if (deleteMemo == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Does not exist id = " + id);
        }
    }
}
