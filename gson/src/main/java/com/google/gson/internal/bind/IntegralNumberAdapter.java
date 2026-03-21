package com.google.gson.internal.bind;
 
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
 
/**
 * Template Method for integral number adapters.
 *
 * Defines the common algorithm:
 * 
 * Handle null</li>
 * Read a raw numeric value
 * Validate it if needed
 * Convert it to the final Number subtype
 *
 */
    public abstract class IntegralNumberAdapter extends TypeAdapter<Number> {
 
    @Override
    public final Number read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
        in.nextNull();
        return null;
        }
    
        try {
        long value = readRawValue(in);
        validate(value, in);
        return convert(value);
        } catch (NumberFormatException e) {
        throw new JsonSyntaxException(e);
        }
    }
    

    
    /**
     * Reads the raw integral value from the reader.
     */
    protected abstract long readRawValue(JsonReader in) throws IOException;
    
    /**
     * Validates the raw value before conversion.
     * Default implementation does nothing.
     */
    protected void validate(long value, JsonReader in) throws IOException {
    }
    
    /**
     * Converts the raw value to the final Number subtype.
     */
    protected abstract Number convert(long value);
}