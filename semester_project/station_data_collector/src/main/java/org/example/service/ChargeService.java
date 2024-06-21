package org.example.service;

import org.example.model.Charge;
import org.example.persistance.ChargeRepo;

import java.util.List;

public class ChargeService {

    private final static ChargeRepo chargeRepo = new ChargeRepo();

    public double getSumOfChargesFor(
            Integer customerId,
            String stationDbConnector,
            Integer stationId
    ) {

        final List<Charge> charges = chargeRepo.findAll(customerId, stationDbConnector);

        final double sum = getSumOfCharges(charges);

        System.out.println("[INFO] Calculated sum of charges for station: " + stationId
                + " customer: " + customerId
                + " => " + sum);

        return sum;
    }

    private static double getSumOfCharges(List<Charge> charges) {
        return charges.stream()
                .map(Charge::amountInKWh)
                .mapToDouble(amount -> {
                    try {
                        return Double.parseDouble(amount);
                    } catch (NumberFormatException e) {
                        return 0;
                    }
                })
                .sum();
    }
}
