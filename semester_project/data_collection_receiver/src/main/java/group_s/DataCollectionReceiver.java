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

        DCRRepository repository = new DCRRepository();

        repository.receiveJobs(jobs);

        repository.receiveStationData(jobs);

    }

}