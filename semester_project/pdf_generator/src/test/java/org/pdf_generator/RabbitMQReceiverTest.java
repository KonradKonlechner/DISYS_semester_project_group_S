package org.pdf_generator;

import com.rabbitmq.client.*;
import org.json.JSONArray;
import org.json.JSONObject;
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
public class RabbitMQReceiverTest {

    private final static String QUEUE_NAME = "collected_charging_data";

    @Mock
    private ConnectionFactory factory;

    @Mock
    private Connection connection;

    @Mock
    private Channel channel;

    @Mock
    private PdfGenRepository pdfGenRepository;

    @InjectMocks
    private RabbitMQ_Receiver receiver;

    @BeforeEach
    public void setUp() throws IOException, TimeoutException {
        when(factory.newConnection()).thenReturn(connection);
        when(connection.createChannel()).thenReturn(channel);
    }

    @Test
    public void shouldSetupConnectionFactory() throws Exception {
        receiver.listen();

        verify(factory).setHost("localhost");
        verify(factory).setPort(30003);
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

        int customerId = 1;

        JSONArray stationChargingData = new JSONArray();

        JSONObject station1Data = new JSONObject();
        station1Data.put("customerId", 1);
        station1Data.put("stationId", 1);
        station1Data.put("chargedAmountkWh", 71.1);

        JSONObject station2Data = new JSONObject();
        station2Data.put("customerId", 1);
        station2Data.put("stationId", 2);
        station2Data.put("chargedAmountkWh", 182.3);

        JSONObject station3Data = new JSONObject();
        station3Data.put("customerId", 1);
        station3Data.put("stationId", 3);
        station3Data.put("chargedAmountkWh", 167.5);

        stationChargingData.put(station1Data);
        stationChargingData.put(station2Data);
        stationChargingData.put(station3Data);

        JSONObject messageBody = new JSONObject();
        messageBody.put("CustomerId", customerId);
        messageBody.put("StationChargingData", stationChargingData);

        final String messageBodyString = messageBody.toString();
        byte[] body = messageBodyString.getBytes(StandardCharsets.UTF_8);

        receiver.listen();

        ArgumentCaptor<DeliverCallback> deliverCallbackCaptor = ArgumentCaptor.forClass(DeliverCallback.class);
        verify(channel).basicConsume(anyString(), anyBoolean(), deliverCallbackCaptor.capture(), (CancelCallback) any());

        DeliverCallback deliverCallback = deliverCallbackCaptor.getValue();
        deliverCallback.handle("consumerTag", mockDelivery(body));

        verify(pdfGenRepository).createBill(customerId, stationChargingData);

    }

    private Delivery mockDelivery(byte[] body) {
        Envelope envelope = mock(Envelope.class);
        AMQP.BasicProperties properties = mock(AMQP.BasicProperties.class);
        return new Delivery(envelope, properties, body);
    }
}



