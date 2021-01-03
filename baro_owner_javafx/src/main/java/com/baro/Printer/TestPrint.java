package com.baro.Printer;

import com.fazecast.jSerialComm.SerialPort;
import javafx.print.PrinterJob;

import javax.print.*;
import javax.print.attribute.DocAttributeSet;
import javax.print.event.PrintServiceAttributeEvent;
import javax.print.event.PrintServiceAttributeListener;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class TestPrint {
    public static void printTest() {
        System.out.println("portNames : " + SerialPort.getCommPorts().toString());
//        String content = "";
//        PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
//
//
//        content += "Number of print services: " + services.length;
//        content += "\n";
//
//        if(services.length != 0 || services != null) {
//            int i = 1;
//            for(PrintService service : services) {
//                String name = service.getName();
//
//                content += "Printer " + i + " name: " + name;
//                content += "\n";
//                i++;
//            }
//        }
//
//        //System.out.println(content);
//        //services[0].createPrintJob().print();
        String portName = "COM9";
        Integer baudrate = 9600;
        Integer timeout = 1000;

        try {
            SerialPort serialPort = SerialPort.getCommPort(portName);
            serialPort.openPort();
            serialPort.setBaudRate(baudrate);
            serialPort.setParity(SerialPort.NO_PARITY);
            serialPort.setNumDataBits(8);
            serialPort.setNumStopBits(SerialPort.ONE_STOP_BIT);
            serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, timeout, timeout);
            OutputStream os = serialPort.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
            writer.write("printer serialize");
            writer.flush();

            serialPort.closePort();
        }
        catch (IOException e) {

        }

    }
}
