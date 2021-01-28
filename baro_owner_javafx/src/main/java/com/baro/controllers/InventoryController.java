package com.baro.controllers;

import com.baro.JsonParsing.Category;
import com.baro.JsonParsing.CategoryParsing;
import com.baro.JsonParsing.Menu;
import com.baro.JsonParsing.MenuParsing;
import com.google.gson.Gson;
import com.jfoenix.controls.JFXListCell;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.controls.JFXToggleButton;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.MapChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class InventoryController implements Initializable {

    public GridPane menuList_header;
    public VBox base;
    @FXML private JFXTabPane categoryTabPane;
    @FXML private JFXListView<GridPane> menuList;
    Preferences preferences = Preferences.userRoot();

    private String owner_store_id;
    public CategoryParsing categoryParsing;
    public MenuParsing menuParsing;

    ColumnConstraints col1 = new ColumnConstraints();
    ColumnConstraints col2 = new ColumnConstraints();
    ColumnConstraints col3 = new ColumnConstraints();

    RowConstraints row1 = new RowConstraints();
    RowConstraints row2 = new RowConstraints();

    private int lastSelectedIndex;
    @FXML
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        owner_store_id = preferences.get("store_id", "");
        setMenuListHeader();
        getOwnerStoreCategory();
        getOwnerStoreMenu();
    }

    private void setMenuListHeader() {
        System.out.println(base.getPrefWidth()/3);
        col1.setHgrow(Priority.ALWAYS);
        col1.setMaxWidth(base.getPrefWidth()/3);

        col2.setHgrow(Priority.ALWAYS);
        col2.setHalignment(HPos.CENTER);
        col2.setMaxWidth(base.getPrefWidth()/3);

        col3.setHgrow(Priority.ALWAYS);
        col3.setHalignment(HPos.CENTER);
        col3.setMaxWidth(base.getPrefWidth()/3);

        row1.setVgrow(Priority.ALWAYS);

        row2.setVgrow(Priority.NEVER);
        row2.setMaxHeight(20);

        menuList_header.getColumnConstraints().add(0, col2);
        menuList_header.getColumnConstraints().add(1, col2);
        menuList_header.getColumnConstraints().add(2, col3);
        menuList_header.getRowConstraints().add(0, row1);

        Label menuInfoLabel = new Label("메뉴 정보");
        Label menuPriceLabel = new Label("메뉴 가격");
        Label setCanSellLabel = new Label("재고 관리");

        menuList_header.addRow(0 , menuInfoLabel, menuPriceLabel, setCanSellLabel);
    }

    private void getOwnerStoreMenu() {
        if(owner_store_id.equals("")) {
            System.out.println("가져올 가게 정보가 없습니다.");
        }
        try{
            URL url = new URL("http://3.35.180.57:8080/MenuFindByStoreId.do?store_id="+owner_store_id);
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

            boolean result = menuUpdateSaveSoldOutParsing(bf.toString());

            //서버에서 response가 true 일때를 분기문에 추가시켜주기.
            if(result){
                parsingMenu(bf.toString());
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

    private void parsingMenu(String toString) {
        menuParsing = new Gson().fromJson(toString, MenuParsing.class);

        setCategoryClickEvent();

        categoryTabPane.getSelectionModel().select(1);
        categoryTabPane.getSelectionModel().select(0);
    }
    private void setCategoryClickEvent() {
        categoryTabPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
            @Override
            public void changed(ObservableValue<? extends Tab> observable, Tab oldTab, Tab newTab) {
                menuList.getItems().clear();
                for (int i = 0; i < menuParsing.menu.size(); i++) {
                    Menu menu = menuParsing.menu.get(i);
                    if(newTab.getId().equals(menu.category_id+"")) {
                        GridPane cell = new GridPane();
//                        cell.setMaxHeight(110);
                        Label menuName = new Label(menu.menu_name);
                        Label menuInfo = new Label(menu.menu_info);
                        Label menuPrice = new Label(menu.menu_defaultprice + "원");

                        menuInfo.setStyle("-fx-font-size: 15pt");
                        JFXToggleButton toggleButton = new JFXToggleButton();
                        toggleButton.setText("판매중");
                        toggleButton.setStyle("-fx-font-size: 20; -fx-text-fill: forestgreen;");
//                        toggleButton.unToggleColorProperty().set((Paint.valueOf("#228B22")));
                        toggleButton.toggleColorProperty().set((Paint.valueOf("#FF0000")));
                        toggleButton.setUnToggleColor((Paint.valueOf("#228B22")));
                        toggleButton.setUnToggleLineColor((Paint.valueOf("#558955")));

                        if (menu.is_soldout.equals("Y")) {
                            toggleButton.setText("품절");
                            toggleButton.setStyle("-fx-font-size: 20; -fx-text-fill: red");
                            toggleButton.setSelected(true);
                        }
                        toggleButton.selectedProperty().addListener(new ChangeListener<Boolean>() {
                            @Override
                            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                                if (newValue) {
                                    menuUpdateSaveSoldOut(menu.menu_id);
                                    toggleButton.setText("품절");
                                    toggleButton.setStyle("-fx-font-size: 20; -fx-text-fill: red");
                                } else {
                                    menuUpdateSaveInStoke(menu.menu_id);
                                    toggleButton.setText("판매중");
                                    toggleButton.setStyle("-fx-font-size: 20; -fx-text-fill: forestgreen");
//                                    toggleButton.toggleColorProperty().setValue(Paint.valueOf("#228B22"));
                                }
                            }
                        });
                        cell.getColumnConstraints().add(0, col1);
                        cell.getColumnConstraints().add(1, col2);
                        cell.getColumnConstraints().add(2, col3);

                        cell.getRowConstraints().add(0, row1);
                        cell.getRowConstraints().add(1, row2);
                        cell.addRow(0, menuName, menuPrice, toggleButton);
                        cell.addRow(1, menuInfo);
                        menuList.getItems().add(cell);
                    }
                }
            }
        });
    }

    public void getOwnerStoreCategory() {
        if(owner_store_id.equals("")) {
            System.out.println("가져올 가게 정보가 없습니다.");
        }
        try{
            URL url = new URL("http://3.35.180.57:8080/CategoryFindByStoreId.do?store_id="+owner_store_id);
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

            boolean result = menuUpdateSaveSoldOutParsing(bf.toString());

            //서버에서 response가 true 일때를 분기문에 추가시켜주기.
            if(result){
                parsingCategory(bf.toString());
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

    private void parsingCategory(String toString) {
        categoryParsing = new Gson().fromJson(toString, CategoryParsing.class);
        makeCategoryTab();
    }

    private void makeCategoryTab() {
        for (int i = 0; i < categoryParsing.category.size(); i++) {
            Category category = categoryParsing.category.get(i);
            Tab tab = new Tab(category.category_name);
            tab.setId(category.category_id+"");
            categoryTabPane.getTabs().add(tab);
        }
        categoryTabPane.setTabMaxHeight(100);
        categoryTabPane.setTabMinHeight(100);
    }

    public void menuUpdateSaveSoldOut(int menuId) {
        try{
            URL url = new URL("http://3.35.180.57:8080/MenuUpdateSaveSoldOut.do?menu_id="+menuId);
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

            boolean result = menuUpdateSaveSoldOutParsing(bf.toString());

            //서버에서 response가 true 일때를 분기문에 추가시켜주기.
            if(result){

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
    public void menuUpdateSaveInStoke(int menuId) {
        try{
            URL url = new URL("http://3.35.180.57:8080/MenuUpdateDeleteSoldOut.do?menu_id="+menuId);
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

            boolean result = menuUpdateSaveSoldOutParsing(bf.toString());

            //서버에서 response가 true 일때를 분기문에 추가시켜주기.
            if(result){

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
    private boolean menuUpdateSaveSoldOutParsing(String toString) {
        JSONObject jsonObject = new JSONObject(toString);
        return (jsonObject.getBoolean("result"));
    }
}
