package org.example;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class WiseSayingApp {

    private final Scanner sc = new Scanner(System.in);
    private final List<WiseSaying> list = new ArrayList<>();
    private int lastId = 0;
    private final String DB_FOLDER = "db/wiseSaying";
    private final String LAST_ID_FILE = DB_FOLDER + "/lastId.txt";

    public void run() {
        ensureDbFolderExists();
        loadLastId();
        loadWiseSayings();

        System.out.println("== 명언 앱 ==");

        while (true) {
            System.out.print("명령) ");
            String cmd = sc.nextLine().trim();

            if (cmd.equals("종료")) {
                break;
            } else if (cmd.equals("등록")) {
                registerWiseSaying();
                saveLastId();
            } else if (cmd.equals("목록")) {
                displayWiseSayings();
            } else if (cmd.startsWith("삭제?id=")) {
                deleteWiseSaying(cmd);
            } else if (cmd.startsWith("수정?id=")) {
                updateWiseSaying(cmd);
            } else {
                System.out.println("알 수 없는 명령입니다.");
            }
        }

        sc.close();
    }

    /** 지정한 경로에 폴더가 존재하지 않을 경우, 해당 경로에 폴더를 생성하는 메서드 */
    private void ensureDbFolderExists() {
        try {
            Files.createDirectories(Paths.get(DB_FOLDER));
        } catch (IOException e) {
            System.out.println("데이터베이스 폴더 생성 중 오류 발생: " + e.getMessage());
        }
    }

    /**  지정한 경로 내에 있는 txt 파일에서 마지막으로 작성된 명언 id를 불러오는 메서드 */
    private void loadLastId() {
        try {
            Path path = Paths.get(LAST_ID_FILE);
            if (Files.exists(path)) {
                lastId = Integer.parseInt(Files.readString(path).trim());
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("마지막 ID를 불러오는 중 오류 발생: " + e.getMessage());
        }
    }

    /**  마지막으로 작성된 명언 id를 지정한 경로에 txt 파일로 저장하는 메서드 */
    private void saveLastId() {
        try {
            Files.writeString(Paths.get(LAST_ID_FILE), String.valueOf(lastId));
        } catch (IOException e) {
            System.out.println("마지막 ID 저장 중 오류 발생: " + e.getMessage());
        }
    }

    /**  지정한 경로 내에 있는 JSON 파일들을 읽고, 각 파일 내용을 WiseSaying 인스턴스로 변환한 뒤 리스트에 저장하는 메서드 */
    private void loadWiseSayings() {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(DB_FOLDER), "*.json")) {
            for (Path entry : stream) {
                String json = Files.readString(entry);
                WiseSaying wiseSaying = parseJsonToWiseSaying(json);
                if (wiseSaying != null) {
                    list.add(wiseSaying);
                }
            }
        } catch (IOException e) {
            System.out.println("명언 데이터를 불러오는 중 오류 발생: " + e.getMessage());
        }
    }

    /** 특정 WiseSaying 인스턴스를 JSON 형식으로 변환하고, {id}.json 파일로 저장하는 메서드 */
    private void saveWiseSayingToFile(WiseSaying wiseSaying) {
        try {
            String filePath = DB_FOLDER + "/" + wiseSaying.getId() + ".json";
            String json = createJsonFromWiseSaying(wiseSaying);
            Files.writeString(Paths.get(filePath), json);
        } catch (IOException e) {
            System.out.println(wiseSaying.getId() + "번 명언 저장 중 오류 발생: " + e.getMessage());
        }
    }

    /** 지정한 경로 내에서 id에 해당하는 JSON 파일을 삭제하는 메서드 */
    private void deleteWiseSayingFile(int id) {
        try {
            String filePath = DB_FOLDER + "/" + id + ".json";
            Files.deleteIfExists(Paths.get(filePath));
        } catch (IOException e) {
            System.out.println(id + "번 명언 파일 삭제 중 오류 발생: " + e.getMessage());
        }
    }

    /** 사용자에게 입력받은 값으로 WiseSaying 인스턴스를 생성하여 리스트에 추가하고 파일로 저장하는 메서드 */
    private void registerWiseSaying() {
        System.out.print("명언 : ");
        String content = sc.nextLine();
        System.out.print("작가 : ");
        String author = sc.nextLine();

        WiseSaying wiseSaying = new WiseSaying(++lastId, content, author);
        list.add(wiseSaying);
        saveWiseSayingToFile(wiseSaying);
        System.out.println(lastId + "번 명언이 등록되었습니다.");
    }

    /** 리스트에 저장된 명언들을 화면에 출력하는 메서드 */
    private void displayWiseSayings() {
        if (list.isEmpty()) {
            System.out.println("등록된 명언이 없습니다.");
            return;
        }

        System.out.println("번호 / 작가 / 명언");
        System.out.println("----------------------");
        for (int i = list.size() - 1; i >= 0; i--) {
            System.out.println(list.get(i));
        }
    }

    /** 지정한 WiseSaying 인스턴스를 리스트와 파일에서 제거하는 메서드 */
    private void deleteWiseSaying(String cmd) {
        int id = extractIdFromCommand(cmd);
        if (id == -1) return;

        WiseSaying wiseSaying = findWiseSayingById(id);
        if (wiseSaying != null) {
            list.remove(wiseSaying);
            deleteWiseSayingFile(id);
            System.out.println(id + "번 명언이 삭제되었습니다.");
        } else {
            System.out.println("해당 번호는 존재하지 않습니다.");
        }
    }

    /** 지정한 WiseSaying 인스턴스의 content와 author를 수정하고, 변경된 내용을 파일에 저장하는 메서드 */
    private void updateWiseSaying(String cmd) {
        int id = extractIdFromCommand(cmd);
        if (id == -1) return;

        WiseSaying wiseSaying = findWiseSayingById(id);
        if (wiseSaying != null) {
            System.out.print("명언(기존) : ");
            System.out.println(wiseSaying.getContent());
            System.out.print("명언 : ");
            String newContent = sc.nextLine();
            System.out.print("작가(기존) : ");
            System.out.println(wiseSaying.getAuthor());
            System.out.print("작가 : ");
            String newAuthor = sc.nextLine();

            wiseSaying.setContent(newContent);
            wiseSaying.setAuthor(newAuthor);
            saveWiseSayingToFile(wiseSaying);
            System.out.println(id + "번 명언이 수정되었습니다.");
        } else {
            System.out.println("해당 번호는 존재하지 않습니다.");
        }
    }

    /** 지정한 WiseSaying 인스턴스를 찾는 메서드 */
    private WiseSaying findWiseSayingById(int id) {
        for (WiseSaying wiseSaying : list) {
            if (wiseSaying.getId() == id) {
                return wiseSaying;
            }
        }
        return null;
    }

    /** 사용자에게 입력받은 명령 문자열에서 id값을 추출하는 메서드 */
    private int extractIdFromCommand(String cmd) {
        try {
            return Integer.parseInt(cmd.split("=")[1]);
        } catch (NumberFormatException e) {
            System.out.println("올바른 ID 값을 입력하세요.");
            return -1;
        }
    }

    /** 직렬화 메서드(WiseSaying to JSON) */
    private String createJsonFromWiseSaying(WiseSaying wiseSaying) {
        return "{\n" +
                "  \"id\": " + wiseSaying.getId() + ",\n" +
                "  \"content\": \"" + wiseSaying.getContent().replace("\"", "\\\"") + "\",\n" +
                "  \"author\": \"" + wiseSaying.getAuthor().replace("\"", "\\\"") + "\"\n" +
                "}";
    }

    /** 역직렬화 메서드(JSON to WiseSaying) */
    private WiseSaying parseJsonToWiseSaying(String json) {
        try {
            String[] lines = json.split("\n");
            int id = Integer.parseInt(lines[1].split(":")[1].trim().replace(",", ""));
            String content = lines[2].split(":")[1].trim().replace("\"", "").replace(",", "");
            String author = lines[3].split(":")[1].trim().replace("\"", "").replace("}", "");
            return new WiseSaying(id, content, author);
        } catch (Exception e) {
            System.out.println("JSON 파싱 중 오류 발생: " + e.getMessage());
            return null;
        }
    }
}
