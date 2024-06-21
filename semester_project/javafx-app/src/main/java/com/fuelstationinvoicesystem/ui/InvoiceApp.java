package com.fuelstationinvoicesystem.ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class InvoiceApp extends Application {

    private TextField customerIdField;
    private Label statusLabel;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Invoice Generator");

        customerIdField = new TextField();
        customerIdField.setPromptText("Enter Customer ID");

        Button generateButton = new Button("Generate Invoice");
        generateButton.setOnAction(e -> generateInvoice());

        Button downloadButton = new Button("Download Invoice");
        downloadButton.setOnAction(e -> downloadInvoice());

        statusLabel = new Label();

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(customerIdField, generateButton, downloadButton, statusLabel);

        Scene scene = new Scene(layout, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void generateInvoice() {
        String customerId = customerIdField.getText();
        if (customerId.isEmpty()) {
            statusLabel.setText("Customer ID cannot be empty");
            return;
        }

        new Thread(() -> {
            HttpURLConnection connection = null;
            try {
                URL url = new URL("http://localhost:8080/invoices/" + customerId);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);

                try (OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream())) {
                    writer.write("");
                    writer.flush();
                }

                int responseCode = connection.getResponseCode();

                Platform.runLater(() -> {
                    if (responseCode == 200) {
                        statusLabel.setText("Invoice generation started for customer: " + customerId);
                    } else {
                        statusLabel.setText("Failed to start invoice generation: " + responseCode);
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> statusLabel.setText("Error: " + e.getMessage()));
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }).start();
    }

    private void downloadInvoice() {
        String customerId = customerIdField.getText();
        if (customerId.isEmpty()) {
            statusLabel.setText("Customer ID cannot be empty");
            return;
        }

        new Thread(() -> {
            HttpURLConnection connection = null;
            try {
                URL url = new URL("http://localhost:8080/invoices/" + customerId);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                int responseCode = connection.getResponseCode();

                if (responseCode == 200) {
                    File tempFile = File.createTempFile("invoice_", ".pdf");
                    try (InputStream inputStream = connection.getInputStream();
                         FileOutputStream outputStream = new FileOutputStream(tempFile)) {
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }
                    }

                    // Open the PDF file with the default system viewer
                    if (Desktop.isDesktopSupported()) {
                        Desktop.getDesktop().open(tempFile);
                    } else {
                        Platform.runLater(() -> statusLabel.setText("PDF downloaded to: " + tempFile.getAbsolutePath()));
                    }

                    Platform.runLater(() -> statusLabel.setText("Invoice downloaded for customer: " + customerId));
                } else {
                    Platform.runLater(() -> statusLabel.setText("Failed to download invoice: " + responseCode));
                }
            } catch (Exception e) {
                Platform.runLater(() -> statusLabel.setText("Error: " + e.getMessage()));
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }).start();
    }
}
