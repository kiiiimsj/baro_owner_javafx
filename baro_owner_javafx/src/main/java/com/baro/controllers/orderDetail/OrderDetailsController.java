package com.baro.controllers.orderDetail;

import com.baro.JsonParsing.Order;
import com.baro.JsonParsing.OrderDetailParsing;
import com.baro.Printer.ReceiptPrint;
import com.baro.controllers.OrderController;
import com.baro.controllers.SettingTimerController;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import org.json.JSONObject;
import sample.Main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class OrderDetailsController implements Initializable {
    public Button cancelBtn;
    public HBox button_area;
    public VBox base;
    @FXML
    private Label phoneLabel;
    @FXML
    private Label dateLabel;
    @FXML
    private Label requestLabel;
    @FXML
    private Label totalPriceLabel;
    @FXML
    private Label discountPriceLabel;
    @FXML
    private Label finalPriceLabel;
    @FXML
    public Button setTime;
    @FXML
    public Button printButton;
    @FXML
    public Button completeBtn;
    @FXML
    private SplitPane splitPane;
    @FXML
    private ScrollPane receipt_preview_scroll;
    private OrderDetailParsing data;
    private Order order;
    private double pos;
    private final SimpleBooleanProperty changeToAccept = new SimpleBooleanProperty();
    private final SimpleBooleanProperty changeToCancel = new SimpleBooleanProperty();
    private final SimpleBooleanProperty changeToDone = new SimpleBooleanProperty();

    private Preferences preferences = Preferences.userRoot();

    private FXMLLoader print_fxml_loader;
    private ReceiptPrint print;
    private Parent printParent;
    private Scene printScene;

    public boolean withOutButton;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        print_fxml_loader = new FXMLLoader(getClass().getResource("/printInterface.fxml"));
        try {
            printParent = print_fxml_loader.load();
            printScene = new Scene(printParent);
        } catch (IOException e) {
            e.printStackTrace();
        }
        print = print_fxml_loader.<ReceiptPrint>getController();

        pos = splitPane.getDividers().get(0).getPosition();
        splitPane.getDividers().get(0).positionProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                splitPane.getDividers().get(0).setPosition(pos);
            }
        });
    }
    public SimpleBooleanProperty getChangeToAccept(){
        return changeToAccept;
    }
    public SimpleBooleanProperty getChangeToCancel(){
        return changeToCancel;
    }
    public SimpleBooleanProperty getChangeToDone(){
        return changeToDone;
    }
    public void changeToAccept(){

    }
    public void setData(OrderDetailParsing data,Order order) {
        this.data = data;
        this.order = order;
        if (order.order_state.equals(Order.ACCEPT)) {
            changeToAccept.set(true);
        }else{
            completeBtn.setVisible(false);
        }
    }
    public void configureLeftUI(){
        System.out.println("withoutbutton : " + withOutButton);
        if(withOutButton) {
            //button_area.setVisible(false);
            //button_area.setMaxHeight(0);
            base.getChildren().remove(1);
            splitPane.getItems().remove(0);
            splitPane.setMaxHeight(700);
            splitPane.setMaxWidth(700);


            receipt_preview_scroll.setMaxHeight(700);
            receipt_preview_scroll.setMinWidth(700);

            base.setMaxWidth(700);
            base.setMaxHeight(700);
        }

        phoneLabel.setText(order.phone);
        dateLabel.setText(order.order_date);
        requestLabel.setText(data.requests);
        totalPriceLabel.setText(order.total_price+" 원");
        discountPriceLabel.setText(order.discount_price + " 원");
        finalPriceLabel.setText("결제 금액 : "+(order.total_price - order.discount_price) + " 원");
        if (order.order_state.equals(Order.PREPARING)){
            setTime.setVisible(true);
        }else if (order.order_state.equals(Order.ACCEPT)){
            setTime.setVisible(false);
        }
        printButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Stage stage = new Stage(StageStyle.UTILITY);
                stage.initModality(Modality.WINDOW_MODAL);
                stage.setTitle("프린터 옵션");
                stage.setResizable(false);

                stage.setScene(printScene);

                if(!preferences.getBoolean("printBefore", false)) {
                    stage.show();
                } else {
                    print.startPrint();
                }
                stage.onCloseRequestProperty().set(new EventHandler<WindowEvent>() {
                    @Override
                    public void handle(WindowEvent event) {
                        if(event.getEventType() == WindowEvent.WINDOW_CLOSE_REQUEST) {
                            System.out.println("close interface");
                            if(print.serialPort.isOpen()) {
                                try {
                                    print.printOutput.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                print.serialPort.closePort();
                            }
                        }
                    }
                });
            }
        });
    }
    
    public void makeReceiptPreView(){
        print.makeString(data, order);
        System.out.println(print.headerContent.toString() +""+ print.orderGetTextContent.toString() +""+ print.customerPhone.toString() +""+ print.orderDateContent.toString()
                +""+ print.content.toString() +""+ print.texTitleText.toString() +""+ print.totalTitleText.toString() +""+ print.customerRequest.toString());

        String header = print.headerContent.toString();
        String orderGetTextContent = print.orderGetTextContent.toString();
        String customerPhone = print.customerPhone.toString();
        String orderDataContent = print.orderDateContent.toString();
        String content = print.content.toString();
        String texTitleText = print.texTitleText.toString();
        String totalTitleText = print.totalTitleText.toString();
        String customerRequest = print.customerRequest.toString();

        VBox scrollContent = new VBox();

        Label headerText = new Label(header);
        Label orderGetTextContentOrderText = new Label("주문이");
        Label orderGetTextContentText = new Label(orderGetTextContent.substring(3, orderGetTextContent.length()));
        Label customerPhoneText = new Label(customerPhone);
        Label orderDataContentText = new Label(orderDataContent);
        Label contentText = new Label(content);
        Label texTitleTextText = new Label(texTitleText);
        Label totalTitleTextText = new Label(totalTitleText);
        Label customerRequestText = new Label(customerRequest);

        headerText.setMaxWidth(600);
        headerText.setAlignment(Pos.CENTER);
        headerText.setStyle("-fx-font-size: 30pt");

        orderGetTextContentOrderText.setMaxWidth(600);
        orderGetTextContentOrderText.setAlignment(Pos.CENTER);
        orderGetTextContentOrderText.setStyle("-fx-font-size: 30pt");

        orderGetTextContentText.setMaxWidth(600);
        orderGetTextContentText.setAlignment(Pos.CENTER);
        orderGetTextContentText.setStyle("-fx-font-size: 30pt");

        customerPhoneText.setAlignment(Pos.CENTER);
        customerPhoneText.setStyle("-fx-font-size: 30pt");

        totalTitleTextText.setAlignment(Pos.CENTER);
        totalTitleTextText.setStyle("-fx-font-size: 30pt");


        scrollContent.getChildren().addAll(headerText, orderGetTextContentOrderText, orderGetTextContentText, customerPhoneText, orderDataContentText
        ,contentText, texTitleTextText, totalTitleTextText, customerRequestText);



        receipt_preview_scroll.setContent(scrollContent);
    }
    public void clickSettingTimes(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SettingTimer.fxml"));
            Stage stage = new Stage(StageStyle.UTILITY);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(Main.getPrimaryStage());
            stage.setTitle("시간 설정");
            Parent parent = loader.load();
            SettingTimerController controller = loader.<SettingTimerController>getController();
            controller.getChangeToAccept().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    if (newValue == true){
                        changeToAccept.set(newValue);
                        order.order_state = Order.ACCEPT;
                        setTime.setVisible(false);
                        completeBtn.setVisible(true);
                        stage.close();
                    }
                }
            });
            controller.setData(order.receipt_id,order.phone);
            Scene scene = new Scene(parent);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private boolean getRequestSuccess(String toString) {
        JSONObject jsonObject = new JSONObject(toString);
        return (jsonObject.getBoolean("result"));
    }
    public void clickCancel(ActionEvent event) {
        try{
            URL url = new URL("http://3.35.180.57:8080/BillingCancel.do");
            URLConnection con = url.openConnection();
            HttpURLConnection http = (HttpURLConnection) con;
            http.setRequestMethod("POST");
            http.setRequestProperty("Content-Type","application/json;utf-8");
            http.setRequestProperty("Accept","application/json");
            http.setDoOutput(true);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("nick", "하태영");
            jsonObject.put("cancel_reason", "고객님의 주문이 가게사정에 의해 취소처리 되었습니다");
            jsonObject.put("receipt_id", order.receipt_id);
            jsonObject.put("store_name", Preferences.userRoot().get("store_name",null));
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
            boolean result = getRequestSuccess(bf.toString());

            if (result) {
                System.out.println("성공");
                changeToCancel.set(true);
                sendCustomerMessage();
            }else{
                System.out.println("실패");
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
    private void sendCustomerMessage() {
        try{
            URL url = new URL("http://3.35.180.57:8080/OwnerSendMessage.do");
            URLConnection con = url.openConnection();
            HttpURLConnection http = (HttpURLConnection) con;
            http.setRequestMethod("POST");
            http.setRequestProperty("Content-Type","application/json;utf-8");
            http.setRequestProperty("Accept","application/json");
            http.setDoOutput(true);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("phone",order.phone);
            jsonObject.put("title", "주문 취소");
            jsonObject.put("content", "고객님의 주문이 가게사정에 의해 취소처리 되었습니다");

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
            boolean result = getRequestSuccess(bf.toString());

            if (result) {
                System.out.println("성공");
            }else{
                System.out.println("실패");
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
    private void sendCustomerDoneMessage() {
        try{
            URL url = new URL("http://3.35.180.57:8080/OwnerSendMessage.do");
            URLConnection con = url.openConnection();
            HttpURLConnection http = (HttpURLConnection) con;
            http.setRequestMethod("POST");
            http.setRequestProperty("Content-Type","application/json;utf-8");
            http.setRequestProperty("Accept","application/json");
            http.setDoOutput(true);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("phone", order.phone);
            jsonObject.put("title", "제조 완료");
            jsonObject.put("content", "고객님의 주문이 완료되었습니다. 수령해가세요!");

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
            boolean result = getRequestSuccess(bf.toString());

            if (result) {
                System.out.println("성공");
            }else{
                System.out.println("실패");
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

    public void clickDone(ActionEvent event) {
        try{
            URL url = new URL("http://3.35.180.57:8080/OwnerSetOrderStatusComplete.do");
            URLConnection con = url.openConnection();
            HttpURLConnection http = (HttpURLConnection) con;
            http.setRequestMethod("PUT");
            http.setRequestProperty("Content-Type","application/json;utf-8");
            http.setRequestProperty("Accept","application/json");
            http.setDoOutput(true);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("receipt_id", order.receipt_id);
            jsonObject.put("store_id", preferences.get("store_id",null));
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
            boolean result = getRequestSuccess(bf.toString());

            if (result) {
                System.out.println("성공");
                changeToDone.set(true);
                sendCustomerDoneMessage();
            }else{
                System.out.println("실패");
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
}
