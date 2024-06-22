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

    public static void receive(DeliverCallback deliverCallback) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(30003);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        System.out.println(" [*] Waiting for messages.");
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {});
    }

    public static void listen() throws IOException, TimeoutException {

        // get message from RabbitMQ to read data for invoice generation
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String receivedInput = new String(delivery.getBody(), StandardCharsets.UTF_8);

            try {
                JSONObject collectedData = new JSONObject(receivedInput);

                int customerId = collectedData.getInt("CustomerId");

                JSONArray stationChargingData = (JSONArray) collectedData.get("StationChargingData");

                PdfGenRepository.createBill(customerId, stationChargingData);

            } catch (JSONException e) {
                System.out.print(e.getMessage());
            }

        };

        receive(deliverCallback);
    }

}
