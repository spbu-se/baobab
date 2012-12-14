package ru.spbu.math.baobab.lang;

import junit.framework.TestCase;

import ru.spbu.math.baobab.model.Auditorium;
import ru.spbu.math.baobab.model.AuditoriumExtent;
import ru.spbu.math.baobab.model.TimeSlotExtent;
import ru.spbu.math.baobab.server.AuditoriumExtentImpl;
import ru.spbu.math.baobab.server.AuditoriumImpl;
import ru.spbu.math.baobab.server.TimeSlotExtentImpl;

/**
 * Tests for AuditoriumCommandParser
 * 
 * @author dageev
 */
public class AuditoriumCommandParserTest extends TestCase {
  public void testCommandCorrectnessEng() {
    AuditoriumExtent auditoriumExtent = new AuditoriumExtentImpl();
    AuditoriumCommandParser parser = new AuditoriumCommandParser(auditoriumExtent);
    assertTrue(parser.parse("define auditorium test_name within 5"));
    Auditorium auditorium = new AuditoriumImpl("test_name", 5);
    assertEquals(auditorium, auditoriumExtent.find("test_name"));
  }

  public void testCommandCorrectnessRus() {
    AuditoriumExtent auditoriumExtent = new AuditoriumExtentImpl();
    AuditoriumCommandParser parser = new AuditoriumCommandParser(auditoriumExtent);
    assertTrue(parser.parse("определить помещение test_name на площадке 5"));
    Auditorium auditorium = new AuditoriumImpl("test_name", 5);
    assertEquals(auditorium, auditoriumExtent.find("test_name"));
  }

  public void testIncorrectCommand() {
    TimeSlotExtent timeSlotExtent = new TimeSlotExtentImpl();
    TimeSlotCommandParser parser = new TimeSlotCommandParser(timeSlotExtent);
    assertFalse(parser.parse("define auditorium test_name within "));
  }
}
