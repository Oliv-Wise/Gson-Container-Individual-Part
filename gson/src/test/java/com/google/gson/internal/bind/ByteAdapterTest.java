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
 
public final class ByteAdapterTest {
 
  private final ByteAdapter adapter = new ByteAdapter();
 
  @Test
  public void testReadByte() throws IOException {
    JsonReader reader = new JsonReader(new StringReader("12"));
 
    Number result = adapter.read(reader);
 
    assertThat(result).isInstanceOf(Byte.class);
    assertThat(result.byteValue()).isEqualTo((byte) 12);
  }
 
  @Test
  public void testReadByteOutOfRangeThrows() {
    JsonReader reader = new JsonReader(new StringReader("256"));
 
    JsonSyntaxException e = assertThrows(JsonSyntaxException.class, () -> adapter.read(reader));
 
    assertThat(e).hasMessageThat().contains("Lossy conversion");
  }
 
  @Test
  public void testWriteByte() throws IOException {
    StringWriter stringWriter = new StringWriter();
    JsonWriter writer = new JsonWriter(stringWriter);
 
    adapter.write(writer, Byte.valueOf((byte) -128));
    writer.close();
 
    assertThat(stringWriter.toString()).isEqualTo("-128");
  }
 
  @Test
  public void testWriteNullByte() throws IOException {
    StringWriter stringWriter = new StringWriter();
    JsonWriter writer = new JsonWriter(stringWriter);
 
    adapter.write(writer, null);
    writer.close();
 
    assertThat(stringWriter.toString()).isEqualTo("null");
  }
}