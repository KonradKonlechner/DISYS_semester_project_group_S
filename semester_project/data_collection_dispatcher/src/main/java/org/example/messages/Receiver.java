package org.example.messages;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.example.service.DCDOrchestrator;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class Receiver {
    private final static String RECEIVE_QUEUE_NAME = "data_collection_dispatcher_queue";

    private final ConnectionFactory factory = new ConnectionFactory();
    private final DCDOrchestrator orchestrator = new DCDOrchestrator();


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
            try {
                orchestrator.orchestrateDataCollectionDispatch(Integer.parseInt(message));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };

        channel.basicConsume(RECEIVE_QUEUE_NAME, true, deliverCallback, consumerTag -> {
        });

    }
}
