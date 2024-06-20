package org.example.mapper;

import org.example.model.Dto.DataCollectionOutput;

import java.util.List;

public class DataCollectionOutputMapper {

    public static List<DataCollectionOutput> mapToList(
            Integer customerId,
            List<Integer> stationIds
    ) {
        return stationIds.stream()
                .map(stationId -> new DataCollectionOutput(
                        customerId,
                        stationId
                ))
                .toList();
    }
}
