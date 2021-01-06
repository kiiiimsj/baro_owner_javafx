package com.baro.Printer;

import com.baro.JsonParsing.Extras;
import com.baro.JsonParsing.OrderDetail;
import com.fazecast.jSerialComm.SerialPort;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class ReceiptPrint {
    public String customer_phone;
    public String order_date;

    public ArrayList<OrderDetail> menus;
    //menu_name, menu_count menu_price
    public int coupon;

    public String requirements_spec;

    public int totalPriceStr;


    public void printReceipt() throws IOException, DocumentException {
        String portName = "COM9";
        Integer baudrate = 57600;
        Integer timeout = 1000;
        System.out.println("printStart");
        SerialPort serialPort = SerialPort.getCommPort(portName);
        if(SerialPort.getCommPort("COM9").isOpen()) {
            System.out.println("isOpen");
            return;
        }
        serialPort.openPort();
        System.out.println("OpenPort" + serialPort.isOpen()+"");
        serialPort.setBaudRate(baudrate);
        serialPort.setParity(SerialPort.EVEN_PARITY);
        serialPort.setNumDataBits(8);
        serialPort.setNumStopBits(SerialPort.ONE_STOP_BIT);
        serialPort.setComPortTimeouts(SerialPort.TIMEOUT_NONBLOCKING, timeout, timeout);

        OutputStream printOutput = serialPort.getOutputStream();

        StringBuilder headerContent = new StringBuilder("[BARO]\n").append("주문이 접수되었습니다.\n\n");
        System.out.println("headerContent " + headerContent.toString());
        StringBuilder customerPhone = new StringBuilder("고객번호 : ").append(customer_phone);
        StringBuilder orderDateContent = new StringBuilder("주문시간 : ").append(order_date);
        StringBuilder content = new StringBuilder("---------------------------------------------------\n")
                                          .append("메뉴명                    수량                          금액\n");

        if(menus != null) {
            for (int i = 0; i < menus.size(); i++) {
                OrderDetail menu = menus.get(i);

                int lent1 = 20;
                content.append(menu.menu_name);
                for (int k = 0; k < lent1 - menu.menu_name.length(); k++) {
                    content.append(" ");
                }

                int lent2 = 28;
                content.append(menu.order_count);
                for (int k = 0; k < lent2; k++) {
                    content.append(" ");
                }

                content.append(menu.menu_defaultprice).append("\n");

                if (menu.extras == null || menu.extras.size() == 0) continue;

                for (int j = 0; j < menu.extras.size(); j++) {
                    Extras extras = menu.extras.get(j);
                    content.append("   •").append(extras.extra_name);
                    if (extras.extra_count > 1) {
                        int lent3 = 18;
                        for (int k = 0; k < lent3 - menu.menu_name.length(); k++) {
                            content.append(" ");
                        }
                        content.append(" X").append(extras.extra_count).append("\n");

                        int lent4 = 28;
                        for (int k = 0; k < lent4; k++) {
                            content.append(" ");
                        }
                        content.append(" X").append(extras.extra_price).append("원").append("\n");
                    } else {
                        content.append("\n");
                    }
                }
            }
        }
        content.append("---------------------------------------------------\n\n\n");

        System.out.println("end Content build : " + content.toString());

        StringBuilder totalPrice = new StringBuilder()
                .append("쿠폰");
        int lent5 = 60;
        for (int k = 0; k < lent5; k++) {
            totalPrice.append(" ");
        }
        totalPrice.append("-"+coupon+" 원\n");
        totalPrice.append("---------------------------------------------------\n");

        System.out.println("couponPrice " + totalPrice.toString());

        StringBuilder texCoupon = new StringBuilder()
                .append("총 결제 금액");
        int lent6 = 15;
        for (int k = 0; k < lent6; k++) {
            texCoupon.append(" ");
        }
        texCoupon.append(totalPriceStr + " 원\n\n");

        System.out.println("texCoupon : " + texCoupon.toString());

        StringBuilder customerRequest = new StringBuilder()
                .append("요청사항                                   \n")
                .append("  •")
                .append(requirements_spec).append("\n");

        System.out.println("costomerRequest " + customerRequest.toString());


        System.out.println("printOutput Write start");
        printOutput.write(new byte[]{0x1b, 0x21, 0x31});
        printOutput.write(headerContent.toString().getBytes());
        printOutput.write(customerPhone.toString().getBytes());
        printOutput.write(new byte[]{0x1b, 0x21, 0x01});
        printOutput.write(orderDateContent.toString().getBytes());
        printOutput.write(content.toString().getBytes());
        printOutput.write(new byte[]{0x1b, 0x21, 0x31});
        printOutput.write(totalPrice.toString().getBytes());
        printOutput.write(new byte[]{0x1b, 0x21, 0x01});


        printOutput.write(texCoupon.toString().getBytes());
        printOutput.write(customerRequest.toString().getBytes());

        printOutput.write(new byte[] {0x1b, 0x61, 0x31});
        printOutput.flush();
        printOutput.close();


        serialPort.closePort();

        System.out.println("serialPort close " +serialPort.isOpen() + "");
        exportToPdf(headerContent.toString(), customerPhone.toString(), orderDateContent.toString(),
                content.toString(), totalPrice.toString(), texCoupon.toString(), customerRequest.toString());

    }
    public void exportToPdf(String headerContent, String phone, String orderDate, String content,
                            String totalPrice, String texCoupon, String customerRequest) throws IOException, DocumentException {

        BaseFont base_font = BaseFont.createFont("/font/NotoSansCJKkr-Regular.otf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        Font font = new Font(base_font, 10);
        Font header_font = new Font(base_font, 15);
        header_font.setColor(BaseColor.BLACK);
        
        File dir = new File("E:/");
        String file_name = "receipt.pdf";

        Paragraph headerParagraph = new Paragraph(headerContent, header_font);
        Paragraph phoneParagraph = new Paragraph(phone, header_font);
        Paragraph orderDateParagraph = new Paragraph(orderDate, font);
        Paragraph contentParagraph = new Paragraph(content, font);
        Paragraph totalPriceParagraph = new Paragraph(totalPrice, font);
        Paragraph texCouponParagraph = new Paragraph(texCoupon, header_font);
        Paragraph customerRequestParagraph = new Paragraph(customerRequest, font);

        headerParagraph.setAlignment(Paragraph.ALIGN_CENTER);
        phoneParagraph.setAlignment(Paragraph.ALIGN_LEFT);
        orderDateParagraph.setAlignment(Paragraph.ALIGN_LEFT);
        contentParagraph.setAlignment(Paragraph.ALIGN_LEFT);
        totalPriceParagraph.setAlignment(Paragraph.ALIGN_LEFT);
        texCouponParagraph.setAlignment(Paragraph.ALIGN_LEFT);
        customerRequestParagraph.setAlignment(Paragraph.ALIGN_LEFT);
        
        //if(!dir.exists()) dir.mkdirs(); //파일경로 없으면 생성
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(dir + "/" + file_name));

        document.open();
        document.setPageSize(new Rectangle(200, 1000));
        document.setMargins(10,10,10,10);
        document.newPage();

        document.add(headerParagraph);
        document.add(phoneParagraph);
        document.add(orderDateParagraph);
        document.add(contentParagraph);
        document.add(totalPriceParagraph);
        document.add(texCouponParagraph);
        document.add(customerRequestParagraph);

        document.close();
    }
}
