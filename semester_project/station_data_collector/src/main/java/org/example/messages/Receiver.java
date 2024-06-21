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

    private final ConnectionFactory factory;
    private final SDCOrchestrator orchestrator;
    private final ObjectMapper mapper;

    public Receiver() {
        this.factory = new ConnectionFactory();
        this.orchestrator = new SDCOrchestrator();
        this.mapper = new ObjectMapper();
    }

    @SuppressWarnings("unused")
    public Receiver(ConnectionFactory factory, SDCOrchestrator orchestrator, ObjectMapper mapper) {
        this.factory = factory;
        this.orchestrator = orchestrator;
        this.mapper = mapper;
    }

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

        channel.basicConsume(RECEIVE_QUEUE_NAME, true, deliverCallback, consumerTag -> {});

    }
}
