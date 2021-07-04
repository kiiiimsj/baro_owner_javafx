package com.baro.utils;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.prefs.Preferences;

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
    public static boolean IS_TIMER_THREAD_START = true;

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
    public static LocalDate getFirstDayOfMonth() {
        LocalDate firstDay = LocalDate.now().withDayOfMonth(1);
        return firstDay;
    }


    public static String nameOfToday() {
        DateFormatSymbols dfs = new DateFormatSymbols(Locale.KOREA);
        String weekdays[] = dfs.getWeekdays();

        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DAY_OF_WEEK);
        String nameOfDay = weekdays[day];

        return nameOfDay;
    }
    public static String nameOfFirstDayMonth() {
        LocalDate date = LocalDate.now().withDayOfMonth(1);
        String nameOfDay = "";
        DateFormatSymbols dfs = new DateFormatSymbols(Locale.KOREA);
        String weekdays[] = dfs.getWeekdays();

        if(date.getDayOfWeek().getValue() == 7) {
            nameOfDay = weekdays[1];
        }else {
            //화요일이 월요일로 찍히는 버그 + 1 씩해줌.
            nameOfDay = weekdays[date.getDayOfWeek().getValue() + 1];
        }

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
    public static String enter(String s) {
        StringBuilder sb = new StringBuilder();
        StringTokenizer stringTokenizer = new StringTokenizer(s, " ");
        int index = 0;
        while (stringTokenizer.hasMoreTokens()) {
            System.out.println(index);
            index++;
            sb.append(stringTokenizer.nextToken()+" ");
            if(index ==3) {
                sb.append('\n');
            }
        }
        return sb.toString();
    }
    public static void onHourTimerStart(Label timerLabel, TimerReset timerReset) {
        IS_TIMER_THREAD_START = true;
        new Thread((new Runnable() {
            @Override
            public void run() {
                while (IS_TIMER_THREAD_START) {
//                    System.out.println("TIMER_THREAD : " + IS_TIMER_THREAD_START);
                    Calendar calendar = GregorianCalendar.getInstance();
                    String minuteString = DateConverter.pad(2, '0', calendar.get(Calendar.MINUTE) + "");
                    String secondString = DateConverter.pad(2, '0', calendar.get(Calendar.SECOND) + "");
                    try {
                        Thread.sleep(1000);
                        final int minuteFinal = 59 - (Integer.parseInt(minuteString) % 60);
                        ;
                        final int secondFinal = 59 - Integer.parseInt(secondString);

                        if(minuteFinal == 0 && secondFinal <= 5) {
                            changeDiscountRate();
                        }

                        if (minuteFinal == 0 && secondFinal == 1) {
                            timerReset.timerReset();
                        }

                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                timerLabel.setText(DateConverter.pad(2, '0', minuteFinal + "") + ":" + DateConverter.pad(2, '0', secondFinal + ""));
                            }
                        });

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        })).start();
    }
    public static void fifteenTimerStop() {
        IS_TIMER_THREAD_START = false;
    }
    private static void changeDiscountRate() {
        Preferences preferences = Preferences.userRoot();
        int getNewDiscountRate = preferences.getInt("new_discount_rate", 0);
        int storeId = preferences.getInt("store_id", 0);
        if(storeId == 0 || getNewDiscountRate == -1) {
            return;
        }
        try {
            URL url = new URL("http://3.35.180.57:8080/SetStoreDiscount.do");
            URLConnection con = url.openConnection();
            HttpURLConnection http = (HttpURLConnection) con;
            http.setRequestMethod("POST");
            http.setRequestProperty("Content-Type", "application/json;utf-8");
            http.setRequestProperty("Accept", "application/json");
            http.setDoOutput(true);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("store_id", storeId);
            jsonObject.put("discount_rate", getNewDiscountRate);
            OutputStream os = http.getOutputStream();

            byte[] input = jsonObject.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line;
            StringBuffer bf = new StringBuffer();

            while ((line = br.readLine()) != null) {
                bf.append(line);
            }
            br.close();

            System.out.println("response" + bf.toString());
            if(GetBool.getBool(bf.toString())) {
                preferences.putInt("new_discount_rate", -1);
            }else {

            }
        }
        catch(MalformedURLException e){
            e.printStackTrace();
        } catch(ProtocolException e){
            e.printStackTrace();
        } catch(IOException e){
            e.printStackTrace();
        }
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
