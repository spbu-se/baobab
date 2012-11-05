package ru.spbu.math.baobab.server.sql;

import java.util.ArrayList;
import java.util.Collection;

import ru.spbu.math.baobab.model.Attendee;
import ru.spbu.math.baobab.model.Attendee.Type;
import ru.spbu.math.baobab.model.AttendeeExtent;
import junit.framework.TestCase;

/**
 * Tests for AttendeeSqlImpl.
 * 
 * @author aoool
 * 
 */

public class AttendeeSqlImplTest extends TestCase {

  public void testGetName() {
    AttendeeExtent attendeeExtent = new AttendeeExtentSqlImpl();
    Attendee student = attendeeExtent.create("student", "Test", Type.STUDENT);
    assertEquals(student.getName(), "Test");
  }

  public void testGetID() {
    AttendeeExtent attendeeExtent = new AttendeeExtentSqlImpl();
    Attendee student = attendeeExtent.create("student", "Test", Type.STUDENT);
    assertEquals(student.getID(), "student");
  }

  public void testGetType() {
    AttendeeExtent attendeeExtent = new AttendeeExtentSqlImpl();
    Attendee group = attendeeExtent.create("group", "Test", Type.ACADEMIC_GROUP);
    assertEquals(group.getType(), Type.ACADEMIC_GROUP);
  }

  public void testSetID() {
    AttendeeExtent attendeeExtent = new AttendeeExtentSqlImpl();
    Attendee student = attendeeExtent.create("student", "Test", Type.STUDENT);
    Attendee teacher = attendeeExtent.create("teacher", "Test", Type.TEACHER);
    Attendee group = attendeeExtent.create("group", "Test", Type.ACADEMIC_GROUP);
    group.addGroupMember(student);
    group.addGroupMember(teacher);
    try {
      teacher.setID("student");
      fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      // OK: can't set new ID if attendee with this ID already exists
    }
    teacher.setID("SuperTeacher");
    assertEquals(teacher.getID(), "SuperTeacher");
  }

  public void testIsGroup() {
    AttendeeExtent attendeeExtent = new AttendeeExtentSqlImpl();
    Attendee teacher = attendeeExtent.create("1", "Test1", Type.TEACHER);
    assertEquals(teacher.isGroup(), false);
    Attendee academicGroup = attendeeExtent.create("2", "Test2", Type.ACADEMIC_GROUP);
    assertEquals(academicGroup.isGroup(), true);
    Attendee ffGroup = attendeeExtent.create("3", "Test3", Type.FREE_FORM_GROUP);
    assertEquals(ffGroup.isGroup(), true);
  }

  public void testGetGroupMembers() {
    AttendeeExtent attendeeExtent = new AttendeeExtentSqlImpl();
    Attendee academicGroup = attendeeExtent.create("1", "Test1", Type.ACADEMIC_GROUP);
    Attendee teacher = attendeeExtent.create("2", "Test2", Type.TEACHER);
    Attendee student = attendeeExtent.create("3", "Test3", Type.STUDENT);
    assertTrue(academicGroup.getGroupMembers().isEmpty());
    academicGroup.addGroupMember(teacher); // testAddGroupMember
    academicGroup.addGroupMember(student);
    Collection<Attendee> members = new ArrayList<Attendee>();
    members.add(teacher);
    members.add(student);
    assertEquals(academicGroup.getGroupMembers(), members);
    assertNull(teacher.getGroupMembers());
  }
}
