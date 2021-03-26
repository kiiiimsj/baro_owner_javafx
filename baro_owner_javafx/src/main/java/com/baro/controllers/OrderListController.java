package com.baro.controllers;

import com.baro.Dialog.InternetConnectDialog;
import com.baro.JsonParsing.Order;
import com.baro.JsonParsing.OrderDetailParsing;
import com.baro.JsonParsing.OrderList;
import com.baro.controllers.orderDetail.OrderDetailsController;
import com.baro.utils.DateConverter;
import com.baro.utils.LayoutSize;
import com.google.gson.Gson;
import com.jfoenix.controls.JFXToggleButton;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.*;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;
import com.baro.Main;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.prefs.Preferences;


public class OrderListController implements DiscountRateController.ClickClose, DateConverter.TimerReset {

    public Label new_store_discount_rate;
    public Label arrow_right;
    public Label baro_discount_timer;
    public AnchorPane orderListSideContainer;
    public Button prev_tab;
    public Button next_tab;
    public HBox paging_ui;
    public HBox discount_rate_height;
    public VBox no_data;

    @Override
    public void timerReset() {
        String getText = new_store_discount_rate.getText().toString();
        if(getText.equals("")) {
            return;
        }
        int newDiscount = Integer.parseInt(getText.substring(0, getText.indexOf("%")));
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                store_discount_rate.setText(newDiscount+"%");
                new_store_discount_rate.setVisible(false);
                arrow_right.setVisible(false);
            }
        });
    }

    public interface MoveToSetting {
        void moveSetting();
    }

    public Label store_discount_rate;
    public VBox discount_rate_set;
    @FXML
    private JFXToggleButton isOpenBtn;
    @FXML
    private TilePane childContainer;
    @FXML
    private Label pagingLabel;
    @FXML
    private AnchorPane orderDetailsContainer;
    private WebSocketClient webSocketClient;

    public static OrderList orderList = new OrderList();
    public static int lastSelectedTabIndex = 0;
    public final static int ONEPAGEORDER = 7; // 한 페이지에 들어가는 갯수
    public static int CURRNETPAGE = 1; // 현재 페이지
    public static int ENTIREPAGE = 1; // 전체페이지 수
    public static Boolean LASTPAGEFULL = false; // 마지막페이지가 가득찼냐

    public MoveToSetting moveToSetting;
    int orderIndex = 0;
    int discountRate = 0;

    private SimpleIntegerProperty notReadedOrder = new SimpleIntegerProperty();
    public InternetConnectDialog.Reload reload;

    String store_id;
    Preferences preferences = Preferences.userRoot();
    AlarmPopUp popUp = new AlarmPopUp();
    String isOpen = preferences.get("is_open", "");

    MediaPlayer player;

    /// Life cycle
    @FXML
    public void initialize() {
        System.out.println("call OrderList");
        paging_ui.setPrefWidth(LayoutSize.ORDER_LIST_WIDTH);
        paging_ui.setPrefHeight(LayoutSize.ORDER_LIST_ORDER_CELL_HEIGHT);

        pagingLabel.setPrefHeight(LayoutSize.ORDER_LIST_ORDER_CELL_HEIGHT);
        prev_tab.setPrefHeight(LayoutSize.ORDER_LIST_ORDER_CELL_HEIGHT);
        next_tab.setPrefHeight(LayoutSize.ORDER_LIST_ORDER_CELL_HEIGHT);

        pagingLabel.setPrefWidth((LayoutSize.ORDER_LIST_WIDTH / 3.0)  - 5);
        prev_tab.setPrefWidth((LayoutSize.ORDER_LIST_WIDTH / 3.0)  - 5);
        next_tab.setPrefWidth((LayoutSize.ORDER_LIST_WIDTH / 3.0)  - 5);

        orderListSideContainer.setMinWidth(LayoutSize.INSIDE_PANE_WIDTH);
        orderListSideContainer.setMinHeight(LayoutSize.INSIDE_PANE_HEIGHT);

        childContainer.setPrefWidth(LayoutSize.ORDER_LIST_WIDTH);
        childContainer.setPrefHeight(LayoutSize.ORDER_LIST_HEIGHT);

        discount_rate_height.setPrefHeight(LayoutSize.ORDER_LIST_TOP_AREA_HEIGHT);
        try {
            Media media = new Media(getClass().getResource("/baro_voice.mp3").toURI().toString());
            player = new MediaPlayer(media);
            player.setOnEndOfMedia(new Runnable() {
                @Override
                public void run() {
                    player.seek(player.getStartTime());
                }
            });
            player.setCycleCount(MediaPlayer.INDEFINITE);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        if(isOpen.equals("Y")) {
            isOpenBtn.setSelected(true);

//            isOpenBtn.setText("영업종료 하기");
//            isOpenBtn.setStyle("-fx-background-color: red; -fx-text-fill: #ffffff; -fx-font-size: 20pt; -fx-font-family: 'Noto Sans CJK KR Regular'");
        } else {
            isOpenBtn.setSelected(false);
//            isOpenBtn.setText("영업게시 하기");
//            isOpenBtn.setStyle("-fx-background-color: #8333e6; -fx-text-fill: #ffffff; -fx-font-size: 20pt; -fx-font-family: 'Noto Sans CJK KR Regular'");
        }


        isOpenBtn.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                System.out.println(newValue);
                store_is_open_change(newValue, false);
            }
        });
        notReadedOrder.addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                popUp.controller.changeCount((int)newValue);
            }
        });
        discount_rate_set.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try {
                    double centerXPosition = orderListSideContainer.getScene().getX() + orderListSideContainer.getScene().getWidth()/2d;
                    double centerYPosition = orderListSideContainer.getScene().getY() + orderListSideContainer.getScene().getHeight()/2d;

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/discount_rate_page.fxml"));
                    Parent parent = loader.load();
                    Scene discountRateScene = new Scene(parent);

                    DiscountRateController discountRateController = loader.<DiscountRateController>getController();
                    discountRateController.clickClose = OrderListController.this;
                    Stage stage = new Stage(StageStyle.UNDECORATED);
                    stage.initModality(Modality.APPLICATION_MODAL);
//                    stage.setResizable(false);
                    stage.setScene(discountRateScene);

                    discountRateController.storeId = store_id;
                    discountRateController.setDiscountRate(discountRate);
                    stage.setWidth(LayoutSize.DIALOG_DISCOUNT_WIDTH);
                    stage.setHeight(LayoutSize.DIALOG_DISCOUNT_HEIGHT);
                    stage.setX(LayoutSize.CENTER_IN_PARENT_X);
                    stage.setY(LayoutSize.CENTER_IN_PARENT_Y);
//                    stage.setX(centerXPosition);
//                    stage.setY(centerYPosition);

                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        store_id = preferences.get("store_id", null);
        System.out.println("store_id" + store_id);
        try {
            connect();
        } catch (NoRouteToHostException e) {
            InternetConnectDialog internetConnectDialog = new InternetConnectDialog();
            internetConnectDialog.call(reload);
        }
        GetDiscountRate();
        configureOrderListView();
        store_discount_rate.setText(discountRate+"%");
    }
    private void GetDiscountRate() {
        try {
            URL url = new URL("http://3.35.180.57:8080/GetStoreDiscount.do?store_id="+store_id);
            URLConnection con = url.openConnection();
            HttpURLConnection http = (HttpURLConnection) con;
            http.setRequestMethod("GET");
            http.setRequestProperty("Content-Type", "application/json;utf-8");
            http.setRequestProperty("Accept", "application/json");
            http.setDoOutput(true);

            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line;
            StringBuffer bf = new StringBuffer();

            while ((line = br.readLine()) != null) {
                bf.append(line);
            }
            br.close();

            System.out.println("response" + bf.toString());
            boolean result = getBool(bf.toString());
            ;
            if (result) {
                System.out.println("성공");
                discountRate = new JSONObject(bf.toString()).getInt("discount_rate");
            } else {
                System.out.println("실패");
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void store_is_open_change(boolean is_open, boolean isFromClose) {
        try {
            URL url = new URL("http://3.35.180.57:8080/OwnerSetStoreStatus.do");
            URLConnection con = url.openConnection();
            HttpURLConnection http = (HttpURLConnection) con;
            http.setRequestMethod("PUT");
            http.setRequestProperty("Content-Type", "application/json;utf-8");
            http.setRequestProperty("Accept", "application/json");
            http.setDoOutput(true);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("store_id", store_id);
            if (is_open) {
                jsonObject.put("is_open", "Y");
                preferences.put("is_open", "Y");

            } else {
                jsonObject.put("is_open", "N");
                preferences.put("is_open", "N");
            }


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
            boolean result = getBool(bf.toString());

            if (result) {
                System.out.println("성공");
//                if(isFromClose) {
//                    Stage stage = (Stage)main_page_stack_pane.getScene().getWindow();
//                    stage.close();
//                }
            } else {
                System.out.println("실패");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean getBool(String toString) {
        JSONObject jsonObject = new JSONObject(toString);
        return (jsonObject.getBoolean("result"));
    }

    //주문 들어온 리스트 찍기
    public void configureOrderListView() {
        try {
            URL url = new URL("http://3.35.180.57:8080/OrderFindByStoreId.do?store_id="+store_id);
            URLConnection con = url.openConnection();
            HttpURLConnection http = (HttpURLConnection) con;
            http.setRequestMethod("GET");
            http.setRequestProperty("Content-Type", "application/json;utf-8");
            http.setRequestProperty("Accept", "application/json");
            http.setDoOutput(true);

            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line;
            StringBuffer bf = new StringBuffer();

            while ((line = br.readLine()) != null) {
                bf.append(line);
            }
            br.close();

            System.out.println("response" + bf.toString());
            boolean result = menuUpdateSaveSoldOutParsing(bf.toString());

            //서버에서 response가 true 일때를 분기문에 추가시켜주기.
            if (result) {
                parsingOrders(bf.toString());
                getPageCount();
                if (orderList.orders.size() < ONEPAGEORDER){
                    setList(0, orderList.orders.size() % ONEPAGEORDER);
                }else{
                    setList( orderList.orders.size() - 1 - ONEPAGEORDER,orderList.orders.size() - 1);
                }
            }else{
                no_data.setVisible(true);
                no_data.setManaged(true);
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parsingOrders(String toString) {
        orderList = new Gson().fromJson(toString, OrderList.class);
        for (int i = 0; i < orderList.orders.size(); i++) {
            if(!preferences.get(orderList.orders.get(i).receipt_id, "").equals("")) {
                String receiptId = preferences.get(orderList.orders.get(i).receipt_id, "");
                for (int j = 0; j < orderList.orders.size(); j++) {
                    if (orderList.orders.get(j).getReceipt_id().equals(receiptId)) {
                        String time = preferences.get(orderList.orders.get(i).receipt_id + "time", "");
                        orderList.orders.get(i).setCompleteTime(time);
                    }
                }
            }
        }
//        Collections.reverse(orderList.orders);
    }

    private HBox makeCell(int index) {
        orderIndex = index;
        HBox hBox = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/order.fxml"));
            hBox = loader.load();
            hBox.setId(orderList.orders.get(index).receipt_id+"");
            OrderController controller = loader.<OrderController>getController();
            controller.setData(orderList.orders.get(index),index);
            hBox.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
//                    orderDetailsContainer.getChildren().remove(0,orderDetailsContainer.getChildren().size());
                    OrderDetailParsing details = controller.getDetail();
                    if (details != null) {

                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/orderDetails.fxml"));
                            orderDetailsContainer.getChildren().clear();
                            Parent parent = loader.load();
                            orderDetailsContainer.getChildren().add(parent);
                            OrderDetailsController detailcontroller = loader.<OrderDetailsController>getController();
                            detailcontroller.setData(details,controller.orderData);
                            detailcontroller.configureLeftUI();
                            detailcontroller.getChangeToCancel().addListener(new ChangeListener<Boolean>() {
                                @Override
                                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                                    if (newValue) {
                                        orderDetailsContainer.getChildren().remove(0,orderDetailsContainer.getChildren().size());
                                        orderList.orders.remove(index);
                                        whenDelete();
                                    }
                                }
                            });
                            detailcontroller.getChangeToAccept().addListener(new ChangeListener<Boolean>() {
                                @Override
                                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                                    if (newValue) {
                                        orderList.orders.get(index).setCompleteTime(setTimeLabel(detailcontroller.timeInt));
                                        controller.setData(orderList.orders.get(index), index);
                                        controller.changeToAccept();
//                                        detailcontroller.changeToAccept();
                                    }
                                }
                            });
                            detailcontroller.getChangeToDone().addListener(new ChangeListener<Boolean>() {
                                @Override
                                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                                    if (newValue) {
//                                        orderDetailsContainer.getChildren().remove(0,orderDetailsContainer.getChildren().size());
                                        orderList.orders.remove(index);
                                        whenDelete();
                                    }
                                }
                            });
                            detailcontroller.getNeedToSettingMainPrint().addListener(new ChangeListener<Boolean>() {
                                @Override
                                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                                    //orderDetailsContainer.getScene().getWindow();
                                    moveToSetting.moveSetting();
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            controller.configureUI();
//            controller.is_Done.addListener(new ChangeListener<Boolean>() {
//                @Override
//                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
//                    if (newValue) {
//                        orderListContainer.getChildren().remove(orderListContainer.lookup("#"+orderList.orders.get(index).receipt_id));
//                        orderList.orders.remove(index);
//                    }
//                }
//            });
//            controller.is_Cancel.addListener(new ChangeListener<Boolean>() {
//                @Override
//                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
//                    if (newValue) {
//                        orderListContainer.getChildren().remove(orderListContainer.lookup("#"+orderList.orders.get(index).receipt_id));
//                        orderList.orders.remove(index);
//                    }
//                }
//            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return hBox;
    }
    private String setTimeLabel(int time) {
        Calendar calendar   = GregorianCalendar.getInstance();
        String hourString   = DateConverter.pad(2, '0', calendar.get(Calendar.HOUR)   == 0 ? "12" : calendar.get(Calendar.HOUR) + "");
        String minuteString = DateConverter.pad(2, '0', calendar.get(Calendar.MINUTE) + "");


        System.out.println("time " + time);
        int setMinute = Integer.parseInt(minuteString) + time;
        System.out.println("minStr " + minuteString + " minInt" + setMinute);

        int ifOverSixty = setMinute/60;
        if(setMinute > 60 ) {
            setMinute -= 60 * ifOverSixty;
        }
        int setHour = Integer.parseInt(hourString) + ifOverSixty;

        return DateConverter.pad(2, '0', setHour+"") + ":" +DateConverter.pad(2, '0', setMinute+"")+ " 까지";
    }
    private void setList(int startIndex,int endIndex) {
        System.out.println("setList");
        if(orderList.orders.size() == 0) {
            System.out.println("noData");
            no_data.setVisible(true);
            no_data.setManaged(true);
        }else {
            paging_ui.setVisible(true);
        }
        childContainer.getChildren().remove(0,childContainer.getChildren().size());
        for (int i = endIndex; i > startIndex; i--) {
            HBox hBox = makeCell(i);
            childContainer.getChildren().add(hBox);
        }
    }

    private boolean menuUpdateSaveSoldOutParsing(String toString) {
        JSONObject jsonObject = new JSONObject(toString);
        return (jsonObject.getBoolean("result"));
    }

    /// Private
    private Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            while (true){
                int i = 0;
                webSocketClient.send("ping:::"+store_id);
                while (i<180){
                    i++;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    });
    private void connect() throws NoRouteToHostException{
        System.out.println("aaa");
        URI uri;

        try {
            uri = new URI("ws://3.35.180.57:8080/websocket");
        } catch (Exception e) {
            e.printStackTrace();
            ArrayList<Integer> arrayList = new ArrayList<>();
//            arrayList.remov
            return;
        }
//        catch (NoRouteToHostException e) {
//            e.printStackTrace();
//        }

        WebSocketClient ws = new WebSocketClient(uri, new Draft_17()) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                webSocketClient.send("connect:::" + store_id);
                System.out.println("open!!");
                if (thread.getState() == Thread.State.NEW) {
                    thread.start();
                }else{

                }
            }

            @Override
            public void onMessage(String message) {
                System.out.println("message!!");
                System.out.println(message);
                if (message.charAt(0) != '{'){

                } else {
                    player.seek(player.getStartTime());
                    player.play();

                    JSONObject jsonObject = new JSONObject(message);
                    Order order = new Gson().fromJson(message, Order.class);
                    if(order.discount_price == -1) {
                        order.discount_price = 0;
                    }
                    if(order.discount_rate != 0 ) {
                        order.total_price = (order.total_price * 100 / (100 - order.discount_rate));
                    }
                    order.order_state = Order.PREPARING;
                    order.order_count = jsonObject.getInt("each_count");
                    orderList.orders.add(order);
                    System.out.println(orderList.orders.size());
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
//                            orderListContainer.getChildren().add(makeCell(orderList.orders.size() - 1));
                            getPageCount();
                            if (CURRNETPAGE == ENTIREPAGE){
                                if (orderList.orders.size() % ONEPAGEORDER != 0 ) {
                                    setList(-1,(orderList.orders.size()-1) % ONEPAGEORDER);
                                }else{
                                    setList(-1,ONEPAGEORDER-1);
                                }
                            }else{
                                setList((orderList.orders.size()-1) - CURRNETPAGE * ONEPAGEORDER,(orderList.orders.size()-1) - CURRNETPAGE * ONEPAGEORDER + ONEPAGEORDER);
                            }
                            int temp = notReadedOrder.get() + 1;
                            notReadedOrder.set(temp);
                            popUp.show();
                            popUp.popup.showingProperty().addListener(new ChangeListener<Boolean>() {
                                @Override
                                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                                    if (newValue) {
                                        popUp.controller.changeCount(notReadedOrder.get());
                                    }else{
                                        notReadedOrder.set(0);
                                        player.stop();
                                    }
                                }
                            });
                        }
                    });
                }
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                System.out.println("close! reaseon :" + reason);
                webSocketClient = null;
                try {
                    OrderListController.this.connect();
                } catch (NoRouteToHostException e) {
                    e.printStackTrace();
                    InternetConnectDialog internetConnectDialog = new InternetConnectDialog();
                    internetConnectDialog.call(reload);
                }
            }

            @Override
            public void onError(Exception ex) {
                System.out.println("error! :" + ex);
            }
        };
        webSocketClient = ws;
        webSocketClient.connect();
    }

    @Override
    public void clickClose() {

    }

    @Override
    public void clickSet() {
        if(preferences.getInt("new_discount_rate", -1) != -1) {
            GetDiscountRate();
            new_store_discount_rate.setVisible(true);
            arrow_right.setVisible(true);
            new_store_discount_rate.setText(discountRate+"%");
        }else {
            new_store_discount_rate.setVisible(false);
            arrow_right.setVisible(false);
            return;
        }
    }

    public class AlarmPopUp{
        Popup popup;
        PopUpController controller;
        public AlarmPopUp() {
            try {
                popup = new Popup();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/popUp.fxml"));
                AnchorPane parent = loader.load();
                controller = loader.<PopUpController>getController();
                popup.getContent().add(parent);
                popup.setX(Screen.getScreens().get(0).getBounds().getMaxX()-popup.getWidth()-1);
                popup.setY(Screen.getScreens().get(0).getBounds().getMaxY()-popup.getHeight()-1);
            } catch (IOException /*| ClassNotFoundException*/ e) {
                e.printStackTrace();
            }
        }
        private void show(){
            popup.show(Main.getPrimaryStage());
        }
        private void hide(){
            popup.hide();
        }
    }

    ///////// 페이징 처리

    public void getPageCount(){
        ArrayList<Order> orders = orderList.orders;
        if (orders.size() % ONEPAGEORDER == 0){
            ENTIREPAGE = orders.size() / ONEPAGEORDER;
        }else{
            ENTIREPAGE = orders.size() / ONEPAGEORDER + 1;
        }
        if (ENTIREPAGE<CURRNETPAGE){
            CURRNETPAGE--;
        }
        pagingLabel.setText(CURRNETPAGE + " / " + ENTIREPAGE);
    }
    public void tapPrevPage(ActionEvent event) {
        if (CURRNETPAGE == 1){
            return;
        }
        CURRNETPAGE--;
        pagingLabel.setText(CURRNETPAGE + " / " + ENTIREPAGE);
        setList((orderList.orders.size()-1) - (CURRNETPAGE - 1) * ONEPAGEORDER - ONEPAGEORDER,(orderList.orders.size()-1) - (CURRNETPAGE - 1) * ONEPAGEORDER );
    }
    public void tapNextPage(ActionEvent event) {
        if (CURRNETPAGE == ENTIREPAGE){
            return;
        }
        CURRNETPAGE++;
        pagingLabel.setText(CURRNETPAGE + " / " + ENTIREPAGE);
        if (CURRNETPAGE == ENTIREPAGE){
            if (orderList.orders.size() % ONEPAGEORDER != 0 ) {
                setList(-1,(orderList.orders.size()-1) % ONEPAGEORDER);
            }else{
                setList(-1,ONEPAGEORDER-1);
            }
        }else{
            setList((orderList.orders.size()-1) - CURRNETPAGE * ONEPAGEORDER,(orderList.orders.size()-1) - CURRNETPAGE * ONEPAGEORDER + ONEPAGEORDER);
        }
    }
    public void whenDelete(){
        getPageCount();
        if (CURRNETPAGE == 1 && ENTIREPAGE == 1){
            if (orderList.orders.size() % ONEPAGEORDER != 0 ) {
                setList(-1,(orderList.orders.size()-1) % ONEPAGEORDER);
            }else{
                if (orderList.orders.size() == 0 ){
                    //아무것도안함
                }else {
                    setList(-1, ONEPAGEORDER - 1);
                }
            }
        }else {
            setList((orderList.orders.size()-1) - (CURRNETPAGE - 1) * ONEPAGEORDER - ONEPAGEORDER,(orderList.orders.size()-1) - (CURRNETPAGE - 1) * ONEPAGEORDER );
        }
    }
}
