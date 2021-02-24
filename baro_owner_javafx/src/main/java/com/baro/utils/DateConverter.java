package com.baro.utils;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.util.StringConverter;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.*;

public class DateConverter {
    public interface TimerReset{
        void timerReset();
    }

    final public static int YEAR = 0;
    final public static int MONTH = 1;
    final public static int DAY = 2;
    final public static int HOUR = 3;
    final public static int MINUTE = 4;
    final public static int SECOND = 5;

    public TimerReset timerReset;
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
    public static LocalDate getFirstDayOfWeek() {
        LocalDate mondayDate = LocalDate.now().with(WeekFields.of(Locale.FRANCE).getFirstDayOfWeek());
        return mondayDate;
    }


    public static String nameOfToday() {
        DateFormatSymbols dfs = new DateFormatSymbols(Locale.KOREA);
        String weekdays[] = dfs.getWeekdays();

        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DAY_OF_WEEK);
        String nameOfDay = weekdays[day];

        return nameOfDay;
    }


    public static String minusDayFromNow(int minusInt) {
        DateFormatSymbols dfs = new DateFormatSymbols(Locale.KOREA);
        String weekdays[] = dfs.getWeekdays();

        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DAY_OF_WEEK);
        int mDay = minusInt + (day - minusInt);
        String nameOfDay = weekdays[mDay];

        return nameOfDay;
    }

    public static String getNameOfDate(int year, int month, int day) {
        String dateString = String.format("%d-%d-%d", year, month, day);
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-M-d").parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String dayOfWeek = new SimpleDateFormat("EEEE", Locale.KOREA).format(date);

        System.out.println(dayOfWeek);
        return dayOfWeek.substring(0, 1);
    }

    public static String pad(int fieldWidth, char padChar, String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = s.length(); i < fieldWidth; i++) {
            sb.append(padChar);
        }
        sb.append(s);

        return sb.toString();
    }
    public static void fifteenTimer(Label timerLabel, TimerReset timerReset) {
        new Thread((new Runnable() {
            @Override
            public void run() {
                while (true) {
                    System.out.println("thread run");
                    Calendar calendar = GregorianCalendar.getInstance();
                    String minuteString = DateConverter.pad(2, '0', calendar.get(Calendar.MINUTE) + "");
                    String secondString = DateConverter.pad(2, '0', calendar.get(Calendar.SECOND) +"");
                    try {
                        Thread.sleep(1000);
                        final int minuteFinal = 14 - (Integer.parseInt(minuteString) % 15);;
                        final int secondFinal = 59 - Integer.parseInt(secondString);

                        if(minuteFinal==0 && secondFinal == 1) {
                            timerReset.timerReset();
                        }
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                timerLabel.setText(DateConverter.pad(2,'0', minuteFinal+"")+":"+DateConverter.pad(2, '0',secondFinal+""));
                            }
                        });

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        })).start();
    }
    public static void test30Thread(Label timerLabel, TimerReset timerReset) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int testThread = 30;
                while (true) {
                    try {
                        Thread.sleep(1000);
                        final int time = testThread--;
                        if(testThread == 0) {
                            timerReset.timerReset();
                            testThread = 30;
                        }
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                timerLabel.setText(time+"");
                            }
                        });

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}
