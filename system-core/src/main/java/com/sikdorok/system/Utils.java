package com.sikdorok.system;

public class Utils {

    public static int random(int min, int max) {
        return (int) ((Math.random() * (max - min + 1)) + min);
    }

    public static void executionTime(long beforeTime, long afterTime, String title) {
        if (title == null || title.isBlank()) title = "";
        long secDiffTime = (afterTime - beforeTime); //두 시간에 차 계산
        System.out.printf("[%s] 수행시간 : %d ms\n", title, secDiffTime);
    }
    
}
