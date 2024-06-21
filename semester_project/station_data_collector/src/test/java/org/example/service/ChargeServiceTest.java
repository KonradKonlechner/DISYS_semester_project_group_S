package org.example.service;

import org.example.model.Charge;
import org.example.persistance.ChargeRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChargeServiceTest {

    @Mock
    private ChargeRepo repo;

    @InjectMocks
    private ChargeService service;

    @Test
    void shouldReturnZeroWhenNoChargesWhereFound() {
        when(repo.findAll(any(), any()))
                .thenReturn(List.of());

        final double result = service.getSumOfChargesFor(123, "URL", 321);

        assertEquals(0, result);
    }

    @Test
    void shouldReturnCorrectSumOfChargedAmount() {
        when(repo.findAll(any(), any()))
                .thenReturn(
                        List.of(
                                createCharge("123"),
                                createCharge("321"),
                                createCharge("0.000000000000000000000000000000000000000000000001"),
                                createCharge("27.18")
                        )
                );

        final double expected = 471.180000000000000000000000000000000000000000000001;

        final double result = service.getSumOfChargesFor(123, "URL", 321);

        assertEquals(expected, result);
    }

    private Charge createCharge(String amount) {
        return new Charge(
                1,
                1,
                amount
        );
    }

    @Test
    void shouldHandleIncorrectValueFormatsLikeZeros() {
        when(repo.findAll(any(), any()))
                .thenReturn(
                        List.of(
                                createCharge("123"),
                                createCharge("321"),
                                createCharge("27.18"),
                                createCharge("abc"),
                                createCharge("A.18"),
                                createCharge("21.18kWh")
                        )
                );

        final double expected = 471.18;

        final double result = service.getSumOfChargesFor(123, "URL", 321);

        assertEquals(expected, result);
    }

}