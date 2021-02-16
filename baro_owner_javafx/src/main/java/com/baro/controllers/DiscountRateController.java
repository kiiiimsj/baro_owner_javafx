package com.baro.controllers;

import com.baro.utils.GetBool;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;
import java.util.ResourceBundle;

public class DiscountRateController implements Initializable {
    public HBox top_bar;

    public interface ClickClose{
        void clickClose();
    }
    public Label getDiscountRate;
    public int discountRate = 0;
    public TextField setNewDiscountRate;
    public Button setButton;
    public String storeId;
    public FontAwesomeIconView close;
    public ClickClose clickClose;

    private double initialX;
    private double initialY;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
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
        close.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                clickClose.clickClose();
                Stage stage = (Stage) setButton.getScene().getWindow();
                stage.close();
            }
        });
    }
    public void setDiscountRate(int discountRate) {
        this.discountRate = discountRate;
        setNewDiscountRate.setPromptText(discountRate+"");
        //getDiscountRate.setText(discountRate+"%");
    }

    public void clickSetButton(ActionEvent actionEvent) {
        int newDiscountRate = Integer.parseInt(setNewDiscountRate.getText());
        try {
            URL url = new URL("http://3.35.180.57:8080/SetStoreDiscount.do");
            URLConnection con = url.openConnection();
            HttpURLConnection http = (HttpURLConnection) con;
            http.setRequestMethod("POST");
            http.setRequestProperty("Content-Type", "application/json;utf-8");
            http.setRequestProperty("Accept", "application/json");
            http.setDoOutput(true);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("store_id", storeId);
            jsonObject.put("discount_rate", newDiscountRate);
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
            if(GetBool.getBool(bf.toString())) {
                clickClose.clickClose();
                Stage stage = (Stage) setButton.getScene().getWindow();
                stage.close();
            }else {

            }
        }
        catch(MalformedURLException e){
            e.printStackTrace();
        } catch(ProtocolException e){
            e.printStackTrace();
        } catch(IOException e){
            e.printStackTrace();
        }
    }
}
