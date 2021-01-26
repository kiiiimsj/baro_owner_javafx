package com.baro.utils;

import java.util.StringTokenizer;

public class DateConverter {
    /***************************************************************************
     *
     * yyyy년 ww월 dd일 hh시 mm분 ss초 -> {yyyy,ww,dd,hh,mm}로 변환
     *
     **************************************************************************/
    final public static int YEAR = 0;
    final public static int MONTH = 1;
    final public static int DAY = 2;
    final public static int HOUR = 3;
    final public static int MINUTE = 4;
    final public static int SECOND = 5;
    public static String[] dateConverteToTime(String date) {
        //년 월 일 시 분 초 뒤에 다 띄어쓰기 있음
        String[] converte = new String[6];

        StringTokenizer st0 = new StringTokenizer(date, "년");
        converte[YEAR] = st0.nextToken();

        StringTokenizer st1 = new StringTokenizer(st0.nextToken(), "월");
        converte[MONTH] = st1.nextToken().trim();

        StringTokenizer st2 = new StringTokenizer(st1.nextToken(), "일");
        converte[DAY] = st2.nextToken().trim();

        StringTokenizer st3 = new StringTokenizer(st2.nextToken(), "시");
        converte[HOUR] = st3.nextToken().trim();

        StringTokenizer st4 = new StringTokenizer(st3.nextToken(), "분");
        converte[MINUTE] = st4.nextToken().trim();

        StringTokenizer st5 = new StringTokenizer(st4.nextToken(), "초");
        converte[SECOND] = st5.nextToken().trim();

        for (int i = 0; i < SECOND; i++) {
            if(converte[i].length() == 1) {
                converte[i] = "0" + converte[i];
            }
        }

        return converte;
    }
}
