package com.google.gson.internal.bind;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.google.gson.stream.JsonReader;
import java.io.IOException;
import java.io.StringReader;
import org.junit.jupiter.api.Test;

class FloatStrategyTest {

    private final FloatStrategy strategy = new FloatStrategy();

    @Test
    void read_shouldReturnFloatValueWrappedAsNumber() throws IOException {
        JsonReader reader = new JsonReader(new StringReader("12.5"));

        Number result = strategy.read(reader);
        assertInstanceOf(Float.class, result);
        assertEquals(12.5f, result.floatValue(), 0.000001f);
    }

    @Test
    void toDouble_shouldReturnDoubleValueOfNumber() {
        Number value = 42.75f;

        double result = strategy.toDouble(value);

        assertEquals(42.75, result, 0.000001);
    }

    @Test
    void toOutputNumber_shouldReturnOriginalValueWhenItIsAlreadyFloat() {
        Number originalValue = 6.5f;
        double numericValue = 9.75;

        Number result = strategy.toOutputNumber(originalValue, numericValue);

        assertSame(originalValue, result);
    }

    @Test
    void toOutputNumber_shouldReturnFloatWhenOriginalValueIsNotFloat() {
        Number originalValue = 6.5;
        double numericValue = 9.75;

        Number result = strategy.toOutputNumber(originalValue, numericValue);

        assertInstanceOf(Float.class, result);
        assertEquals(9.75f, result.floatValue(), 0.000001f);
    }
}