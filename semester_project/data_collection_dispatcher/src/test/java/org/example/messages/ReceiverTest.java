package org.example.messages;

import com.rabbitmq.client.*;
import org.example.service.DCDOrchestrator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReceiverTest {

    private final static String QUEUE_NAME = "data_collection_dispatcher_queue";

    @Mock
    private ConnectionFactory factory;

    @Mock
    private Connection connection;

    @Mock
    private Channel channel;

    @Mock
    private DCDOrchestrator orchestrator;

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
        verify(channel).queueDeclare(eq(QUEUE_NAME), eq(false), eq(false), eq(false), isNull());
        verify(channel).basicConsume(eq(QUEUE_NAME), eq(true), any(DeliverCallback.class), (CancelCallback) any());
    }

    @Test
    public void shouldExecuteDeliverCallbackOnMessageReceived() throws Exception {
        final String messageBody = "123";
        byte[] body = messageBody.getBytes(StandardCharsets.UTF_8);
        final Integer expected = 123;

        receiver.listen();

        ArgumentCaptor<DeliverCallback> deliverCallbackCaptor = ArgumentCaptor.forClass(DeliverCallback.class);
        verify(channel).basicConsume(anyString(), anyBoolean(), deliverCallbackCaptor.capture(), (CancelCallback) any());

        DeliverCallback deliverCallback = deliverCallbackCaptor.getValue();
        deliverCallback.handle("consumerTag", mockDelivery(body));

        verify(orchestrator).orchestrateDataCollectionDispatch(expected);
    }

    @Test
    public void shouldThrowRunTimeExceptionWhenNumberFormatExceptionIsCaught() {
        final String messageBody = "123abc";
        byte[] body = messageBody.getBytes(StandardCharsets.UTF_8);

        assertThrows(RuntimeException.class, () -> {
            receiver.listen();
            ArgumentCaptor<DeliverCallback> deliverCallbackCaptor = ArgumentCaptor.forClass(DeliverCallback.class);
            verify(channel).basicConsume(anyString(), anyBoolean(), deliverCallbackCaptor.capture(), (CancelCallback) any());

            DeliverCallback deliverCallback = deliverCallbackCaptor.getValue();
            deliverCallback.handle("consumerTag", mockDelivery(body));
        });
    }

    private Delivery mockDelivery(byte[] body) {
        Envelope envelope = mock(Envelope.class);
        AMQP.BasicProperties properties = mock(AMQP.BasicProperties.class);
        return new Delivery(envelope, properties, body);
    }
}
