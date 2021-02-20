package com.baro.controllers;

import com.baro.Dialog.InternetConnectDialog;
import com.baro.JsonParsing.LoginParsing;

import com.google.gson.Gson;
import com.jfoenix.controls.JFXCheckBox;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.json.JSONObject;
import org.json.JSONString;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;


public class LoginController implements Initializable {
    public VBox base;
    public HBox top_bar;
    public FontAwesomeIconView minimum;
    public FontAwesomeIconView maximum;
    public FontAwesomeIconView close;
    @FXML private TextField phone_tf;
    @FXML private PasswordField password_tf;
    @FXML private Button login_btn;
    @FXML private JFXCheckBox save_id_pw;

    double initialX;
    double initialY;
    //데이터 저장을 위한 preferences
    Preferences preferences = Preferences.userRoot();
    public MainController.ReturnOrderListWhenApplicationClose returnOrderListWhenApplicationClose;
    public InternetConnectDialog.Reload reload;
    LoginParsing loginParsing;

   @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("로그인 컨트롤러 실행!!");
       if(preferences.getBoolean("isSave", false)) {
           save_id_pw.setSelected(true);
           phone_tf.setText(preferences.get("userId", ""));
           password_tf.setText(preferences.get("userPw", ""));
       }else {
           save_id_pw.setSelected(false);
       }
       topBarConfigureUI();
    }

    private void topBarConfigureUI() {
        top_bar.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent me) {
                if (me.getButton() != MouseButton.MIDDLE) {
                    initialX = me.getSceneX();
                    initialY = me.getSceneY();
                }
            }
        });

        top_bar.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent me) {
                if (me.getButton() != MouseButton.MIDDLE) {
                    top_bar.getScene().getWindow().setX(me.getScreenX() - initialX);
                    top_bar.getScene().getWindow().setY(me.getScreenY() - initialY);
                }
            }
        });
        minimum.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
                stage.setIconified(true);
            }
        });
        maximum.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
                stage.setFullScreenExitHint(" ");
                stage.setFullScreen(true);
            }
        });
        close.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
                stage.close();
            }
        });
    }

    public void loginAction(ActionEvent event) {

       //점주의 휴대폰번호 및 비밀번호
        String id = phone_tf.getText();
        String owner_pass = password_tf.getText();

        System.out.println("phone1" + id);
        System.out.println("pass1" + owner_pass);

       System.out.println("로그인 버튼 클릭!!");
       try{
           URL url = new URL("http://3.35.180.57:8080/OwnerLogin.do");
           URLConnection con = url.openConnection();
           HttpURLConnection http = (HttpURLConnection) con;
           http.setRequestMethod("POST");
           http.setRequestProperty("Content-Type","application/json;utf-8");
           http.setRequestProperty("Accept","application/json");
           http.setDoOutput(true);
           JSONObject jsonObject = new JSONObject();
           jsonObject.put("id", id);
           jsonObject.put("pass", owner_pass);
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

           boolean result = loginjsonParsing(bf.toString());

           //서버에서 response가 true 일때를 분기문에 추가시켜주기.
           if(result){
               if(save_id_pw.isSelected()) {
                   preferences.putBoolean("isSave", true);
                   preferences.put("userId", id);
                   preferences.put("userPw", owner_pass);
               }
               else {
                   preferences.remove("isSave");
                   preferences.remove("userId");
                   preferences.remove("userPw");
               }
               Stage primaryStage = (Stage)login_btn.getScene().getWindow();
               FXMLLoader loader = new FXMLLoader(getClass().getResource("/main_page.fxml"));



               MainController mainController = new MainController(returnOrderListWhenApplicationClose);
               mainController.reload = reload;
               loader.setController(mainController);

               if(mainController == null) {
                   System.out.println("mainControllerNull");
               }
//               mainController.returnOrderListWhenApplicationClose = returnOrderListWhenApplicationClose;

               if(returnOrderListWhenApplicationClose == null) {
                   System.out.println("returnOrderListWhenApplicationCloseNull");
               }

               Parent parent = loader.load();
//               MainController mainController = new MainController(returnOrderListWhenApplicationClose);
//               loader.setController(mainController);
//               mainController = new MainController(returnOrderListWhenApplicationClose);

               Scene scene = new Scene(parent);
               primaryStage.setScene(scene);
               primaryStage.show();
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

    private boolean loginjsonParsing(String result){
       Gson gson = new Gson();
        loginParsing = gson.fromJson(result, LoginParsing.class);
        boolean result1 = loginParsing.isResult();
        //result1 = true;
        if(result1) {
            //store_id, nick, store_name, email, is_open 저장
            preferences.put("store_id", loginParsing.getStore_id());
            preferences.put("phone", loginParsing.getPhone());
            preferences.put("store_name", loginParsing.getStore_name());
            preferences.put("email", loginParsing.getEmail());
            preferences.put("is_open", loginParsing.getIs_open());
            //꺼낼때는 get 으로 써주고 두번째 파라미터에는 null 넣어주면 됨. 모르겠으면 물어봐
        }
        return result1;
    }

    public void saveUserInfo(ActionEvent actionEvent) {
       if(actionEvent.getEventType() == ActionEvent.ACTION) {
            if(save_id_pw.isSelected()) {
                System.out.println("selected!");
            }
            else {
                System.out.println("deselected!");
            }
        }
    }
}
