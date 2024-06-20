package org.example.model.dto;

import java.util.List;

public record JobStarterInfoOutput(
        Integer customerId,
        List<StationInfo> stations
) {
}
