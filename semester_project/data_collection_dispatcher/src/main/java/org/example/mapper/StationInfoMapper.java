package org.example.mapper;

import org.example.model.Station;
import org.example.model.dto.StationInfo;

public class StationInfoMapper {

    public static StationInfo map(Station station) {
        return new StationInfo(
                station.stationId(),
                station.longitude(),
                station.latitude()
        );
    }
}
