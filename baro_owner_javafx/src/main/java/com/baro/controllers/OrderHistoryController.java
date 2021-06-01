package com.baro.controllers;

import com.baro.Dialog.NoDataDialog;
import com.baro.JsonParsing.Order;
import com.baro.JsonParsing.OrderDetailParsing;
import com.baro.JsonParsing.OrderHistoryList;
import com.baro.JsonParsing.OrderList;
import com.baro.controllers.orderDetail.OrderDetailsController;
import com.baro.utils.DateConverter;
import com.baro.utils.GetState;
import com.baro.utils.LayoutSize;
import com.google.gson.Gson;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXListView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
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
    public Button look_up_button;
    public ListView<HBox> dailySales;
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
    NoDataDialog noDataDialog;
    Preferences preferences = Preferences.userRoot();
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("init");
        search_hbox.setPrefWidth(LayoutSize.ORDER_HISTORY_TOP_AREA_WIDTH);
        configuration();
    }
    private void configuration() {
//        search_by_phone
        noDataDialog = new NoDataDialog();
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
                if(!search_by_phone.getText().toString().equals("")) {
                    getOrderCompleteListByPhone(search_by_phone.getText());
                }else {
                    getOrderCompleteListByDate();
                }
//                search_by_phone.setText("");
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
    public void getOrderCompleteListByPhone(String phone){
        if(start_date_picker.getValue() == null && end_date_picker.getValue() == null) {
            return;
        }
        final int store_id = preferences.getInt("store_id", 0);
        try{
            URL url = new URL("http://3.35.180.57:8080/OrderCompleteListByDateAndPhone.do");
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
            jsonObject.put("phone", phone);

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
            if(new JSONObject(bf.toString()).getBoolean("result")){
                search_hbox.setVisible(true);
                parsingCompleteList(bf.toString());
            }else {
                dailySales.setVisible(false);
                search_hbox.setVisible(false);
                totalCount.setVisible(false);
                noDataDialog.call();
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
    public void getOrderCompleteListByDate() {
        System.out.println("reload");
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
            System.out.println(bf.toString());
            if(new JSONObject(bf.toString()).getBoolean("result")){
                search_hbox.setVisible(true);
                parsingCompleteList(bf.toString());
            }else {
                dailySales.setVisible(false);
                search_hbox.setVisible(false);
                totalCount.setVisible(false);
                noDataDialog.call(new JSONObject(bf.toString()).getString("message"));
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

    private void parsingCompleteList(String toString) {
        OrderHistoryList orderList = new Gson().fromJson(toString, OrderHistoryList.class);
        if(!orderList.result || orderList.orders == null) {
            return;
        }
        Collections.reverse(orderList.orders);
        dailySales.setVisible(true);

        if(orderList.orders.size() == 0) {
            HBox empty = new HBox();
            Label emptyLabel = new Label("불러 올 정보가 없습니다.");
            emptyLabel.setStyle("-fx-font-size: 30px");
            empty.getChildren().add(emptyLabel);
            dailySales.getItems().add(empty);
            return;
        }

        if(dailySales.getItems().size() != 0) {
            dailySales.getItems().clear();
        }

        int doneCount = 0;
        int cancelCount = 0;
        System.out.println(orderList.orders.size());
        for (int i = 0; i < orderList.orders.size(); i++) {

            Order order = orderList.orders.get(i);
//            if(clickSearch) {
//                if(search_by_phone.getText().length() != 0) {
//                    if(!orderList.orders.get(i).phone.equals(searchByPhone(search_by_phone.getText()))) {
//                        continue;
//                    }
//                }else {
//                    clickSearch = false;
//                }
//            }

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
            System.out.println("makeCell");
            HBox cell = new HBox();
            cell.setSpacing(5);
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
            menuContent.setSpacing(5);
            menuContent.setAlignment(Pos.BASELINE_LEFT);

            Label phoneText = new Label("고객번호 : "+order.phone);
            Label receiptIdText = new Label("영수증번호 : " +order.receipt_id);

            Label discountText = new Label("할인액 : "+order.discount_price+"원");
            if(order.discount_price != 0 ) {
                discountText.setVisible(true);
            }else {
                discountText.setVisible(false);
            }
            Label discountRateText = new Label("바로할인액 : " + ((int)(order.total_price * (order.discount_rate / 100.0))) +"원");
            if(order.discount_rate != 0) {
                discountRateText.setVisible(true);
            }else {
                discountRateText.setVisible(false);
            }
            HBox totalPriceHBox = new HBox();
            Label totalPriceLabel = new Label("총 액 : ");
            Label totalPriceText = new Label(order.total_price+"원");
            HBox line = new HBox();
//            line.setStyle("-fx-border-color: black;-fx-border-width: 0 0 2 0");
            StackPane strikeThrough = new StackPane();
            strikeThrough.getChildren().addAll(totalPriceText, line);
            StackPane.setMargin(line, new Insets(0, 0, 20, 0));

            Label totalPriceDiscountRateText;

            if(order.discount_rate != 0) {
                totalPriceDiscountRateText = new Label(" > "+((order.total_price - (int)(order.total_price * (order.discount_rate / 100.0))) - order.discount_price)+"원");
                totalPriceDiscountRateText.setStyle("-fx-font-size: 20pt; ");
//                totalPriceText.setStrikethrough(true);
                totalPriceHBox.getChildren().addAll(totalPriceLabel, strikeThrough, totalPriceDiscountRateText);
            }
            else {
//                totalPriceText.setStrikethrough(false);
                totalPriceHBox.getChildren().addAll(totalPriceLabel, totalPriceText);
            }
            menuContent.getChildren().addAll(receiptIdText, phoneText, discountRateText, discountText, totalPriceHBox);

            receiptIdText.setStyle("-fx-font-size: 12pt;");
            phoneText.setStyle("-fx-font-size: 18pt; ");
            discountRateText.setStyle("-fx-font-size: 18pt;");
            discountText.setStyle("-fx-font-size: 18pt; ");
            totalPriceText.setStyle("-fx-font-size: 20pt;");
            totalPriceLabel.setStyle("-fx-font-size: 20pt; ");
            
            cell.getChildren().addAll(dateHbox, menuContent);
            cell.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    getDetail(order.receipt_id, order, cell);
                }
            });
            dailySales.getItems().add(cell);
            System.out.println("dailySales.getItems().size()" + dailySales.getItems().size());
        }
        totalCount.setVisible(true);

        see_done.setText("완료\n"+ doneCount+ "건");
        see_cancel.setText("취소\n"+cancelCount+ "건");
        totalCount.setText("검색된 총 내역 : "+(orderList.orders.size())+"건");

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
        makeDetail(orderDetailParsing, order, cell);
    }
    private void makeDetail(OrderDetailParsing orderDetailParsing, Order order, HBox cell) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/orderDetails.fxml"));
            Parent parent = loader.load();
            OrderDetailsController detailcontroller = loader.<OrderDetailsController>getController();

            detailcontroller.withOutButton = true;
            detailcontroller.setData(orderDetailParsing ,order);
            detailcontroller.configureLeftUI();
            detailcontroller.getChangeToCancel().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    System.out.println("cancel");
                    if (newValue) {
                        System.out.println("cancel_new_value_true");
                        ((Label)((HBox)(cell.getChildren().get(0))).getChildren().get(2)).setText("취소");
                        String doneStr = see_done.getText();
                        String cancelStr = see_cancel.getText();
                        int doneCount = Integer.parseInt(doneStr.substring(2, doneStr.indexOf("건")));
                        int cancelCount = Integer.parseInt(cancelStr.substring(2, doneStr.indexOf("건")));
                        see_done.setText("완료\n"+doneCount+"건");
                        see_cancel.setText("취소\n"+cancelCount+ "건");
                    }else {
                        System.out.println("cancel_new_value_false");
                    }

                }
            });
            Scene scene = new Scene(parent);
            Stage stage = new Stage(StageStyle.UNDECORATED);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("세부정보");
            //stage.setResizable(false);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

