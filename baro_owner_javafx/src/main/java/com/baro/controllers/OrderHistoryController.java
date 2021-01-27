package com.baro.controllers;

import com.baro.JsonParsing.Order;
import com.baro.JsonParsing.OrderDetailParsing;
import com.baro.JsonParsing.OrderList;
import com.baro.controllers.orderDetail.OrderDetailsController;
import com.baro.utils.DateConverter;
import com.baro.utils.GetState;
import com.google.gson.Gson;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXListView;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.prefs.Preferences;

public class OrderHistoryController implements Initializable {
    public TextField search_by_phone;
    public JFXDatePicker start_date_picker;
    public JFXDatePicker end_date_picker;
    public JFXButton look_up_button;
    public JFXListView<HBox> dailySales;
    public Button button_search_by_phone;
    public HBox search_hbox;
    public VBox content_vbox;
    public Button see_done;
    public Button see_cancel;
    public Label totalCount;

    private StringConverter dateConverter;
    private OrderDetailParsing orderDetailParsing;

    private boolean clickSearch = false;
    private boolean clickSeeDoneButton = false;
    private boolean clickSeeCancelButton = false;
    Preferences preferences = Preferences.userRoot();
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dailySales.setVisible(false);
        search_hbox.setVisible(false);
        totalCount.setVisible(false);
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
        see_done.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                clickSeeDoneButton = true;
                getOrderCompleteListByDate();

            }
        });
        see_cancel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                clickSeeCancelButton = true;
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
        Collections.reverse(orderList.orders);
        dailySales.setVisible(true);
        if(dailySales.getItems().size() != 0) {
            dailySales.getItems().clear();
        }

        if(orderList.orders.size() == 0) {
            HBox empty = new HBox();
            Label emptyLabel = new Label("불러 올 정보가 없습니다.");
            emptyLabel.setStyle("-fx-font-size: 30px");
            empty.getChildren().add(emptyLabel);
            dailySales.getItems().add(empty);
        }

        int doneCount = 0;
        int cancelCount = 0;

        for (int i = 0; i < orderList.orders.size(); i++) {
            Order order = orderList.orders.get(i);
            if(clickSearch) {
                if(search_by_phone.getText().length() != 0) {
                    if(!orderList.orders.get(i).phone.equals(searchByPhone(search_by_phone.getText()))) {
                        continue;
                    }
                }else {
                    clickSearch = false;
                }
            }

            Label orderStateBox = new Label(GetState.getState(order.order_state));
            orderStateBox.setPrefSize(80, 50);
            orderStateBox.setTextAlignment(TextAlignment.CENTER);
            orderStateBox.setAlignment(Pos.CENTER);
            if(order.order_state.equals("DONE")) {
                orderStateBox.setStyle("-fx-font-size: 18pt;-fx-background-color: rgb(0,230,0) ; -fx-text-fill: white; -fx-background-radius: 5px");
                doneCount += 1;
            }
            if(order.order_state.equals("CANCEL")) {
                orderStateBox.setStyle("-fx-font-size: 18pt;-fx-background-color: rgb(255,69,0) ; -fx-text-fill: white; -fx-background-radius: 5px");
                cancelCount += 1;
            }

            if(clickSeeDoneButton) {
                if(orderList.orders.get(i).order_state.equals("CANCEL")) {
                    continue;
                }
            }
            if(clickSeeCancelButton) {
                if(orderList.orders.get(i).order_state.equals("DONE")) {
                    continue;
                }
            }
            HBox cell = new HBox();
            cell.setSpacing(20);
            String[] converteDate = DateConverter.dateConverteToTime(order.order_date);

            VBox dateHbox = new VBox();
            dateHbox.setMinHeight(200);
            dateHbox.setMinWidth(200);
            dateHbox.setAlignment(Pos.BASELINE_CENTER);
            Label orderDateTimeMinuteText = new Label(converteDate[DateConverter.HOUR] + ":" +converteDate[DateConverter.MINUTE]);
            Label orderDateYearMonthDayText = new Label(converteDate[DateConverter.MONTH] + "/" + converteDate[DateConverter.DAY]);

            dateHbox.getChildren().addAll(orderDateYearMonthDayText, orderDateTimeMinuteText, orderStateBox);


            orderDateYearMonthDayText.setStyle("-fx-font-size: 30pt;");
            orderDateTimeMinuteText.setStyle("-fx-font-size: 20pt;");


            VBox menuContent = new VBox();
            menuContent.setSpacing(20);
            menuContent.setMinWidth(500);
            menuContent.setMaxWidth(500);
            menuContent.setAlignment(Pos.BASELINE_LEFT);

            Label phoneText = new Label("고객번호 : "+order.phone);
            Label receiptIdText = new Label("영수증번호 : " +order.receipt_id);
            Label discountText = new Label("할인액 : "+order.discount_price+"");
            Label totalPriceText = new Label("총 액 : "+order.total_price+"");

            menuContent.getChildren().addAll(receiptIdText, phoneText, discountText, totalPriceText);

            receiptIdText.setStyle("-fx-font-size: 12pt;");
            phoneText.setStyle("-fx-font-size: 18pt; ");
            discountText.setStyle("-fx-font-size: 18pt; ");
            totalPriceText.setStyle("-fx-font-size: 20pt; ");
            
            cell.getChildren().addAll(dateHbox, menuContent);
            cell.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    getDetail(order.receipt_id, order, cell);
                }
            });
            dailySales.getItems().add(cell);
        }
        totalCount.setVisible(true);

        see_done.setText("완료 보기\n"+ doneCount+ "건");
        see_cancel.setText("취소 보기\n"+cancelCount+ "건");
        totalCount.setText("검색된 총 내역 : "+(doneCount + cancelCount)+"건");

        clickSeeDoneButton = false;
        clickSeeCancelButton = false;
    }

    private String searchByPhone(String text) {
        StringBuilder phone = new StringBuilder();
        if(text.length() == 13) {
            StringTokenizer st = new StringTokenizer(text, "-");
            while(st.hasMoreTokens()) {
                phone.append(st.nextToken());
            }
        }else {
            phone.append(text);
        }
        return phone.toString();
    }

    public void getDetail(String receipt_id, Order order, HBox cell){
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
    private void parsingOrders(String toString, Order order, HBox cell) {
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

