package org.pdf_generator;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;

public class PdfGenRepository {

    public static ArrayList<StationChargingRate> stationChargingRates = new ArrayList<>();

    public static final String INVOICE_LOGO_PATH_SUFFIX = "\\pdf_generator\\invoice_logo.png";

    public static String getNameOfCustomerById(int customerId) {
        try (Connection connection = DB.connect()) {
            if (connection == null) {
                System.out.println("[X] System error: connection to database could not be established");
                return "";
            }

            System.out.println("Connected to the PostgreSQL customer database.");

            String query = "SELECT first_name, last_name FROM customer WHERE id = ?";

            PreparedStatement s = connection.prepareStatement(query);

            s.setInt(1, customerId);

            ResultSet rs = s.executeQuery();

            rs.next();

            String customerFirstName = rs.getString("first_name");
            String customerLastName = rs.getString("last_name");
            String customerFullName = customerFirstName + " " + customerLastName;

            System.out.println("Query result from database -> first name: " + customerFirstName + ", last name: " + customerLastName);

            return customerFullName;

        } catch (SQLException se) {
            System.out.println("### ATTENTION! A SQLException has been thrown!");
        }

        return "no name available";
    }

    public static void createBill(int customerId, JSONArray stationChargingData) {

        // initialise station charging rates
        stationChargingRates.add(new StationChargingRate(1, 0.3));
        stationChargingRates.add(new StationChargingRate(2, 0.25));
        stationChargingRates.add(new StationChargingRate(3, 0.42));

        // get full customer name from database
        String customerName = getNameOfCustomerById(customerId);

        if (customerName.equals("no name available")) {
            System.out.println("No customer with such id!");
        }

        // define path where to store pdf document and where to find logo image file
        String currentDirPath = System.getProperty("user.dir");
        String invoiceFilePath = "";
        String logoImageFilePath = "";
        if(currentDirPath.contains("semester_project")) {
            String[] splitPathString = currentDirPath.split("semester_project");
            invoiceFilePath = splitPathString[0] + "semester_project" + "\\invoices\\customer_" + customerId + "_invoice.pdf";
            logoImageFilePath = splitPathString[0] + "semester_project" + INVOICE_LOGO_PATH_SUFFIX;
        } else {
            invoiceFilePath = currentDirPath + "\\semester_project\\invoices\\customer_" + customerId + "_invoice.pdf";
            logoImageFilePath = currentDirPath + "\\semester_project" + INVOICE_LOGO_PATH_SUFFIX;
        }

        // generation of pdf document
        try {
            PdfWriter writer = new PdfWriter(invoiceFilePath);
            PdfDocument pdf = new PdfDocument(writer);
            Document doc = new Document(pdf);

            doc.add(new Paragraph("EVC Power GmbH | Höchstädtplatz 6, 1200 Wien - AUSTRIA | Tel.: +43 1 33340770 | Email: office@evc-power.com").setFontSize(10).setBorderBottom(new SolidBorder(1)));

            ImageData imageData = ImageDataFactory.create(logoImageFilePath);
            doc.add(new Image(imageData).setMaxHeight(80).setHorizontalAlignment(HorizontalAlignment.RIGHT));

            doc.add(new Paragraph("INVOICE #0695/2024").setFontSize(28));
            Paragraph datePar = new Paragraph("Date: " + LocalDateTime.now().format(DateTimeFormatter.ISO_DATE));
            datePar.setTextAlignment(TextAlignment.RIGHT);

            doc.add(datePar);
            doc.add(new Paragraph("Customer ID: " + customerId).setFontSize(14));
            doc.add(new Paragraph("Customer Name: " + customerName).setFontSize(14));
            doc.add(new Paragraph("Address: 123 Electric Drive Ave., Chargington, CA, USA 94101").setFontSize(14));

            Table table = new Table(UnitValue.createPercentArray(4)).useAllAvailableWidth().setMarginTop(50);
            table.addHeaderCell(getHeaderCell("Charging Station ID"));
            table.addHeaderCell(getHeaderCell("Consumed Energy (kWh)"));
            table.addHeaderCell(getHeaderCell("Rate (€/kWh)"));
            table.addHeaderCell(getHeaderCell("Total EUR"));

            double sumOfChargingEnergy = 0.0;
            double sumOfStationCost = 0.0;

            for (Object scd : stationChargingData) {
                JSONObject jsonStationChargingData = (JSONObject) scd;
                int stationId = jsonStationChargingData.getInt("stationId");
                String stationIdString = String.format("%d", stationId);
                Optional<StationChargingRate> stationRate = stationChargingRates.stream()
                        .filter(scr -> scr.getStationId() == stationId)
                        .findFirst();

                if (stationRate.isPresent()) {
                    final double chargingRate = stationRate.get().getChargingRate();
                    String rateString = String.format("%.2f", chargingRate).replace(",", ".");

                    double stationEnergyAmount = jsonStationChargingData.getDouble("chargedAmountkWh");
                    String stationEnergyAmountString = String.format("%.2f", stationEnergyAmount).replace(",", ".");
                    double totalStationCost = stationEnergyAmount * chargingRate;
                    String totalStationCostString = String.format("%.2f", totalStationCost).replace(",", ".");

                    sumOfChargingEnergy += stationEnergyAmount;
                    sumOfStationCost += totalStationCost;

                    table.addCell(stationIdString);
                    table.addCell(stationEnergyAmountString);
                    table.addCell(rateString);
                    table.addCell(totalStationCostString);
                }
            }

            table.addCell(getFooterCell("TOTAL SUM"));

            String energyString = String.format("%.2f", sumOfChargingEnergy).replace(",", ".");

            table.addCell(getFooterCell(energyString));

            table.addCell(getFooterCell(""));

            String totalCostString = String.format("%.2f", sumOfStationCost).replace(",", ".");

            table.addCell(getFooterCell(totalCostString));

            doc.add(table);

            System.out.println(" [x] Generating invoice pdf for CustomerId: " + customerId + ", Total Energy: " + energyString + "kWh, Total Cost: " + totalCostString + " €");

            Paragraph totalPar = new Paragraph("TOTAL: " + totalCostString + " EUR");
            totalPar.setMarginTop(20);
            totalPar.setFontSize(16);
            totalPar.setBold();
            totalPar.setTextAlignment(TextAlignment.RIGHT);
            doc.add(totalPar);

            doc.add(new Paragraph("Payment is required within 14 business days of invoice date.").setBold().setMarginTop(50));
            doc.add(new Paragraph("Thank you!").setFontSize(26).setBold());

            doc.add(new Paragraph("Payment information:").setFontSize(14).setBold().setUnderline());

            Text bankNameTitle = new Text("Bank Name: ").setBold();
            Text bankNameText = new Text("Unicredit Bank Austria AG");
            Paragraph bankNamePar = new Paragraph().add(bankNameTitle).add(bankNameText);

            doc.add(bankNamePar);

            Text bankAccountTitle = new Text("Bank Account: ").setBold();
            Text bankAccountText = new Text("IBAN: AT01 1000 1000 0111 1234, BIC: BABAAT11XXX");
            Paragraph bankAccountPar = new Paragraph().add(bankAccountTitle).add(bankAccountText);

            doc.add(bankAccountPar);

            doc.close();

            System.out.println("Generated pdf file: " + invoiceFilePath);

        } catch (IOException e) {
            System.err.println("Failed to create bill " + e.getMessage());
        }
    }

    private static Cell getHeaderCell(String s) {
        return new Cell().add(new Paragraph(s)).setBold().setBackgroundColor(ColorConstants.GREEN);
    }

    private static Cell getFooterCell(String s) {
        return new Cell().add(new Paragraph(s)).setBold().setBackgroundColor(ColorConstants.LIGHT_GRAY);
    }

}
