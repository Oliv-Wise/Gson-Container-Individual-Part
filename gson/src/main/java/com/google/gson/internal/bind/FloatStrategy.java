package com.google.gson.internal.bind;

import com.google.gson.stream.JsonReader;
import java.io.IOException;

public final class FloatStrategy implements FloatingPointStrategy {

  @Override
  public Number read(JsonReader in) throws IOException {
    return (float) in.nextDouble();
  }

  @Override
  public double toDouble(Number value) {
    return value.doubleValue();
  }

  @Override
  public Number toOutputNumber(Number originalValue, double numericValue) {
    float floatValue = (float) numericValue;
    return originalValue instanceof Float ? originalValue : floatValue;
  }
}