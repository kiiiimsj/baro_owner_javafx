package com.baro.utils;

import javafx.util.StringConverter;

import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Locale;
import java.util.StringTokenizer;

public class DateConverter {
    final public static int YEAR = 0;
    final public static int MONTH = 1;
    final public static int DAY = 2;
    final public static int HOUR = 3;
    final public static int MINUTE = 4;
    final public static int SECOND = 5;

    /***************************************************************************
     *
     * yyyy년 ww월 dd일 hh시 mm분 ss초 -> {yyyy,ww,dd,hh,mm}로 변환
     *
     **************************************************************************/
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


    /***************************************************************************
     *
     * 날짜 패턴 반환
     *
     **************************************************************************/
    public static StringConverter setDateConverter() {
        String pattern = "yyyy-MM-dd";

        StringConverter dateConverter;
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);
        dateConverter = new StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }

            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, dateFormatter);
                } else {
                    return null;
                }
            }
        };
        return dateConverter;
    }


    public static String nameOfDay() {
        DateFormatSymbols dfs = new DateFormatSymbols(Locale.KOREA);
        String weekdays[] = dfs.getWeekdays();

        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DAY_OF_WEEK);
        String nameOfDay = weekdays[day];

        return nameOfDay;
    }


    public static String minusFromTodayName(int minusInt) {
        DateFormatSymbols dfs = new DateFormatSymbols(Locale.KOREA);
        String weekdays[] = dfs.getWeekdays();

        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DAY_OF_WEEK);
        int mDay = minusInt + (day - minusInt);
        String nameOfMDay = weekdays[mDay];

        return nameOfMDay;
    }
}
