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
  public void testSetID() {
    AttendeeExtent attendeeExtent = new AttendeeExtentSqlImpl();
    // Attendee constructor for student is used here instead of attendeeExtent.create(...) and shows us that Attendee
    // with UID=="student" already exists in database
    Attendee student = new AttendeeSqlImpl(1, "student", "Test1", Type.STUDENT, null, attendeeExtent);
    Attendee teacher = new AttendeeSqlImpl(2, "teacher", "Test2", Type.TEACHER, null, attendeeExtent);
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
    // create Attendee who is not a group
    Attendee student = new AttendeeSqlImpl(1, "student", "Test1", Type.STUDENT, null, attendeeExtent);
    assertEquals(student.isGroup(), false);
    // create Attendee who is a group
    Attendee academicGroup = new AttendeeSqlImpl(2, "academicGroup", "Test2", Type.ACADEMIC_GROUP, 2, attendeeExtent);
    assertEquals(academicGroup.isGroup(), true);
  }

  @Test
  public void testGetGroupMembers_AddGroupMember() {
    String GET_GROUP_MEMBERS_QUERY = "SELECT A_member.id A_member.uid A_member.name A_member.type A_member.group_id "
        + "FROM Attendee A_group JOIN AttendeeGroup AG ON (A_group.group_id = AG.id) "
        + "JOIN GroupMember GM ON (GM.group_id = AG.id) JOIN Attendee A_member "
        + "ON (A_member.id = GM.attendee_id) WHERE A_group.id";
    AttendeeExtent attendeeExtent = new AttendeeExtentSqlImpl();
    // create student
    Attendee student = new AttendeeSqlImpl(1, "student", "Test1", Type.STUDENT, null, attendeeExtent);
    // create teacher
    Attendee teacher = new AttendeeSqlImpl(2, "teacher", "Test2", Type.TEACHER, null, attendeeExtent);
    // create Group
    Attendee group = new AttendeeSqlImpl(3, "group", "Test3", Type.FREE_FORM_GROUP, 3, attendeeExtent);
    expectSql(GET_GROUP_MEMBERS_QUERY).withParameters(1, 3);
    assertTrue(group.getGroupMembers().isEmpty());
    // Test for addGroupMember
    expectSql("SELECT id FROM Attendee WHERE uid").withParameters(1, "teacher").withResult(row("id", 2));
    expectSql("INSERT INTO GroupMember SET group_id attendee_id").withParameters(1, 3, 2, 2);
    group.addGroupMember(teacher);
    expectSql("SELECT id FROM Attendee WHERE uid").withParameters(1, "student").withResult(row("id", 1));
    expectSql("INSERT INTO GroupMember SET group_id attendee_id").withParameters(1, 3, 2, 1);
    group.addGroupMember(student);
    Collection<Attendee> attendees = new ArrayList<Attendee>();
    attendees.add(student);
    attendees.add(teacher);
    expectSql(GET_GROUP_MEMBERS_QUERY).withParameters(1, 3).withResult(
        row("id", 1, "uid", "student", "name", "Test1", "type", Type.STUDENT.ordinal(), "group_id", null),
        row("id", 2, "uid", "teacher", "name", "Test2", "type", Type.TEACHER.ordinal(), "group_id", null));
    assertEquals(group.getGroupMembers(), attendees);
    // Test for attendee who is not a group
    assertNull(teacher.getGroupMembers());
  }
}
