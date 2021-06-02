package com.baro.controllers;

import com.baro.Dialog.NoDataDialog;
import com.baro.Dialog.PrintDialog;
import com.baro.Printer.ReceiptPrint;
import com.baro.utils.LayoutSize;
import com.fazecast.jSerialComm.SerialPort;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class SettingController implements Initializable, PrintDialog.PrintDialogInterface {
    public enum ADD_PRINT {
        NAME_EX,
        NO_NAME,
        NO_PB
    }

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
        select_com_port_combo.setPrefWidth(         LayoutSize.COMBO_BOX_WIDTH);
        select_baud_rate_combo.setPrefWidth(        LayoutSize.COMBO_BOX_WIDTH);
        select_data_bit_combo.setPrefWidth(         LayoutSize.COMBO_BOX_WIDTH);
        insert_print_name_field.setPrefWidth(       LayoutSize.COMBO_BOX_WIDTH);

        ObservableList<String> portList         = FXCollections.observableList(makePortList);
        ObservableList<Integer> baudRateList    = FXCollections.observableList(makeBaudRateList);
        ObservableList<Integer> dataBitList     = FXCollections.observableList(makeDataBit);


        select_com_port_combo.setItems(         portList);
        select_baud_rate_combo.setItems(        baudRateList);
        select_data_bit_combo.setItems(         dataBitList);

        select_parity_combo.setVisible(false);
        select_stop_bit_combo.setVisible(false);
        select_flow_controller_combo.setVisible(false);
        
        select_parity_combo.setPrefWidth(           LayoutSize.COMBO_BOX_WIDTH);
        select_stop_bit_combo.setPrefWidth(         LayoutSize.COMBO_BOX_WIDTH);
        select_flow_controller_combo.setPrefWidth(  LayoutSize.COMBO_BOX_WIDTH);
        ObservableList<String> parityList       = FXCollections.observableList(makeParity);
        ObservableList<String> stopBitList      = FXCollections.observableList(makeStopBit);
        ObservableList<String> flowList         = FXCollections.observableList(makeFlowController);
        select_parity_combo.setItems(           parityList);
        select_stop_bit_combo.setItems(         stopBitList);
        select_flow_controller_combo.setItems(  flowList);

        setEvent();
    }

    public void setPrintList() {
        if(save_print_grid_pane.getChildren().size() != 0 || save_print_grid_pane.getChildren() != null) {
            save_print_grid_pane.getChildren().removeAll();
            save_print_grid_pane.getChildren().clear();
        }
        for (int i = 0; i < 5 ; i++) {
            System.out.println("savePrintName : "+ preferences.get("savePrintName"+i+"", ""));
            final int index = i;
            if(!preferences.get("savePrintName"+i+"", "").equals("")) {
                if(preferences.get("setMainPrint", "").equals(preferences.get("savePrintName"+i+"", ""))) {
                    AnchorPane main = new AnchorPane();
                    main.setStyle("-fx-background-color: #3d3d3d");
                    Label mainLabel = new Label("주 프린터");
                    mainLabel.setStyle("-fx-font-size: 15px;-fx-background-color: #8333e6;-fx-text-fill: white;-fx-background-radius: 5px;-fx-padding: 2");

                    main.getChildren().addAll(modifyPrint(index), mainLabel);
                    AnchorPane.setTopAnchor(mainLabel, -10.0);
                    AnchorPane.setRightAnchor(mainLabel, -10.0);

                    AnchorPane.setTopAnchor(modifyPrint(index), 0.0);
                    AnchorPane.setRightAnchor(modifyPrint(index), 0.0);
                    AnchorPane.setLeftAnchor(modifyPrint(index), 0.0);

                    save_print_grid_pane.getChildren().add(index, main);
                }else {
                    save_print_grid_pane.getChildren().add(index, modifyPrint(index));
                }
            }else {
                save_print_grid_pane.getChildren().add(index, addNewPrint(index));
            }
        }
    }

    public Label modifyPrint(final int index) {
        Label print = new Label(preferences.get("savePrintName"+index+"", ""));
        print.setMinWidth(LayoutSize.PRINT_LABEL_WIDTH);
        print.setMinHeight(LayoutSize.PRINT_LABEL_HEIGHT);
        print.setAlignment(Pos.CENTER);
        print.setStyle("-fx-text-fill: gray;-fx-background-color: white;-fx-font-size: 20px;-fx-background-radius: 5px");
        print.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                print.setStyle("-fx-text-fill: white;-fx-background-color: gray;-fx-font-size: 20px;-fx-background-radius: 5px");
                System.out.println("PrintOld : " + oldValueLabelIndex);
                System.out.println(save_print_grid_pane.getChildren().get(oldValueLabelIndex).getClass().toString());
                if(oldValueLabelIndex != index) {
                    if(save_print_grid_pane.getChildren().get(oldValueLabelIndex).getClass().toString().equals("class javafx.scene.layout.AnchorPane")) {
                        System.out.println(save_print_grid_pane.getChildren().get(oldValueLabelIndex).getClass());
                        ((AnchorPane)save_print_grid_pane.getChildren().get(oldValueLabelIndex))
                                .getChildren()
                                .get(0)
                                .setStyle("-fx-text-fill: gray;-fx-background-color: white;-fx-font-size: 20px;-fx-background-radius: 5px");
                    }else {
                        save_print_grid_pane
                                .getChildren()
                                .get(oldValueLabelIndex)
                                .setStyle("-fx-text-fill: gray;-fx-background-color: white;-fx-font-size: 20px;-fx-background-radius: 5px");
                    }
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
                        printDialog.call(SettingController.this, index, PrintDialog.MODIFY_PRINT);
                    }
                });
                delete_print.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        printDialog.call(SettingController.this, index, PrintDialog.DEL_PRINT);
                    }
                });
            }
        });
        return print;
    }
    public Label addNewPrint(final int index) {
        Label noPrint = new Label("+");
        noPrint.setMinWidth(LayoutSize.PRINT_LABEL_WIDTH);
        noPrint.setMinHeight(LayoutSize.PRINT_LABEL_HEIGHT);
        noPrint.setAlignment(Pos.CENTER);
        noPrint.setStyle("-fx-text-fill: gray;-fx-background-color: white;-fx-font-size: 20px;-fx-background-radius: 5px");
        noPrint.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                noPrint.setStyle("-fx-text-fill: white;-fx-background-color: gray;-fx-font-size: 20px;-fx-background-radius: 5px");
                System.out.println("noPrintOld : " + oldValueLabelIndex);
                System.out.println("noPrint : " + index);
                if(oldValueLabelIndex != index) {
                    if(save_print_grid_pane.getChildren().get(oldValueLabelIndex).getClass().toString().equals("class javafx.scene.layout.AnchorPane")) {
                        System.out.println(save_print_grid_pane.getChildren().get(oldValueLabelIndex).getClass());
                        ((AnchorPane)save_print_grid_pane.getChildren().get(oldValueLabelIndex))
                                .getChildren()
                                .get(0)
                                .setStyle("-fx-text-fill: gray;-fx-background-color: white;-fx-font-size: 20px;-fx-background-radius: 5px");
                    }else {
                        save_print_grid_pane
                                .getChildren()
                                .get(oldValueLabelIndex)
                                .setStyle("-fx-text-fill: gray;-fx-background-color: white;-fx-font-size: 20px;-fx-background-radius: 5px");
                    }
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
                        printDialog.call(SettingController.this, index, PrintDialog.ADD_PRINT);
                    }
                });
            }
        });
        return noPrint;
    }

    private void deleteMainPrint() {
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
                        ,"없음", "1", "없음");
            }
        });
        set_main_print.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                printDialog.call(SettingController.this, 0, PrintDialog.SET_MAIN);
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
            printOutput.write(ReceiptPrint.TXT_ALIGN_CT);
            printOutput.write(textString1.getBytes("EUC-KR"));

            printOutput.write(ReceiptPrint.TXT_NORMAL);
            printOutput.write(ReceiptPrint.TXT_ALIGN_LT);
            printOutput.write(textString2.getBytes("EUC-KR"));
            printOutput.write(textString3.getBytes("EUC-KR"));
            printOutput.write(textString4.getBytes("EUC-KR"));
            printOutput.write(textString5.getBytes("EUC-KR"));
            printOutput.write(textString6.getBytes("EUC-KR"));

            printOutput.write("\n\n\n\n\n\n\n\n\n".getBytes("EUC-KR"));
            printOutput.write(ReceiptPrint.PAPER_FULL_CUT);
            printOutput.flush();
            printOutput.close();
            serialPort.closePort();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void MODIFY_PRINT(int index) {
        preferences.put("savePrintName"+index+"", insert_print_name_field.getText());
        preferences.put("savePortName"+index+"", select_com_port_combo.getValue());
        preferences.putInt("saveBaudRate"+index+"", select_baud_rate_combo.getValue());
        preferences.putInt("saveDataBit"+index+"", select_data_bit_combo.getValue());

        preferences.put("saveParity"+index+"", "없음");
        preferences.put("saveStopBit"+index+"", "1");
        preferences.put("saveFlowControll"+index+"", "없음");

        save_print_grid_pane.getChildren().remove(index);
        save_print_grid_pane.getChildren().add(index, modifyPrint(index));
    }

    @Override
    public void ADD_PRINT(int index) {
        System.out.println("savePrint");
        print_info_combo_box.setVisible(false);
        save_print.setVisible(false);
        delete_print.setVisible(false);
        set_main_print.setVisible(false);
        test_print.setVisible(false);

        if(checkName(insert_print_name_field.getText())) {
            preferences.put("savePrintName"+index+"", insert_print_name_field.getText());
            preferences.put("savePortName"+index+"", select_com_port_combo.getValue());
            preferences.putInt("saveBaudRate"+index+"", select_baud_rate_combo.getValue());
            preferences.putInt("saveDataBit"+index+"", select_data_bit_combo.getValue());

            preferences.put("saveParity"+index+"", "없음");
            preferences.put("saveStopBit"+index+"", "1");
            preferences.put("saveFlowControll"+index+"", "없음");
//                        save_print_grid_pane.getChildren().remove(index);
//                        save_print_grid_pane.add(addPrint(index), index, 0);
            save_print_grid_pane.getChildren().remove(index);
            save_print_grid_pane.getChildren().add(index, modifyPrint(index));
        }else {
            save_print_grid_pane
                    .getChildren()
                    .get(index)
                    .setStyle("-fx-text-fill: gray;-fx-background-color: white;-fx-font-size: 20px;-fx-background-radius: 5px");
        }
        System.out.println("passThrough");
    }

    @Override
    public void DEL_PRINT(int index) {
        print_info_combo_box.setVisible(false);
        save_print.setVisible(false);
        delete_print.setVisible(false);
        set_main_print.setVisible(false);
        test_print.setVisible(false);

        if(preferences.get("setMainPrint", "").equals( preferences.get("savePrintName"+index+"",""))) {
            deleteMainPrint();
        }
//        preferences.put("savePrintName"+index+"", "");
//        preferences.put("savePortName"+index+"", "");
//        preferences.putInt("saveBaudRate"+index+"", -1);
//        preferences.putInt("saveDataBit"+index+"", -1);
//        preferences.put("saveParity"+index+"", "");
//        preferences.put("saveStopBit"+index+"","");
//        preferences.put("saveFlowControll"+index+"","");

        preferences.remove("savePrintName"+index+"");
        preferences.remove("savePortName"+index+"");
        preferences.remove("saveBaudRate"+index+"");
        preferences.remove("saveDataBit"+index+"");
        preferences.remove("saveParity"+index+"");
        preferences.remove("saveStopBit"+index+"");
        preferences.remove("saveFlowControll"+index+"");

        save_print_grid_pane.getChildren().remove(index);
        save_print_grid_pane.getChildren().add(index, addNewPrint(index));
    }

    @Override
    public void SET_MAIN(int index) {
        preferences.put("setMainPrint", insert_print_name_field.getText());
        preferences.put("setMainPortName", select_com_port_combo.getValue());
        preferences.putInt("setMainBaudRate", select_baud_rate_combo.getValue());
        preferences.putInt("setMainDataBit", select_data_bit_combo.getValue());
        preferences.put("setMainParity", "없음");
        preferences.put("setMainStopBit","1");
        preferences.put("setMainFlowControll","없음");
        setPrintList();
    }

    @Override
    public void DEL_MAIN(int index) {

    }
    public boolean checkName(String name) {
        NoDataDialog nameInVaild = new NoDataDialog();
        for (int i = 0; i < 5; i++) {
            final int index = i;
            if(name.trim().equals("")) {
                nameInVaild.call("공백은 이름으로 설정이 불가합니다.");
                return false;
            }
            if(preferences.get("savePrintName"+index+"", "").equals(name)) {
                nameInVaild.call("같은 이름의 설정이 존재합니다.");
                return false;
            }
        }
        return true;
    }
}
