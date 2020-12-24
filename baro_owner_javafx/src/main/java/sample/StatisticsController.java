package sample;

import com.baro.JsonParsing.Statistics;
import com.baro.JsonParsing.StatisticsParsing;
import com.google.gson.Gson;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.text.Text;
import javafx.util.StringConverter;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class StatisticsController implements Initializable {
    Preferences preferences = Preferences.userRoot();
    private String owner_store_id;
    @FXML private JFXDatePicker start_date_picker;
    @FXML private JFXDatePicker end_date_picker;
    @FXML private JFXButton look_up_button;
    @FXML private Text total_sales;
    @FXML private Text total_number_of_sales;
    @FXML private LineChart<Number, String> line_chart;
    @FXML private NumberAxis x_axis;
    @FXML private CategoryAxis y_axis;
    private StringConverter dateConverter;
    private StatisticsParsing statisticsParsing;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configuration();
    }

    private void configuration() {
        String pattern = "yyyy-MM-dd";
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
        start_date_picker.setConverter(dateConverter);
        end_date_picker.setConverter(dateConverter);
        start_date_picker.setValue(LocalDate.now());
        end_date_picker.setValue(LocalDate.now().minusMonths(1));
        owner_store_id = preferences.get("store_id", "");
        look_up_button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("clicked");
                System.out.println("storeId : " +owner_store_id);
                System.out.println("start_date_picker : " + end_date_picker.getValue());
                System.out.println("end_date_picker : " + start_date_picker.getValue());
                getStatisticsSalesValue();
            }
        });
    }
    private void getStatisticsSalesValue() {
        if(start_date_picker.getValue() == null && end_date_picker.getValue() == null) {
            return;
        }
        try{
            URL url = new URL("http://3.35.180.57:8080/OwnerSetstatistics.do");
            URLConnection con = url.openConnection();
            HttpURLConnection http = (HttpURLConnection) con;
            http.setRequestMethod("POST");
            http.setRequestProperty("Content-Type","application/json;utf-8");
            http.setRequestProperty("Accept","application/json");
            http.setDoOutput(true);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("store_id", owner_store_id);
            jsonObject.put("start_date", end_date_picker.getValue()+"");
            jsonObject.put("end_date", start_date_picker.getValue()+"");
            OutputStream os = http.getOutputStream();

            byte[] input = jsonObject.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line;
            StringBuffer bf = new StringBuffer();

            while((line = br.readLine()) != null) {
                bf.append(line);
            }
            br.close();

            System.out.println("response" + bf.toString());

            //서버에서 response가 true 일때를 분기문에 추가시켜주기.
            if(new JSONObject(bf.toString()).getBoolean("result")){
                parsingStatisticsDate(bf.toString());
            }
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void getStatisticsCountValue() {
        if(start_date_picker.getValue() == null && end_date_picker.getValue() == null) {
            return;
        }
        try{
            URL url = new URL("http://3.35.180.57:8080/OrderCompleteListByDate.do");
            URLConnection con = url.openConnection();
            HttpURLConnection http = (HttpURLConnection) con;
            http.setRequestMethod("POST");
            http.setRequestProperty("Content-Type","application/json;utf-8");
            http.setRequestProperty("Accept","application/json");
            http.setDoOutput(true);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("store_id", owner_store_id);
            jsonObject.put("start_date", end_date_picker.getValue());
            jsonObject.put("end_date", start_date_picker.getValue());
            OutputStream os = http.getOutputStream();

            byte[] input = jsonObject.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line;
            StringBuffer bf = new StringBuffer();

            while((line = br.readLine()) != null) {
                bf.append(line);
            }
            br.close();

            System.out.println("response" + bf.toString());

            //서버에서 response가 true 일때를 분기문에 추가시켜주기.
            if(new JSONObject(bf.toString()).getBoolean("result")){
                parsingStatisticsDate(bf.toString());
            }
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void parsingStatisticsDate(String toString) {
        statisticsParsing = new Gson().fromJson(toString, StatisticsParsing.class);
        setStatisticsData();
    }

    private void setStatisticsData() {
        ObservableList<XYChart.Series<Number, String>> list = FXCollections.observableArrayList();

        XYChart.Series series = new XYChart.Series();
        series.setName("날짜");

        XYChart.Series series2 = new XYChart.Series();
        series2.setName("판매액");
        int totalPrice = 0;
        for (int i = 0; i < statisticsParsing.statistics.size() ; i++) {
            Statistics statisticsData = statisticsParsing.statistics.get(i);
            totalPrice += statisticsData.price;
            series.getData().add(new XYChart.Data(i, statisticsData.date));
            series2.getData().add(new XYChart.Data(i, String.valueOf(statisticsData.price)));
        }
        total_sales.setText(totalPrice + " 원");

        list.addAll(series, series2);
        line_chart.setData(list);
    }
}
