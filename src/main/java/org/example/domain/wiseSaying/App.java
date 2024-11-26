package org.example.domain.wiseSaying;

import org.example.domain.wiseSaying.controller.WiseSayingController;

import java.util.Scanner;

public class App {

    private final Scanner sc = new Scanner(System.in);
    private final WiseSayingController wiseSayingController;

    public App() {
        wiseSayingController = new WiseSayingController(sc);
    }

    public void run() {
        System.out.println("== 명언 앱 ==");

        while (true) {
            System.out.print("명령) ");
            String cmd = sc.nextLine().trim();

            if (cmd.equals("종료")) {
                System.out.println("프로그램을 종료합니다.");
                break;
            }

            // 사용자 입력 명령을 WiseSayingController에 전달하여 처리
            wiseSayingController.handleCommand(cmd);
        }
    }
}
