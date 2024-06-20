package org.example.service;

import org.example.mapper.StationInfoMapper;
import org.example.model.Station;
import org.example.model.dto.StationInfo;
import org.example.repositpory.connection.StationRepository;

import java.util.List;

public class StationService {


    private static final StationRepository repository = new StationRepository();

    public List<StationInfo> getAllStationInformation() {
        final List<Station> stations = repository.findAllStations();
        return stations.stream().map(StationInfoMapper::map).toList();
    }
}
