package com.fuelstationinvoicesystem;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQSender {

    private final AmqpTemplate amqpTemplate;

    @Value("${rabbitmq.queue}")
    private String queueName;

    // Konstruktor mit @Autowired, um die AmqpTemplate-Instanz zu injizieren
    @Autowired
    public RabbitMQSender(AmqpTemplate amqpTemplate) {
        this.amqpTemplate = amqpTemplate;
    }

    // Methode zum Senden von Nachrichten an die RabbitMQ-Warteschlange
    public void send(String message) {
        amqpTemplate.convertAndSend(queueName, message);
        System.out.println("Send msg = " + message);
    }
}
