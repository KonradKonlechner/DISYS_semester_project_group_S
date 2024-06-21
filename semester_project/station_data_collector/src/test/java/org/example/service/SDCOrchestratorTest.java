package org.example.service;

import org.example.messages.Sender;
import org.example.model.dto.ChargingDataOutput;
import org.example.model.dto.DataCollectionInput;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SDCOrchestratorTest {

    @Mock
    private ChargeService chargeService;
    @Mock
    private Sender sender;

    @InjectMocks
    private SDCOrchestrator orchestrator;

    @Captor
    private final ArgumentCaptor<ChargingDataOutput> dataCollectionInputArgumentCaptor =
            ArgumentCaptor.forClass(ChargingDataOutput.class);

    @Test
    void shouldCallServiceAndSender() throws IOException, TimeoutException {
        final DataCollectionInput input = new DataCollectionInput(
                123,
                321,
                "URL"
        );

        orchestrator.orchestrateStationDataCollection(input);

        verify(chargeService).getSumOfChargesFor(123, "URL", 321);
        verify(sender).send(any());
    }

    @Test
    void shouldReturnCorrectlyMappedObject() throws IOException, TimeoutException {
        final DataCollectionInput input = new DataCollectionInput(
                123,
                321,
                "URL"
        );
        final ChargingDataOutput expected = new ChargingDataOutput(
                123,
                321,
                420.69
        );

        when(chargeService.getSumOfChargesFor(any(), any(), any())).thenReturn(420.69);

        orchestrator.orchestrateStationDataCollection(input);

        verify(sender).send(dataCollectionInputArgumentCaptor.capture());

        assertEquals(expected, dataCollectionInputArgumentCaptor.getValue());
    }

}