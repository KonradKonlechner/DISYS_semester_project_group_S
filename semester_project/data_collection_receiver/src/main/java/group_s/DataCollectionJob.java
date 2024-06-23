package group_s;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class DataCollectionJob {

    private final int customerId;

    private final ArrayList<Integer> stationIds;

    private final int numOfStations;

    private int receivedMessageCount = 0;

    private final JSONArray stationChargingData = new JSONArray();

    public DataCollectionJob(int customerId, JSONArray stations) {
        this.customerId = customerId;
        this.stationIds = new ArrayList<>();
        stations.forEach(item -> {
            JSONObject station = (JSONObject) item;
            int stationId = station.getInt("stationId");
            this.stationIds.add(stationId);
        });
        this.numOfStations = stations.length();
    }

    public int getCustomerId() {
        return customerId;
    }

    public ArrayList<Integer> getStationIds() {
        return stationIds;
    }

    public int getNumOfStations() {
        return numOfStations;
    }

    public int getReceivedMessageCount() {
        return receivedMessageCount;
    }

    public JSONArray getStationChargingData() {
        return stationChargingData;
    }

    public void addStationChargingData(JSONObject newStationChargingData) {
        this.stationChargingData.put(newStationChargingData);
    }

    public void increaseMessageCount() {
        this.receivedMessageCount++;
    }
}
