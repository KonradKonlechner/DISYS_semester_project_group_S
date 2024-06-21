package org.example.service;

import org.example.messages.Sender;
import org.example.model.Station;
import org.example.model.dto.DataCollectionOutput;
import org.example.model.dto.JobStarterInfoOutput;
import org.example.model.dto.StationInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DCDOrchestratorTest {

    @Mock
    private Sender sender;
    @Mock
    private StationService stationService;

    @InjectMocks
    private DCDOrchestrator orchestrator;

    @Test
    void shouldCallServiceAndSenderCorrectlyWhenNoStationExists() throws IOException, TimeoutException {

        when(stationService.getAllStations()).thenReturn(List.of());

        orchestrator.orchestrateDataCollectionDispatch(123);

        verify(stationService, times(1)).getAllStations();
        verify(sender, times(1)).send((JobStarterInfoOutput) any());
        verify(sender, times(0)).send((DataCollectionOutput) any());
    }

    @Test
    void shouldCallServiceAndSenderCorrectlyWhenOneStationExists() throws IOException, TimeoutException {

        when(stationService.getAllStations()).thenReturn(
                List.of(
                        createStation(1)
                )
        );

        orchestrator.orchestrateDataCollectionDispatch(123);

        verify(stationService, times(1)).getAllStations();
        verify(sender, times(1)).send((JobStarterInfoOutput) any());
        verify(sender, times(1)).send((DataCollectionOutput) any());
    }

    private Station createStation(int id) {
        return new Station(
                id,
                "URL" + id,
                "123.321",
                "321.123"
        );
    }

    @Test
    void shouldMapJobStarterInfoOutputCorrectly() throws IOException, TimeoutException {
        final ArgumentCaptor<JobStarterInfoOutput> captor = ArgumentCaptor.forClass(JobStarterInfoOutput.class);

        final JobStarterInfoOutput expected = new JobStarterInfoOutput(
                123,
                List.of(
                        new StationInfo(
                                1,
                                "123.321",
                                "321.123"
                        )
                )
        );

        when(stationService.getAllStations()).thenReturn(
                List.of(
                        createStation(1)
                )
        );

        orchestrator.orchestrateDataCollectionDispatch(123);

        verify(sender).send(captor.capture());

        assertEquals(expected, captor.getValue());
    }

    @Test
    void shouldMapDataCollectionOutputCorrectly() throws IOException, TimeoutException {
        final ArgumentCaptor<DataCollectionOutput> captor = ArgumentCaptor.forClass(DataCollectionOutput.class);

        final List<DataCollectionOutput> expected = List.of(
                new DataCollectionOutput(
                        123,
                        1,
                        "URL1"
                ),
                new DataCollectionOutput(
                        123,
                        2,
                        "URL2"
                )
        );

        when(stationService.getAllStations()).thenReturn(
                List.of(
                        createStation(1),
                        createStation(2)
                )
        );

        orchestrator.orchestrateDataCollectionDispatch(123);

        verify(sender, times(2)).send(captor.capture());

        assertEquals(expected, captor.getAllValues());
    }
}