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

        statusLabel = new Label();

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(customerIdField, generateButton, statusLabel);

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
}
