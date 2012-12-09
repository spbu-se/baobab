package ru.spbu.math.baobab.server;

import ru.spbu.math.baobab.model.Calendar;
import ru.spbu.math.baobab.model.CalendarExtent;
import ru.spbu.math.baobab.server.sql.SqlTestCase;

/**
 * Tests for CalendarExtentImpl
 * 
 * @author vloginova
 */
public class CalendarExtentImplTest extends SqlTestCase {
  public void testCreate() {
    CalendarExtent calendarExtent = new CalendarExtentImpl();
    Calendar calendar = calendarExtent.create("1");
    assertEquals(calendar.getID(), "1");

    try {
      calendarExtent.create("1");
      fail("Expected RuntimeException");
    } catch (RuntimeException e) {
      // can't create with existing name
    }
  }

  public void testFind() {
    CalendarExtent calendarExtent = new CalendarExtentImpl();
    Calendar calendar = new CalendarImpl("3");
    // test find method when auditorium with the given id doen't exist
    assertNull(calendarExtent.find("2"));
    // test find method when auditorium with the given id exists
    calendarExtent.create("3");
    assertTrue(calendarExtent.find("3").equals(calendar));
  }
}
