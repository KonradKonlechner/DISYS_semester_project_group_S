package group_s;

import netscape.javascript.JSException;
import org.json.JSONException;
import org.json.JSONObject;

import static java.lang.Integer.valueOf;

public class Main {
    public static void main(String[] args) throws JSONException {

        JSONObject collectedData = new JSONObject();
        collectedData.put("CustomerId", 3);
        collectedData.put("SumOfChargingEnergy", 390.4);

        RabbitMQ_Sender.sendCollectedData(collectedData);

    }
}