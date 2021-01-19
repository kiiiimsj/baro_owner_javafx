package com.baro;

import com.baro.JsonParsing.Order;
import com.baro.JsonParsing.OrderList;
import com.baro.controllers.NewOrderController;
import com.baro.controllers.OrderController;
import com.baro.controllers.PopUpController;
import com.google.gson.Gson;
import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.controls.JFXToggleButton;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;
import javafx.stage.Popup;
import javafx.stage.Screen;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;
import sample.Main;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.prefs.Preferences;


public class OrderListController {

    private final String TAG = this.getClass().getSimpleName();

    @FXML
    private JFXTabPane tabContainer;

    @FXML
    private Tab orderListTab;
    @FXML
    private AnchorPane orderListSideContainer;

    @FXML
    private Tab inventoryManagementTab;

    @FXML
    private AnchorPane inventoryManagementContainer;

    @FXML
    private Tab infoChangeTab;
    @FXML
    private AnchorPane infoChangeContainer;

    @FXML
    private Tab calculateTab;
    @FXML
    private AnchorPane calculateContainer;

    @FXML
    private Tab statisticsTab;
    @FXML
    private AnchorPane statisticsContainer;

    @FXML
    private Tab settingsTab;
    @FXML
    private AnchorPane settingsContainer;
    @FXML
    private TilePane orderListContainer;
    @FXML
    private JFXToggleButton isOpenBtn;
    @FXML
    private TilePane childContainer;
    @FXML
    private Button pagingButton;
    private WebSocketClient webSocketClient;

    public static OrderList orderList;

    private double tabWidth = 200.0;
    private double tabHeight = 250.0;
    public static int lastSelectedTabIndex = 0;
    public final static int ONEPAGEORDER = 7; // 한 페이지에 들어가는 갯수
    public static int CURRNETPAGE = 1; // 현재 페이지
    public static int ENTIREPAGE = 1; // 전체페이지 수
    public static Boolean LASTPAGEFULL = false; // 마지막페이지가 가득찼냐

    private SimpleIntegerProperty notReadedOrder = new SimpleIntegerProperty();
    String store_id;
    Preferences preferences = Preferences.userRoot();
    AlarmPopUp popUp = new AlarmPopUp();
    boolean isOpen = preferences.getBoolean("is_open", false);
    /// Life cycle
    @FXML
    public void initialize() {
        if(isOpen) {
            isOpenBtn.setText("영업종료 하기");
            isOpenBtn.setStyle("-fx-background-color: red; -fx-text-fill: #ffffff; -fx-font-size: 20pt; -fx-font-family: 'Noto Sans Korean Regular'");
        } else {
            isOpenBtn.setText("영업게시 하기");
            isOpenBtn.setStyle("-fx-background-color: #8333e6; -fx-text-fill: #ffffff; -fx-font-size: 20pt; -fx-font-family: 'Noto Sans Korean Regular'");
        }


        isOpenBtn.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                System.out.println(newValue);
                store_is_open_change(newValue);
            }
        });
        notReadedOrder.addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                popUp.controller.changeCount((int)newValue);
            }
        });

        store_id = preferences.get("store_id", null);
        connect();
        configureSideView();
        configureOrderListView();
    }

    public void store_is_open_change(boolean is_open) {
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
                isOpenBtn.setText("영업종료 하기");
                isOpenBtn.setStyle("-fx-background-color: red; -fx-text-fill: #ffffff; -fx-font-size: 20pt; -fx-font-family: 'Noto Sans Korean Regular'");
                //isOpenBtn.setBackground(new Background(new BackgroundFill(Color.color(131.0, 51.0, 230.0, 1.0), CornerRadii.EMPTY, Insets.EMPTY)));

            } else {
                jsonObject.put("is_open", "N");
                isOpenBtn.setText("영업게시 하기");
                isOpenBtn.setStyle("-fx-background-color: #8333e6; -fx-text-fill: #ffffff; -fx-font-size: 20pt; -fx-font-family: 'Noto Sans Korean Regular'");
                //isOpenBtn.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
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
    private void configureOrderListView() {
        try {
            URL url = new URL("http://3.35.180.57:8080/OrderFindByStoreId.do?store_id=1");
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
                    setList(0,orderList.orders.size() % ONEPAGEORDER);
                }else{
                    setList(0,7);
                }

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
        Collections.reverse(orderList.orders);
    }

    private HBox makeCell(int index) {
        HBox hBox = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/new_order.fxml"));
            hBox = loader.load();
            hBox.setId(orderList.orders.get(index).receipt_id+"");
            NewOrderController controller = loader.<NewOrderController>getController();
            controller.setData(orderList.orders.get(index),index);
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
            controller.configureUI();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return hBox;
    }

    private void setList(int startIndex,int endIndex) {
        childContainer.getChildren().remove(0,childContainer.getChildren().size());
        for (int i = startIndex; i < endIndex; i++) {
            HBox hBox = makeCell(i);
            childContainer.getChildren().add(hBox);
        }

    }

    private boolean menuUpdateSaveSoldOutParsing(String toString) {
        JSONObject jsonObject = new JSONObject(toString);
        return (jsonObject.getBoolean("result"));
    }

    /// Private
    private void configureSideView() {
        tabContainer.setTabMinWidth(tabWidth);
        tabContainer.setTabMaxWidth(tabWidth);
        tabContainer.setTabMinHeight(tabHeight);
        tabContainer.setTabMaxHeight(tabHeight);
        tabContainer.setRotateGraphic(true);
        EventHandler<Event> replaceBackgroundColorHandler = event -> {
            lastSelectedTabIndex = tabContainer.getSelectionModel().getSelectedIndex();
            Tab currentTab = (Tab) event.getTarget();
            if (currentTab.isSelected()) {
                currentTab.setStyle("-fx-background-color: #8333e6");
            } else {
                currentTab.setStyle("-fx-background-color: #8D45E7");
            }
        };
        EventHandler<Event> logoutHandler = event -> {
            Tab currentTab = (Tab) event.getTarget();
            if (currentTab.isSelected()) {
                tabContainer.getSelectionModel().select(lastSelectedTabIndex);
            }
        };

        configureTab(orderListTab, "주문", orderListSideContainer, getClass().getResource("order_list.fxml"), replaceBackgroundColorHandler);
        configureTab(inventoryManagementTab, "재고관리", inventoryManagementContainer, getClass().getResource("/inventory_management.fxml"), replaceBackgroundColorHandler);
        configureTab(infoChangeTab, "주문내역", infoChangeContainer, getClass().getResource("/orderHistory.fxml"), replaceBackgroundColorHandler);
        configureTab(calculateTab, "정산", calculateContainer, getClass().getResource("/calculate.fxml"), replaceBackgroundColorHandler);
        configureTab(statisticsTab, "통계", statisticsContainer, getClass().getResource("/statistics.fxml"), replaceBackgroundColorHandler);
        configureTab(settingsTab, "설정", settingsContainer, getClass().getResource("/settings.fxml"), replaceBackgroundColorHandler);

        orderListTab.setStyle("-fx-background-color: #8333e6");
    }

    //이미지경로 넣기 위한 title 뒤에 String iconPath 뺏음
    private void configureTab(Tab tab, String title, AnchorPane containerPane, URL resourceURL, EventHandler<Event> onSelectionChangedEvent) {
        double imageWidth = 40.0;
        //ImageView imageView = new ImageView(new Image(iconPath));
        //imageView.setFitHeight(imageWidth);
        //imageView.setFitWidth(imageWidth);
        Label label = new Label(title);
        label.setMaxWidth(tabWidth - 20);
        label.setPadding(new Insets(5, 0, 0, 0));
        label.setStyle("-fx-text-fill: white; -fx-font-size: 28pt; -fx-font-weight: normal; -fx-font-family: 'Noto Sans Korean Regular'");
        label.setTextAlignment(TextAlignment.CENTER);
        BorderPane tabPane = new BorderPane();
        tabPane.setRotate(90.0);
        tabPane.setMaxWidth(tabWidth);
        //tabPane.setCenter(imageView);
        tabPane.setBottom(label);
        tab.setText("");
        tab.setGraphic(tabPane);
        tab.setOnSelectionChanged(onSelectionChangedEvent);
        if (containerPane != null && resourceURL != null) {
            try {
                Parent contentView = FXMLLoader.load(resourceURL);
                containerPane.getChildren().add(contentView);
                AnchorPane.setTopAnchor(contentView, 0.0);
                AnchorPane.setBottomAnchor(contentView, 0.0);
                AnchorPane.setRightAnchor(contentView, 0.0);
                AnchorPane.setLeftAnchor(contentView, 0.0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

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
    private void connect() {
        System.out.println("aaa");
        URI uri;

        try {
            uri = new URI("ws://3.35.180.57:8080/websocket");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        webSocketClient = new WebSocketClient(uri, new Draft_17()) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {

                webSocketClient.send("connect:::" + 1);
                System.out.println("open!!");
                thread.start();
            }

            @Override
            public void onMessage(String message) {
                System.out.println("message!!");
                System.out.println(message);
                if (message.charAt(0) != '{'){

                } else {

                    JSONObject jsonObject = new JSONObject(message);
                    Order order = new Gson().fromJson(message, Order.class);
                    order.order_state = Order.PREPARING;
                    orderList.orders.add(order);
                    System.out.println(orderList.orders.size());
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            orderListContainer.getChildren().add(0, makeCell(orderList.orders.size() - 1));
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
            }

            @Override
            public void onError(Exception ex) {
                System.out.println("error! :" + ex);
            }
        };
        webSocketClient.connect();
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
                popup.setX(Screen.getScreens().get(0).getBounds().getMaxX()-popup.getWidth());
                popup.setY(Screen.getScreens().get(0).getBounds().getMaxY()-popup.getHeight());
            } catch (IOException e) {
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
        if (orders.size() % 7 == 0){
            ENTIREPAGE = orders.size() / 7;
        }else{
            ENTIREPAGE = orders.size() / 7 + 1;
        }
        pagingButton.setText(CURRNETPAGE + " / " + ENTIREPAGE);
    }
    public void tapPrevPage(ActionEvent event) {
        if (CURRNETPAGE == 1){
            return;
        }
        CURRNETPAGE--;
        pagingButton.setText(CURRNETPAGE + " / " + ENTIREPAGE);
        setList((CURRNETPAGE - 1) * ONEPAGEORDER,(CURRNETPAGE - 1) * ONEPAGEORDER + 7);
    }
    public void tapNextPage(ActionEvent event) {
        if (CURRNETPAGE == ENTIREPAGE){
            return;
        }
        CURRNETPAGE++;
        pagingButton.setText(CURRNETPAGE + " / " + ENTIREPAGE);
        if (CURRNETPAGE == ENTIREPAGE){
            setList((CURRNETPAGE - 1) * ONEPAGEORDER,(CURRNETPAGE - 1) * ONEPAGEORDER + orderList.orders.size() % ONEPAGEORDER);
        }else{
            setList((CURRNETPAGE - 1) * ONEPAGEORDER,(CURRNETPAGE - 1) * ONEPAGEORDER + 7);
        }
    }

}
