package ru.spbu.math.baobab.model.impl;

import java.util.Arrays;

/**
 * Converts weekday lexeme on Baobab language to convenient type
 * 
 * @author vloginova
 */

public class WeekdayConverter {

  private static final String[] MONDAY = { "Monday", "Mo", "понедельник", "пн" };
  private static final String[] TUESDAY = { "Tuesday", "Tu", "вторник", "вт" };
  private static final String[] WEDNESDAY = { "Wednesday", "We", "среда", "ср" };
  private static final String[] THURSDAY = { "Thursday", "Th", "четверг", "чт" };
  private static final String[] FRIDAY = { "Friday", "Fr", "пятница", "пт" };
  private static final String[] SATURDAY = { "Saturday", "Sa", "суббота", "сб" };
  private static final String[] SUNDAY = { "Sunday", "Su", "воскресенье", "вс" };

  /**
   * converts string to int (weekday in [1..7] range)
   * 
   * @param value
   *          string to convert
   * @return converted int
   */
  public static int convertToInt(String value) throws Exception {
    if (Arrays.asList(MONDAY).contains(value)) {
      return 1;
    }
    if (Arrays.asList(TUESDAY).contains(value)) {
      return 2;
    }
    if (Arrays.asList(WEDNESDAY).contains(value)) {
      return 3;
    }
    if (Arrays.asList(THURSDAY).contains(value)) {
      return 4;
    }
    if (Arrays.asList(FRIDAY).contains(value)) {
      return 5;
    }
    if (Arrays.asList(SATURDAY).contains(value)) {
      return 6;
    }
    if (Arrays.asList(SUNDAY).contains(value)) {
      return 7;
    }
    throw new Exception("convertToInt: Incorrect value");
  }
}
