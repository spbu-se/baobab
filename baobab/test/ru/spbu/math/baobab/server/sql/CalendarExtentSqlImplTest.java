package ru.spbu.math.baobab.server.sql;

import ru.spbu.math.baobab.model.Calendar;
import ru.spbu.math.baobab.model.CalendarExtent;

/**
 * Tests for CalendarExtentSqlImpl
 * 
 * @author vloginova
 */
public class CalendarExtentSqlImplTest extends SqlTestCase {
  public void testCreate() {
    CalendarExtent calendarExtent = new CalendarExtentSqlImpl();
    expectSql("SELECT Calendar WHERE uid").withParameters(1, "1");
    expectSql("INSERT Calendar uid").withParameters(1, "1");
    Calendar calendar = calendarExtent.create("1");
    assertEquals(calendar.getID(), "1");

    try {
      expectSql("SELECT Calendar WHERE uid").withParameters(1, "1").withResult(row("uid", "1"));
      calendarExtent.create("1");
      fail("Expected RuntimeException");
    } catch (RuntimeException e) {
      // can't create with existing name
    }
  }

  public void testFind() {
    CalendarExtent calendarExtent = new CalendarExtentSqlImpl();
    Calendar calendar = new CalendarSqlImpl("3");
    // test find method when auditorium with the given id doen't exist
    expectSql("SELECT Calendar WHERE uid").withParameters(1, "2");
    assertNull(calendarExtent.find("2"));
    // test find method when auditorium with the given id exists
    expectSql("SELECT Calendar WHERE uid").withParameters(1, "3").withResult(
        row("uid", "3"));
    assertTrue(calendarExtent.find("3").equals(calendar));
  }
}
