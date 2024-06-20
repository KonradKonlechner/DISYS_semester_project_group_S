package org.example.model.dto;

import java.io.Serializable;

public record StationInfo(
        Integer stationId,
        String longitude,
        String latitude
) implements Serializable {
}
