package org.example.model.dto;

import java.io.Serializable;

public record ChargingDataOutput(
        Integer customerId,
        Integer stationId,
        Double chargedAmountkWh
) implements Serializable {
}
