package ru.spbu.math.baobab.model.impl;

import java.util.Arrays;
import java.util.List;

import ru.spbu.math.baobab.model.EvenOddWeek;

/**
 * Converts odd/even lexeme on Baobab language to convenient type
 * 
 * @author vloginova
 */
public class OddEvenWeekConverter {

  private static final List<String> ODD = Arrays.asList("odd", "нечетный");
  private static final List<String> EVEN = Arrays.asList("even", "четный");

  /**
   * converts string to EvenOddWeek
   * 
   * @param value string to convert
   * @return converted EvenOddWeek
   */
  public static EvenOddWeek convertToOddEvenWeek(String value) {
    if (ODD.contains(value)) {
      return EvenOddWeek.ODD;
    }
    if (EVEN.contains(value)) {
      return EvenOddWeek.EVEN;
    }
    throw new IllegalArgumentException("convertToOddEvenWeek: Incorrect value");
  }
}
