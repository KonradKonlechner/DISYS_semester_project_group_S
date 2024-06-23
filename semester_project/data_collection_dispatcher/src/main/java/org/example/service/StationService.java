package org.example.service;

import org.example.model.Station;
import org.example.repositpory.StationRepository;

import java.util.List;

public class StationService {

    // This service might seem useless as its only method just calls the repository
    // But it was build with scalability and clean code in scalable applications in mind
    private final StationRepository repository;

    public StationService() {
        this.repository = new StationRepository();
    }

    @SuppressWarnings("unused")
    public StationService(StationRepository repository) {
        this.repository = repository;
    }

    public List<Station> getAllStations() {
        return repository.findAllStations();
    }
}
