package org.example.service;

import org.example.messages.Sender;
import org.example.model.dto.ChargingDataOutput;
import org.example.model.dto.DataCollectionInput;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class SDCOrchestrator {

    private final Sender sender = new Sender();
    public void orchestrateStationDataCollection(DataCollectionInput input) throws IOException, TimeoutException {
        // ToDo: implement some magic to get the data matching input
        // FixMe: remove mock

        final ChargingDataOutput chargingDataOutput = new ChargingDataOutput(
                input.customerId(),
                input.stationId(),
                "420.69"
        );

        sender.send(chargingDataOutput);
    }
}
