package ru.spbu.math.baobab.server.sql;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;

import ru.spbu.math.baobab.model.Attendee;
import ru.spbu.math.baobab.model.Attendee.Type;
import ru.spbu.math.baobab.model.AttendeeExtent;

/**
 * Tests for AttendeeSqlImpl.
 * 
 * @author aoool
 */
public class AttendeeSqlImplTest extends SqlTestCase {

  @Test
  public void testGetName() {
    AttendeeExtent attendeeExtent = new AttendeeExtentSqlImpl();
    expectSql("SELECT id name type group_id FROM Attendee WHERE uid").withParameters(1, "student");
    expectSql("INSERT INTO Attendee SET uid name type").withParameters(1, "student", 2, "Test1", 3,
        Type.STUDENT.ordinal());
    expectSql("SELECT id FROM Attendee WHERE uid").withParameters(1, "student").withResult(row("id", 1));
    expectSql("INSERT INTO AttendeeGroup SET id").withParameters(1, 1);
    expectSql("UPDATE Attendee SET group_id WHERE id").withParameters(1, 1, 2, 1);
    Attendee attendee = attendeeExtent.create("student", "Test1", Type.STUDENT);
    assertEquals(attendee.getName(), "Test1");
  }

  @Test
  public void testGetID() {
    AttendeeExtent attendeeExtent = new AttendeeExtentSqlImpl();
    expectSql("SELECT id name type group_id FROM Attendee WHERE uid").withParameters(1, "teacher");
    expectSql("INSERT INTO Attendee SET uid name type").withParameters(1, "teacher", 2, "Test2", 3,
        Type.TEACHER.ordinal());
    expectSql("SELECT id FROM Attendee WHERE uid").withParameters(1, "teacher").withResult(row("id", 2));
    expectSql("INSERT INTO AttendeeGroup SET id").withParameters(1, 2);
    expectSql("UPDATE Attendee SET group_id WHERE id").withParameters(1, 2, 2, 2);
    Attendee teacher = attendeeExtent.create("teacher", "Test2", Type.TEACHER);
    assertEquals(teacher.getID(), "teacher");
  }

  @Test
  public void testGetType() {
    AttendeeExtent attendeeExtent = new AttendeeExtentSqlImpl();
    expectSql("SELECT id name type group_id FROM Attendee WHERE uid").withParameters(1, "group");
    expectSql("INSERT INTO Attendee SET uid name type").withParameters(1, "group", 2, "Test3", 3,
        Type.ACADEMIC_GROUP.ordinal());
    expectSql("SELECT id FROM Attendee WHERE uid").withParameters(1, "group").withResult(row("id", 3));
    expectSql("INSERT INTO AttendeeGroup SET id").withParameters(1, 3);
    expectSql("UPDATE Attendee SET group_id WHERE id").withParameters(1, 3, 2, 3);
    Attendee group = attendeeExtent.create("group", "Test3", Type.ACADEMIC_GROUP);
    assertEquals(group.getType(), Type.ACADEMIC_GROUP);
  }

  @Test
  public void testSetID() {
    AttendeeExtent attendeeExtent = new AttendeeExtentSqlImpl();
    // create student
    expectSql("SELECT id name type group_id FROM Attendee WHERE uid").withParameters(1, "student");
    expectSql("INSERT INTO Attendee SET uid name type").withParameters(1, "student", 2, "Test1", 3,
        Type.STUDENT.ordinal());
    expectSql("SELECT id FROM Attendee WHERE uid").withParameters(1, "student").withResult(row("id", 1));
    expectSql("INSERT INTO AttendeeGroup SET id").withParameters(1, 1);
    expectSql("UPDATE Attendee SET group_id WHERE id").withParameters(1, 1, 2, 1);
    Attendee student = attendeeExtent.create("student", "Test1", Type.STUDENT);
    // create teacher
    expectSql("SELECT id name type group_id FROM Attendee WHERE uid").withParameters(1, "teacher");
    expectSql("INSERT INTO Attendee SET uid name type").withParameters(1, "teacher", 2, "Test2", 3,
        Type.TEACHER.ordinal());
    expectSql("SELECT id FROM Attendee WHERE uid").withParameters(1, "teacher").withResult(row("id", 2));
    expectSql("INSERT INTO AttendeeGroup SET id").withParameters(1, 2);
    expectSql("UPDATE Attendee SET group_id WHERE id").withParameters(1, 2, 2, 2);
    Attendee teacher = attendeeExtent.create("teacher", "Test2", Type.TEACHER);
    // create Group
    expectSql("SELECT id name type group_id FROM Attendee WHERE uid").withParameters(1, "group");
    expectSql("INSERT INTO Attendee SET uid name type").withParameters(1, "group", 2, "Test3", 3,
        Type.FREE_FORM_GROUP.ordinal());
    expectSql("SELECT id FROM Attendee WHERE uid").withParameters(1, "group").withResult(row("id", 3));
    expectSql("INSERT INTO AttendeeGroup SET id").withParameters(1, 3);
    expectSql("UPDATE Attendee SET group_id WHERE id").withParameters(1, 3, 2, 3);
    Attendee group = attendeeExtent.create("group", "Test3", Type.FREE_FORM_GROUP);
    try {
      expectSql("SELECT id name type group_id FROM Attendee WHERE uid").withParameters(1, "student").withResult(
          row("id", 1, "name", "Test1", "type", Type.STUDENT.ordinal(), "group_id", 0));
      // this query will never be used if everything is working good:
      // expectSql("UPDATE Attendee SET uid WHERE id").withParameters(1, "student", 2, 2);
      teacher.setID("student");
      fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      // OK: can't set new ID if attendee with this ID already exists
    }
    expectSql("SELECT id name type group_id FROM Attendee WHERE uid").withParameters(1, "SuperTeacher");
    expectSql("UPDATE Attendee SET uid WHERE id").withParameters(1, "SuperTeacher", 2, 2);
    teacher.setID("SuperTeacher");
    assertEquals(teacher.getID(), "SuperTeacher");
  }

  @Test
  public void testIsGroup() {
    AttendeeExtent attendeeExtent = new AttendeeExtentSqlImpl();
    // create student
    expectSql("SELECT id name type group_id FROM Attendee WHERE uid").withParameters(1, "student");
    expectSql("INSERT INTO Attendee SET uid name type").withParameters(1, "student", 2, "Test1", 3,
        Type.STUDENT.ordinal());
    expectSql("SELECT id FROM Attendee WHERE uid").withParameters(1, "student").withResult(row("id", 1));
    expectSql("INSERT INTO AttendeeGroup SET id").withParameters(1, 1);
    expectSql("UPDATE Attendee SET group_id WHERE id").withParameters(1, 1, 2, 1);
    Attendee student = attendeeExtent.create("student", "Test1", Type.STUDENT);
    assertEquals(student.isGroup(), false);
    // create Academic Group
    expectSql("SELECT id name type group_id FROM Attendee WHERE uid").withParameters(1, "academicGroup");
    expectSql("INSERT INTO Attendee SET uid name type").withParameters(1, "academicGroup", 2, "Test2", 3,
        Type.ACADEMIC_GROUP.ordinal());
    expectSql("SELECT id FROM Attendee WHERE uid").withParameters(1, "academicGroup").withResult(row("id", 2));
    expectSql("INSERT INTO AttendeeGroup SET id").withParameters(1, 2);
    expectSql("UPDATE Attendee SET group_id WHERE id").withParameters(1, 2, 2, 2);
    Attendee academicGroup = attendeeExtent.create("academicGroup", "Test2", Type.ACADEMIC_GROUP);
    assertEquals(academicGroup.isGroup(), true);
    // create Free Form Group
    // create academicGroup
    expectSql("SELECT id name type group_id FROM Attendee WHERE uid").withParameters(1, "ffGroup");
    expectSql("INSERT INTO Attendee SET uid name type").withParameters(1, "ffGroup", 2, "Test3", 3,
        Type.FREE_FORM_GROUP.ordinal());
    expectSql("SELECT id FROM Attendee WHERE uid").withParameters(1, "ffGroup").withResult(row("id", 3));
    expectSql("INSERT INTO AttendeeGroup SET id").withParameters(1, 3);
    expectSql("UPDATE Attendee SET group_id WHERE id").withParameters(1, 3, 2, 3);
    Attendee ffGroup = attendeeExtent.create("ffGroup", "Test3", Type.FREE_FORM_GROUP);
    assertEquals(ffGroup.isGroup(), true);
  }

  @Test
  public void testGetGroupMembers_AddGroupMember() {
    AttendeeExtent attendeeExtent = new AttendeeExtentSqlImpl();
    // create student
    expectSql("SELECT id name type group_id FROM Attendee WHERE uid").withParameters(1, "student");
    expectSql("INSERT INTO Attendee SET uid name type").withParameters(1, "student", 2, "Test1", 3,
        Type.STUDENT.ordinal());
    expectSql("SELECT id FROM Attendee WHERE uid").withParameters(1, "student").withResult(row("id", 1));
    expectSql("INSERT INTO AttendeeGroup SET id").withParameters(1, 1);
    expectSql("UPDATE Attendee SET group_id WHERE id").withParameters(1, 1, 2, 1);
    Attendee student = attendeeExtent.create("student", "Test1", Type.STUDENT);
    // create teacher
    expectSql("SELECT id name type group_id FROM Attendee WHERE uid").withParameters(1, "teacher");
    expectSql("INSERT INTO Attendee SET uid name type").withParameters(1, "teacher", 2, "Test2", 3,
        Type.TEACHER.ordinal());
    expectSql("SELECT id FROM Attendee WHERE uid").withParameters(1, "teacher").withResult(row("id", 2));
    expectSql("INSERT INTO AttendeeGroup SET id").withParameters(1, null);
    expectSql("UPDATE Attendee SET group_id WHERE id").withParameters(1, null, 2, 2);
    Attendee teacher = attendeeExtent.create("teacher", "Test2", Type.TEACHER);
    // create Group
    expectSql("SELECT id name type group_id FROM Attendee WHERE uid").withParameters(1, "group");
    expectSql("INSERT INTO Attendee SET uid name type").withParameters(1, "group", 2, "Test3", 3,
        Type.FREE_FORM_GROUP.ordinal());
    expectSql("SELECT id FROM Attendee WHERE uid").withParameters(1, "group").withResult(row("id", 3));
    expectSql("INSERT INTO AttendeeGroup SET id").withParameters(1, 3);
    expectSql("UPDATE Attendee SET group_id WHERE id").withParameters(1, 3, 2, 3);
    Attendee group = attendeeExtent.create("group", "Test3", Type.FREE_FORM_GROUP);
    assertEquals(group.isGroup(), true);
    expectSql(
        "SELECT A_member.id A_member.uid A_member.name A_member.type A_member.group_id "
            + "FROM Attendee A_group JOIN AttendeeGroup AG ON (A_group.group_id = AG.id) "
            + "JOIN GroupMember GM ON (GM.group_id = AG.id) JOIN Attendee A_member "
            + "ON (A_member.id = GM.attendee_id) WHERE A_group.id").withParameters(1, 3);
    assertTrue(group.getGroupMembers().isEmpty());

    // Test for addGroupMember
    expectSql("SELECT id FROM Attendee WHERE uid").withParameters(1, "teacher").withResult(row("id", 2));
    expectSql("INSERT INTO GroupMember SET group_id attendee_id").withParameters(1, 3, 2, 2);
    group.addGroupMember(teacher);
    expectSql("SELECT id FROM Attendee WHERE uid").withParameters(1, "student").withResult(row("id", 1));
    expectSql("INSERT INTO GroupMember SET group_id attendee_id").withParameters(1, 3, 2, 1);
    group.addGroupMember(student);
    Collection<Attendee> members = new ArrayList<Attendee>();
    members.add(student);
    members.add(teacher);
    //
    expectSql(
        "SELECT A_member.id A_member.uid A_member.name A_member.type A_member.group_id "
            + "FROM Attendee A_group JOIN AttendeeGroup AG ON (A_group.group_id = AG.id) "
            + "JOIN GroupMember GM ON (GM.group_id = AG.id) JOIN Attendee A_member "
            + "ON (A_member.id = GM.attendee_id) WHERE A_group.id").withParameters(1, 3).withResult(
        row("A_member.id", 1, "A_member.uid", "student", "A_member.name", "Test1", "A_member.type",
            Type.STUDENT.ordinal(), "A_member.group_id", 0),
        row("A_member.id", 2, "A_member.uid", "teacher", "A_member.name", "Test2", "A_member.type",
            Type.TEACHER.ordinal(), "A_member.group_id", 0));
    assertTrue(group.getGroupMembers().equals(members));
    // Test for attendee who is not a group
    // this query will never be used if everything is working good:
    // expectSql(
    // "SELECT A_member.id A_member.uid A_member.name A_member.type A_member.group_id "
    // + "FROM Attendee A_group JOIN AttendeeGroup AG ON (A_group.group_id = AG.id) "
    // + "JOIN GroupMember GM ON (GM.group_id = AG.id) JOIN Attendee A_member "
    // + "ON (A_member.id = GM.attendee_id) WHERE A_group.id").withParameters(1, 2);
    // assertNull(teacher.getGroupMembers());
  }
}
