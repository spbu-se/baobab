package ru.spbu.math.baobab.model.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ru.spbu.math.baobab.lang.Parser;
import ru.spbu.math.baobab.model.EvenOddWeek;
import ru.spbu.math.baobab.model.TimeSlot;
import ru.spbu.math.baobab.model.TimeSlotExtent;

public class TimeSlotConverter {

  private final static Pattern TIMESLOT_KEY_ENG_PATTERN = Pattern.compile(String.format("(%s)\\(s+(%s))?\\s+(%s)",
      Parser.ID_PATTERN, Parser.EVEN_ODD_PATTERN_ENG, Parser.WEEKDAY_PATTERN_ENG));
  private final static Pattern TIMESLOT_KEY_RUS_PATTERN = Pattern.compile(String.format("(%s)\\(s+(%s))?\\s+(%s)",
      Parser.ID_PATTERN, Parser.EVEN_ODD_PATTERN_RUS, Parser.WEEKDAY_PATTERN_RUS));

  public static TimeSlot convertToTimeSlot(String timeSlotKey, TimeSlotExtent extent) {
    TimeSlot timeSlot = getTimeSlot(TIMESLOT_KEY_RUS_PATTERN, timeSlotKey, extent);
    if (timeSlot == null) {
      getTimeSlot(TIMESLOT_KEY_ENG_PATTERN, timeSlotKey, extent);
    }
    return timeSlot;
  }

  public static TimeSlot convertToTimeSlot(String name, Date date, TimeSlotExtent extent) {
    for (TimeSlot slot : extent.getAll()) {
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(date);
      int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
      if (slot.getName().equals(name) && slot.getDayOfWeek() == weekDay
          && slot.getEvenOddWeek().equals(EvenOddWeek.ALL)) {
        return slot;
      }
    }
    throw new IllegalArgumentException("convertToTimeSlot: therre is no such time slot");
  }

  private static TimeSlot getTimeSlot(Pattern pattern, String timeSlotKey, TimeSlotExtent extent) {
    Matcher matcher = pattern.matcher(timeSlotKey);
    String name = matcher.group(1);
    EvenOddWeek flashing = OddEvenWeekConverter.convertToOddEvenWeek(matcher.group(3) == null ? "" : matcher.group(3));
    int weekDay = WeekDayConverter.convertToInt(matcher.group(4));
    for (TimeSlot slot : extent.getAll()) {
      if (slot.getName().equals(name) && slot.getDayOfWeek() == weekDay && slot.getEvenOddWeek().equals(flashing)) {
        return slot;
      }
    }
    throw new IllegalArgumentException("convertToTimeSlot: therre is no such time slot");
  }
}
