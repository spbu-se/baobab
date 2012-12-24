package ru.spbu.math.baobab.server.sql;

import java.util.Collection;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;

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
    expectSql("SELECT Calendar WHERE uid").withParameters(1, "3").withResult(row("uid", "3"));
    assertTrue(calendarExtent.find("3").equals(calendar));
  }
  
  public void testGetAll() {
    CalendarExtent calendarExtent = new CalendarExtentSqlImpl();
    expectSql("SELECT FROM Calendar").withResult(row("uid", "cal1"), row("uid", "cal2"));
    Collection<Calendar> calendars = calendarExtent.getAll();
    assertEquals(Sets.newHashSet("cal1", "cal2"), Sets.newHashSet(Collections2.transform(calendars, new Function<Calendar, String>() {
      @Override
      public String apply(Calendar c) {
        return c.getID();
      }
    })));
  }
}
