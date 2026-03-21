package com.google.gson.internal.bind;
 
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import java.io.IOException;
import com.google.gson.stream.JsonWriter;
 
public class ByteAdapter extends IntegralNumberAdapter {
 private static final long maxValue = 255;
  @Override
  protected long readRawValue(JsonReader in) throws IOException {
    return in.nextInt();
  }
 
  @Override
  protected void validate(long value, JsonReader in) {
    // Allow up to 255 to support unsigned values
    if (value > maxValue || value < Byte.MIN_VALUE) {
      throw new JsonSyntaxException(
          "Lossy conversion from " + value + " to byte; at path " + in.getPreviousPath());
    }
  }
 
  @Override
  protected Number convert(long value) {
    return (byte) value;
  }
      @Override
    public final void write(JsonWriter out, Number value) throws IOException {
        if (value == null) {
        out.nullValue();
        } else {
        out.value(value.byteValue());
        }
    }
}