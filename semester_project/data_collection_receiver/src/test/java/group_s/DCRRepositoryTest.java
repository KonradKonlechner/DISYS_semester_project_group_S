package group_s;

import com.rabbitmq.client.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static org.mockito.Mockito.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DCRRepositoryTest {

    @Mock
    private RabbitMQ_Receiver receiver;

    @Mock
    private RabbitMQ_Sender sender;

    @InjectMocks
    private DCRRepository repository;

    @Test
    public void shouldExecuteReceiveJobStartInfo() throws Exception {

        int customerId = 1;

        JSONArray stations = initStations();

        JSONObject messageBody = new JSONObject();
        messageBody.put("customerId", customerId);
        messageBody.put("stations", stations);

        final String messageBodyString = messageBody.toString();
        byte[] body = messageBodyString.getBytes(StandardCharsets.UTF_8);

        ArrayList<DataCollectionJob> jobs = new ArrayList<>();

        repository.receiveJobs(jobs);

        ArgumentCaptor<DeliverCallback> deliverCallbackCaptor = ArgumentCaptor.forClass(DeliverCallback.class);

        verify(receiver).receiveJobStartInfo(deliverCallbackCaptor.capture());

        DeliverCallback deliverCallback = deliverCallbackCaptor.getValue();
        deliverCallback.handle("consumerTag", mockDelivery(body));

        assertEquals(1, jobs.get(0).getCustomerId());
        assertEquals(3, jobs.get(0).getNumOfStations());
    }

    @Test
    public void shouldExecuteReceiveStationChargingData() throws Exception {

        int customerId = 3;

        JSONObject messageBody = new JSONObject();
        messageBody.put("customerId", customerId);
        messageBody.put("stationId", 1);
        messageBody.put("chargedAmountkWh", 166.8);

        final String messageBodyString = messageBody.toString();
        byte[] body = messageBodyString.getBytes(StandardCharsets.UTF_8);

        ArrayList<DataCollectionJob> jobs = new ArrayList<>();

        JSONArray stations = initStations();
        jobs.add(new DataCollectionJob(3, stations));

        repository.receiveStationData(jobs);

        ArgumentCaptor<DeliverCallback> deliverCallbackCaptor = ArgumentCaptor.forClass(DeliverCallback.class);

        verify(receiver).receiveStationChargingData(deliverCallbackCaptor.capture());

        DeliverCallback deliverCallback = deliverCallbackCaptor.getValue();
        deliverCallback.handle("consumerTag", mockDelivery(body));

        assertEquals(3, jobs.get(0).getCustomerId());
        assertEquals(messageBodyString, jobs.get(0).getStationChargingData().get(0).toString());
    }

    @Test
    public void shouldExecuteSendJobData() {

        int customerId = 3;

        JSONArray stationChargingData = initStationChargingData(customerId);

        repository.sendJobData(customerId, stationChargingData);

        JSONObject sentData = new JSONObject();

        sentData.put("CustomerId", customerId);
        sentData.put("StationChargingData", stationChargingData);

        ArgumentCaptor<JSONObject> sentDataCaptor = ArgumentCaptor.forClass(JSONObject.class);

        verify(sender).sendCollectedData(sentDataCaptor.capture());

        assertEquals(sentData.toString(), sentDataCaptor.getValue().toString());

    }

    private Delivery mockDelivery(byte[] body) {
        Envelope envelope = mock(Envelope.class);
        AMQP.BasicProperties properties = mock(AMQP.BasicProperties.class);
        return new Delivery(envelope, properties, body);
    }

    private JSONArray initStations () {

        JSONArray stations = new JSONArray();

        JSONObject station1Info = new JSONObject();
        station1Info.put("stationId", 1);
        station1Info.put("longitude", "16.378605");
        station1Info.put("latitude", "48.184193");

        JSONObject station2Info = new JSONObject();
        station2Info.put("stationId", 2);
        station2Info.put("longitude", "16.377747");
        station2Info.put("latitude", "48.186115");

        JSONObject station3Info = new JSONObject();
        station3Info.put("stationId", 3);
        station3Info.put("longitude", "16.376785");
        station3Info.put("latitude", "48.23294");

        stations.put(station1Info);
        stations.put(station2Info);
        stations.put(station3Info);

        return stations;
    }

    private JSONArray initStationChargingData (int customerId) {

        JSONArray stationChargingData = new JSONArray();

        JSONObject station1Data = new JSONObject();
        station1Data.put("customerId", customerId);
        station1Data.put("stationId", 1);
        station1Data.put("chargedAmountkWh", 166.8);

        JSONObject station2Data = new JSONObject();
        station2Data.put("customerId", customerId);
        station2Data.put("stationId", 2);
        station2Data.put("chargedAmountkWh", 176.0);

        JSONObject station3Data = new JSONObject();
        station3Data.put("customerId", customerId);
        station3Data.put("stationId", 3);
        station3Data.put("chargedAmountkWh", 151.1);

        stationChargingData.put(station1Data);
        stationChargingData.put(station2Data);
        stationChargingData.put(station3Data);

        return stationChargingData;
    }

}