package org.example.model.dto;

import java.io.Serializable;

public record ChargingDataOutput(
        Integer customerId,
        Integer stationId,
        String chargedAmountkWh // ToDo: is this really a String, not float/double?
) implements Serializable {
}
