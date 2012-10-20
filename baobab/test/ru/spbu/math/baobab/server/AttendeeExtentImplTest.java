package ru.spbu.math.baobab.server;

import ru.spbu.math.baobab.model.Attendee;
import ru.spbu.math.baobab.model.Attendee.Type;
import ru.spbu.math.baobab.model.AttendeeExtent;
import junit.framework.TestCase;

/**
 * Some tests for AttendeeExtentImpl
 * 
 * @author agudulin
 */
public class AttendeeExtentImplTest extends TestCase {

  public void testCreate() {
    AttendeeExtent attendeeExtent = new AttendeeExtentImpl();
    
    attendeeExtent.create("123", "Dmitry", Type.TEACHER);
    try {
      attendeeExtent.create("123", "Anton", Type.TEACHER);
      fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      // OK: can't add another attendee with existing id
    }
  }

  public void testFind() {
    AttendeeExtent attendeeExtent = new AttendeeExtentImpl();
    attendeeExtent.create("1", "Test", Type.TEACHER);
    Attendee teacher = attendeeExtent.create("2", "Test", Type.TEACHER);
    attendeeExtent.create("3", "Test", Type.STUDENT);
    
    assertNull(attendeeExtent.find("123"));
    assertEquals(attendeeExtent.find("2"), teacher);
  }

}
