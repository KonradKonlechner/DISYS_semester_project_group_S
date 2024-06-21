package org.example.service;

import org.example.messages.Sender;
import org.example.model.dto.ChargingDataOutput;
import org.example.model.dto.DataCollectionInput;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class SDCOrchestrator {

    private final ChargeService chargeService = new ChargeService();
    private final Sender sender = new Sender();
    public void orchestrateStationDataCollection(DataCollectionInput input) throws IOException, TimeoutException {

        final double summedCharges = chargeService.getSumOfChargesFor(input.customerId(), input.databaseUrl(), input.stationId());

        final ChargingDataOutput chargingDataOutput = new ChargingDataOutput(
                input.customerId(),
                input.stationId(),
                summedCharges
        );

        sender.send(chargingDataOutput);
    }
}
