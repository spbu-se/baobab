package ru.spbu.math.baobab.model.impl;

import java.util.Arrays;
import java.util.List;

/**
 * Converts weekday lexeme on Baobab language to convenient type
 * 
 * @author vloginova
 */
public class WeekDayConverter {

  private static final List<String> MONDAY = Arrays.asList("Monday", "Mo", "понедельник", "пн");
  private static final List<String> TUESDAY = Arrays.asList("Tuesday", "Tu", "вторник", "вт");
  private static final List<String> WEDNESDAY = Arrays.asList("Wednesday", "We", "среда", "ср");
  private static final List<String> THURSDAY = Arrays.asList("Thursday", "Th", "четверг", "чт");
  private static final List<String> FRIDAY = Arrays.asList("Friday", "Fr", "пятница", "пт");
  private static final List<String> SATURDAY = Arrays.asList("Saturday", "Sa", "суббота", "сб");
  private static final List<String> SUNDAY = Arrays.asList("Sunday", "Su", "воскресенье", "вс");

  /**
   * converts string to int (weekday in [1..7] range)
   * 
   * @param value string to convert
   * @return converted int
   */
  public static int convertToInt(String value) {
    if (MONDAY.contains(value)) {
      return 1;
    }
    if (TUESDAY.contains(value)) {
      return 2;
    }
    if (WEDNESDAY.contains(value)) {
      return 3;
    }
    if (THURSDAY.contains(value)) {
      return 4;
    }
    if (FRIDAY.contains(value)) {
      return 5;
    }
    if (SATURDAY.contains(value)) {
      return 6;
    }
    if (SUNDAY.contains(value)) {
      return 7;
    }
    throw new IllegalArgumentException("convertToInt: Incorrect value");
  }
}
