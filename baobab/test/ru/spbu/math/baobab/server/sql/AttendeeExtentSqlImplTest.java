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

  public void testCreate() {
    AttendeeExtent attendeeExtent = new AttendeeExtentSqlImpl();
    expectSql("SELECT id name type group_id FROM Attendee WHERE uid").withParameters(1, "student");
    expectSql("INSERT INTO Attendee SET uid name type").withParameters(1, "student", 2, "Test1", 3,
        Type.STUDENT.ordinal());
    expectSql("SELECT id FROM Attendee WHERE uid").withParameters(1, "student").withResult(row("id", 1));
    expectSql("INSERT INTO AttendeeGroup SET id").withParameters(1, null);
    expectSql("UPDATE Attendee SET group_id WHERE id").withParameters(1, null, 2, 1);
    Attendee attendee = attendeeExtent.create("student", "Test1", Type.STUDENT);
    assertEquals(attendee.isGroup(), false);
    try {
      expectSql("SELECT id name type group_id FROM Attendee WHERE uid").withParameters(1, "student").withResult(
          row("id", 1, "name", "Test1", "type", Type.STUDENT.ordinal(), "group_id", 0));
      attendeeExtent.create("student", "Test2", Type.TEACHER);
      fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      // OK: can't add another attendee with existing id
    }
    // create group
    expectSql("SELECT id name type group_id FROM Attendee WHERE uid").withParameters(1, "group1");
    expectSql("INSERT INTO Attendee SET uid name type").withParameters(1, "group1", 2, "Test3", 3,
        Type.FREE_FORM_GROUP.ordinal());
    expectSql("SELECT id FROM Attendee WHERE uid").withParameters(1, "group1").withResult(row("id", 3));
    expectSql("INSERT INTO AttendeeGroup SET id").withParameters(1, 3);
    expectSql("UPDATE Attendee SET group_id WHERE id").withParameters(1, 3, 2, 3);
    Attendee group1 = attendeeExtent.create("group1", "Test3", Type.FREE_FORM_GROUP);
    assertEquals(group1.isGroup(), true);
    // create another group
    expectSql("SELECT id name type group_id FROM Attendee WHERE uid").withParameters(1, "group2");
    expectSql("INSERT INTO Attendee SET uid name type")
        .withParameters(1, "group2", 2, "Test4", 3, Type.CHAIR.ordinal());
    expectSql("SELECT id FROM Attendee WHERE uid").withParameters(1, "group2").withResult(row("id", 4));
    expectSql("INSERT INTO AttendeeGroup SET id").withParameters(1, 4);
    expectSql("UPDATE Attendee SET group_id WHERE id").withParameters(1, 4, 2, 4);
    Attendee group2 = attendeeExtent.create("group2", "Test4", Type.CHAIR);
    assertEquals(group2.isGroup(), true);
  }

  public void testFind() {
    AttendeeExtent attendeeExtent = new AttendeeExtentSqlImpl();
    // create attendee
    expectSql("SELECT id name type group_id FROM Attendee WHERE uid").withParameters(1, "student");
    expectSql("INSERT INTO Attendee SET uid name type").withParameters(1, "student", 2, "Test1", 3,
        Type.STUDENT.ordinal());
    expectSql("SELECT id FROM Attendee WHERE uid").withParameters(1, "student").withResult(row("id", 1));
    expectSql("INSERT INTO AttendeeGroup SET id").withParameters(1, null);
    expectSql("UPDATE Attendee SET group_id WHERE id").withParameters(1, null, 2, 1);
    attendeeExtent.create("student", "Test1", Type.STUDENT);
    // create another attendee
    expectSql("SELECT id name type group_id FROM Attendee WHERE uid").withParameters(1, "teacher");
    expectSql("INSERT INTO Attendee SET uid name type").withParameters(1, "teacher", 2, "Test2", 3,
        Type.TEACHER.ordinal());
    expectSql("SELECT id FROM Attendee WHERE uid").withParameters(1, "teacher").withResult(row("id", 2));
    expectSql("INSERT INTO AttendeeGroup SET id").withParameters(1, null);
    expectSql("UPDATE Attendee SET group_id WHERE id").withParameters(1, null, 2, 2);
    Attendee teacher = attendeeExtent.create("teacher", "Test2", Type.TEACHER);
    // create third attendee
    expectSql("SELECT id name type group_id FROM Attendee WHERE uid").withParameters(1, "group");
    expectSql("INSERT INTO Attendee SET uid name type").withParameters(1, "group", 2, "Test3", 3,
        Type.FREE_FORM_GROUP.ordinal());
    expectSql("SELECT id FROM Attendee WHERE uid").withParameters(1, "group").withResult(row("id", 3));
    expectSql("INSERT INTO AttendeeGroup SET id").withParameters(1, 3);
    expectSql("UPDATE Attendee SET group_id WHERE id").withParameters(1, 3, 2, 3);
    attendeeExtent.create("group", "Test3", Type.FREE_FORM_GROUP);
    // test find method when attendee with the given id doen't exist
    expectSql("SELECT id name type group_id FROM Attendee WHERE uid").withParameters(1, "blablabla");
    assertNull(attendeeExtent.find("blablabla"));
    // test find method when attendee with the given id exists
    expectSql("SELECT id name type group_id FROM Attendee WHERE uid").withParameters(1, "teacher").withResult(
        row("id", 2, "name", "Test2", "type", Type.TEACHER.ordinal(), "group_id", null));
    assertTrue(attendeeExtent.find("teacher").equals(teacher));
  }
}
