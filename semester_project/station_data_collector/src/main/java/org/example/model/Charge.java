package org.example.model;

public record Charge(
        Integer chargeId,
        Integer userId,
        String amountInKWh
) {
}
