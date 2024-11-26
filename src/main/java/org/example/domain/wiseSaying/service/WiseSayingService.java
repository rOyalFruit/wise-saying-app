package org.example.domain.wiseSaying.service;

import org.example.domain.wiseSaying.entity.WiseSaying;
import org.example.domain.wiseSaying.repository.WiseSayingRepository;

import java.util.List;

/**
 * 비즈니스 로직을 처리하고 리포지토리와 컨트롤러 사이의 중간 계층 역할을 수행.
 */
public class WiseSayingService {
    private final WiseSayingRepository wiseSayingRepository;

    public WiseSayingService() {
        this.wiseSayingRepository = new WiseSayingRepository();
    }

    /**
     * 새로운 명언을 등록.
     * @param content 명언 내용
     * @param author 명언 작가
     * @return 등록된 명언의 ID
     */
    public int register(String content, String author) {
        return wiseSayingRepository.save(new WiseSaying(content, author));
    }

    /**
     * 모든 명언을 조회.
     * @return 등록된 모든 명언 목록
     */
    public List<WiseSaying> getAll() {
        return wiseSayingRepository.findAll();
    }

    /**
     * 특정 ID의 명언을 조회.
     * @param id 조회할 명언의 ID
     * @return 조회된 명언 객체, 없으면 null
     */
    public WiseSaying getById(int id) {
        return wiseSayingRepository.findById(id);
    }

    /**
     * 특정 ID의 명언을 삭제.
     * @param id 삭제할 명언의 ID
     * @return 삭제 성공 여부
     */
    public boolean deleteById(int id) {
        return wiseSayingRepository.deleteById(id);
    }

    /**
     * 특정 ID의 명언을 수정.
     * @param id 수정할 명언의 ID
     * @param content 새로운 명언 내용
     * @param author 새로운 명언 작가
     */
    public void update(int id, String content, String author) {
        wiseSayingRepository.update(id, content, author);
    }

    /**
     * 모든 명언 데이터를 JSON 파일로 저장.
     */
    public void buildDataJson() {
        wiseSayingRepository.saveAllToJson();
    }
}