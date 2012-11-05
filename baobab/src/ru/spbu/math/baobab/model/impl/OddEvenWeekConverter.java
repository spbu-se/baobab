package ru.spbu.math.baobab.model.impl;

import java.util.Arrays;
import ru.spbu.math.baobab.model.EvenOddWeek;

/**
 * Converts odd/even lexeme on Baobab language to convenient type
 * 
 * @author vloginova
 */

public class OddEvenWeekConverter {
  private static final String[] ODD = { "odd", "нечетный" };
  private static final String[] EVEN = { "even", "четный" };

  /**
   * converts string to EvenOddWeek
   * 
   * @param value
   *          string to convert
   * @return converted EvenOddWeek
   */
  public static EvenOddWeek convertToOddEvenWeek(String value) throws Exception {
    if (Arrays.asList(ODD).contains(value)) {
      return EvenOddWeek.ODD;
    }
    if (Arrays.asList(EVEN).contains(value)) {
      return EvenOddWeek.EVEN;
    }
    throw new Exception("convertToOddEvenWeek: Incorrect value");
  }
}
