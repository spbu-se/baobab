package ru.spbu.math.baobab.model.impl;

import ru.spbu.math.baobab.model.TimeInstant;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Converts time lexeme on Baobab language to convenient type
 * 
 * @author vloginova
 */
public class TimeInstantConverter {

  private final static Pattern TIME_PATTERN = Pattern.compile("(\\d{1,2}):(\\d{2})");

  /**
   * converts string to TimeInstant
   * 
   * @param value
   *          string to convert
   * @return converted TimeInstant
   */
  public static TimeInstant convertToTimeInstant(String value) {
    Matcher commandMatch = TIME_PATTERN.matcher(value);
    if (commandMatch.matches()) {
      int hour = Integer.parseInt(commandMatch.group(1));
      int minute = Integer.parseInt(commandMatch.group(2));
      return new TimeInstant(hour, minute);
    }
    throw new IllegalArgumentException("convertToTimeInstant: Incorrect value");
  }
}
