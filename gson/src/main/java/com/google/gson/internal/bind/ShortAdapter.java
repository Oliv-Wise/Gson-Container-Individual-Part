package com.google.gson.internal.bind;
 
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import java.io.IOException;
import com.google.gson.stream.JsonWriter;
public class ShortAdapter extends IntegralNumberAdapter {
 
  @Override
  protected long readRawValue(JsonReader in) throws IOException {
    return in.nextInt();
  }
 
  private static final long maxValue = 65535;

  @Override
  protected void validate(long value, JsonReader in) {
    if (value > maxValue || value < Short.MIN_VALUE) {
      throw new JsonSyntaxException(
          "Lossy conversion from " + value + " to short; at path " + in.getPreviousPath());
    }
  }
 
  @Override
  protected Number convert(long value) {
    return (short) value;
  }
      @Override
    public final void write(JsonWriter out, Number value) throws IOException {
        if (value == null) {
        out.nullValue();
        } else {
        out.value(value.shortValue());
        }
    }
}