/*
 * Copyright (C) 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.gson.typeadapters;

import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

public final class UtcDateTypeAdapter extends TypeAdapter<Date> {

  private static final TimeZone UTC_TIME_ZONE = TimeZone.getTimeZone("UTC");
  private static final int YEAR_LENGTH = 4;
  private static final int DATE_PART_LENGTH = 2;
  private static final int MILLIS_LENGTH = 3;
  private static final int SINGLE_CHAR_LENGTH =1;
  private static final int ZERO_BASED_MONTH_OFFSET = 1;
  private static final int DECIMAL_RADIX = 10;

  private static final class DateParts {
  final int year;
  final int month;
  final int day;
  final int offset;
 
  DateParts(int year, int month, int day, int offset) {
    this.year = year;
    this.month = month;
    this.day = day;
    this.offset = offset;
  }
}
 
private static final class TimeParts {
  final int hour;
  final int minutes;
  final int seconds;
  final int milliseconds;
  final int offset;
 
  TimeParts(int hour, int minutes, int seconds, int milliseconds, int offset) {
    this.hour = hour;
    this.minutes = minutes;
    this.seconds = seconds;
    this.milliseconds = milliseconds;
    this.offset = offset;
  }
}
 
private static final class TimeZoneParts {
  final TimeZone timeZone;
  final int offset;
 
  TimeZoneParts(TimeZone timeZone, int offset) {
    this.timeZone = timeZone;
    this.offset = offset;
  }
}

  @Override
  public void write(JsonWriter out, Date date) throws IOException {
    if (date == null) {
      out.nullValue();
    } else {
      String value = format(date, true, UTC_TIME_ZONE);
      out.value(value);
    }
  }

  @Override
  public Date read(JsonReader in) throws IOException {
    try {
      if (in.peek().equals(JsonToken.NULL)) {
        in.nextNull();
        return null;
      } else {
        String date = in.nextString();
        // Instead of using iso8601Format.parse(value), we use Jackson's date parsing
        // This is because Android doesn't support XXX because it is JDK 1.6
        return parse(date, new ParsePosition(0));
      }
    } catch (ParseException e) {
      throw new JsonParseException(e);
    }
  }

  // Date parsing code from Jackson databind ISO8601Utils.java
  // https://github.com/FasterXML/jackson-databind/blob/2.8/src/main/java/com/fasterxml/jackson/databind/util/ISO8601Utils.java
  private static final String GMT_ID = "GMT";

  /**
   * Format date into yyyy-MM-ddThh:mm:ss[.sss][Z|[+-]hh:mm]
   *
   * @param date the date to format
   * @param millis true to include millis precision otherwise false
   * @param tz timezone to use for the formatting (GMT will produce 'Z')
   * @return the date formatted as yyyy-MM-ddThh:mm:ss[.sss][Z|[+-]hh:mm]
   */
  private static String format(Date date, boolean millis, TimeZone tz) {
    Calendar calendar = new GregorianCalendar(tz, Locale.US);
    calendar.setTime(date);

    // estimate capacity of buffer as close as we can (yeah, that's pedantic ;)
    int capacity = "yyyy-MM-ddThh:mm:ss".length();
    capacity += millis ? ".sss".length() : 0;
    capacity += tz.getRawOffset() == 0 ? "Z".length() : "+hh:mm".length();
    StringBuilder formatted = new StringBuilder(capacity);

    padInt(formatted, calendar.get(Calendar.YEAR), "yyyy".length());
    formatted.append('-');
    padInt(formatted, calendar.get(Calendar.MONTH) + 1, "MM".length());
    formatted.append('-');
    padInt(formatted, calendar.get(Calendar.DAY_OF_MONTH), "dd".length());
    formatted.append('T');
    padInt(formatted, calendar.get(Calendar.HOUR_OF_DAY), "hh".length());
    formatted.append(':');
    padInt(formatted, calendar.get(Calendar.MINUTE), "mm".length());
    formatted.append(':');
    padInt(formatted, calendar.get(Calendar.SECOND), "ss".length());
    if (millis) {
      formatted.append('.');
      padInt(formatted, calendar.get(Calendar.MILLISECOND), "sss".length());
    }

    int offset = tz.getOffset(calendar.getTimeInMillis());
    if (offset != 0) {
      int hours = Math.abs((offset / (60 * 1000)) / 60);
      int minutes = Math.abs((offset / (60 * 1000)) % 60);
      formatted.append(offset < 0 ? '-' : '+');
      padInt(formatted, hours, "hh".length());
      formatted.append(':');
      padInt(formatted, minutes, "mm".length());
    } else {
      formatted.append('Z');
    }

    return formatted.toString();
  }

  /**
   * Zero pad a number to a specified length
   *
   * @param buffer buffer to use for padding
   * @param value the integer value to pad if necessary.
   * @param length the length of the string we should zero pad
   */
  private static void padInt(StringBuilder buffer, int value, int length) {
    String strValue = Integer.toString(value);
    for (int i = length - strValue.length(); i > 0; i--) {
      buffer.append('0');
    }
    buffer.append(strValue);
  }

  /**
   * Parse a date from ISO-8601 formatted string. It expects a format
   * [yyyy-MM-dd|yyyyMMdd][T(hh:mm[:ss[.sss]]|hhmm[ss[.sss]])]?[Z|[+-]hh:mm]]
   *
   * @param date ISO string to parse in the appropriate format.
   * @param pos The position to start parsing from, updated to where parsing stopped.
   * @return the parsed date
   * @throws ParseException if the date is not in the appropriate format
   */
  private static Date parse(String date, ParsePosition pos) throws ParseException {
  Exception fail = null;
  try {
    int initialOffset = pos.getIndex();
 
    DateParts dateParts = parseDateParts(date, initialOffset);
    TimeParts timeParts = parseTimeParts(date, dateParts.offset);
    TimeZoneParts timeZoneParts = parseTimeZone(date, timeParts.offset);
 
    Calendar calendar = buildCalendar(dateParts, timeParts, timeZoneParts.timeZone);
    pos.setIndex(timeZoneParts.offset);
    return calendar.getTime();
    // If we get a ParseException it'll already have the right message/offset.
    // Other exception types can convert here.
  } catch (IndexOutOfBoundsException | IllegalArgumentException e) {
    fail = e;
  }
 
  String input = (date == null) ? null : ("'" + date + "'");
  throw new ParseException(
      "Failed to parse date [" + input + "]: "+ fail.getMessage(), pos.getIndex());
}

private static DateParts parseDateParts(String date, int offset) {
  int year = parseInt(date, offset, offset += YEAR_LENGTH);
  if (checkOffset(date, offset, '-')) {
    offset += SINGLE_CHAR_LENGTH;
  }
 
  int month = parseInt(date, offset, offset += DATE_PART_LENGTH);
  if (checkOffset(date, offset, '-')) {
    offset += SINGLE_CHAR_LENGTH;
  }
 
  int day = parseInt(date, offset, offset += DATE_PART_LENGTH);
  return new DateParts(year, month, day, offset);
}
 
private static TimeParts parseTimeParts(String date, int offset) {
  int hour = 0;
  int minutes = 0;
  int seconds = 0;
  // always use 0 otherwise returned date will include millis of current time
  int milliseconds = 0;
 
  if (checkOffset(date, offset, 'T')) {
    hour = parseInt(date, offset += SINGLE_CHAR_LENGTH, offset += DATE_PART_LENGTH);
    if (checkOffset(date, offset, ':')) {
      offset += SINGLE_CHAR_LENGTH;
    }
 
    minutes = parseInt(date, offset, offset += DATE_PART_LENGTH);
    if (checkOffset(date, offset, ':')) {
      offset += SINGLE_CHAR_LENGTH;
    }
 
    // second and milliseconds can be optional
    if (date.length() > offset) {
      char c = date.charAt(offset);
      if (c != 'Z' && c != '+' && c != '-') {
        seconds = parseInt(date, offset, offset += DATE_PART_LENGTH);
 
        // milliseconds can be optional in the format
        if (checkOffset(date, offset, '.')) {
          milliseconds = parseInt(date, offset += SINGLE_CHAR_LENGTH, offset += MILLIS_LENGTH);
        }
      }
    }
  }
 
  return new TimeParts(hour, minutes, seconds, milliseconds, offset);
}
 
private static TimeZoneParts parseTimeZone(String date, int offset) {
  String timezoneId;
  if (date.length() <= offset) {
    throw new IllegalArgumentException("No time zone indicator");
  }
 
  char timezoneIndicator = date.charAt(offset);
  if (timezoneIndicator == '+' || timezoneIndicator == '-') {
    String timezoneOffset = date.substring(offset);
    timezoneId = GMT_ID + timezoneOffset;
    offset += timezoneOffset.length();
  } else if (timezoneIndicator == 'Z') {
    timezoneId = GMT_ID;
    offset += SINGLE_CHAR_LENGTH;
  } else {
    throw new IndexOutOfBoundsException("Invalid time zone indicator " + timezoneIndicator);
  }
 
  TimeZone timeZone = TimeZone.getTimeZone(timezoneId);
  if (!timeZone.getID().equals(timezoneId)) {
    throw new IndexOutOfBoundsException();
  }
 
  return new TimeZoneParts(timeZone, offset);
}
 
private static Calendar buildCalendar(
    DateParts dateParts, TimeParts timeParts, TimeZone timeZone) {
  Calendar calendar = new GregorianCalendar(timeZone);
  calendar.setLenient(false);
  calendar.set(Calendar.YEAR, dateParts.year);
  calendar.set(Calendar.MONTH, dateParts.month - ZERO_BASED_MONTH_OFFSET);
  calendar.set(Calendar.DAY_OF_MONTH, dateParts.day);
  calendar.set(Calendar.HOUR_OF_DAY, timeParts.hour);
  calendar.set(Calendar.MINUTE, timeParts.minutes);
  calendar.set(Calendar.SECOND, timeParts.seconds);
  calendar.set(Calendar.MILLISECOND, timeParts.milliseconds);
  return calendar;
}

  /**
   * Check if the expected character exist at the given offset in the value.
   *
   * @param value the string to check at the specified offset
   * @param offset the offset to look for the expected character
   * @param expected the expected character
   * @return true if the expected character exist at the given offset
   */
  private static boolean checkOffset(String value, int offset, char expected) {
    return (offset < value.length()) && (value.charAt(offset) == expected);
  }

  /**
   * Parse an integer located between 2 given offsets in a string using same logic as in Integer.parseInt() but less generic we're not supporting negative values
   *
   * @param value the string to parse
   * @param beginIndex the start index for the integer in the string
   * @param endIndex the end index for the integer in the string
   * @return the int
   * @throws NumberFormatException if the value is not a number
   */
  private static int parseInt(String value, int beginIndex, int endIndex)
      throws NumberFormatException {
    if (beginIndex < 0 || endIndex > value.length() || beginIndex > endIndex) {
      throw new NumberFormatException(value);
    }
    
    int i = beginIndex;
    int result = 0;
    int digit;
    if (i < endIndex) {
      digit = Character.digit(value.charAt(i++), DECIMAL_RADIX);
      if (digit < 0) {
        throw new NumberFormatException("Invalid number: " + value);
      }
      result = -digit;
    }
    while (i < endIndex) {
      digit = Character.digit(value.charAt(i++), DECIMAL_RADIX);
      if (digit < 0) {
        throw new NumberFormatException("Invalid number: " + value);
      }
      result *= DECIMAL_RADIX;
      result -= digit;
    }
    return -result;
  }
}
