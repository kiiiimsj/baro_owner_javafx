package com.baro.controllers;

import com.baro.JsonParsing.MenuStatistics;
import com.baro.JsonParsing.Statistics;
import com.baro.JsonParsing.StatisticsMenuParsing;
import com.baro.JsonParsing.StatisticsParsing;
import com.google.gson.Gson;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXListView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
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
    @FXML private Label total_sales;
    @FXML private Label total_number_of_sales;
    @FXML private LineChart<String, Number> line_chart;
    @FXML private CategoryAxis x_axis;
    @FXML private NumberAxis y_axis;
    @FXML private JFXListView<AnchorPane> totalMenuList;
    @FXML private JFXListView<AnchorPane> dailySales;
    private Pane scrollContent = new Pane();

    private StringConverter dateConverter;
    private StatisticsParsing statisticsParsing;
    private StatisticsMenuParsing statisticsMenuParsing;


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
        start_date_picker.setValue(LocalDate.now().minusMonths(1));
        end_date_picker.setValue(LocalDate.now());
        owner_store_id = preferences.get("store_id", "");
        look_up_button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                getStatisticsSalesValue();
                getStatisticsMenusData();
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
            jsonObject.put("start_date", start_date_picker.getValue());
            jsonObject.put("end_date", end_date_picker.getValue());
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
                parsingStatisticsData(bf.toString());
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
    private void getStatisticsMenusData() {
        if(start_date_picker.getValue() == null && end_date_picker.getValue() == null) {
            return;
        }
        try{
            URL url = new URL("http://3.35.180.57:8080/OwnerMenuStatistics.do");
            URLConnection con = url.openConnection();
            HttpURLConnection http = (HttpURLConnection) con;
            http.setRequestMethod("POST");
            http.setRequestProperty("Content-Type","application/json;utf-8");
            http.setRequestProperty("Accept","application/json");
            http.setDoOutput(true);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("store_id", owner_store_id);
            jsonObject.put("start_date", start_date_picker.getValue());
            jsonObject.put("end_date", end_date_picker.getValue());
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
                parsingStatisticsMenuData(bf.toString());
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
    private void parsingStatisticsMenuData(String jsonToString) {
        statisticsMenuParsing = new Gson().fromJson(jsonToString, StatisticsMenuParsing.class);
        setMenuStatisticsData();

    }
    private void setMenuStatisticsData() {
        int totalCount = 0;
        int totalPrice = 0;
        StringBuilder space = new StringBuilder("\t\t\t");
        //scrollContent.getChildren().clear();
        totalMenuList.getItems().clear();
        totalMenuList.setStyle("-fx-font-size:15pt; -fx-text-fill: black; -fx-background-color: #ff000000");
        AnchorPane header = new AnchorPane();
        header.setId("header");
        Text name = new Text("메뉴이름" + space);
        Text count = new Text("메뉴개수" + space);
        Text price = new Text("메뉴가격");

        name.setStyle("-fx-font-size: 15pt; -fx-text-fill: black");
        count.setStyle("-fx-font-size: 15pt; -fx-text-fill: black");
        price.setStyle("-fx-font-size: 15pt; -fx-text-fill: black");

        header.getChildren().addAll(name, count, price);
        header.getChildren().get(1).setLayoutX(150);
        header.getChildren().get(2).setLayoutX(300);

        totalMenuList.getItems().add(header);

        for (int i = 0; i < statisticsMenuParsing.menuStatisticsList.size(); i++) {

            MenuStatistics menuStatistics = statisticsMenuParsing.menuStatisticsList.get(i);
            totalCount += menuStatistics.menu_count;
            totalPrice += menuStatistics.menu_total_price;
            AnchorPane cell = new AnchorPane();
            Text menuName = new Text(menuStatistics.menu_name+ space.toString());
            if(menuStatistics.menu_name.length() <= 2) {
                space.append("\t\t");
            }
            Text menuTotalCount = new Text(menuStatistics.menu_count+"");
            Text menuTotalPrice = new Text(menuStatistics.menu_total_price+"원");

            menuName.setStyle("-fx-font-size: 15pt; -fx-text-fill: black");
            menuTotalCount.setStyle("-fx-font-size: 15pt; -fx-text-fill: black");
            menuTotalPrice.setStyle("-fx-font-size: 15pt; -fx-text-fill: black");
            cell.getChildren().addAll(menuName, menuTotalCount, menuTotalPrice);
            cell.getChildren().get(1).setLayoutX(150);
            cell.getChildren().get(2).setLayoutX(300);
            totalMenuList.getItems().add(cell);
        }
        totalMenuList.getItems().get(0).setStyle("-fx-background-color: #8333e6");

        total_sales.setText(totalPrice+"원");

        total_number_of_sales.setText(totalCount+"개");
        totalMenuList.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.out.println("clicked on" + totalMenuList.getSelectionModel().getSelectedItem());
            }
        });
    }
    private void parsingStatisticsData(String toString) {
        statisticsParsing = new Gson().fromJson(toString, StatisticsParsing.class);
        setStatisticsData();
        setDailySalesStatisticsData();
    }

    private void setStatisticsData() {
        line_chart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("총 판매액");
        for (int i = 0; i < statisticsParsing.statistics.size() ; i++) {
            Statistics statisticsData = statisticsParsing.statistics.get(i);
            String date = statisticsData.date.substring(5);
            series.getData().add(new XYChart.Data<>(date, statisticsData.price));
        }
        y_axis.setLabel("판매액");
        x_axis.setLabel("날짜");

        line_chart.getData().add(series);
    }
    private void setDailySalesStatisticsData() {
        StringBuilder space = new StringBuilder("\t\t\t");
        dailySales.getItems().clear();
        dailySales.setStyle("-fx-font-size:15pt; -fx-text-fill: black; -fx-background-color: #ff000000");
        AnchorPane header = new AnchorPane();
        header.setId("header");
        Text dateText = new Text("날짜/일" + space);
        Text dayPriceText = new Text("일 판매액");

        dateText.setStyle("-fx-font-size: 15pt");
        dayPriceText.setStyle("-fx-font-size: 15pt");

        header.getChildren().addAll(dateText, dayPriceText);
        header.getChildren().get(1).setLayoutX(200);

        dailySales.getItems().add(header);

        for (int i = 0; i < statisticsParsing.statistics.size(); i++) {
            Statistics dailyStatistics = statisticsParsing.statistics.get(i);
            AnchorPane cell = new AnchorPane();

            Text dailyDate = new Text(dailyStatistics.date+ space.toString());
            Text dailyPrice = new Text(dailyStatistics.price+"원");

            dailyDate.setStyle("-fx-font-size: 15pt");
            dailyPrice.setStyle("-fx-font-size: 15pt");

            cell.getChildren().addAll(dailyDate, dailyPrice);
            cell.getChildren().get(1).setLayoutX(200);
            dailySales.getItems().add(cell);
        }
        dailySales.getItems().get(0).setStyle("-fx-background-color: #8333e6");
        dailySales.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.out.println("clicked on" + dailySales.getSelectionModel().getSelectedItem());
            }
        });
    }
}



