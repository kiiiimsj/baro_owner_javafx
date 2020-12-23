package com.baro;

import com.jfoenix.controls.JFXTabPane;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.TilePane;
import javafx.scene.text.TextAlignment;
import java.io.IOException;
import java.net.URL;


public class OrderListController {
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


    private double tabWidth = 90.0;
    public static int lastSelectedTabIndex = 0;
    /// Life cycle
    @FXML
    public void initialize() {

        configureSideView();
        configureMainView();
    }

    private void configureMainView(){

    }

    /// Private
    private void configureSideView() {
        tabContainer.setTabMinWidth(tabWidth);
        tabContainer.setTabMaxWidth(tabWidth);
        tabContainer.setTabMinHeight(tabWidth);
        tabContainer.setTabMaxHeight(tabWidth);
        tabContainer.setRotateGraphic(true);
        EventHandler<Event> replaceBackgroundColorHandler = event -> {
            lastSelectedTabIndex = tabContainer.getSelectionModel().getSelectedIndex();
            Tab currentTab = (Tab) event.getTarget();
            if (currentTab.isSelected()) {
                currentTab.setStyle("-fx-background-color: -fx-focus-color;");
            } else {
                currentTab.setStyle("-fx-background-color: -fx-accent;");
            }
        };
        EventHandler<Event> logoutHandler = event -> {
            Tab currentTab = (Tab) event.getTarget();
            if (currentTab.isSelected()) {
                tabContainer.getSelectionModel().select(lastSelectedTabIndex);
            }
        };

        configureTab(orderListTab, "주 문", orderListSideContainer, getClass().getResource("order_list.fxml"), replaceBackgroundColorHandler);
        configureTab(inventoryManagementTab, "재고관리", inventoryManagementContainer, getClass().getResource("/inventory_management.fxml"), replaceBackgroundColorHandler);
        configureTab(infoChangeTab, "정보변경", infoChangeContainer, getClass().getResource("/info_change.fxml"), replaceBackgroundColorHandler);
        configureTab(calculateTab, "정 산", calculateContainer, getClass().getResource("/calculate.fxml"), replaceBackgroundColorHandler);
        configureTab(statisticsTab, "통 계", statisticsContainer, getClass().getResource("/statistics.fxml"), replaceBackgroundColorHandler);
        configureTab(settingsTab, "설 정", settingsContainer, getClass().getResource("/settings.fxml"), replaceBackgroundColorHandler);

        orderListTab.setStyle("-fx-background-color: -fx-focus-color;");
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
        label.setStyle("-fx-text-fill: black; -fx-font-size: 8pt; -fx-font-weight: normal;");
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
}
