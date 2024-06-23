package org.pdf_generator;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class RabbitMQ_Receiver {

    private final static String QUEUE_NAME = "collected_charging_data";

    private final ConnectionFactory factory;
    private final PdfGenRepository pdfGenRepository;

    public RabbitMQ_Receiver() {
        this.factory = new ConnectionFactory();
        this.pdfGenRepository = new PdfGenRepository();
    }
    @SuppressWarnings("usused")
    public RabbitMQ_Receiver(ConnectionFactory factory, PdfGenRepository pdfGenRepository) {
        this.factory = factory;
        this.pdfGenRepository = pdfGenRepository;
    }

    public void listen() throws IOException, TimeoutException {

        factory.setHost("localhost");
        factory.setPort(30003);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        System.out.println(" [*] Waiting for messages.");

        // get message from RabbitMQ to read data for invoice generation
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String receivedInput = new String(delivery.getBody(), StandardCharsets.UTF_8);

            try {
                JSONObject collectedData = new JSONObject(receivedInput);

                int customerId = collectedData.getInt("CustomerId");

                JSONArray stationChargingData = (JSONArray) collectedData.get("StationChargingData");

                pdfGenRepository.createBill(customerId, stationChargingData);

            } catch (JSONException e) {
                System.out.print(e.getMessage());
            }

        };

        channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {});
    }

}
