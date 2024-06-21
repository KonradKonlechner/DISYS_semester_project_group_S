package org.example.messages;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.example.model.dto.DataCollectionOutput;
import org.example.model.dto.JobStarterInfoOutput;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Sender {

    private final static String DATA_COLLECTION_DISPATCH_QUEUE = "data_collection_dispatch";
    private final static String JOB_STARTED_INFO_QUEUE = "job_started_info";

    private final ConnectionFactory factory;
    private final ObjectMapper mapper;

    public Sender() {
        this.factory = new ConnectionFactory();
        this.mapper = new ObjectMapper();
        factory.setHost("localhost");
        factory.setPort(30003);
    }

    @SuppressWarnings("unused")
    public Sender(ConnectionFactory factory, ObjectMapper mapper) {
        this.factory = factory;
        this.mapper = mapper;
    }

    public void send(DataCollectionOutput output) throws IOException, TimeoutException {

        final String message = mapper.writeValueAsString(output);

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.queueDeclare(DATA_COLLECTION_DISPATCH_QUEUE, false, false, false, null);
            channel.basicPublish("", DATA_COLLECTION_DISPATCH_QUEUE, null, message.getBytes());
            System.out.println("[INFO] Nachricht gesendet: '" + message + "' an " + DATA_COLLECTION_DISPATCH_QUEUE);
        }
    }

    public void send(JobStarterInfoOutput output) throws IOException, TimeoutException {

        final String message = mapper.writeValueAsString(output);

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.queueDeclare(JOB_STARTED_INFO_QUEUE, false, false, false, null);
            channel.basicPublish("", JOB_STARTED_INFO_QUEUE, null, message.getBytes());
            System.out.println("[INFO] Nachricht gesendet: '" + message + "' an " + JOB_STARTED_INFO_QUEUE);
        }
    }
}
