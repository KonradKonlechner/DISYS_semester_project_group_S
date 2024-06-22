package org.pdf_generator;

import java.io.IOException;

import java.util.concurrent.TimeoutException;

public class PdfGenerator {

    public static void main(String[] args) throws IOException, TimeoutException {

       RabbitMQ_Receiver.listen();

    }

}