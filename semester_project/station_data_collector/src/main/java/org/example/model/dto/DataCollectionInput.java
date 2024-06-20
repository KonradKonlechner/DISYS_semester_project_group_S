package org.example.model.dto;

import java.io.Serializable;

public record DataCollectionInput(
        Integer customerId,
        Integer stationId
) implements Serializable {}
