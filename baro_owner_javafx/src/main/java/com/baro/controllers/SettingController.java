package com.baro.controllers;

import com.baro.Printer.ReceiptPrint;
import com.fazecast.jSerialComm.SerialPort;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class SettingController implements Initializable {

    Preferences preferences = Preferences.userRoot();
    private ArrayList<Integer> makeDataBit = new ArrayList<Integer>() {{
        add(5); add(6); add(7); add(8);
    }};
    private ArrayList<Integer> makeBaudRateList = new ArrayList<Integer>() {{
        add(110); add(300); add(1200); add(2400); add(4800); add(9600); add(19200); add(38400);
    }};
    final private ArrayList<String> makeParity = new ArrayList<String>() {{
        add("짝수"); add("홀수"); add("없음"); add("표시"); add("공백");
    }};
    final private ArrayList<String> makeStopBit = new ArrayList<String>() {{
        add("1"); add("1.5"); add("2");
    }};
    final private ArrayList<String> makeFlowController = new ArrayList<String>() {{
        add("Xon/Xoff"); add("DSR"); add("DTR"); add("RTS"); add("CTS"); add("없음");
    }};

    public ArrayList<String> makePortList = new ArrayList<>();
    @FXML
    Label select_com_port_combo_text;
    @FXML
    Label select_data_bit_combo_text;
    @FXML
    Label select_baud_rate_combo_text;
    @FXML
    private Label insert_print_name_text;
     @FXML
    private Label select_parity_combo_text;
     @FXML
    private Label select_stop_bit_combo_text;
     @FXML
    private Label select_flow_controller_combo_text;

     @FXML
    public TextField insert_print_name_field;
    @FXML
    private ComboBox<String> select_com_port_combo;
    @FXML
    private ComboBox<Integer> select_baud_rate_combo;
    @FXML
    private ComboBox<Integer> select_data_bit_combo;
     @FXML
    private ComboBox<String> select_parity_combo;
     @FXML
    private ComboBox<String> select_stop_bit_combo;
     @FXML
    private ComboBox<String> select_flow_controller_combo;
    
    @FXML
    private Button save_print;
    @FXML
    private Button test_print;

    String getPortName;
    int getBaudRate;
    int getDataBit;
    String getParity;
    String getStopBit;
    String getFlowControll;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        insert_print_name_text.setAlignment(Pos.CENTER_RIGHT);
        select_com_port_combo_text.setAlignment(Pos.CENTER_RIGHT);
        select_data_bit_combo_text.setAlignment(Pos.CENTER_RIGHT);
        select_baud_rate_combo_text.setAlignment(Pos.CENTER_RIGHT);
        select_parity_combo_text.setAlignment(Pos.CENTER_RIGHT);
        select_stop_bit_combo_text.setAlignment(Pos.CENTER_RIGHT);
        select_flow_controller_combo_text.setAlignment(Pos.CENTER_RIGHT);

        getPortName = preferences.get("savePortName", "");
        getBaudRate = preferences.getInt("saveBaudRate", 0);
        getDataBit = preferences.getInt("saveDataBit", 0);
        getParity = preferences.get("saveParity", "");
        getStopBit = preferences.get("saveStopBit", "");
        getFlowControll = preferences.get("saveFlowControll", "");

        makePortList.add(getPortName);
        if (SerialPort.getCommPorts().length != 0 ) {
            for (SerialPort port : SerialPort.getCommPorts()) {
                if (!port.getDescriptivePortName().equals(getPortName)) {
                    makePortList.add(port.getSystemPortName());
                }
            }
        }
        ObservableList<String> portList = FXCollections.observableList(makePortList);
        select_com_port_combo.setItems(portList);
        ObservableList<Integer> baudRateList = FXCollections.observableList(makeBaudRateList);
        select_baud_rate_combo.setItems(baudRateList);
        ObservableList<Integer> dataBitList = FXCollections.observableList(makeDataBit);
        select_data_bit_combo.setItems(dataBitList);
        ObservableList<String> parityList = FXCollections.observableList(makeParity);
        select_parity_combo.setItems(parityList);
        ObservableList<String> stopBitList = FXCollections.observableList(makeStopBit);
        select_stop_bit_combo.setItems(stopBitList);
        ObservableList<String> flowList = FXCollections.observableList(makeFlowController);
        select_flow_controller_combo.setItems(flowList);
        select_com_port_combo.setValue(getPortName);
        select_baud_rate_combo.setValue(getBaudRate);
        select_data_bit_combo.setValue(getDataBit);
        setEvent();
    }
    public void setEvent() {
        save_print.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(event.getEventType() == ActionEvent.ACTION) {
                    HashMap<String, Object> printSave = new HashMap<>();
                    //printSave.put("savePrint");
                    preferences.put("savePrintName", insert_print_name_field.getText());
                    preferences.put("savePortName", select_com_port_combo.getValue());
                    preferences.putInt("saveBaudRate", select_baud_rate_combo.getValue());
                    preferences.putInt("saveDataBit", select_data_bit_combo.getValue());
                    preferences.put("saveParity", select_parity_combo.getValue());
                    preferences.put("saveStopBit", select_stop_bit_combo.getValue());
                    preferences.put("saveFlowControll", select_flow_controller_combo.getValue());
                }
            }
        });
        test_print.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                testPrint(select_com_port_combo.getValue(), select_baud_rate_combo.getValue(), select_data_bit_combo.getValue()
                        ,select_parity_combo.getValue(), select_stop_bit_combo.getValue(), select_flow_controller_combo.getValue());
            }
        });
    }
    public void testPrint(String portName, int baudrate, int dataBit, String parity, String stopBit, String flow){
        int timeout = 1000;
        SerialPort serialPort;
        String textString1 = "BARO 영수증 출력 테스트\nportName : " + portName;
        String textString2 = "baudRate : " + baudrate;
        String textString3 = "dataBit : " + dataBit;
        String textString4 = "parity : " + parity;
        String textString5 = "stopBit : " + stopBit;
        String textString6 = "flow : " + flow;

        serialPort= SerialPort.getCommPort(portName);

        serialPort.openPort();
        serialPort.setBaudRate(baudrate);
        serialPort.setNumDataBits(dataBit);
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

        OutputStream printOutput = serialPort.getOutputStream();

        try {
            printOutput.write(ReceiptPrint.SET_WIDTH_57_203DPI);
            printOutput.write(ReceiptPrint.TXT_FONT_A);
            printOutput.write(ReceiptPrint.TXT_2WIDTH);
            printOutput.write(ReceiptPrint.TXT_ALIGN_LT);
            printOutput.write(textString1.getBytes("EUC-KR"));

            printOutput.write(ReceiptPrint.TXT_NORMAL);
            printOutput.write(ReceiptPrint.TXT_ALIGN_CT);
            printOutput.write(textString2.getBytes("EUC-KR"));
            printOutput.write(textString3.getBytes("EUC-KR"));
            printOutput.write(textString4.getBytes("EUC-KR"));
            printOutput.write(textString5.getBytes("EUC-KR"));
            printOutput.write(textString6.getBytes("EUC-KR"));

            printOutput.write("\n\n\n".getBytes("EUC-KR"));
            printOutput.write(ReceiptPrint.PAPER_FULL_CUT);
            printOutput.flush();
            printOutput.close();
            serialPort.closePort();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
