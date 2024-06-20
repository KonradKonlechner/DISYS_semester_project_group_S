package group_s;

import netscape.javascript.JSException;
import org.json.JSONException;
import org.json.JSONObject;

public class Main {
    public static void main(String[] args) throws JSONException {

        JSONObject collectedData = new JSONObject();
        collectedData.put("CustomerId", 2);
        collectedData.put("SumOfChargingEnergy", 90);

        RabbitMQ_Sender.sendCollectedData(collectedData);

        System.out.println("Sent data to RabbitMQ - CustomerId: " + collectedData.getInt("CustomerId") + ", Energy: " + collectedData.getDouble("SumOfChargingEnergy"));

    }
}