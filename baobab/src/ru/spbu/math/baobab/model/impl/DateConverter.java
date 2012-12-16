package ru.spbu.math.baobab.model.impl;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateConverter {
  private final static Pattern DATE_PATTERN = Pattern.compile("(\\d{4})-(\\d{2})-(\\d{2})");

  /**
   * converts string to Date
   * 
   * @param value string to convert
   * @return converted Date
   */
  public static Date convertToDate(String value) {
    Matcher commandMatch = DATE_PATTERN.matcher(value);
    if (commandMatch.matches()) {
      GregorianCalendar calendar = new GregorianCalendar(Integer.parseInt(commandMatch.group(1)),
          Integer.parseInt(commandMatch.group(2)) - 1, Integer.parseInt(commandMatch.group(3)));
      return calendar.getTime();
    }
    throw new IllegalArgumentException("convertToTimeInstant: Incorrect value");
  }
}
