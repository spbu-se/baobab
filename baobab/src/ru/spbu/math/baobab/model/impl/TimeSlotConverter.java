package ru.spbu.math.baobab.model.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ru.spbu.math.baobab.lang.Parser;
import ru.spbu.math.baobab.model.EvenOddWeek;
import ru.spbu.math.baobab.model.TimeSlot;
import ru.spbu.math.baobab.model.TimeSlotExtent;

/**
 * Converts time slot lexeme (name or time slot key) on Baobab language to convenient type
 * 
 * @author vloginova
 */
public class TimeSlotConverter {

  private final static Pattern TIMESLOT_KEY_ENG_PATTERN = Pattern.compile(String.format("(%s)(\\s+(%s))?\\s+(%s)",
      Parser.ID_PATTERN, Parser.EVEN_ODD_PATTERN_ENG, Parser.WEEKDAY_PATTERN_ENG));
  private final static Pattern TIMESLOT_KEY_RUS_PATTERN = Pattern.compile(String.format("(%s)(\\s+(%s))?\\s+(%s)",
      Parser.ID_PATTERN, Parser.EVEN_ODD_PATTERN_RUS, Parser.WEEKDAY_PATTERN_RUS));

  /**
   * Converts time slot key string to time slot. Throws IllegalArgumentException in case of absence time slot in
   * TimeSlotExtent collection
   * 
   * @param timeSlotKey this string contains time slot key
   * @param extent TimeSlotExtent for searching time slot
   * @return found time slot
   */
  public static TimeSlot convertToTimeSlot(String timeSlotKey, TimeSlotExtent extent) {
    TimeSlot timeSlot = getTimeSlot(TIMESLOT_KEY_RUS_PATTERN, timeSlotKey, extent);
    if (timeSlot == null) {
      timeSlot = getTimeSlot(TIMESLOT_KEY_ENG_PATTERN, timeSlotKey, extent);
    }
    if (timeSlot == null) {
      throw new IllegalArgumentException("convertToTimeSlot: there is no such time slot");
    }
    return timeSlot;
  }

  /**
   * Converts time slot name string to time slot using Date. Throws IllegalArgumentException in case of absence time slot in
   * TimeSlotExtent collection
   * 
   * @param name time slot name
   * @param date date for week day determination
   * @param extent TimeSlotExtent for searching time slot
   * @return found time slot
   */
  public static TimeSlot convertToTimeSlot(String name, Date date, TimeSlotExtent extent) {
    for (TimeSlot slot : extent.getAll()) {
      Calendar calendar = (Calendar) Calendar.getInstance().clone();
      calendar.setTime(date);
      int weekDay = calendar.get(Calendar.DAY_OF_WEEK) - 1;
      if (weekDay == 0) {
        weekDay = 7;
      }
      if (slot.getName().equals(name) && slot.getDayOfWeek() == weekDay
          && slot.getEvenOddWeek().equals(EvenOddWeek.ALL)) {
        return slot;
      }
    }
    throw new IllegalArgumentException("convertToTimeSlot: there is no such time slot");
  }

  private static TimeSlot getTimeSlot(Pattern pattern, String timeSlotKey, TimeSlotExtent extent) {
    Matcher matcher = pattern.matcher(timeSlotKey);
    if (matcher.matches()) {
      String name = Parser.unquote(matcher.group(1));
      EvenOddWeek flashing = OddEvenWeekConverter
          .convertToOddEvenWeek(matcher.group(3) == null ? "" : matcher.group(3));
      int weekDay = WeekDayConverter.convertToInt(matcher.group(4));
      for (TimeSlot slot : extent.getAll()) {
        if (slot.getName().equals(name) && slot.getDayOfWeek() == weekDay && slot.getEvenOddWeek().equals(flashing)) {
          return slot;
        }
      }
    }
    return null;
  }
}
