package org.example.model.dto;

import java.io.Serializable;

public record DataCollectionOutput(
        Integer customerId,
        Integer stationId
) implements Serializable {}
