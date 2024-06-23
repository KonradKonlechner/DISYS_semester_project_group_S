package group_s;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;


import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static org.mockito.Mockito.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RabbitMQSenderTest {

    private final static String COLLECTED_DATA_QUEUE_NAME = "collected_charging_data";

    @Mock
    private ConnectionFactory factory;

    @Mock
    private Connection connection;

    @Mock
    private Channel channel;

    @InjectMocks
    private RabbitMQ_Sender sender;

    @BeforeEach
    public void setUp() throws IOException, TimeoutException {
        when(factory.newConnection()).thenReturn(connection);
        when(connection.createChannel()).thenReturn(channel);
    }

    @Test
    public void sendCollectedData_shouldCallMethodsForPublishingCorrectly() throws IOException, TimeoutException {

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

        JSONObject data = new JSONObject();
        data.put("CustomerId", customerId);
        data.put("StationChargingData", stationChargingData);

        sender.sendCollectedData(data);

        verify(factory).newConnection();
        verify(connection).createChannel();
        verify(channel).queueDeclare(eq(COLLECTED_DATA_QUEUE_NAME), eq(false), eq(false), eq(false), isNull());
        verify(channel).basicPublish(eq(""), eq(COLLECTED_DATA_QUEUE_NAME), isNull(), eq(data.toString().getBytes()));
    }

}