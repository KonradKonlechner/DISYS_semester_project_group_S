package org.example.service;

import org.example.mapper.StationInfoMapper;
import org.example.messages.Sender;
import org.example.model.Station;
import org.example.model.dto.DataCollectionOutput;
import org.example.model.dto.JobStarterInfoOutput;
import org.example.model.dto.StationInfo;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static org.example.mapper.DataCollectionOutputMapper.mapToList;

public class DCDOrchestrator {

    private final Sender sender = new Sender();
    private final StationService stationService = new StationService();

    public void orchestrateDataCollectionDispatch(Integer customerId) throws IOException, TimeoutException {

        List<Station> stations = stationService.getAllStations();
        List<StationInfo> stationInfos = stations.stream().map(StationInfoMapper::map).toList();

        JobStarterInfoOutput jobStarterInfoOutput = new JobStarterInfoOutput(
                customerId,
                stationInfos
        );

        List<DataCollectionOutput> dataCollectionOutputList = mapToList(
                customerId,
                stations
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
