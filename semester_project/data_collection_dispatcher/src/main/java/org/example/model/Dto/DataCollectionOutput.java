package org.example.model.Dto;

import java.io.Serializable;

public record DataCollectionOutput(
        Integer customerId,
        Integer stationId
) implements Serializable {}
