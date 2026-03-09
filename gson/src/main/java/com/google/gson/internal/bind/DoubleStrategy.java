package com.google.gson.internal.bind;

import com.google.gson.stream.JsonReader;
import java.io.IOException;

final class DoubleStrategy implements FloatingPointStrategy {

  @Override
  public Double read(JsonReader in) throws IOException {
    return in.nextDouble();
  }

  @Override
  public double toDouble(Number value) {
    return value.doubleValue();
  }

  @Override
  public Number toOutputNumber(Number originalValue, double numericValue) {
    return numericValue;
  }
}