package org.example.domain.wiseSaying.controller;

import org.example.domain.wiseSaying.service.WiseSayingService;

import java.util.Scanner;

/**
 * 사용자의 명령어를 처리하고 서비스 계층을 호출하여 비즈니스 로직 수행.
 */
public class WiseSayingController {
    private final WiseSayingService wiseSayingService;
    private final Scanner sc;

    public WiseSayingController(Scanner sc) {
        this.sc = sc;
        this.wiseSayingService = new WiseSayingService();
    }

    /**
     * 사용자 명령어를 처리하는 메인 핸들러.
     * @param cmd 입력된 명령어
     */
    public void handleCommand(String cmd) {
        if (cmd.equals("등록")) {
            register();                             // 명언 등록
        } else if (cmd.equals("목록")) {
            list();                                 // 명언 목록 출력
        } else if (cmd.startsWith("삭제?id=")) {
            delete(cmd);                            // 특정 id의 명언 삭제
        } else if (cmd.startsWith("수정?id=")) {
            update(cmd);                            // 특정 id의 명언 수정
        } else if (cmd.equals("빌드")) {
            build();                                // 전체 명언 데이터 json 파일 생성
        } else {
            System.out.println("알 수 없는 명령입니다.");
        }
    }

    /**
     * 새로운 명언을 등록하는 메서드
     */
    private void register() {
        System.out.print("명언 : ");
        String content = sc.nextLine();
        System.out.print("작가 : ");
        String author = sc.nextLine();

        int id = wiseSayingService.register(content, author);
        System.out.println(id + "번 명언이 등록되었습니다.");
    }

    /**
     * 등록된 모든 명언을 출력하는 메서드
     */
    private void list() {
        var wiseSayings = wiseSayingService.getAll();
        if (wiseSayings.isEmpty()) {
            System.out.println("등록된 명언이 없습니다.");
            return;
        }

        System.out.println("번호 / 작가 / 명언");
        System.out.println("----------------------");
        for (var wiseSaying : wiseSayings) {
            System.out.println(wiseSaying);
        }
    }

    /**
     * 특정 ID의 명언을 삭제하는 메서드
     * @param cmd 삭제 명령어 (예: "삭제?id=1")
     */
    private void delete(String cmd) {
        int id = extractIdFromCommand(cmd);
        if (id == -1) return;

        if (wiseSayingService.deleteById(id)) {
            System.out.println(id + "번 명언이 삭제되었습니다.");
        } else {
            System.out.println("해당 번호는 존재하지 않습니다.");
        }
    }

    /**
     * 특정 ID의 명언을 수정하는 메서드
     * @param cmd 수정 명령어 (예: "수정?id=1")
     */
    private void update(String cmd) {
        int id = extractIdFromCommand(cmd);
        if (id == -1) return;

        var wiseSaying = wiseSayingService.getById(id);
        if (wiseSaying == null) {
            System.out.println("해당 번호는 존재하지 않습니다.");
            return;
        }

        System.out.print("명언(기존) : ");
        System.out.println(wiseSaying.getContent());
        System.out.print("명언 : ");
        String newContent = sc.nextLine();

        System.out.print("작가(기존) : ");
        System.out.println(wiseSaying.getAuthor());
        System.out.print("작가 : ");
        String newAuthor = sc.nextLine();

        wiseSayingService.update(id, newContent, newAuthor);
        System.out.println(id + "번 명언이 수정되었습니다.");
    }

    /**
     * 모든 명언 데이터를 JSON 파일로 생성하는 메서드
     */
    private void build() {
        wiseSayingService.buildDataJson();
        System.out.println("data.json 파일이 생성되었습니다.");
    }

    /**
     * 명령어에서 ID를 추출하는 유틸리티 메서드
     * @param cmd ID를 포함한 명령어
     * @return 추출된 ID, 실패 시 -1 반환
     */
    private int extractIdFromCommand(String cmd) {
        try {
            return Integer.parseInt(cmd.split("=")[1]);
        } catch (NumberFormatException e) {
            System.out.println("올바른 ID 값을 입력하세요.");
            return -1;
        }
    }
}
