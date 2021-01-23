package com.baro.Printer;

import com.baro.JsonParsing.Extras;
import com.baro.JsonParsing.Order;
import com.baro.JsonParsing.OrderDetail;
import com.baro.JsonParsing.OrderDetailParsing;
import com.baro.OrderListController;
import com.fazecast.jSerialComm.SerialPort;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import com.sun.deploy.util.WinRegistry;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReceiptPrint implements Initializable {
    /**
     * 프린터 출력 세부 설정 바이트 값
     * **/
    public static final byte[] CTL_LF          = {0x0a};          // Print and line feed

    // Beeper
    public static final byte[] BEEPER          = {0x1b,0x42,0x05,0x09}; // Beeps 5 times for 9*50ms each time

    // Line Spacing
    public static final byte[] LINE_SPACE_24   = {0x1b,0x33,24}; // Set the line spacing at 24
    public static final byte[] LINE_SPACE_30   = {0x1b,0x33,30}; // Set the line spacing at 30

    //Image
    public static final byte[] SELECT_BIT_IMAGE_MODE = {0x1B, 0x2A, 33};

    // Printer hardware
    public static final byte[] HW_INIT         = {0x1b,0x40};          // Clear data in buffer and reset modes

    // Cash Drawer
    public static final byte[] CD_KICK_2       = {0x1b,0x70,0x00};      // Sends a pulse to pin 2 []
    public static final byte[] CD_KICK_5       = {0x1b,0x70,0x01};      // Sends a pulse to pin 5 []

    // Paper
    public static final byte[]  PAPER_FULL_CUT = {0x1d,0x56,0x00}; // Full cut paper
    public static final byte[]  PAPER_PART_CUT = {0x1d,0x56,0x01}; // Partial cut paper
    public static final byte[]  PAPER_FULL_CUT_WITH_FEED_3 = {0x1d,0x56,0x66,0x03};

    // Text format
    public static final byte[] TXT_NORMAL      = {0x1b,0x21,0x00}; // Normal text
    public static final byte[] TXT_2HEIGHT     = {0x1b,0x21,0x10}; // Double height text
    public static final byte[] TXT_2WIDTH      = {0x1b,0x21,0x20}; // Double width text
    public static final byte[] TXT_4SQUARE     = {0x1b,0x21,0x30}; // Quad area text
    public static final byte[] TXT_UNDERL_OFF  = {0x1b,0x2d,0x00}; // Underline font OFF
    public static final byte[] TXT_UNDERL_ON   = {0x1b,0x2d,0x01}; // Underline font 1-dot ON
    public static final byte[] TXT_UNDERL2_ON  = {0x1b,0x2d,0x02}; // Underline font 2-dot ON
    public static final byte[] TXT_BOLD_OFF    = {0x1b,0x45,0x00}; // Bold font OFF
    public static final byte[] TXT_BOLD_ON     = {0x1b,0x45,0x01}; // Bold font ON
    public static final byte[] TXT_FONT_A      = {0x1b,0x4d,0x48}; // Font type A
    public static final byte[] TXT_FONT_B      = {0x1b,0x4d,0x01};// Font type B
    public static final byte[] TXT_ALIGN_LT    = {0x1b,0x61,0x00}; // Left justification
    public static final byte[] TXT_ALIGN_CT    = {0x1b,0x61,0x01}; // Centering
    public static final byte[] TXT_ALIGN_RT    = {0x1b,0x61,0x02}; // Right justification

    // Char code table
    public static final byte[] CHARCODE_PC437  = {0x1b,0x74,0x00}; // USA){ Standard Europe
    public static final byte[] CHARCODE_JIS    = {0x1b,0x74,0x01}; // Japanese Katakana
    public static final byte[] CHARCODE_PC850  = {0x1b,0x74,0x02}; // Multilingual
    public static final byte[] CHARCODE_PC860  = {0x1b,0x74,0x03}; // Portuguese
    public static final byte[] CHARCODE_PC863  = {0x1b,0x74,0x04}; // Canadian-French
    public static final byte[] CHARCODE_PC865  = {0x1b,0x74,0x05}; // Nordic
    public static final byte[] CHARCODE_WEU    = {0x1b,0x74,0x06}; // Simplified Kanji, Hirakana
    public static final byte[] CHARCODE_GREEK  = {0x1b,0x74,0x07}; // Simplified Kanji
    public static final byte[] CHARCODE_HEBREW = {0x1b,0x74,0x08}; // Simplified Kanji
    public static final byte[] CHARCODE_PC1252 = {0x1b,0x74,0x10}; // Western European Windows Code Set
    public static final byte[] CHARCODE_PC866  = {0x1b,0x74,0x12}; // Cirillic //2
    public static final byte[] CHARCODE_PC852  = {0x1b,0x74,0x13}; // Latin 2
    public static final byte[] CHARCODE_PC858  = {0x1b,0x74,0x14}; // Euro
    public static final byte[] CHARCODE_THAI42 = {0x1b,0x74,0x15}; // Thai character code 42
    public static final byte[] CHARCODE_THAI11 = {0x1b,0x74,0x16}; // Thai character code 11
    public static final byte[] CHARCODE_THAI13 = {0x1b,0x74,0x17}; // Thai character code 13
    public static final byte[] CHARCODE_THAI14 = {0x1b,0x74,0x18}; // Thai character code 14
    public static final byte[] CHARCODE_THAI16 = {0x1b,0x74,0x19}; // Thai character code 16
    public static final byte[] CHARCODE_THAI17 = {0x1b,0x74,0x1a}; // Thai character code 17
    public static final byte[] CHARCODE_THAI18 = {0x1b,0x74,0x1b}; // Thai character code 18

    // Barcode format
    public static final byte[] BARCODE_TXT_OFF = {0x1d,0x48,0x00}; // HRI printBarcode chars OFF
    public static final byte[] BARCODE_TXT_ABV = {0x1d,0x48,0x01}; // HRI printBarcode chars above
    public static final byte[] BARCODE_TXT_BLW = {0x1d,0x48,0x02}; // HRI printBarcode chars below
    public static final byte[] BARCODE_TXT_BTH = {0x1d,0x48,0x03}; // HRI printBarcode chars both above and below
    public static final byte[] BARCODE_FONT_A  = {0x1d,0x66,0x00}; // Font type A for HRI printBarcode chars
    public static final byte[] BARCODE_FONT_B  = {0x1d,0x66,0x01}; // Font type B for HRI printBarcode chars
    public static final byte[] BARCODE_HEIGHT  = {0x1d,0x68,0x64}; // Barcode Height [1-255]
    public static final byte[] BARCODE_WIDTH   = {0x1d,0x77,0x03}; // Barcode Width  [2-6]
    public static final byte[] BARCODE_UPC_A   = {0x1d,0x6b,0x00}; // Barcode type UPC-A
    public static final byte[] BARCODE_UPC_E   = {0x1d,0x6b,0x01}; // Barcode type UPC-E
    public static final byte[] BARCODE_EAN13   = {0x1d,0x6b,0x02}; // Barcode type EAN13
    public static final byte[] BARCODE_EAN8    = {0x1d,0x6b,0x03}; // Barcode type EAN8
    public static final byte[] BARCODE_CODE39  = {0x1d,0x6b,0x04}; // Barcode type CODE39
    public static final byte[] BARCODE_ITF     = {0x1d,0x6b,0x05}; // Barcode type ITF
    public static final byte[] BARCODE_NW7     = {0x1d,0x6b,0x06}; // Barcode type NW7

    // Printing Density
    public static final byte[] PD_N50          = {0x1d,0x7c,0x00}; // Printing Density -50%
    public static final byte[] PD_N37          = {0x1d,0x7c,0x01}; // Printing Density -37.5%
    public static final byte[] PD_N25          = {0x1d,0x7c,0x02}; // Printing Density -25%
    public static final byte[] PD_N12          = {0x1d,0x7c,0x03}; // Printing Density -12.5%
    public static final byte[] PD_0            = {0x1d,0x7c,0x04}; // Printing Density  0%
    public static final byte[] PD_P50          = {0x1d,0x7c,0x08}; // Printing Density +50%
    public static final byte[] PD_P37          = {0x1d,0x7c,0x07}; // Printing Density +37.5%
    public static final byte[] PD_P25          = {0x1d,0x7c,0x06}; // Printing Density +25%
    public static final byte[] PD_P12          = {0x1d,0x7c,0x05}; // Printing Density +12.5%

    public static final byte[] SET_WIDTH_57_203DPI = {0x1d,0x57,(byte)0xC0,0x01};
    public static final byte[] SET_WIDTH_57_180DPI = {0x1d,0x57,0x68,0x02};

    public static final byte[] SET_WIDTH_79_180DPI = {0x1d,0x57,0x00,0x02};
    public static final byte[] SET_WIDTH_79_203DPI = {0x1d,0x57,0x40,0x02};

    /**
     * 프린터 출력 선택 스피너
     * **/
    @FXML
    private ComboBox<String> select_com_port_combo;
    public ArrayList<String> makePortList = new ArrayList<>();
    @FXML
    private ComboBox<Integer> select_baud_rate_combo;
    final private ArrayList<Integer> makeBaudRateList = new ArrayList<Integer>() {{
        add(110); add(300); add(1200); add(2400); add(4800); add(9600); add(19200); add(38400);
    }};
    @FXML
    private ComboBox<Integer> select_data_bit_combo;
    final private ArrayList<Integer> makeDataBit = new ArrayList<Integer>() {{
       add(5); add(6); add(7); add(8);
    }};
    @FXML
    private ComboBox<String> select_parity_combo;
    final private ArrayList<String> makeParity = new ArrayList<String>() {{
       add("짝수"); add("홀수"); add("없음"); add("표시"); add("공백");
    }};
    @FXML
    private ComboBox<String> select_stop_bit_combo;
    final private ArrayList<String> makeStopBit = new ArrayList<String>() {{
        add("1"); add("1.5"); add("2");
    }};
    @FXML
    private ComboBox<String> select_flow_controller_combo;
    final private ArrayList<String> makeFlowController = new ArrayList<String>() {{
        add("Xon/Xoff"); add("DSR"); add("DTR"); add("RTS"); add("CTS"); add("없음");
    }};

    @FXML
    private Button print;
    @FXML
    private Button this_port_okay;
    @FXML
    private VBox bottom_area;

    /**
     * jSerialComm과 입력 스트림 
     * 저장된 프린터 설정값 읽어오는 preferences
     * **/
    public SerialPort serialPort;
    public OutputStream printOutput;
    private final Preferences preferences = Preferences.userRoot();

    /**
     * Output으로 출력되는 문자열 
     * **/
    public StringBuilder headerContent = new StringBuilder();
    public StringBuilder orderGetTextContent = new StringBuilder();
    public StringBuilder customerPhone = new StringBuilder("고객번호:");
    public StringBuilder orderDateContent = new StringBuilder("주문시간:");
    public StringBuilder texTitleText = new StringBuilder();
    public StringBuilder totalTitleText = new StringBuilder();
    public StringBuilder customerRequest = new StringBuilder();
    public StringBuilder content = new StringBuilder("=====================================\n");

    final int MENU_AREA = 25;
    final int COUNT_AREA = 4;
    final int PRICE_AREA = 8;
    //TOTAL WIDTH AREA = 37


    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
    public void startPrint() {
        bottom_area.setVisible(false);
        print.setVisible(false);

        setSpinner();
    }



    /***************************************************************************
     *
     * 스피너 클릭 이벤트 및 저장된 preferences 확인
     *
     **************************************************************************/
    public void setSpinner() {
        System.out.println("setSpinner");
        if(!preferences.getBoolean("printBefore", false) ) {
            System.out.println("setSpinnerIn");
            String getPortName = preferences.get("savePortName", "");
            int getBaudRate = preferences.getInt("saveBaudRate", 0);
            int getDataBit = preferences.getInt("saveDataBit", 0);
            String getParity = preferences.get("saveParity", "");
            String getStopBit = preferences.get("saveStopBit", "");
            String getFlowControll = preferences.get("saveFlowControll", "");

            if(!getPortName.equals("") && getBaudRate != 0 && getDataBit != 0
            && !getParity.equals("") && !getStopBit.equals("") && !getFlowControll.equals("")) {
                try {
                    printReceipt(getPortName, getBaudRate, getDataBit, getParity, getStopBit, getFlowControll);
                } catch (DocumentException | IOException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("setSpinner1");
        makePortList.add("선택");
        if (SerialPort.getCommPorts().length != 0 ) {
            for (SerialPort port: SerialPort.getCommPorts()) {
                makePortList.add(port.getSystemPortName());
            }
            ObservableList<String> list = FXCollections.observableList(makePortList);
            select_com_port_combo.setItems(list);
        }
        select_com_port_combo.setValue("선택");


        ObservableList<Integer> list = FXCollections.observableList(makeBaudRateList);
        select_baud_rate_combo.setItems(list);

        ObservableList<Integer> list2 = FXCollections.observableList(makeDataBit);
        select_data_bit_combo.setItems(list2);

        ObservableList<String> list3 = FXCollections.observableList(makeParity);
        select_parity_combo.setItems(list3);

        ObservableList<String> list4 = FXCollections.observableList(makeStopBit);
        select_stop_bit_combo.setItems(list4);

        ObservableList<String> list5 = FXCollections.observableList(makeFlowController);
        select_flow_controller_combo.setItems(list5);
        
        this_port_okay.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(event.getEventType() == ActionEvent.ACTION) {
                    System.out.println("buttonClick");
                    if(select_com_port_combo.getValue().equals("선택")) {

                    }else {
                        //port_device_name.setText();
                        bottom_area.setVisible(true);
                        print.setVisible(true);
                    }
                }
            }
        });
        select_baud_rate_combo.setValue(9600);
        select_data_bit_combo.setValue(8);
        select_parity_combo.setValue("없음");
        select_stop_bit_combo.setValue("1");
        select_flow_controller_combo.setValue("DSR");

        print.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(event.getEventType() == ActionEvent.ACTION) {
                    try {
                        System.out.println(select_com_port_combo.getValue() + " : " +select_baud_rate_combo.getValue() + " : " + select_data_bit_combo.getValue());
                        printReceipt(select_com_port_combo.getValue(), select_baud_rate_combo.getValue(), select_data_bit_combo.getValue()
                        ,select_parity_combo.getValue(), select_stop_bit_combo.getValue(), select_flow_controller_combo.getValue());

                    } catch (DocumentException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }



    /***************************************************************************
     *
     * 주문정보 문자열로 변환
     *
     **************************************************************************/
    public void makeReceiptString(OrderDetailParsing order, Order orderInfo){
        headerContent.append("[BARO]\n");
        orderGetTextContent.append("주문이\n접수되었습니다.\n\n");
        customerPhone.append(orderInfo.phone, 7, orderInfo.phone.length()).append("\n");
        orderDateContent.append(orderInfo.order_date.substring(0, orderInfo.order_date.length() - 1)).append("\n");
        content.append("메뉴명                 수량      금액\n")
        .append("-------------------------------------\n");

        /**
         *  SET_WIDTH_57 인 경우 전체 길이 37로 잡고 계산
         *  SET_WIDTH_79 인 경우 전체 길이 59로 잡고 계산
         * **/

        if(order != null || order.orders != null) {
            for (int i = 0; i < order.orders.size(); i++) {
                OrderDetail menu = order.orders.get(i);


                int menuLength = 0;
                String menuName;

                for (int j = 0; j < menu.menu_name.length(); j++) {
                    if(menu.menu_name.charAt(j) >= 44032) {
                        menuLength += 2;
                    }
                    else {
                        menuLength += 1;
                    }
                }

                if(menuLength > 20) {
                    menuName = menu.menu_name.substring(0, 10);
                    menuName += "...";
                    menuLength = 23;

                }else {
                    menuName = menu.menu_name;
                }
                content.append(menuName);

                int MENU_TEXT_PADDING = MENU_AREA - menuLength;
                for (int j = 0; j <MENU_TEXT_PADDING ; j++) {
                    content.append(" ");
                }
                content.append(menu.order_count);
                int COUNT_TEXT_PADDING = COUNT_AREA - (String.valueOf(menu.order_count).length());
                for (int l = 0; l < COUNT_TEXT_PADDING ; l++) {
                    content.append(" ");
                }

                int PRICE_TEXT_PADDING = PRICE_AREA - (String.valueOf(menu.menu_defaultprice).length()+2);
                for (int y = 0; y < PRICE_TEXT_PADDING; y ++) {
                    content.append(" ");
                }
                content.append(menu.menu_defaultprice+"원\n");

                if (menu.extras == null || menu.extras.size() == 0) continue;

                for (int j = 0; j < menu.extras.size(); j++) {
                    Extras extras = menu.extras.get(j);
                    int extraNameLength = 1;
                    String extraName;
                    content.append("-");

                    for (int t = 0; t < extras.extra_name.length(); t++) {
                        if(extras.extra_name.charAt(t) >= 44032) {
                            extraNameLength += 2;
                        }
                        else {
                            extraNameLength += 1;
                        }
                    }
                    if(extraNameLength > 19) {
                        extraName = extras.extra_name.substring(0, 10);
                        extraName += "...";
                        extraNameLength = 22;
                    }else {
                        extraName = extras.extra_name;
                    }
                    content.append(extraName);
                    int EXTRA_MENU_PADDING = MENU_AREA - extraNameLength;
                    for (int k = 0; k <EXTRA_MENU_PADDING ; k++) {
                        content.append(" ");
                    }
                    content.append(extras.extra_count);
                    int EXTRA_COUNT_PADDING = COUNT_AREA - (String.valueOf(extras.extra_count).length());
                    for (int k = 0; k <EXTRA_COUNT_PADDING ; k++) {
                        content.append(" ");
                    }
                    int EXTRA_PRICE_PADDING = PRICE_AREA - (String.valueOf(extras.extra_price).length()+2);
                    for (int k = 0; k <EXTRA_PRICE_PADDING ; k++) {
                        content.append(" ");
                    }
                    content.append((extras.extra_price * extras.extra_count)+"원\n");

                }
            }

        }
        texTitleText.append("-------------------------------------\n").append("쿠폰 : ")
                .append(orderInfo.discount_price)
                .append("원")
                .append("\n");

        totalTitleText.append("------------------\n")
                .append("결제 금액:")
                .append(orderInfo.total_price - orderInfo.discount_price)
                .append("원")
                .append("\n\n")
                .append("------------------\n");

        customerRequest.append("요청사항\n")
                .append("  -")
                .append(order.requests)
                .append("\n\n\n\n\n\n\n\n\n");
        /**
         * CUT_PAPER시 너무 빠르게 잘려 내용이 잘리는 이슈 - 개행문자 추가로 영수증 내용 위로 올림
         * **/
    }


    /***************************************************************************
     *
     * 입력된 문자열 Output으로 변환
     *
     **************************************************************************/
    public void printReceipt(String portName, int baudrate, int dataBit, String parity, String stopBit, String flow) throws IOException, DocumentException {
        Integer timeout = 1000;

        serialPort = SerialPort.getCommPort(portName);
        if(SerialPort.getCommPort(portName).isOpen()) {
            System.out.println("isOpen");
            return;
        }

        serialPort.openPort();
        serialPort.setBaudRate(baudrate);
        switch (parity) {
            case "짝수":
                serialPort.setParity(SerialPort.ODD_PARITY);
                break;
            case "홀수":
                serialPort.setParity(SerialPort.EVEN_PARITY);
                break;
            case "없음":
                serialPort.setParity(SerialPort.NO_PARITY);
                break;
            case "표시":
                serialPort.setParity(SerialPort.MARK_PARITY);
                break;
            case "공백":
                serialPort.setParity(SerialPort.SPACE_PARITY);
                break;
        }
        switch (stopBit) {
            case "1" :
                serialPort.setNumStopBits(SerialPort.ONE_STOP_BIT);
                break;
            case "1.5" :
                serialPort.setNumStopBits(SerialPort.ONE_POINT_FIVE_STOP_BITS);
                break;
            case "2" :
                serialPort.setNumStopBits(SerialPort.TWO_STOP_BITS);
                break;
        }
        serialPort.setNumDataBits(dataBit);

        switch (flow) {
            case "Xon/Xoff" :
                serialPort.setFlowControl(SerialPort.FLOW_CONTROL_XONXOFF_IN_ENABLED);
                break;
            case "DSR" :
                serialPort.setFlowControl(SerialPort.FLOW_CONTROL_DSR_ENABLED);
                break;
            case "DTR" :
                serialPort.setFlowControl(SerialPort.FLOW_CONTROL_DTR_ENABLED);
                break;
            case "CTS" :
                serialPort.setFlowControl(SerialPort.FLOW_CONTROL_CTS_ENABLED);
                break;
            case "RTS" :
                serialPort.setFlowControl(SerialPort.FLOW_CONTROL_RTS_ENABLED);
                break;
            case "없음" :
                serialPort.setFlowControl(SerialPort.FLOW_CONTROL_DISABLED);
                break;
        }
        serialPort.setComPortTimeouts(SerialPort.TIMEOUT_NONBLOCKING, timeout, timeout);

        printOutput = serialPort.getOutputStream();

        printOutput.write(SET_WIDTH_57_203DPI);
        printOutput.write(TXT_FONT_A);

        printOutput.write(TXT_4SQUARE);
        printOutput.write(TXT_ALIGN_CT);
        printOutput.write(headerContent.toString().getBytes("EUC-KR"));

        printOutput.write(TXT_2HEIGHT);
        printOutput.write(TXT_ALIGN_CT);
        printOutput.write(orderGetTextContent.toString().getBytes("EUC-KR"));

        printOutput.write(TXT_NORMAL);
        printOutput.write(TXT_ALIGN_LT);
        printOutput.write(orderDateContent.toString().getBytes("EUC-KR"));

        printOutput.write(TXT_2HEIGHT);
        printOutput.write(TXT_ALIGN_LT);
        printOutput.write(customerPhone.toString().getBytes("EUC-KR"));

        printOutput.write(TXT_NORMAL);
        printOutput.write(TXT_ALIGN_LT);
        printOutput.write(content.toString().getBytes("EUC-KR"));

        printOutput.write(TXT_NORMAL);
        printOutput.write(TXT_ALIGN_RT);
        printOutput.write(texTitleText.toString().getBytes("EUC-KR"));

        printOutput.write(TXT_4SQUARE);
        printOutput.write(TXT_ALIGN_RT);
        printOutput.write(totalTitleText.toString().getBytes("EUC-KR"));

        printOutput.write(TXT_NORMAL);
        printOutput.write(TXT_ALIGN_LT);
        printOutput.write(customerRequest.toString().getBytes("EUC-KR"));

        printOutput.write("\n\n\n".getBytes("EUC-KR"));
        printOutput.write(PAPER_FULL_CUT);

        printOutput.flush();
        printOutput.close();
        serialPort.closePort();

        System.out.println("serialPort close " +serialPort.isOpen() + "");

        preferences.put("savePortName", portName);
        preferences.putInt("saveBaudRate", baudrate);
        preferences.putInt("saveDataBit", dataBit);
        preferences.put("saveParity", parity);
        preferences.put("saveStopBit", stopBit);
        preferences.put("saveFlowControll", flow);
        preferences.putBoolean("printBefore", true);
    }
}
