package org.pdf_generator;

public class StationChargingRate {

    private int stationId;

    private double chargingRate;

    public StationChargingRate(int stationId, double chargingRate) {
        this.stationId = stationId;
        this.chargingRate = chargingRate;
    }

    public int getStationId() {
        return stationId;
    }

    public double getChargingRate() {
        return chargingRate;
    }
}
