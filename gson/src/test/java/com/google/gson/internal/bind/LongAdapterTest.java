package com.google.gson.internal.bind;
 
import static com.google.common.truth.Truth.assertThat;
 
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import org.junit.Test;
 
public final class LongAdapterTest {
 
  private final LongAdapter adapter = new LongAdapter();
 
  @Test
  public void testReadLong() throws IOException {
    JsonReader reader = new JsonReader(new StringReader("123456789"));
 
    Number result = adapter.read(reader);
 
    assertThat(result).isInstanceOf(Long.class);
    assertThat(result.longValue()).isEqualTo(123456789L);
  }
 
  @Test
  public void testWriteLongUsesLongValue() throws IOException {
    StringWriter stringWriter = new StringWriter();
    JsonWriter writer = new JsonWriter(stringWriter);
 
    adapter.write(writer, Double.valueOf(1.5));
    writer.close();
 
    assertThat(stringWriter.toString()).isEqualTo("1");
  }
 
  @Test
  public void testWriteNullLong() throws IOException {
    StringWriter stringWriter = new StringWriter();
    JsonWriter writer = new JsonWriter(stringWriter);
 
    adapter.write(writer, null);
    writer.close();
 
    assertThat(stringWriter.toString()).isEqualTo("null");
  }
}