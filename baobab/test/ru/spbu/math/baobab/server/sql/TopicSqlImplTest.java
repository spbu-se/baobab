package ru.spbu.math.baobab.server.sql;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;

import ru.spbu.math.baobab.model.Attendee;
import ru.spbu.math.baobab.model.AttendeeExtent;
import ru.spbu.math.baobab.model.AuditoriumExtent;
import ru.spbu.math.baobab.model.EvenOddWeek;
import ru.spbu.math.baobab.model.TimeInstant;
import ru.spbu.math.baobab.model.TimeSlotExtent;
import ru.spbu.math.baobab.model.Topic;
import ru.spbu.math.baobab.server.AuditoriumExtentImpl;
import ru.spbu.math.baobab.server.TimeSlotExtentImpl;

public class TopicSqlImplTest extends SqlTestCase {

  @Test
  public void testAddAttendee() {
    TimeSlotExtent timeSlotExtent = new TimeSlotExtentImpl();
    TimeInstant start = new TimeInstant(9, 30);
    TimeInstant finish = new TimeInstant(11, 5);
    timeSlotExtent.create("first double class", start, finish, 2, EvenOddWeek.ODD);

    AuditoriumExtent auditoriumExtent = new AuditoriumExtentImpl();
    auditoriumExtent.create("1", 20);

    Topic topic = new TopicSqlImpl("CS101-2012", Topic.Type.LECTURE_COURSE,
        "Computer Science introduction course in year 2012", timeSlotExtent, auditoriumExtent);
    Attendee student = new AttendeeSqlImpl(1, "student", "Test1", Attendee.Type.STUDENT, null, new AttendeeExtentSqlImpl());

    expectSql("INSERT TopicAttendee SET topic_id attendee_id").withParameters(1, topic.getID(), 2, student.getID());
    topic.addAttendee(student);
  }

  @Test
  public void testGetAttendees() {
    TimeSlotExtent timeSlotExtent = new TimeSlotExtentImpl();
    TimeInstant start = new TimeInstant(9, 30);
    TimeInstant finish = new TimeInstant(11, 5);
    timeSlotExtent.create("first double class", start, finish, 2, EvenOddWeek.ODD);

    AuditoriumExtent auditoriumExtent = new AuditoriumExtentImpl();
    auditoriumExtent.create("1", 20);

    Topic topic = new TopicSqlImpl("CS101-2012", Topic.Type.LECTURE_COURSE,
        "Computer Science introduction course in year 2012", timeSlotExtent, auditoriumExtent);
    AttendeeExtent extent = new AttendeeExtentSqlImpl();
    Attendee student1 = new AttendeeSqlImpl(1, "student1", "Test1", Attendee.Type.STUDENT, null, extent);
    Attendee student2 = new AttendeeSqlImpl(2, "student2", "Test2", Attendee.Type.STUDENT, null, extent);

    expectSql("INSERT TopicAttendee SET topic_id attendee_id").withParameters(1, topic.getID(), 2, student1.getID());
    topic.addAttendee(student1);
    expectSql("INSERT TopicAttendee SET topic_id attendee_id").withParameters(1, topic.getID(), 2, student2.getID());
    topic.addAttendee(student2);

    expectSql("SELECT * FROM Attendee WHERE topic_id").withParameters(1, topic.getID()).withResult(
        row(
            "id", 1,
            "uid", "student1",
            "name", "Test1",
            "type", Attendee.Type.STUDENT.ordinal(),
            "group_id", null),
        row(
            "id", 2,
            "uid", "student2",
            "name", "Test2",
            "type", Attendee.Type.STUDENT.ordinal(),
            "group_id", null)
    );

    List<Attendee> attendeesFromDb = (List<Attendee>) topic.getAttendees();
    List<Attendee> attendees = Lists.newArrayList();
    attendees.add(student1);
    attendees.add(student2);

    assertEquals(attendees, attendeesFromDb);
  }
}
