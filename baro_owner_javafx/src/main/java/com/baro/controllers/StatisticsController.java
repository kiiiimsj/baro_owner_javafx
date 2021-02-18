package com.baro.controllers;

import com.baro.JsonParsing.MenuStatistics;
import com.baro.JsonParsing.Statistics;
import com.baro.JsonParsing.StatisticsMenuParsing;
import com.baro.JsonParsing.StatisticsParsing;
import com.baro.utils.DateConverter;
import com.baro.utils.LayoutWidthHeight;
import com.google.gson.Gson;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXListView;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.util.StringConverter;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class StatisticsController implements Initializable {
    public VBox daily_sales_vbox;
    public VBox total_menu_vbox;
    public GridPane menu_list_header;
    public HBox daily_hbox;
    public VBox total_price_vbox;

    @FXML private JFXDatePicker start_date_picker;
    @FXML private JFXDatePicker end_date_picker;
    @FXML private JFXButton look_up_button;
    @FXML private Label total_sales;
    @FXML private Label total_number_of_sales;
    @FXML private LineChart<String, Number> line_chart;
    @FXML private CategoryAxis x_axis;
    @FXML private NumberAxis y_axis;
    @FXML private JFXListView<GridPane> totalMenuList;
    @FXML private JFXListView<GridPane> dailySales;

    /**
     * 날짜 컨버터와 파싱 객체
     * **/
    private StringConverter dateConverter;
    private StatisticsParsing statisticsParsing;
    private StatisticsMenuParsing statisticsMenuParsing;

    /**
     * 그리드 페인 col, row
     * **/
    ColumnConstraints dailyMenuGridCol1 = new ColumnConstraints();
    ColumnConstraints dailyMenuGridCol2 = new ColumnConstraints();
    ColumnConstraints dailyMenuGridCol3 = new ColumnConstraints();

    ColumnConstraints dailyGridCol1 = new ColumnConstraints();
    ColumnConstraints dailyGridCol2 = new ColumnConstraints();


    RowConstraints row1 = new RowConstraints();

    /**
     * store_id 값 가져올때 사용
     * **/
    Preferences preferences = Preferences.userRoot();
    private String owner_store_id;



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        total_menu_vbox.setVisible(false);
        daily_hbox.setVisible(false);
        configuration();
    }
    



    /***************************************************************************
     *
     * 페이지 UI                                                               
     *
     **************************************************************************/
    private void configuration() {
        setListViewSetHeader();
;
        dateConverter = DateConverter.setDateConverter();
        start_date_picker.setConverter(dateConverter);
        end_date_picker.setConverter(dateConverter);
        start_date_picker.setValue(LocalDate.now().minusMonths(1));
        end_date_picker.setValue(LocalDate.now());
        owner_store_id = preferences.get("store_id", "");
        look_up_button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                total_menu_vbox.setVisible(true);
                daily_hbox.setVisible(true);
                getStatisticsSalesValue();
                getStatisticsMenusData();
            }
        });
    }




    /***************************************************************************
     *                                                                         
     * 리스트 페인의 헤더 설정 (그리드 페인)                                                              
     *
     **************************************************************************/

    /**
     *  메뉴 리스트 헤더는 라벨과 리스트 뷰 사이에 들어가는데
     *  라벨의 글자에 헤더가 가려져서 fxml 에서 선언하여 미리 자리를 잡아놓음
     */
    private void setListViewSetHeader() {
        dailyMenuGridCol1.setHgrow(Priority.ALWAYS);
        dailyMenuGridCol1.setPrefWidth((LayoutWidthHeight.MAIN_PAGE_WIDTH - LayoutWidthHeight.MAIN_TAB_PANE_WIDTH) / 3.0);

        dailyMenuGridCol2.setHgrow(Priority.ALWAYS);
        dailyMenuGridCol2.setHalignment(HPos.CENTER);
        dailyMenuGridCol2.setPrefWidth((LayoutWidthHeight.MAIN_PAGE_WIDTH - LayoutWidthHeight.MAIN_TAB_PANE_WIDTH) / 3.0);

        dailyMenuGridCol3.setHgrow(Priority.ALWAYS);
        dailyMenuGridCol3.setHalignment(HPos.CENTER);
        dailyMenuGridCol3.setPrefWidth((LayoutWidthHeight.MAIN_PAGE_WIDTH - LayoutWidthHeight.MAIN_TAB_PANE_WIDTH) / 3.0);

        dailyGridCol1.setHgrow(Priority.ALWAYS);
        dailyGridCol1.setPrefWidth(((LayoutWidthHeight.MAIN_PAGE_WIDTH - LayoutWidthHeight.MAIN_TAB_PANE_WIDTH) / 2.0) / 3.0);

        dailyGridCol2.setHgrow(Priority.ALWAYS);
        dailyGridCol2.setPrefWidth(((LayoutWidthHeight.MAIN_PAGE_WIDTH - LayoutWidthHeight.MAIN_TAB_PANE_WIDTH) / 2.0) / 3.0);

        row1.setVgrow(Priority.ALWAYS);


        GridPane daily_sales_header = new GridPane();
        daily_sales_header.setPadding(new Insets(0, 0, 0, 5));

        Label dateLabel = new Label("날짜/일");
        Label dayPriceLabel = new Label("일 판매액");

        daily_sales_header.getColumnConstraints().add(0, dailyGridCol1);
        daily_sales_header.getColumnConstraints().add(1, dailyGridCol2);
        daily_sales_header.getRowConstraints().add(0, row1);

        dateLabel.setStyle("-fx-font-size: 15pt; -fx-text-fill: white");
        dayPriceLabel.setStyle("-fx-font-size: 15pt; -fx-text-fill: white");

        daily_sales_header.setStyle("-fx-background-color: #8333e6");

        daily_sales_header.addRow(0, dateLabel, dayPriceLabel);
        daily_sales_vbox.getChildren().add(0, daily_sales_header);

        Label name = new Label("메뉴이름");
        Label count = new Label("판매개수");
        Label price = new Label("판매총액");

        name.setStyle("-fx-font-size: 15pt; -fx-text-fill: white");
        count.setStyle("-fx-font-size: 15pt; -fx-text-fill: white");
        price.setStyle("-fx-font-size: 15pt; -fx-text-fill: white");

        menu_list_header.getColumnConstraints().add(0, dailyMenuGridCol1);
        menu_list_header.getColumnConstraints().add(1, dailyMenuGridCol2);
        menu_list_header.getColumnConstraints().add(2, dailyMenuGridCol3);

        menu_list_header.getRowConstraints().add(0, row1);

//        menu_list_header.addRow(0, name, count, price);
        menu_list_header.addColumn(0, name);
        menu_list_header.addColumn(1, count);
        menu_list_header.addColumn(2, price);
        //menu_list_header.setPadding(new Insets(0, 0, 0, 10));
        menu_list_header.setStyle("-fx-background-color: #8333e6");
    }


    /***************************************************************************
     *
     * 라인차트와 리스트 데이터 받아오기                                                             
     *
     **************************************************************************/
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




    /***************************************************************************
     *
     * 메뉴 별 판매 데이터 가져오기                                                             
     *
     **************************************************************************/
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
            System.out.println(start_date_picker.getValue() + " : " + end_date_picker.getValue());
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




    /***************************************************************************
     *
     * 메뉴 별 판매 데이터 파싱                                                              
     *
     **************************************************************************/
    private void parsingStatisticsMenuData(String jsonToString) {
        statisticsMenuParsing = new Gson().fromJson(jsonToString, StatisticsMenuParsing.class);
        setMenuStatisticsData();

    }




    /***************************************************************************
     *
     * 메뉴 별 판매 데이터 설정 / 총 판매액, 판매 개수도 여기서 설정                                                              
     *
     **************************************************************************/
    private void setMenuStatisticsData() {
        int totalCount = 0;
        int totalPrice = 0;
        //scrollContent.getChildren().clear();

        totalMenuList.getItems().clear();
        totalMenuList.setStyle("-fx-font-size:15pt; -fx-text-fill: black; -fx-background-color: #ff000000");

        //totalMenuList.getItems().add(header);

        for (int i = 0; i < statisticsMenuParsing.menuStatisticsList.size(); i++) {
            MenuStatistics menuStatistics = statisticsMenuParsing.menuStatisticsList.get(i);
            totalCount += menuStatistics.menu_count;
            totalPrice += menuStatistics.menu_total_price;
            GridPane cell = new GridPane();

            cell.getColumnConstraints().add(0, dailyMenuGridCol1);
            cell.getColumnConstraints().add(1, dailyMenuGridCol2);
            cell.getColumnConstraints().add(2, dailyMenuGridCol3);
            cell.getRowConstraints().add(0, row1);

            Label menuName = new Label(menuStatistics.menu_name);
            Label menuTotalCount = new Label(menuStatistics.menu_count+"");
            Label menuTotalPrice = new Label(menuStatistics.menu_total_price+"원");

            menuName.setStyle("-fx-font-size: 15pt; -fx-text-fill: black");
            menuTotalCount.setStyle("-fx-font-size: 15pt; -fx-text-fill: black");
            menuTotalPrice.setStyle("-fx-font-size: 15pt; -fx-text-fill: black");

            cell.addRow(0, menuName, menuTotalCount, menuTotalPrice);

            totalMenuList.getItems().add(cell);
        }
        total_sales.setText(totalPrice+"원");
        total_number_of_sales.setText(totalCount+"개");
    }




    /***************************************************************************
     *
     * 라인차트 통계 데이터 파싱                                                              
     *
     **************************************************************************/
    private void parsingStatisticsData(String toString) {
        statisticsParsing = new Gson().fromJson(toString, StatisticsParsing.class);
        setStatisticsData();
        setDailySalesStatisticsData();
    }




    /***************************************************************************
     *
     * 라인차트 데이터 설정                                                             
     *
     **************************************************************************/
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




    /***************************************************************************
     *
     * 일일 판매 리스트뷰 설정                                                        
     *
     **************************************************************************/
    private void setDailySalesStatisticsData() {
        dailySales.getItems().clear();
        dailySales.setStyle("-fx-font-size:15pt; -fx-text-fill: black; -fx-background-color: #ff000000");

        for (int i = 0; i < statisticsParsing.statistics.size(); i++) {
            Statistics dailyStatistics = statisticsParsing.statistics.get(i);
            GridPane cell = new GridPane();

            cell.getColumnConstraints().add(0, dailyGridCol1);
            cell.getColumnConstraints().add(1, dailyGridCol2);
            cell.getRowConstraints().add(0, row1);

            Label dailyDate = new Label(dailyStatistics.date);
            Label dailyPrice = new Label(dailyStatistics.price+"원");

            dailyDate.setStyle("-fx-font-size: 15pt; -fx-text-fill: black");
            dailyPrice.setStyle("-fx-font-size: 15pt; -fx-text-fill: black");

            cell.addRow(0, dailyDate, dailyPrice);
            dailySales.getItems().add(cell);
        }
    }
}



