package org.example;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

public class WiseSayingApp {
    public void run(){
        Scanner sc = new Scanner(System.in);
        List<WiseSaying> list = new ArrayList<>();
        int id = 0;

        System.out.println("== 명언 앱 ==");

        while(true){
            System.out.print("명령) ");
            String cmd = sc.nextLine();

            if(cmd.equals("종료")){
                break;
            }else if(cmd.equals("등록")){
                System.out.print("명언 : ");
                String content = sc.nextLine();
                System.out.print("작가 : ");
                String author = sc.nextLine();

                WiseSaying wiseSaying = new WiseSaying(++id, content, author);
                list.add(wiseSaying);
                System.out.println(id + "번 명언이 등록되었습니다.");
            }else if(cmd.equals("목록")){
                System.out.println("번호 / 작가 / 명언");
                System.out.println("----------------------");
                for(int i = list.size()-1; i >= 0; i--){
                    System.out.println(list.get(i));
                }
            }else if(cmd.startsWith("삭제?id=")){
                boolean isDeleted = false; // 삭제 여부를 추적하는 변수
                String temp = cmd.substring(cmd.indexOf('=')+1);

                try {
                    int num = Integer.parseInt(temp);

                    for(int i = 0; i < list.size(); i++){
                        if(list.get(i).getId() == num){
                            list.remove(i);
                            System.out.println(num + "번 명언이 삭제되었습니다.");
                            isDeleted = true;
                            break;
                        }
                    }
                    if (!isDeleted) {
                        System.out.println("해당 번호는 존재하지 않습니다."); // 삭제되지 않은 경우 메시지 표시
                    }
                }catch (Exception e){
                    System.out.println("올바른 값을 입력하세요.");
                }
            }
        }

        sc.close();
    }
}
