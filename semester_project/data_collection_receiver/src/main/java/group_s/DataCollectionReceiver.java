package group_s;

import com.rabbitmq.client.DeliverCallback;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class DataCollectionReceiver {
    public static void main(String[] args) throws JSONException, IOException, TimeoutException {

        ArrayList<DataCollectionJob> jobs = new ArrayList<>();

        receiveJobs(jobs);

        receiveStationData(jobs);

    }

    private static void receiveJobs(ArrayList<DataCollectionJob> jobs) throws IOException, TimeoutException {

        // get message from RabbitMQ to read job info
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {

            String receivedInput = new String(delivery.getBody(), StandardCharsets.UTF_8);

            try {

                JSONObject jobStartInfo = new JSONObject(receivedInput);
                int customerId = jobStartInfo.getInt("customerId");
                JSONArray stations = (JSONArray) jobStartInfo.get("stations");
                int numOfStations = stations.length();

                System.out.println(" [x] Received CustomerId: " + customerId + ", number of stations: " + numOfStations);

                stations.forEach(item -> {
                    JSONObject station = (JSONObject) item;
                    int stationId = station.getInt("stationId");
                    System.out.println("StationId: " + stationId);
                });

                DataCollectionJob newJob = new DataCollectionJob(customerId, stations);
                jobs.add(newJob);
            } catch (Exception e) {
                System.out.print(e.getMessage());
            }
        };

        RabbitMQ_Receiver.receiveJobStartInfo(deliverCallback);

    }

    private static void receiveStationData(ArrayList<DataCollectionJob> jobs) throws IOException, TimeoutException {

        // get message from RabbitMQ to read station charging data
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String receivedInput = new String(delivery.getBody(), StandardCharsets.UTF_8);

            try {
                // Wait until jobs are created
                while (jobs.isEmpty()) {
                    TimeUnit.SECONDS.sleep(1);
                }

                JSONObject stationChargingData = new JSONObject(receivedInput);
                int customerId = stationChargingData.getInt("customerId");
                int stationId = stationChargingData.getInt("stationId");
                double chargedEnergy = stationChargingData.getDouble("chargedAmountkWh");
                System.out.println(" [x] Received CustomerId: " + customerId + ", stationId: " + stationId + ". charged energy: " + chargedEnergy);

                ArrayList<DataCollectionJob> doneJobs = new ArrayList<>();

                for (DataCollectionJob job : jobs) {
                    if (job.getCustomerId() == customerId && job.getStationIds().contains(stationId)) {
                        job.addStationChargingData(stationChargingData);
                        job.increaseMessageCount();
                        if (job.getReceivedMessageCount() == job.getNumOfStations()) {
                            sendJobData(job.getCustomerId(), job.getStationChargingData());
                            doneJobs.add(job);
                        }
                    }
                }

                jobs.removeAll(doneJobs);

            } catch (Exception e) {
                System.out.print(e.getMessage());
            }
        };

        RabbitMQ_Receiver.receiveStationChargingData(10000, deliverCallback);

    }

    private static void sendJobData(int customerId, JSONArray stationChargingData) {

        JSONObject collectedData = new JSONObject();

        collectedData.put("CustomerId", customerId);
        collectedData.put("StationChargingData", stationChargingData);

        RabbitMQ_Sender.sendCollectedData(collectedData);

        System.out.println("Sent charging data to RabbitMQ - job for CustomerId: " + customerId);

    }

}