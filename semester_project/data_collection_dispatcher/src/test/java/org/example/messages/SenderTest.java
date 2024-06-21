package org.example.messages;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.example.model.dto.DataCollectionOutput;
import org.example.model.dto.JobStarterInfoOutput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SenderTest {

    private final static String DATA_COLLECTION_QUEUE = "data_collection_dispatch";
    private final static String JOB_STARTED_QUEUE = "job_started_info";

    @Mock
    private ConnectionFactory factory;

    @Mock
    private Connection connection;

    @Mock
    private Channel channel;

    @Mock
    private ObjectMapper mapper;

    @InjectMocks
    private Sender sender;

    @BeforeEach
    public void setUp() throws IOException, TimeoutException {
        when(factory.newConnection()).thenReturn(connection);
        when(connection.createChannel()).thenReturn(channel);
    }

    @Test
    public void DATA_COLLECTION_shouldCallMethodsForPublishingCorrectly() throws IOException, TimeoutException {
        DataCollectionOutput output = new DataCollectionOutput(123, 321, "url");
        String message = "mappedResult";
        when(mapper.writeValueAsString(output)).thenReturn(message);

        sender.send(output);

        verify(factory).newConnection();
        verify(connection).createChannel();
        verify(channel).queueDeclare(eq(DATA_COLLECTION_QUEUE), eq(false), eq(false), eq(false), isNull());
        verify(channel).basicPublish(eq(""), eq(DATA_COLLECTION_QUEUE), isNull(), eq(message.getBytes()));
    }

    @Test
    public void JOB_STARTED_INFO_shouldCallMethdosForPublishingCorrectly() throws IOException, TimeoutException {

        JobStarterInfoOutput output = new JobStarterInfoOutput(
                123,
                List.of()
        );

        String message = "mappedResult";
        when(mapper.writeValueAsString(output)).thenReturn(message);

        sender.send(output);

        verify(factory).newConnection();
        verify(connection).createChannel();
        verify(channel).queueDeclare(eq(JOB_STARTED_QUEUE), eq(false), eq(false), eq(false), isNull());
        verify(channel).basicPublish(eq(""), eq(JOB_STARTED_QUEUE), isNull(), eq(message.getBytes()));
    }
}
