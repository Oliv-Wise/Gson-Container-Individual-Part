package com.google.gson.internal.bind;
 
import com.google.gson.stream.JsonReader;
import java.io.IOException;
import com.google.gson.stream.JsonWriter;
 
public class LongAdapter extends IntegralNumberAdapter {
 
  @Override
  protected long readRawValue(JsonReader in) throws IOException {
    return in.nextLong();
  }
 
  @Override
  protected Number convert(long value) {
    return value;
  }

    @Override
    public final void write(JsonWriter out, Number value) throws IOException {
        if (value == null) {
        out.nullValue();
        } else {
        out.value(value.longValue());
        }
    }

}