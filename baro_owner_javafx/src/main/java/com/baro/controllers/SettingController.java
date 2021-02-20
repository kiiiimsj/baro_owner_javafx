package com.baro.controllers;

import com.baro.Dialog.PrintDialog;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.TextAlignment;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class SettingController implements Initializable, PrintDialog.PrintDialogInterface {


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

    public HBox print_info_combo_box;
    @FXML
    private HBox save_print_grid_pane;
    @FXML
    private Button test_print;
    public Button delete_print;
    public Button set_main_print;
    public Button save_print;

    public PrintDialog printDialog;
    boolean clickNo = false;
    boolean clickYes = false;

//    String getPortName;
//    int getBaudRate;
//    int getDataBit;
//    String getParity;
//    String getStopBit;
//    String getFlowControll;

    int oldValueLabelIndex = 0;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        print_info_combo_box.setVisible(false);
        delete_print.setVisible(false);
        set_main_print.setVisible(false);
        save_print.setVisible(false);
        test_print.setVisible(false);
        printDialog = new PrintDialog();

//        getPortName = preferences.get("savePortName", "");
//        getBaudRate = preferences.getInt("saveBaudRate", 0);
//        getDataBit = preferences.getInt("saveDataBit", 0);
//        getParity = preferences.get("saveParity", "");
//        getStopBit = preferences.get("saveStopBit", "");
//        getFlowControll = preferences.get("saveFlowControll", "");

        setPrintList();

//        makePortList.add(getPortName);
        if (SerialPort.getCommPorts().length != 0 ) {
            for (SerialPort port : SerialPort.getCommPorts()) {
                makePortList.add(port.getSystemPortName());
//                if (!port.getDescriptivePortName().equals(getPortName)) {
//                    makePortList.add(port.getSystemPortName());
//                }
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

        setEvent();
    }
    public void setPrintList() {
        if(save_print_grid_pane.getChildren().size() != 0) {
            save_print_grid_pane.getChildren().removeAll();
        }
        for (int i = 0; i < 5 ; i++) {
            System.out.println("savePrintName : "+ preferences.get("savePrintName"+i+"", ""));
            final int index = i;
            if(!preferences.get("savePrintName"+i+"", "").equals("")) {
                save_print_grid_pane.getChildren().add(index, addPrint(index));
            }else {
                save_print_grid_pane.getChildren().add(index, addNoPrint(index));
            }
        }
    }
    public Label addPrint(final int index) {
        Label print = new Label(preferences.get("savePrintName"+index+"", ""));
        print.setMinWidth(230);
        print.setMinHeight(100);
        print.setAlignment(Pos.CENTER);
        print.setStyle("-fx-text-fill: gray;-fx-background-color: white;-fx-font-size: 30px;-fx-background-radius: 5px");
        print.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                print.setStyle("-fx-text-fill: white;-fx-background-color: gray;-fx-font-size: 30px;-fx-background-radius: 5px");
                System.out.println("PrintOld : " + oldValueLabelIndex);
                System.out.println("Print : " + index);
                if(oldValueLabelIndex != index) {
                    save_print_grid_pane
                            .getChildren()
                            .get(oldValueLabelIndex)
                            .setStyle("-fx-text-fill: gray;-fx-background-color: white;-fx-font-size: 30px;-fx-background-radius: 5px");
                }
                oldValueLabelIndex = index;

                print_info_combo_box.setVisible(true);
                save_print.setVisible(true);
                delete_print.setVisible(true);
                set_main_print.setVisible(true);
                test_print.setVisible(true);

                insert_print_name_field.setText(preferences.get("savePrintName"+index+"", ""));
                select_com_port_combo.setValue(preferences.get("savePortName"+index+"", ""));
                select_baud_rate_combo.setValue(preferences.getInt("saveBaudRate"+index+"", -1));
                select_data_bit_combo.setValue(preferences.getInt("saveDataBit"+index+"", -1));
                select_parity_combo.setValue(preferences.get("saveParity"+index+"", ""));
                select_stop_bit_combo.setValue(preferences.get("saveStopBit"+index+"",""));
                select_flow_controller_combo.setValue(preferences.get("saveFlowControll"+index+"",""));
                save_print.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        printDialog.call(SettingController.this, PrintDialog.SAVE);
                        if(printDialog.yes.isPressed()) {
                            preferences.put("savePrintName"+index+"", insert_print_name_field.getText());
                            preferences.put("savePortName"+index+"", select_com_port_combo.getValue());
                            preferences.putInt("saveBaudRate"+index+"", select_baud_rate_combo.getValue());
                            preferences.putInt("saveDataBit"+index+"", select_data_bit_combo.getValue());
                            preferences.put("saveParity"+index+"", select_parity_combo.getValue());
                            preferences.put("saveStopBit"+index+"",select_stop_bit_combo.getValue());
                            preferences.put("saveFlowControll"+index+"",select_flow_controller_combo.getValue());
//                        save_print_grid_pane.getChildren().remove(index);
                            //save_print_grid_pane.add(addPrint(index), index, 0);
                            save_print_grid_pane.getChildren().remove(index);
                            save_print_grid_pane.getChildren().add(index, addPrint(index));
                        }
                    }
                });
                delete_print.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        if(preferences.get("setMainPrint", "").equals( preferences.get("savePrintName"+index+"",""))) {
                            deleteMainPrint();
                        }
                        print_info_combo_box.setVisible(false);
                        save_print.setVisible(false);
                        delete_print.setVisible(false);
                        set_main_print.setVisible(false);
                        test_print.setVisible(false);
                        preferences.put("savePrintName"+index+"", "");
                        preferences.put("savePortName"+index+"", "");
                        preferences.putInt("saveBaudRate"+index+"", -1);
                        preferences.putInt("saveDataBit"+index+"", -1);
                        preferences.put("saveParity"+index+"", "");
                        preferences.put("saveStopBit"+index+"","");
                        preferences.put("saveFlowControll"+index+"","");
//                save_print_grid_pane.getChildren().remove(index);
                        //save_print_grid_pane.add(addNoPrint(index), index, 0);
                        save_print_grid_pane.getChildren().remove(index);
                        save_print_grid_pane.getChildren().add(index, addNoPrint(index));
                    }
                });
            }
        });
        return print;
    }
    public Label addNoPrint(final int index) {
        Label noPrint = new Label("+");
        noPrint.setMinWidth(230);
        noPrint.setMinHeight(100);
        noPrint.setAlignment(Pos.CENTER);
        noPrint.setStyle("-fx-text-fill: gray;-fx-background-color: white;-fx-font-size: 30px;-fx-background-radius: 5px");
        noPrint.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                noPrint.setStyle("-fx-text-fill: white;-fx-background-color: gray;-fx-font-size: 30px;-fx-background-radius: 5px");
                System.out.println("noPrintOld : " + oldValueLabelIndex);
                System.out.println("noPrint : " + index);
                if(oldValueLabelIndex != index) {
                    save_print_grid_pane
                            .getChildren()
                            .get(oldValueLabelIndex)
                            .setStyle("-fx-text-fill: gray;-fx-background-color: white;-fx-font-size: 30px;-fx-background-radius: 5px");
                }
                oldValueLabelIndex = index;

                print_info_combo_box.setVisible(true);
                save_print.setVisible(true);

                set_main_print.setVisible(false);
                delete_print.setVisible(false);
                test_print.setVisible(false);

                insert_print_name_field.setText("");
                select_com_port_combo.setValue("");
                select_baud_rate_combo.setValue(0);
                select_data_bit_combo.setValue(0);
                select_parity_combo.setValue("");
                select_stop_bit_combo.setValue("");
                select_flow_controller_combo.setValue("");
                save_print.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        printDialog.call(SettingController.this, PrintDialog.SAVE);
                        if(clickYes) {
                            System.out.println("savePrint");
                            print_info_combo_box.setVisible(false);
                            save_print.setVisible(false);
                            delete_print.setVisible(false);
                            set_main_print.setVisible(false);
                            test_print.setVisible(false);
                            preferences.put("savePrintName"+index+"", insert_print_name_field.getText());
                            preferences.put("savePortName"+index+"", select_com_port_combo.getValue());
                            preferences.putInt("saveBaudRate"+index+"", select_baud_rate_combo.getValue());
                            preferences.putInt("saveDataBit"+index+"", select_data_bit_combo.getValue());
                            preferences.put("saveParity"+index+"", select_parity_combo.getValue());
                            preferences.put("saveStopBit"+index+"",select_stop_bit_combo.getValue());
                            preferences.put("saveFlowControll"+index+"",select_flow_controller_combo.getValue());
//                        save_print_grid_pane.getChildren().remove(index);
//                        save_print_grid_pane.add(addPrint(index), index, 0);
                            save_print_grid_pane.getChildren().remove(index);
                            save_print_grid_pane.getChildren().add(index, addPrint(index));
                        }
                        System.out.println("passThrough");
                    }
                });
            }
        });
        return noPrint;
    }

    private void deleteMainPrint() {
        System.out.println("deleteMain");
        preferences.put("setMainPrint", "");
        preferences.put("setMainPortName", "");
        preferences.putInt("setMainBaudRate", -1);
        preferences.putInt("setMainDataBit", -1);
        preferences.put("setMainParity", "");
        preferences.put("setMainStopBit","");
        preferences.put("setMainFlowControll","");
    }

    public void setEvent() {
        test_print.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                testPrint(select_com_port_combo.getValue(), select_baud_rate_combo.getValue(), select_data_bit_combo.getValue()
                        ,select_parity_combo.getValue(), select_stop_bit_combo.getValue(), select_flow_controller_combo.getValue());
            }
        });
        set_main_print.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                preferences.put("setMainPrint", insert_print_name_field.getText());
                preferences.put("setMainPortName", select_com_port_combo.getValue());
                preferences.putInt("setMainBaudRate", select_baud_rate_combo.getValue());
                preferences.putInt("setMainDataBit", select_data_bit_combo.getValue());
                preferences.put("setMainParity", select_parity_combo.getValue());
                preferences.put("setMainStopBit",select_stop_bit_combo.getValue());
                preferences.put("setMainFlowControll",select_flow_controller_combo.getValue());
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

    @Override
    public void no() {
        clickNo = true;
        clickYes =false;
    }

    @Override
    public void yes() {
        clickNo = false;
        clickYes =true;
    }
}
