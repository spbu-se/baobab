package ru.spbu.math.baobab.lang;

import java.util.List;

import ru.spbu.math.baobab.model.EvenOddWeek;
import ru.spbu.math.baobab.model.TimeSlot;
import ru.spbu.math.baobab.model.TimeSlotExtent;
import ru.spbu.math.baobab.server.TimeSlotExtentImpl;
import junit.framework.TestCase;

/**
 * Tests for script interpreter
 * 
 * @author vloginova
 */
public class TimeSlotCommandParserTest extends TestCase {
  public void testCommandCorrectnessEng() {
    TimeSlotExtent timeSlotExtent = new TimeSlotExtentImpl();
    TimeSlotCommandParser parser = new TimeSlotCommandParser(timeSlotExtent);
    assertTrue(parser.parse("define timeslot 1st_try 12:12 to 12:40 on even Monday"));
    List<TimeSlot> timeSlots = timeSlotExtent.findByWeekDay(1);
    assertTrue(timeSlots.size() == 1);
    assertTrue(timeSlots.get(0).getName().equals("1st_try"));
    assertTrue(timeSlots.get(0).getDayOfWeek() == 1);
    assertTrue(timeSlots.get(0).getEvenOddWeek() == EvenOddWeek.EVEN);
    assertTrue(timeSlots.get(0).getStart().getHour() == 12);
    assertTrue(timeSlots.get(0).getStart().getMinute() == 12);
    assertTrue(timeSlots.get(0).getFinish().getHour() == 12);
    assertTrue(timeSlots.get(0).getFinish().getMinute() == 40);
  }

  public void testCommandCorrectnessRus() {
    TimeSlotExtent timeSlotExtent = new TimeSlotExtentImpl();
    TimeSlotCommandParser parser = new TimeSlotCommandParser(timeSlotExtent);
    assertTrue(parser.parse("определить интервал 1st_try от 1:00 до 3:00 в нечетный вт"));
    assertTrue(timeSlotExtent.findByWeekDay(2).size() == 1);
  }

  public void testIncorrectCommand() {
    TimeSlotExtent timeSlotExtent = new TimeSlotExtentImpl();
    TimeSlotCommandParser parser = new TimeSlotCommandParser(timeSlotExtent);
    assertFalse(parser.parse("define timeslot 1st_try 12:12 to on even Monday"));
  }
}
