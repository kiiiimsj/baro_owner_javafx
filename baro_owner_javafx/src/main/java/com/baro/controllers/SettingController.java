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

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
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
    public ArrayList<String> makePortList = new ArrayList<>();
    @FXML
    Label select_com_port_combo_text;
    @FXML
    Label select_data_bit_combo_text;
    @FXML
    Label select_baud_rate_combo_text;

    @FXML
    private ComboBox<String> select_com_port_combo;
    @FXML
    private ComboBox<Integer> select_baud_rate_combo;
    @FXML
    private ComboBox<Integer> select_data_bit_combo;
    @FXML
    private Button save_print;
    @FXML
    private Button test_print;

    String getPortName;
    int getBaudRate;
    int getDataBit;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        select_com_port_combo_text.setAlignment(Pos.CENTER_RIGHT);
        select_data_bit_combo_text.setAlignment(Pos.CENTER_RIGHT);
        select_baud_rate_combo_text.setAlignment(Pos.CENTER_RIGHT);

        getPortName = preferences.get("savePortName", "");
        getBaudRate = preferences.getInt("saveBaudRate", 0);
        getDataBit = preferences.getInt("saveDataBit", 0);

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
                    preferences.put("savePortName", select_com_port_combo.getValue());
                    preferences.putInt("saveBaudRate", select_baud_rate_combo.getValue());
                    preferences.putInt("saveDataBit", select_data_bit_combo.getValue());
                }
            }
        });
        test_print.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                testPrint(select_com_port_combo.getValue(), select_baud_rate_combo.getValue(), select_data_bit_combo.getValue());
            }
        });
    }
    public void testPrint(String portName, int baudrate, int dataBit){
        int timeout = 1000;
        SerialPort serialPort;
        String textString1 = "BARO 영수증 출력 테스트\nportName : " + portName;
        String textString2 = "baudRate : " + baudrate;
        String textString3 = "dataBit : " + dataBit;

        serialPort= SerialPort.getCommPort(portName);

        serialPort.openPort();
        serialPort.setBaudRate(baudrate);
        serialPort.setParity(SerialPort.EVEN_PARITY);
        serialPort.setNumDataBits(dataBit);
        serialPort.setNumStopBits(SerialPort.ONE_STOP_BIT);
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
