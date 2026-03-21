package com.google.gson.internal.bind;
 
import com.google.gson.stream.JsonReader;
import java.io.IOException;
import com.google.gson.stream.JsonWriter;
 
public class IntegerAdapter extends IntegralNumberAdapter {
 
  @Override
  protected long readRawValue(JsonReader in) throws IOException {
    return in.nextInt();
  }
 
  @Override
  protected Number convert(long value) {
    return (int) value;
  }

        @Override
    public final void write(JsonWriter out, Number value) throws IOException {
        if (value == null) {
        out.nullValue();
        } else {
        out.value(value.intValue());
        }
    }

}