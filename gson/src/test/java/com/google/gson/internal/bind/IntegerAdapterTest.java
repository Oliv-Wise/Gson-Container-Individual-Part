package com.google.gson.internal.bind;
 
import static com.google.common.truth.Truth.assertThat;
 
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import org.junit.Test;
 
public final class IntegerAdapterTest {
 
  private final IntegerAdapter adapter = new IntegerAdapter();
 
  @Test
  public void testReadInteger() throws IOException {
    JsonReader reader = new JsonReader(new StringReader("42"));
 
    Number result = adapter.read(reader);
 
    assertThat(result).isInstanceOf(Integer.class);
    assertThat(result.intValue()).isEqualTo(42);
  }
 
  @Test
  public void testWriteInteger() throws IOException {
    StringWriter stringWriter = new StringWriter();
    JsonWriter writer = new JsonWriter(stringWriter);
 
    adapter.write(writer, Long.valueOf(Integer.MIN_VALUE));
    writer.close();
 
    assertThat(stringWriter.toString()).isEqualTo(String.valueOf(Integer.MIN_VALUE));
  }
 
  @Test
  public void testWriteNullInteger() throws IOException {
    StringWriter stringWriter = new StringWriter();
    JsonWriter writer = new JsonWriter(stringWriter);
 
    adapter.write(writer, null);
    writer.close();
 
    assertThat(stringWriter.toString()).isEqualTo("null");
  }
}