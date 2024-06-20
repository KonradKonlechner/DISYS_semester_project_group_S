package org.example.messages;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.example.model.dto.DataCollectionInput;
import org.example.service.SDCOrchestrator;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class Receiver {

    private final static String RECEIVE_QUEUE_NAME = "data_collection_dispatch";

    private final ConnectionFactory factory = new ConnectionFactory();
    private final SDCOrchestrator orchestrator = new SDCOrchestrator();
    private final ObjectMapper mapper = new ObjectMapper();

    public void listen() throws IOException, TimeoutException {
        factory.setHost("localhost");
        factory.setPort(30003);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(
                RECEIVE_QUEUE_NAME,
                false,
                false,
                false,
                null
        );

        System.out.println("[INFO] Listening for Message: " + RECEIVE_QUEUE_NAME);

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println("[INFO] Received Message: '" + message + "'");
            final DataCollectionInput mappedInput = mapper.readValue(delivery.getBody(), DataCollectionInput.class);
            try {
                orchestrator.orchestrateStationDataCollection(mappedInput);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };

        channel.basicConsume(RECEIVE_QUEUE_NAME, true, deliverCallback, consumerTag -> {
        });

    }
}
