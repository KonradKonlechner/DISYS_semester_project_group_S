package org.example.service;

import org.example.messages.Sender;
import org.example.model.Dto.DataCollectionOutput;
import org.example.model.Dto.JobStarterInfoOutput;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static org.example.mapper.DataCollectionOutputMapper.mapToList;

public class DCDOrchestrator {

    private final Sender sender = new Sender();
    private final StationService stationService = new StationService();

    public void orchestrateDataCollectionDispatch(Integer customerId) throws IOException, TimeoutException {

        List<Integer> stationIds = stationService.getStationIdsForCustomerId(customerId);

        JobStarterInfoOutput jobStarterInfoOutput = new JobStarterInfoOutput(
                customerId,
                stationIds
        );

        List<DataCollectionOutput> dataCollectionOutputList = mapToList(
                customerId,
                stationIds
        );

        send(jobStarterInfoOutput, dataCollectionOutputList);
    }

    private void send(
            JobStarterInfoOutput jobStarterInfoOutput,
            List<DataCollectionOutput> dataCollectionOutputList
    ) throws IOException, TimeoutException {

        for (DataCollectionOutput dataCollectionOutput : dataCollectionOutputList) {
            sender.send(dataCollectionOutput);
        }
        sender.send(jobStarterInfoOutput);
    }
}
