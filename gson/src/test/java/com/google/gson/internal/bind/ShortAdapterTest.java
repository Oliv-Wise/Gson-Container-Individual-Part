package com.google.gson.internal.bind;
import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import org.junit.Test;

public final class ShortAdapterTest {
  private final ShortAdapter adapter = new ShortAdapter();
  @Test
  public void testReadShort() throws IOException {
    JsonReader reader = new JsonReader(new StringReader("1234"));
    Number result = adapter.read(reader);
    assertThat(result).isInstanceOf(Short.class);
    assertThat(result.shortValue()).isEqualTo((short) 1234);
  }
  @Test
  public void testReadShortOutOfRangeThrows() {
    JsonReader reader = new JsonReader(new StringReader("70000"));
    JsonSyntaxException e = assertThrows(JsonSyntaxException.class, () -> adapter.read(reader));
    assertThat(e).hasMessageThat().contains("Lossy conversion");
  }
  @Test
  public void testWriteShort() throws IOException {
    StringWriter stringWriter = new StringWriter();
    JsonWriter writer = new JsonWriter(stringWriter);
    adapter.write(writer, Integer.valueOf(-32768));
    writer.close();
    assertThat(stringWriter.toString()).isEqualTo("-32768");
  }
  @Test
  public void testWriteNullShort() throws IOException {
    StringWriter stringWriter = new StringWriter();
    JsonWriter writer = new JsonWriter(stringWriter);
    adapter.write(writer, null);
    writer.close();
    assertThat(stringWriter.toString()).isEqualTo("null");
  }
}