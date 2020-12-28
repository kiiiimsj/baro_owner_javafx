package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class CalculateController implements Initializable {
    Preferences preferences = Preferences.userRoot();
    private String owner_store_id;
    private int couponPrice;
    private int menuTotalPrice;
    private int thisWeekTotalPrice;

    @FXML public Text this_week_total_price;
    @FXML public Text menu_total_price;
    @FXML public Text coupon_price;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        owner_store_id = preferences.get("store_id", "");
        getStatisticsSalesValue();
    }
    private void getStatisticsSalesValue() {
        try{
            URL url = new URL("http://3.35.180.57:8080/OwnerCalculate.do?store_id="+owner_store_id);
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
            if(new JSONObject(bf.toString()).getBoolean("result")){

                setCalCulateText(bf.toString());
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

    private void setCalCulateText(String jsonString) {
        couponPrice = new JSONObject(jsonString.toString()).getInt("coupon_price");
        menuTotalPrice = new JSONObject(jsonString.toString()).getInt("menu_total_price");
        thisWeekTotalPrice = menuTotalPrice - couponPrice;

        this_week_total_price.setText(thisWeekTotalPrice + " 원");
        coupon_price.setText("- " + couponPrice + " 원");
        menu_total_price.setText("= " +menuTotalPrice + " 원");
    }
}
