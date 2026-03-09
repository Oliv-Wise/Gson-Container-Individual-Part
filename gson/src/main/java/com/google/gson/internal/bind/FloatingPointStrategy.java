package com.google.gson.internal.bind;

import com.google.gson.stream.JsonReader;
import java.io.IOException;

public interface FloatingPointStrategy {
  Number read(JsonReader in) throws IOException;

  double toDouble(Number value);

  Number toOutputNumber(Number originalValue, double numericValue);
}