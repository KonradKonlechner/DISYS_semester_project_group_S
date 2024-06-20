package org.example.service;

import java.util.ArrayList;
import java.util.List;

public class StationService {

    public List<Integer> getStationIdsForCustomerId(Integer customerId) {
        // ToDo add real implementation and remove mock

        return mock(customerId);
    }

    private static List<Integer> mock(Integer customerId) {
        List<Integer> mockedResultList = new ArrayList<>();

        for(int i = 0; i <= customerId %3; i++) {
            mockedResultList.add(i+1);
        }
        return mockedResultList;
    }
}
