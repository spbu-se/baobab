package ru.spbu.math.baobab.model.impl;

import ru.spbu.math.baobab.lang.Parser;
import ru.spbu.math.baobab.model.EvenOddWeek;

/**
 * Converts odd/even lexeme on Baobab language to convenient type. Empty string converts to EvenOddWeek.ALL
 * 
 * @author vloginova
 */
public class OddEvenWeekConverter {
  /**
   * converts string to EvenOddWeek
   * 
   * @param value string to convert
   * @return converted EvenOddWeek
   */
  public static EvenOddWeek convertToOddEvenWeek(String value) {
    if (value.equals("")) {
      return EvenOddWeek.ALL;
    }
    if (Parser.ODD.contains(value)) {
      return EvenOddWeek.ODD;
    }
    if (Parser.EVEN.contains(value)) {
      return EvenOddWeek.EVEN;
    }
    throw new IllegalArgumentException("convertToOddEvenWeek: Incorrect value");
  }
}
