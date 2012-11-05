package ru.spbu.math.baobab.server.sql;

import ru.spbu.math.baobab.model.Attendee.Type;
import ru.spbu.math.baobab.model.AttendeeExtent;
import ru.spbu.math.baobab.model.Attendee;
import junit.framework.TestCase;

/**
 * Tests for AttendeeExtentSqlImpl.
 * 
 * @author aoool
 * 
 */

public class AttendeeExtentSqlImplTest extends TestCase {

  public void testCreate() {
    AttendeeExtent attendeeExtent = new AttendeeExtentSqlImpl();
    attendeeExtent.create("123", "Test1", Type.TEACHER);
    try {
      attendeeExtent.create("123", "Test2", Type.TEACHER);
      fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      // OK: can't add another attendee with existing id
    }
  }

  public void testFind() {
    AttendeeExtent attendeeExtent = new AttendeeExtentSqlImpl();
    attendeeExtent.create("1", "Test1", Type.TEACHER);
    Attendee teacher = attendeeExtent.create("2", "Test2", Type.TEACHER);
    attendeeExtent.create("3", "Test3", Type.STUDENT);
    assertNull(attendeeExtent.find("123"));
    assertEquals(attendeeExtent.find("2"), teacher);
  }
}
