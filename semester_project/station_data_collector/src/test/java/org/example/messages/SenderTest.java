package org.example.messages;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.example.model.dto.ChargingDataOutput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SenderTest {

    private final static String QUEUE = "station_charging_data";

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
    public void shouldCallMethodsForPublishingCorrectly() throws IOException, TimeoutException {
        ChargingDataOutput output = new ChargingDataOutput(123, 321, 123.321);
        String message = "mappedResult";
        when(mapper.writeValueAsString(output)).thenReturn(message);

        sender.send(output);

        verify(factory).newConnection();
        verify(connection).createChannel();
        verify(channel).queueDeclare(eq(QUEUE), eq(false), eq(false), eq(false), isNull());
        verify(channel).basicPublish(eq(""), eq(QUEUE), isNull(), eq(message.getBytes()));
    }
}
