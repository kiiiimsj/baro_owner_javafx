package com.baro.controllers;

import com.baro.JsonParsing.MenuStatistics;
import com.baro.JsonParsing.Order;
import com.baro.JsonParsing.OrderDetailParsing;
import com.baro.JsonParsing.OrderList;
import com.baro.controllers.orderDetail.OrderDetailsController;
import com.google.gson.Gson;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXListView;
import com.sun.org.apache.xpath.internal.operations.Or;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.StringConverter;
import org.json.JSONObject;
import sample.Main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.prefs.Preferences;

public class OrderHistoryController implements Initializable {
    public TextField search_by_phone;
    public JFXDatePicker start_date_picker;
    public JFXDatePicker end_date_picker;
    public JFXButton look_up_button;
    public JFXListView<AnchorPane> dailySales;
    public Button button_search_by_phone;
    public HBox search_hbox;

    private StringConverter dateConverter;
    private OrderDetailParsing orderDetailParsing;

    private boolean clickSearch = false;
    Preferences preferences = Preferences.userRoot();
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dailySales.setVisible(false);
        search_hbox.setVisible(false);
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
        look_up_button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                getOrderCompleteListByDate();
                search_hbox.setVisible(true);
            }
        });
        button_search_by_phone.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                clickSearch = true;
                getOrderCompleteListByDate();
            }
        });
    }
    public void getOrderCompleteListByDate() {
        if(start_date_picker.getValue() == null && end_date_picker.getValue() == null) {
            return;
        }
        final int store_id = preferences.getInt("store_id", 0);
        try{
            URL url = new URL("http://3.35.180.57:8080/OrderCompleteListByDate.do");
            URLConnection con = url.openConnection();
            HttpURLConnection http = (HttpURLConnection) con;
            http.setRequestMethod("POST");
            http.setRequestProperty("Content-Type","application/json;utf-8");
            http.setRequestProperty("Accept","application/json");
            http.setDoOutput(true);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("store_id", store_id);
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
            parsingCompleteList(bf.toString());
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parsingCompleteList(String toString) {
        OrderList orderList = new Gson().fromJson(toString, OrderList.class);
        if(!orderList.result || orderList.orders == null) {
            return;
        }
        dailySales.setVisible(true);
        if(dailySales.getItems().size() != 0) {
            dailySales.getItems().clear();
        }
        dailySales.setStyle("-fx-font-size:15pt; -fx-text-fill: black; -fx-background-color: #ff000000");
        if(orderList.orders.size() == 0) {
            AnchorPane empty = new AnchorPane();
            Label emptyLabel = new Label("불러 올 정보가 없습니다.");
            emptyLabel.setStyle("-fx-font-size: 30px");
            empty.getChildren().add(emptyLabel);
            dailySales.getItems().add(empty);
        }
        AnchorPane header = new AnchorPane();
        header.setId("header");
        Label orderDateLabel = new Label("주문날짜");
        Label phoneLabel = new Label("고객번호");
        Label receiptLabel = new Label("주문번호");
        Label discountLabel = new Label("할인 금액");
        Label totalPriceLabel = new Label("총 주문 금액");
        Label orderStateLabel = new Label("주문상태");


        orderDateLabel.setStyle("-fx-font-size: 12pt; -fx-text-fill: white");
        phoneLabel.setStyle("-fx-font-size: 12pt; -fx-text-fill: white");
        receiptLabel.setStyle("-fx-font-size: 12pt; -fx-text-fill: white");
        discountLabel.setStyle("-fx-font-size: 12pt; -fx-text-fill: white");
        totalPriceLabel.setStyle("-fx-font-size: 12pt; -fx-text-fill: white");
        orderStateLabel.setStyle("-fx-font-size: 12pt; -fx-text-fill: white");


        header.getChildren().addAll(orderDateLabel, phoneLabel, receiptLabel, discountLabel, totalPriceLabel,orderStateLabel );
        header.getChildren().get(0).setLayoutX(100);
        header.getChildren().get(1).setLayoutX(285);
        header.getChildren().get(2).setLayoutX(500);
        header.getChildren().get(3).setLayoutX(700);
        header.getChildren().get(4).setLayoutX(800);
        header.getChildren().get(5).setLayoutX(1000);
        header.setMinHeight(50);

        header.setTranslateX(0);
        header.setTranslateY(0);

        header.setStyle("-fx-background-color: #8333e6");

        dailySales.setTranslateY(50);
        for (int i = 0; i < orderList.orders.size(); i++) {
            if(clickSearch) {
                if(!orderList.orders.get(i).phone.equals(searchByPhone(search_by_phone.getText()))) {
                    continue;
                }
            }
            Order order = orderList.orders.get(i);
            AnchorPane cell = new AnchorPane();
            Text orderDataText = new Text(order.order_date);
            Text phoneText = new Text(order.phone);
            Text receiptIdText = new Text(order.receipt_id);
            Text discountText = new Text(order.discount_price+"");
            Text totalPriceText = new Text(order.total_price+"");
            Text orderStateText = new Text(order.order_state);

            orderDataText.setStyle("-fx-font-size: 12pt; -fx-text-fill: black");
            phoneText.setStyle("-fx-font-size: 12pt; -fx-text-fill: black");
            receiptIdText.setStyle("-fx-font-size: 12pt; -fx-text-fill: black");
            discountText.setStyle("-fx-font-size: 12pt; -fx-text-fill: black");
            totalPriceText.setStyle("-fx-font-size: 12pt; -fx-text-fill: black");
            orderStateText.setStyle("-fx-font-size: 12pt; -fx-text-fill: black");
            
            cell.getChildren().addAll(orderDataText, phoneText, receiptIdText, discountText, totalPriceText, orderStateText);
            cell.getChildren().get(1).setLayoutX(270);
            cell.getChildren().get(2).setLayoutX(430);
            cell.getChildren().get(3).setLayoutX(750);
            cell.getChildren().get(4).setLayoutX(820);
            cell.getChildren().get(5).setLayoutX(1000);
            cell.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    getDetail(order.receipt_id, order, cell);
                }
            });
            dailySales.getItems().add(cell);
        }
    }

    private String searchByPhone(String text) {
        System.out.println(text.length());
        StringBuilder phone = new StringBuilder();
        if(text.length() == 13) {
            StringTokenizer st = new StringTokenizer(text, "-");
            while(st.hasMoreTokens()) {
                phone.append(st.nextToken());
            }
        }else {
            phone.append(text);
        }
        System.out.println("finalphone " + phone.toString());
        return phone.toString();
    }

    public void getDetail(String receipt_id, Order order, AnchorPane cell){
        try{
            URL url = new URL("http://3.35.180.57:8080/OrderListDoneOrCancelForOwner.do?receipt_id="+receipt_id);
            URLConnection con = url.openConnection();
            HttpURLConnection http = (HttpURLConnection) con;
            http.setRequestMethod("GET");
            http.setRequestProperty("Content-Type","application/json;utf-8");
            http.setRequestProperty("Accept","application/json");
            http.setDoOutput(true);

            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line;
            StringBuffer bf = new StringBuffer();

            while((line = br.readLine()) != null) {
                bf.append(line);
            }
            br.close();

            System.out.println("response" + bf.toString());

            //서버에서 response가 true 일때를 분기문에 추가시켜주기.
            parsingOrders(bf.toString(), order, cell);

        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void parsingOrders(String toString, Order order, AnchorPane cell) {
        orderDetailParsing = new Gson().fromJson(toString, OrderDetailParsing.class);
        if(!orderDetailParsing.result || orderDetailParsing.orders == null) {
            return;
        }
        makeDetail(orderDetailParsing, order);
    }
    private void makeDetail(OrderDetailParsing orderDetailParsing, Order order) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/orderDetails.fxml"));
            Parent parent = loader.load();
            OrderDetailsController detailcontroller = loader.<OrderDetailsController>getController();
            detailcontroller.withOutButton = true;
            detailcontroller.setData(orderDetailParsing ,order);
            detailcontroller.configureLeftUI();
            Scene scene = new Scene(parent);
            Stage stage = new Stage();
            stage.setTitle("세부정보");
            //stage.setResizable(false);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

