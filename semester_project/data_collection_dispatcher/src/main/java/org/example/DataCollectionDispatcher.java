package org.example;

import org.example.messages.Receiver;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class DataCollectionDispatcher {

    static Receiver receiver = new Receiver();

    public static void main(String[] args) throws IOException, TimeoutException {
        receiver.listen();
    }
}