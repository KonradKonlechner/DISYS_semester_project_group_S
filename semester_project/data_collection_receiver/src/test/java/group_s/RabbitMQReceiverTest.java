package group_s;

import com.rabbitmq.client.*;
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
public class RabbitMQReceiverTest {

    private final static String JOB_INFO_QUEUE_NAME = "job_started_info";
    private final static String STATION_CHARGING_DATA_QUEUE_NAME = "station_charging_data";

    @Mock
    private ConnectionFactory factory;

    @Mock
    private Connection connection;

    @Mock
    private Channel channel;

    @InjectMocks
    private RabbitMQ_Receiver receiver;

    @BeforeEach
    public void setUp() throws IOException, TimeoutException {
        when(factory.newConnection()).thenReturn(connection);
        when(connection.createChannel()).thenReturn(channel);
    }

    @Test
    public void shouldSetupJobStartInfoListener() throws Exception {

        // get message from RabbitMQ to read job info
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {};
        receiver.receiveJobStartInfo(deliverCallback);

        verify(factory).newConnection();
        verify(connection).createChannel();
        verify(channel).queueDeclare(eq(JOB_INFO_QUEUE_NAME), eq(false), eq(false), eq(false), isNull());
        verify(channel).basicConsume(eq(JOB_INFO_QUEUE_NAME), eq(true), any(DeliverCallback.class), (CancelCallback) any());
    }

    @Test
    public void shouldSetupStationChargingDataListener() throws Exception {

        // get message from RabbitMQ to read job info
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {};
        receiver.receiveStationChargingData(deliverCallback);

        verify(factory).newConnection();
        verify(connection).createChannel();
        verify(channel).queueDeclare(eq(STATION_CHARGING_DATA_QUEUE_NAME), eq(false), eq(false), eq(false), isNull());
        verify(channel).basicConsume(eq(STATION_CHARGING_DATA_QUEUE_NAME), eq(true), any(DeliverCallback.class), (CancelCallback) any());
    }
  
}