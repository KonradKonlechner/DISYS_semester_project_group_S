package org.example.mapper;

import org.example.model.Station;
import org.example.model.dto.DataCollectionOutput;
import org.example.model.dto.StationInfo;

import java.util.List;

public class DataCollectionOutputMapper {

    public static List<DataCollectionOutput> mapToList(
            Integer customerId,
            List<Station> stationInfos
    ) {
        return stationInfos.stream()
                .map(stationInfo -> new DataCollectionOutput(
                        customerId,
                        stationInfo.stationId(),
                        stationInfo.stationDbUrl()
                ))
                .toList();
    }
}
