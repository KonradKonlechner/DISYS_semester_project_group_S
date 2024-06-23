package group_s;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RabbitMQ_Receiver {

    private final ConnectionFactory factory;

    private final static String JOB_START_INFO_QUEUE_NAME = "job_started_info";

    private final static String STATION_CHARGING_DATA_QUEUE_NAME = "station_charging_data";

    public RabbitMQ_Receiver() {
        this.factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(30003);
    }

    public void receiveJobStartInfo(DeliverCallback deliverCallback) throws IOException, TimeoutException {

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.queueDeclare(JOB_START_INFO_QUEUE_NAME, false, false, false, null);
        System.out.println(" [*] Waiting for job start info messages.");
        channel.basicConsume(JOB_START_INFO_QUEUE_NAME, true, deliverCallback, consumerTag -> {});
    }

    public void receiveStationChargingData(DeliverCallback deliverCallback) throws IOException, TimeoutException {

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.queueDeclare(STATION_CHARGING_DATA_QUEUE_NAME, false, false, false, null);
        System.out.println(" [*] Waiting for station charging data messages.");
        channel.basicConsume(STATION_CHARGING_DATA_QUEUE_NAME, true, deliverCallback, consumerTag -> {});
    }

}