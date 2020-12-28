package sample;

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
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.Toggle;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
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

    @FXML private JFXTabPane categoryTabPane;
    @FXML private JFXListView<TextFlow> menuList;
    Preferences preferences = Preferences.userRoot();

    private String owner_store_id;
    public CategoryParsing categoryParsing;
    public MenuParsing menuParsing;

    private int lastSelectedIndex;
    @FXML
    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        Scene scene = new Scene(new Group(), 500, 400);
//        scene.getStylesheets().add("path/inventory_style.css");
        owner_store_id = preferences.get("store_id", "");
        getOwnerStoreCategory();
        getOwnerStoreMenu();
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
                    TextFlow cell = new TextFlow();
                    if(newTab.getId().equals(menu.category_id+"")) {
                        Text menuName = new Text(menu.menu_name+"\n");
                        Text menuInfo = new Text(menu.menu_info+"\t\t");
                        Text menuPrice = new Text(menu.menu_defaultprice+" 원");
                        JFXToggleButton toggleButton = new JFXToggleButton();
                        toggleButton.toggleColorProperty().set(Paint.valueOf("#ff0000"));
                        if(menu.is_soldout.equals("Y")) {
                            toggleButton.setSelected(true);
                        }
                        toggleButton.selectedProperty().addListener(new ChangeListener<Boolean>() {
                            @Override
                            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                                if(newValue) {
                                    menuUpdateSaveSoldOut(menu.menu_id);
                                }
                                else {
                                    menuUpdateSaveInStoke(menu.menu_id);
                                }
                            }
                        });
                        cell.getChildren().addAll(menuName, menuInfo, menuPrice);

                        menuList.getItems().add(cell);
                        System.out.println("getListView :" + menuList.getPrefWidth());
                        cell.getChildren().get(3).setTranslateX(menuList.getPrefWidth());
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
        System.out.println("categoryParsing : "+ categoryParsing.toString());
        makeCategoryTab();
    }

    private void makeCategoryTab() {
        for (int i = 0; i < categoryParsing.category.size(); i++) {
            Category category = categoryParsing.category.get(i);
            Tab tab = new Tab(category.category_name);
            tab.setId(category.category_id+"");
            categoryTabPane.getTabs().add(tab);
        }
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
