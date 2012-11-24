package ru.spbu.math.baobab.server.sql;

import ru.spbu.math.baobab.model.Attendee.Type;
import ru.spbu.math.baobab.model.AttendeeExtent;
import ru.spbu.math.baobab.model.Attendee;

/**
 * Tests for AttendeeExtentSqlImpl.
 * 
 * @author aoool
 * 
 */
public class AttendeeExtentSqlImplTest extends SqlTestCase {

  public void testCreateNonGroupMembers() {
    AttendeeExtent attendeeExtent = new AttendeeExtentSqlImpl();
    expectSql("SELECT id name type group_id FROM Attendee WHERE uid").withParameters(1, "student");
    expectSql("INSERT INTO Attendee SET uid name type").withParameters(1, "student", 2, "Test1", 3,
        Type.STUDENT.ordinal());
    expectSql("SELECT id FROM Attendee WHERE uid").withParameters(1, "student").withResult(row("id", 1));
    expectSql("INSERT INTO AttendeeGroup SET id").withParameters(1, null);
    expectSql("UPDATE Attendee SET group_id WHERE id").withParameters(1, null, 2, 1);
    Attendee attendee = attendeeExtent.create("student", "Test1", Type.STUDENT);
    assertEquals(attendee.getName(), "Test1");
    assertEquals(attendee.getID(), "student");
    assertEquals(attendee.getType(), Type.STUDENT);
    assertEquals(attendee.isGroup(), false);
    try {
      expectSql("SELECT id name type group_id FROM Attendee WHERE uid").withParameters(1, "student").withResult(
          row("id", 1, "name", "Test1", "type", Type.STUDENT.ordinal(), "group_id", null));
      attendeeExtent.create("student", "Test2", Type.TEACHER);
      fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      // OK: can't add another attendee with existing id
    }
  }

  public void testCreateGroupMembers() {
    AttendeeExtent attendeeExtent = new AttendeeExtentSqlImpl();
    // create group
    expectSql("SELECT id name type group_id FROM Attendee WHERE uid").withParameters(1, "group1");
    expectSql("INSERT INTO Attendee SET uid name type").withParameters(1, "group1", 2, "Test3", 3,
        Type.FREE_FORM_GROUP.ordinal());
    expectSql("SELECT id FROM Attendee WHERE uid").withParameters(1, "group1").withResult(row("id", 3));
    expectSql("INSERT INTO AttendeeGroup SET id").withParameters(1, 3);
    expectSql("UPDATE Attendee SET group_id WHERE id").withParameters(1, 3, 2, 3);
    Attendee group1 = attendeeExtent.create("group1", "Test3", Type.FREE_FORM_GROUP);
    assertEquals(group1.getName(), "Test3");
    assertEquals(group1.getID(), "group1");
    assertEquals(group1.isGroup(), true);
    assertEquals(group1.getType(), Type.FREE_FORM_GROUP);
  }

  public void testFind() {
    AttendeeExtent attendeeExtent = new AttendeeExtentSqlImpl();
    Attendee teacher = new AttendeeSqlImpl(2, "teacher", "Test2", Type.TEACHER, null, attendeeExtent);
    // test find method when attendee with the given id doen't exist
    expectSql("SELECT id name type group_id FROM Attendee WHERE uid").withParameters(1, "blablabla");
    assertNull(attendeeExtent.find("blablabla"));
    // test find method when attendee with the given id exists
    expectSql("SELECT id name type group_id FROM Attendee WHERE uid").withParameters(1, "teacher").withResult(
        row("id", 2, "name", "Test2", "type", Type.TEACHER.ordinal(), "group_id", null));
    assertTrue(attendeeExtent.find("teacher").equals(teacher));
  }
}
