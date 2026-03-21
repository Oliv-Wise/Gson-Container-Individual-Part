package com.google.gson.internal.bind;

import static org.junit.jupiter.api.Assertions.*;


import com.google.gson.stream.JsonReader;
import java.io.IOException;
import java.io.StringReader;
import org.junit.jupiter.api.Test;

class DoubleStrategyTest {

    private final DoubleStrategy strategy = new DoubleStrategy();

    @Test
    void read_shouldReturnDoubleValue() throws IOException {
        JsonReader reader = new JsonReader(new StringReader("12.5"));

        Double result = strategy.read(reader);

        assertEquals(12.5, result, 0.000001);
    }

    @Test
    void toDouble_shouldReturnDoubleValueOfNumber() {
        Number value = 42.75f;

        double result = strategy.toDouble(value);

        assertEquals(42.75, result, 0.000001);
    }

    @Test
    void toOutputNumber_shouldReturnDouble() {
        Number originalValue = 3.14f;
        double numericValue = 8.25;

        Number result = strategy.toOutputNumber(originalValue, numericValue);

        assertInstanceOf(Double.class, result);
        assertEquals(8.25, result.doubleValue(), 0.000001);
    }
}