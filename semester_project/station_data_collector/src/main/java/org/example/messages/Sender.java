package org.example.messages;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.example.model.dto.ChargingDataOutput;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Sender {

    private final static String OUTPUT_QUEUE_NAME = "station_charging_data";

    private final ConnectionFactory factory = new ConnectionFactory();
    private final ObjectMapper mapper = new ObjectMapper();

    public Sender() {
        factory.setHost("localhost");
        factory.setPort(30003);
    }


    public void send(ChargingDataOutput output) throws IOException, TimeoutException {

        final String message = mapper.writeValueAsString(output);

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.queueDeclare(OUTPUT_QUEUE_NAME, false, false, false, null);
            channel.basicPublish("", OUTPUT_QUEUE_NAME, null, message.getBytes());
            System.out.println("[INFO] Nachricht gesendet: '" + message + "' an " + OUTPUT_QUEUE_NAME);
        }
    }
}
