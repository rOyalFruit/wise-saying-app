package org.example.domain.wiseSaying.repository;

import org.example.domain.wiseSaying.entity.WiseSaying;
import org.example.domain.wiseSaying.util.WiseSayingJsonUtil;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 파일 시스템을 이용한 영구 저장소 역할을 수행.
 */
public class WiseSayingRepository {
    private final List<WiseSaying> wiseSayings = new ArrayList<>();
    private final AtomicInteger lastId = new AtomicInteger(0);
    private final String DB_FOLDER = "db/wiseSaying";
    private final String LAST_ID_FILE = DB_FOLDER + "/lastId.txt";
    private final String DATA_JSON_FILE = DB_FOLDER + "/data.json";

    /**
     * 리포지토리 생성자
     * 데이터베이스 폴더 생성, 마지막 ID 로드, 기존 명언 데이터 로드를 수행.
     */
    public WiseSayingRepository() {
        ensureDbFolderExists();
        loadLastId();
        loadWiseSayings();
    }

    /**
     * 데이터베이스 폴더가 존재하는지 확인하고, 없으면 생성.
     */
    private void ensureDbFolderExists() {
        try {
            Files.createDirectories(Paths.get(DB_FOLDER));
        } catch (IOException e) {
            System.out.println("데이터베이스 폴더 생성 중 오류 발생: " + e.getMessage());
        }
    }

    /**
     * 마지막으로 사용된 ID를 파일에서 로드.
     */
    private void loadLastId() {
        try {
            Path path = Paths.get(LAST_ID_FILE);
            if (Files.exists(path)) {
                lastId.set(Integer.parseInt(Files.readString(path).trim()));
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("마지막 ID 불러오기 중 오류 발생: " + e.getMessage());
        }
    }

    /**
     * 현재 마지막 ID를 파일에 저장.
     */
    private void saveLastId() {
        try {
            Files.writeString(Paths.get(LAST_ID_FILE), String.valueOf(lastId.get()));
        } catch (IOException e) {
            System.out.println("마지막 ID 저장 중 오류 발생: " + e.getMessage());
        }
    }

    /**
     * 기존에 저장된 모든 명언을 로드.
     */
    private void loadWiseSayings() {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(DB_FOLDER), "*.json")) {
            for (Path entry : stream) {
                if (!entry.getFileName().toString().equals("data.json")) {
                    String json = Files.readString(entry);
                    wiseSayings.add(WiseSayingJsonUtil.fromJson(json));
                }
            }
        } catch (IOException e) {
            System.out.println("명언 데이터 로드 중 오류 발생: " + e.getMessage());
        }
    }

    /**
     * 새로운 명언을 저장.
     * @param wiseSaying 저장할 명언 객체
     * @return 저장된 명언의 ID
     */
    public int save(WiseSaying wiseSaying) {
        if (wiseSaying.getId() == 0) {
            wiseSaying.setId(lastId.incrementAndGet());
        }
        wiseSayings.add(wiseSaying);
        saveToFile(wiseSaying);
        saveLastId();
        return wiseSaying.getId();
    }

    /**
     * 모든 명언을 조회하고 ID 기준 내림차순으로 정렬.
     * @return ID 기준 내림차순으로 정렬된 모든 명언 목록
     */
    public List<WiseSaying> findAll() {
        return wiseSayings.stream()
                .sorted(Comparator.comparingInt(WiseSaying::getId).reversed())
                .collect(Collectors.toList());
    }

    /**
     * 특정 ID의 명언을 조회.
     * @param id 조회할 명언의 ID
     * @return 조회된 명언 객체, 없으면 null
     */
    public WiseSaying findById(int id) {
        return wiseSayings.stream().filter(ws -> ws.getId() == id).findFirst().orElse(null);
    }

    /**
     * 특정 ID의 명언을 삭제.
     * @param id 삭제할 명언의 ID
     * @return 삭제 성공 여부
     */
    public boolean deleteById(int id) {
        boolean removed = wiseSayings.removeIf(ws -> ws.getId() == id);
        if (removed) {
            deleteFile(id);
        }
        return removed;
    }

    /**
     * 특정 ID의 명언을 수정.
     * @param id 수정할 명언의 ID
     * @param content 새로운 명언 내용
     * @param author 새로운 명언 작가
     */
    public void update(int id, String content, String author) {
        WiseSaying wiseSaying = findById(id);
        if (wiseSaying != null) {
            wiseSaying.setContent(content);
            wiseSaying.setAuthor(author);
            saveToFile(wiseSaying);
        }
    }

    /**
     * 명언 객체를 파일로 저장.
     * @param wiseSaying 저장할 명언 객체
     */
    private void saveToFile(WiseSaying wiseSaying) {
        try {
            String json = WiseSayingJsonUtil.toJson(wiseSaying);
            Files.writeString(Paths.get(DB_FOLDER, wiseSaying.getId() + ".json"), json);
        } catch (IOException e) {
            System.out.println(wiseSaying.getId() + "번 명언 저장 중 오류 발생: " + e.getMessage());
        }
    }

    /**
     * 특정 ID의 명언 파일을 삭제.
     * @param id 삭제할 명언의 ID
     */
    private void deleteFile(int id) {
        try {
            Files.deleteIfExists(Paths.get(DB_FOLDER, id + ".json"));
        } catch (IOException e) {
            System.out.println(id + "번 명언 파일 삭제 중 오류 발생: " + e.getMessage());
        }
    }

    /**
     * 모든 명언을 하나의 JSON 파일로 저장.
     * WiseSayingJsonUtil 클래스의 toJsonArray 메소드를 사용하여
     * 명언 리스트를 JSON 형식으로 변환한 후 파일에 저장함.
     */
    public void saveAllToJson() {
        try {
            String json = WiseSayingJsonUtil.toJsonArray(wiseSayings);
            Files.writeString(Paths.get(DATA_JSON_FILE), json);
        } catch (IOException e) {
            System.out.println("data.json 저장 중 오류 발생: " + e.getMessage());
        }
    }
}