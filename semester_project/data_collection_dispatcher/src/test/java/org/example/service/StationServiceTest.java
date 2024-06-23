package org.example.service;

import org.example.repositpory.StationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class StationServiceTest {

    @Mock
    private StationRepository repo;

    @InjectMocks
    private StationService service;

    @Test
    void shouldCallRepository() {
        service.getAllStations();
        verify(repo).findAllStations();
    }
}