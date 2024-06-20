package org.example.messages;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.example.model.Dto.DataCollectionOutput;
import org.example.model.Dto.JobStarterInfoOutput;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Sender {

    private final static String DATA_COLLECTION_DISPATCH_QUEUE = "data_collection_dispatch";
    private final static String JOB_STARTED_INFO_QUEUE = "job_started_info ";

    private final ConnectionFactory factory = new ConnectionFactory();
    private final ObjectMapper mapper = new ObjectMapper();

    public Sender() {
        factory.setHost("localhost");
        factory.setPort(30003);
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
