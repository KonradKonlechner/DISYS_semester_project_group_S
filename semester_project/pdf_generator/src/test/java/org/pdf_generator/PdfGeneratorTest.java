package org.pdf_generator;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class PdfGeneratorTest {

    @Test
    void getNameOfCustomerById_whenCustomerExists_thenCheckName() {

        // Arrange
        int customerId = 1;

        // Act
        String customerName = PdfGenRepository.getNameOfCustomerById(customerId);

        boolean result = (customerName.equals("Luisa Colon"));

        // Assert
        assertTrue(result);
    }

    @Test
    void getNameOfCustomerById_whenCustomerNotExists_thenCheckName() {

        // Arrange
        int customerId = 4;

        // Act
        String customerName = PdfGenRepository.getNameOfCustomerById(customerId);

        boolean result = (customerName.equals("no name available"));

        // Assert
        assertTrue(result);
    }

    @Test
    void createBill_whenCustomerIdAndDataProvided_thenCheckIfFileExists() {

        // Arrange
        int customerId = 1;

        JSONArray stationChargingData = new JSONArray();

        JSONObject station1Data = new JSONObject();
        station1Data.put("customerId", 1);
        station1Data.put("stationId", 1);
        station1Data.put("chargedAmountkWh", 71.1);

        JSONObject station2Data = new JSONObject();
        station2Data.put("customerId", 1);
        station2Data.put("stationId", 2);
        station2Data.put("chargedAmountkWh", 182.3);

        JSONObject station3Data = new JSONObject();
        station3Data.put("customerId", 1);
        station3Data.put("stationId", 3);
        station3Data.put("chargedAmountkWh", 167.5);

        stationChargingData.put(station1Data);
        stationChargingData.put(station2Data);
        stationChargingData.put(station3Data);

        // Act
        PdfGenRepository.createBill(customerId, stationChargingData);

        String currentDirPath = System.getProperty("user.dir");
        String[] splitPathString = currentDirPath.split("semester_project");
        String invoiceFilePath = splitPathString[0] + "semester_project" + "\\invoices\\customer_" + customerId + "_invoice.pdf";

        System.out.println(invoiceFilePath);

        File invoiceFile = new File(invoiceFilePath);

        boolean result = (invoiceFile.isFile());

        // Assert
        assertTrue(result);
    }

}