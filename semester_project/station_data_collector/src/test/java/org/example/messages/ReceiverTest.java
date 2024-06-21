package org.example.messages;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.*;
import org.example.model.dto.DataCollectionInput;
import org.example.service.SDCOrchestrator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReceiverTest {


    private final static String QUEUEUE_NAME = "data_collection_dispatch";

    @Mock
    private ConnectionFactory factory;

    @Mock
    private Connection connection;

    @Mock
    private Channel channel;

    @Mock
    private ObjectMapper mapper;

    @Mock
    private SDCOrchestrator orchestrator;

    @InjectMocks
    private Receiver receiver;

    @BeforeEach
    public void setUp() throws IOException, TimeoutException {
        when(factory.newConnection()).thenReturn(connection);
        when(connection.createChannel()).thenReturn(channel);
    }

    @Test
    public void shouldSetupListener() throws Exception {
        receiver.listen();

        verify(factory).newConnection();
        verify(connection).createChannel();
        verify(channel).queueDeclare(eq(QUEUEUE_NAME), eq(false), eq(false), eq(false), isNull());
        verify(channel).basicConsume(eq(QUEUEUE_NAME), eq(true), any(DeliverCallback.class), (CancelCallback) any());
    }

    @Test
    public void shouldExecuteDeliverCallbackOnMessageReceived() throws Exception {
        final String messageBody = "some message";
        byte[] body = messageBody.getBytes(StandardCharsets.UTF_8);

        DataCollectionInput mappedInput = new DataCollectionInput(1, 2, "url");
        when(mapper.readValue(body, DataCollectionInput.class)).thenReturn(mappedInput);

        receiver.listen();

        ArgumentCaptor<DeliverCallback> deliverCallbackCaptor = ArgumentCaptor.forClass(DeliverCallback.class);
        verify(channel).basicConsume(anyString(), anyBoolean(), deliverCallbackCaptor.capture(), (CancelCallback) any());

        DeliverCallback deliverCallback = deliverCallbackCaptor.getValue();
        deliverCallback.handle("consumerTag", mockDelivery(body));

        verify(mapper).readValue(body, DataCollectionInput.class);
        verify(orchestrator).orchestrateStationDataCollection(mappedInput);
    }

    private Delivery mockDelivery(byte[] body) {
        Envelope envelope = mock(Envelope.class);
        AMQP.BasicProperties properties = mock(AMQP.BasicProperties.class);
        return new Delivery(envelope, properties, body);
    }
}
