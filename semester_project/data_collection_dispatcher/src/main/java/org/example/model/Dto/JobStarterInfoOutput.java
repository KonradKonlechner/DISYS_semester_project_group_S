package org.example.model.Dto;

import java.util.List;

public record JobStarterInfoOutput(
        Integer customerId,
        List<Integer> stationIds
) {
}
