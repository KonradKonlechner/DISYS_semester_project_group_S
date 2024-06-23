package group_s;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.json.JSONObject;

public class RabbitMQ_Sender {

    private final ConnectionFactory factory;

    private final static String COLLECTED_DATA_QUEUE_NAME = "collected_charging_data";

    public RabbitMQ_Sender() {
        this.factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(30003);
    }

    public void sendCollectedData(JSONObject data) {

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.queueDeclare(COLLECTED_DATA_QUEUE_NAME, false, false, false, null);
            channel.basicPublish("", COLLECTED_DATA_QUEUE_NAME, null, data.toString().getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
