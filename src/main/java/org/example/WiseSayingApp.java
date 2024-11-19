package org.example;

import java.util.Scanner;

public class WiseSayingApp {
    public void run(){
        Scanner sc = new Scanner(System.in);
        System.out.println("== 명언 앱 ==");

        while(true){
            System.out.print("명령) ");
            String cmd = sc.nextLine();

            if(cmd.equals("종료")){
                break;
            }
        }
    }
}
