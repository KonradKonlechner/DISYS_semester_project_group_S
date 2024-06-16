package org.pdf_generator;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.UnitValue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.rabbitmq.client.DeliverCallback;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

import org.pdf_generator.RabbitMQ_Receiver;

public class Main {

    public static final String LOREM_IPSUM_TEXT = "Lorem ipsum dolor sit amet, consectetur adipisici elit, sed eiusmod tempor incidunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquid ex ea commodi consequat. Quis aute iure reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint obcaecat cupiditat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";
    public static final String GOOGLE_MAPS_PNG = "./google_maps.png";
    public static final String TARGET_PDF = "customer_01_invoice.pdf";

    public static void main(String[] args ) throws IOException, TimeoutException {

        // get message from RabbitMQ to read customerId
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String ReceivedInput = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println(" [x] Received '" + ReceivedInput + "'");
            getNameOfCustomerById(Integer.parseInt(ReceivedInput));
            //String output = executeInternal(input);
            //Producer.send(output, outDestination, brokerUrl);
        };

        RabbitMQ_Receiver.receive( 10000, deliverCallback);

    }

    private static void getNameOfCustomerById(int customerId) {
        try (Connection c = DB.connect()) {

            System.out.println("Connected to the PostgreSQL customer database.");

            String query = "SELECT first_name, last_name FROM customer WHERE id = ?";

            PreparedStatement s = c.prepareStatement(query);

            s.setInt(1, customerId);

            ResultSet rs = s.executeQuery();

            rs.next();

            String customerFirstName = rs.getString("first_name");
            String customerLastName = rs.getString("last_name");

            System.out.println("Vorname: " + customerFirstName + ", Nachname: " + customerLastName);

            /*
            while(rs.next()) {
                System.out.println("Vorname: " + rs.getString("first_name") + ", Nachname: " + rs.getString("last_name"));
            }*/
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    private static Cell getHeaderCell(String s) {
        return new Cell().add(new Paragraph(s)).setBold().setBackgroundColor(ColorConstants.GRAY);
    }

    public void createBill(String input, String filename) {
        try {
            PdfWriter writer = new PdfWriter(filename);
            PdfDocument pdf = new PdfDocument(writer);
            Document doc = new Document(pdf);

            doc.add( new Paragraph(input).setFontSize(14).setBold() );
            doc.add( new Paragraph(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)) );
            doc.add( new Paragraph("Cost 10€").setFontColor(ColorConstants.RED) );

            doc.close();

        } catch (IOException e) {
            System.err.println("Failed to create bill " + e.getMessage());
        }
    }


    /*
    PdfWriter writer = new PdfWriter(TARGET_PDF);
    PdfDocument pdf = new PdfDocument(writer);
    Document document = new Document(pdf);

    Paragraph loremIpsumHeader = new Paragraph("Invoice")
            .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA))
            .setFontSize(14)
            .setBold()
            .setFontColor(ColorConstants.RED);
        document.add(loremIpsumHeader);
        document.add(new Paragraph(LOREM_IPSUM_TEXT));

    Paragraph listHeader = new Paragraph("Lorem Ipsum ...")
            .setFont(PdfFontFactory.createFont(StandardFonts.TIMES_BOLD))
            .setFontSize(14)
            .setBold()
            .setFontColor(ColorConstants.BLUE);
    List list = new List()
            .setSymbolIndent(12)
            .setListSymbol("\u2022")
            .setFont(PdfFontFactory.createFont(StandardFonts.TIMES_BOLD));
        list.add(new ListItem("lorem ipsum 1"))
            .add(new ListItem("lorem ipsum 2"))
            .add(new ListItem("lorem ipsum 3"))
            .add(new ListItem("lorem ipsum 4"))
            .add(new ListItem("lorem ipsum 5"))
            .add(new ListItem("lorem ipsum 6"));
        document.add(listHeader);
        document.add(list);

    Paragraph tableHeader = new Paragraph("Lorem Ipsum Table ...")
            .setFont(PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN))
            .setFontSize(18)
            .setBold()
            .setFontColor(ColorConstants.GREEN);
        document.add(tableHeader);
    Table table = new Table(UnitValue.createPercentArray(4)).useAllAvailableWidth();
        table.addHeaderCell(getHeaderCell("Ipsum 1"));
        table.addHeaderCell(getHeaderCell("Ipsum 2"));
        table.addHeaderCell(getHeaderCell("Ipsum 3"));
        table.addHeaderCell(getHeaderCell("Ipsum 4"));
        table.setFontSize(14).setBackgroundColor(ColorConstants.WHITE);
        table.addCell("lorem 1");
        table.addCell("lorem 2");
        table.addCell("lorem 3");
        table.addCell("lorem 4");
        document.add(table);

        document.add(new AreaBreak());

    Paragraph imageHeader = new Paragraph("Lorem Ipsum Image ...")
            .setFont(PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN))
            .setFontSize(18)
            .setBold()
            .setFontColor(ColorConstants.GREEN);
        document.add(imageHeader);
    ImageData imageData = ImageDataFactory.create(GOOGLE_MAPS_PNG);
        document.add(new Image(imageData));

        document.close();*/


}