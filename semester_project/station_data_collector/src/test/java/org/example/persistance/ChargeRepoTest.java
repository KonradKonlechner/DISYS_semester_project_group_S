package org.example.persistance;

import org.example.model.Charge;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class ChargeRepoTest {

    private final ChargeRepo repo = new ChargeRepo();

    // this is not how any unit test should be done, but the way we implemented the repo, no other test would make
    // more sense anyway
    // normally, with a more standardised tech stack, we would use entity managers to load entities in a database that
    // is created for the test

    @Test
    void shouldReturnEmptyWhenNoConnectionCouldBeBuild() {
        List<Charge> result = repo.findAll(123, "url");
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnNoneEmptyWhenValuesAreAvailable() {
        List<Charge> result = repo.findAll(1, "localhost:30011");
        assertFalse(result.isEmpty());
    }

    @Test
    void shouldReturnNoneEmptyWhenNoValuesAreAvailable() {
        List<Charge> result = repo.findAll(Integer.MIN_VALUE, "localhost:30011");
        assertTrue(result.isEmpty());
    }

}