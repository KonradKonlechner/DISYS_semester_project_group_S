package org.example.model;

public record Station(
        Integer stationId,
        String stationDbUrl,
        String longitude,
        String latitude
) {
}
