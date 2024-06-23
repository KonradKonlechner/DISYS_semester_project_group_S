package org.example.repositpory;

import org.example.model.Station;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class StationRepositoryTest {

    private final StationRepository repo = new StationRepository();

    // this is not how any unit test should be done, but the way we implemented the repo, no other test would make
    // more sense anyway
    // normally, with a more standardised tech stack, we would use entity managers to load entities in a database that
    // is created for the test
    @Test
    void shouldReturnNoneEmptyWhenValuesAreAvailable() {
        List<Station> result = repo.findAllStations();
        assertFalse(result.isEmpty());
        assertEquals(3, result.size());
    }

}