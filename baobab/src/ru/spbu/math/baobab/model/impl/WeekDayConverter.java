package ru.spbu.math.baobab.model.impl;

import ru.spbu.math.baobab.lang.Parser;

/**
 * Converts weekday lexeme on Baobab language to convenient type
 * 
 * @author vloginova
 */
public class WeekDayConverter {
  /**
   * converts string to int (weekday in [1..7] range)
   * 
   * @param value string to convert
   * @return converted int
   */
  public static int convertToInt(String value) {
    if (Parser.MONDAY.contains(value)) {
      return 1;
    }
    if (Parser.TUESDAY.contains(value)) {
      return 2;
    }
    if (Parser.WEDNESDAY.contains(value)) {
      return 3;
    }
    if (Parser.THURSDAY.contains(value)) {
      return 4;
    }
    if (Parser.FRIDAY.contains(value)) {
      return 5;
    }
    if (Parser.SATURDAY.contains(value)) {
      return 6;
    }
    if (Parser.SUNDAY.contains(value)) {
      return 7;
    }
    throw new IllegalArgumentException("convertToInt: Incorrect value");
  }
}
