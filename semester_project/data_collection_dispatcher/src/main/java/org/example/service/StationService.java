package org.example.service;

import org.example.model.Station;
import org.example.repositpory.StationRepository;

import java.util.List;

public class StationService {


    private static final StationRepository repository = new StationRepository();

    public List<Station> getAllStations() {
        return repository.findAllStations();
    }
}
