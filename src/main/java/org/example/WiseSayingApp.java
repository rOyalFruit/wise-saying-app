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
                String temp = cmd.substring(cmd.indexOf('=')+1);
                int num = Integer.parseInt(temp);

                for(WiseSaying wiseSaying: list){
                    if(wiseSaying.getId() == num){
                        list.remove(wiseSaying);
                        System.out.println(num + "번 명언이 삭제되었습니다.");
                        break;
                    }

                }
            }
        }

        sc.close();
    }
}
