package org.example;

import java.util.*;

public class WiseSayingApp {

    private final Scanner sc = new Scanner(System.in);
    private final List<WiseSaying> list = new ArrayList<>();
    private int lastId = 0;

    public void run() {
        System.out.println("== 명언 앱 ==");

        while (true) {
            System.out.print("명령) ");
            String cmd = sc.nextLine().trim();

            if (cmd.equals("종료")) {
                break;
            } else if (cmd.equals("등록")) {
                registerWiseSaying();
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

    private void registerWiseSaying() {
        System.out.print("명언 : ");
        String content = sc.nextLine();
        System.out.print("작가 : ");
        String author = sc.nextLine();

        WiseSaying wiseSaying = new WiseSaying(++lastId, content, author);
        list.add(wiseSaying);
        System.out.println(lastId + "번 명언이 등록되었습니다.");
    }

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

    private void deleteWiseSaying(String cmd) {
        int id = extractIdFromCommand(cmd);
        if (id == -1) return;

        WiseSaying wiseSaying = findWiseSayingById(id);
        if (wiseSaying != null) {
            list.remove(wiseSaying);
            System.out.println(id + "번 명언이 삭제되었습니다.");
        } else {
            System.out.println(id + "번 명언은 존재하지 않습니다.");
        }
    }

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
            System.out.println(id + "번 명언이 수정되었습니다.");
        } else {
            System.out.println(id + "번 명언은 존재하지 않습니다.");
        }
    }

    private WiseSaying findWiseSayingById(int id) {
        for (WiseSaying wiseSaying : list) {
            if (wiseSaying.getId() == id) {
                return wiseSaying;
            }
        }
        return null;
    }

    private int extractIdFromCommand(String cmd) {
        try {
            return Integer.parseInt(cmd.split("=")[1]);
        } catch (NumberFormatException e) {
            System.out.println("올바른 ID 값을 입력하세요.");
            return -1;
        }
    }
}