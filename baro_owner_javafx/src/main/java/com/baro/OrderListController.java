package com.baro;

import com.baro.JsonParsing.Order;
import com.baro.JsonParsing.OrderDetail;
import com.baro.JsonParsing.OrderDetailParsing;
import com.baro.JsonParsing.OrderList;
import com.baro.controllers.NewOrderController;
import com.baro.controllers.OrderController;
import com.baro.controllers.PopUpController;
import com.baro.controllers.orderDetail.OrderDetailsController;
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
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;
import javafx.stage.*;
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
    @FXML
    private AnchorPane orderDetailsContainer;
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
    String isOpen = preferences.get("is_open", "");
    /// Life cycle
    @FXML
    public void initialize() {
        if(isOpen.equals("Y")) {
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
        System.out.println("store_id" + store_id);
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
        System.out.println(orderList.orders.size()+"");
//        Collections.reverse(orderList.orders);
    }

    private HBox makeCell(int index) {
        HBox hBox = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/new_order.fxml"));
            hBox = loader.load();
            hBox.setId(orderList.orders.get(index).receipt_id+"");
            NewOrderController controller = loader.<NewOrderController>getController();
            controller.setData(orderList.orders.get(index),index);
            hBox.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    orderDetailsContainer.getChildren().remove(0,orderDetailsContainer.getChildren().size());
                    OrderDetailParsing details = controller.getDetail();
                    if (details != null){
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/orderDetails.fxml"));
                        try {
                            Parent parent = loader.load();
                            orderDetailsContainer.getChildren().add(parent);
                            OrderDetailsController detailcontroller = loader.<OrderDetailsController>getController();
                            detailcontroller.setData(details,controller.orderData);
                            detailcontroller.configureLeftUI();
                            detailcontroller.makeReceiptPreView();
                            detailcontroller.getChangeToCancel().addListener(new ChangeListener<Boolean>() {
                                @Override
                                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                                    if (newValue) {
                                        orderDetailsContainer.getChildren().remove(0,orderDetailsContainer.getChildren().size());
                                        orderListContainer.getChildren().remove(orderListContainer.lookup("#"+orderList.orders.get(index).receipt_id));

                                    }
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
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
    private void configureSideView() {
        setTabs(orderListTab, "주문");
        setTabs(inventoryManagementTab, "재고관리");
        setTabs(infoChangeTab, "주문내역");
        setTabs(calculateTab, "정산");
        setTabs(statisticsTab, "통계");
        setTabs(settingsTab, "설정");

        tabContainer.setTabMinWidth(tabWidth);
        tabContainer.setTabMaxWidth(tabWidth);
        tabContainer.setTabMinHeight(tabHeight);
        tabContainer.setTabMaxHeight(tabHeight);
        tabContainer.setRotateGraphic(true);
        tabContainer.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
            @Override
            public void changed(ObservableValue<? extends Tab> observable, Tab oldValue, Tab newValue) {
                switch (observable.getValue().getId()) {
                    case "orderListTab":
                        configureTab(orderListTab, orderListSideContainer, null);
                        break;
                    case "inventoryManagementTab":
                        configureTab(inventoryManagementTab, inventoryManagementContainer, getClass().getResource("/inventory_management.fxml") );
                        break;
                    case "infoChangeTab":
                        configureTab(infoChangeTab, infoChangeContainer, getClass().getResource("/orderHistory.fxml"));
                        break;
                    case "calculateTab":
                        configureTab(calculateTab,  calculateContainer, getClass().getResource("/calculate.fxml") );
                        break;
                    case "statisticsTab":
                        configureTab(statisticsTab,  statisticsContainer, getClass().getResource("/statistics.fxml"));
                        break;
                    case "settingsTab":
                        configureTab(settingsTab,  settingsContainer, getClass().getResource("/settings.fxml"));
                        break;
                    default:
                        break;
                }
                oldValue.setStyle("-fx-background-color: #8D45E7");
            }
        });
        orderListTab.setStyle("-fx-background-color: #8333e6");
    }
    private void setTabs(Tab tab, String title) {
        double imageWidth = 40.0;
//        ImageView imageView = new ImageView(new Image());
//        imageView.setFitHeight(imageWidth);
//        imageView.setFitWidth(imageWidth);
//        tabPane.setCenter(imageView);
        Label label = new Label(title);
        label.setMaxWidth(tabHeight/2);
        label.setMinWidth(tabHeight/2);
        label.setPadding(new Insets(0, 0, 0, 0));
        label.setStyle("-fx-text-fill: white; -fx-font-size: 30px; -fx-font-weight: normal; -fx-font-family: 'Noto Sans Korean Regular'");
        label.setAlignment(Pos.CENTER);

        BorderPane tabPane = new BorderPane();
        tabPane.setRotate(90.0);
        tabPane.setMaxWidth(tabHeight/2);
        tabPane.setMinWidth(tabHeight/2);
        tabPane.setCenter(label);
        tab.setGraphic(tabPane);
    }
    //이미지경로 넣기 위한 title 뒤에 String iconPath 뺏음
    private void configureTab(Tab tab, AnchorPane containerPane, URL resourceURL) {
        tab.setStyle("-fx-background-color: #8333e6");
        try {
            if(resourceURL == null) {
                containerPane.getChildren().removeAll();
            }else {
                Parent contentView = FXMLLoader.load(resourceURL);
                if(containerPane.getChildren().size() != 0) {
                    containerPane.getChildren().remove(0);
                }
                containerPane.getChildren().add(contentView);
                AnchorPane.setTopAnchor(contentView, 0.0);
                AnchorPane.setBottomAnchor(contentView, 0.0);
                AnchorPane.setRightAnchor(contentView, 0.0);
                AnchorPane.setLeftAnchor(contentView, 0.0);
            }

        } catch (IOException e) {
            e.printStackTrace();
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
            ArrayList<Integer> arrayList = new ArrayList<>();
//            arrayList.remov
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
        if (orders.size() % ONEPAGEORDER == 0){
            ENTIREPAGE = orders.size() / ONEPAGEORDER;
        }else{
            ENTIREPAGE = orders.size() / ONEPAGEORDER + 1;
        }
        pagingButton.setText(CURRNETPAGE + " / " + ENTIREPAGE);
    }
    public void tapPrevPage(ActionEvent event) {
        if (CURRNETPAGE == 1){
            return;
        }
        CURRNETPAGE--;
        pagingButton.setText(CURRNETPAGE + " / " + ENTIREPAGE);
        setList((orderList.orders.size()-1) - (CURRNETPAGE - 1) * ONEPAGEORDER - ONEPAGEORDER,(orderList.orders.size()-1) - (CURRNETPAGE - 1) * ONEPAGEORDER );
    }
    public void tapNextPage(ActionEvent event) {
        if (CURRNETPAGE == ENTIREPAGE){
            return;
        }
        CURRNETPAGE++;
        pagingButton.setText(CURRNETPAGE + " / " + ENTIREPAGE);
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

}
