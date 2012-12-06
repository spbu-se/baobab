package ru.spbu.math.baobab.server.sql;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.junit.Test;

import ru.spbu.math.baobab.model.Auditorium;
import ru.spbu.math.baobab.model.AuditoriumExtent;
import ru.spbu.math.baobab.model.Event;
import ru.spbu.math.baobab.model.Topic.Type;
import ru.spbu.math.baobab.model.Attendee;
import ru.spbu.math.baobab.model.EvenOddWeek;
import ru.spbu.math.baobab.model.TimeInstant;
import ru.spbu.math.baobab.model.TimeSlot;
import ru.spbu.math.baobab.model.TimeSlotExtent;
import ru.spbu.math.baobab.model.Topic;
import ru.spbu.math.baobab.model.TopicExtent;
import ru.spbu.math.baobab.server.AuditoriumExtentImpl;
import ru.spbu.math.baobab.server.AuditoriumImpl;
import ru.spbu.math.baobab.server.EventImpl;
import ru.spbu.math.baobab.server.TimeSlotExtentImpl;
import ru.spbu.math.baobab.server.TopicExtentImpl;

/**
 * Tests for SQL-based implementation of Event
 * 
 * @author dageev
 */
public class EventSqlImplTest extends SqlTestCase {

  @Test
  public void testAddEvent() {
    TimeSlotExtent timeSlotExtent = new TimeSlotExtentImpl();
    TimeInstant start = new TimeInstant(9, 30);
    TimeInstant finish = new TimeInstant(11, 5);
    TimeSlot ts1 = timeSlotExtent.create("first double class", start, finish, 2, EvenOddWeek.ODD);
    
    AuditoriumExtent auditoriumExtent = new AuditoriumExtentImpl();
    Auditorium auditorium = auditoriumExtent.create("1", 20);
    
    Topic topic = new TopicSqlImpl("CS101-2012", Type.LECTURE_COURSE,
        "Computer Science introduction course in year 2012", timeSlotExtent, auditoriumExtent);;

    Calendar cal = Calendar.getInstance();
    Date date = cal.getTime();
    java.sql.Date sqlDate = new java.sql.Date(date.getTime());
     
    expectSql("INSERT Event SET date time_slot_id topic_id auditorium_num").withParameters(1, sqlDate , 2,
        ts1.getID(), 3, topic.getID(), 4, auditorium.getID());
    expectSql("SELECT FROM Event WHERE date time_slot_id topic_id")
       .withParameters(1, sqlDate,
                       2, ts1.getID(),
                       3, topic.getID())
       .withResult(row("id", 1));
       
    Event event = topic.addEvent(date, ts1, auditorium);
    Event event1 = new EventImpl(1, date, ts1, auditorium, topic);
    assertEquals(event, event1);
  }

  @Test
  public void testGetEvents() {
    TimeSlotExtent timeSlotExtent = new TimeSlotExtentImpl();
    TimeInstant start = new TimeInstant(9, 30);
    TimeInstant finish = new TimeInstant(11, 5);
    TimeSlot ts1 = timeSlotExtent.create("first double class", start, finish, 2, EvenOddWeek.ODD);
    
    AuditoriumExtent auditoriumExtent = new AuditoriumExtentImpl();
    Auditorium auditorium = auditoriumExtent.create("1", 20);
 
    Topic topic = new TopicSqlImpl("CS101-2012", Type.LECTURE_COURSE,
        "Computer Science introduction course in year 2012", timeSlotExtent, auditoriumExtent);
    
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.DAY_OF_WEEK, 3);
    Date date = cal.getTime();
    java.sql.Date sqlDate = new java.sql.Date(date.getTime());  
    
    cal.set(Calendar.DAY_OF_WEEK, 5);
    Date date1 = cal.getTime();
    java.sql.Date sqlDate1 = new java.sql.Date(date1.getTime());
     
    Event event = new EventImpl(1, date, ts1, auditorium, topic);
    Event event1 = new EventImpl(2, date1, ts1, auditorium, topic);
    List<Event> events = Arrays.asList(event, event1);
    expectSql("SELECT Event WHERE topic_id")
       .withParameters(1, topic.getID())
       .withResult(
          row("date", sqlDate,
              "timeslot_id", ts1.getID(),
              "auditorium_num", auditorium.getID()
              ),
          row("date", sqlDate1,
              "timeslot_id", ts1.getID(),
              "auditorium_num", auditorium.getID()));
    List<Event> eventsFromDb = (List<Event>) topic.getEvents();
    assertEquals(events, eventsFromDb);      
  }
  
  @Test
  public void testAddAllEvents() {
    TimeSlotExtent timeSlotExtent = new TimeSlotExtentImpl();
    TimeInstant start = new TimeInstant(9, 30);
    TimeInstant finish = new TimeInstant(11, 5);
    TimeSlot ts = timeSlotExtent.create("first double class", start, finish, 2, EvenOddWeek.ODD);
    
    AuditoriumExtent auditoriumExtent = new AuditoriumExtentImpl();
    Auditorium auditorium = auditoriumExtent.create("1", 20);
    
    Topic topic = new TopicSqlImpl("CS101-2012", Type.LECTURE_COURSE,
        "Computer Science introduction course in year 2012", timeSlotExtent, auditoriumExtent);

    Calendar cal = Calendar.getInstance(new Locale("ru", "RU"));
    cal.set(2012, Calendar.NOVEMBER, 11);
    Date startDate = cal.getTime();
    cal.set(2012, Calendar.DECEMBER, 3);
    Date finishDate = cal.getTime();
    cal.set(2012, 10, 13);
    Date date = cal.getTime();
    cal.set(2012, 10, 27);
    Date date1 = cal.getTime();
    
    expectInsert("INSERT INTO Event"); 
    expectSql("SELECT FROM Event WHERE date time_slot_id topic_id")
       .withParameters(1, new java.sql.Date(date.getTime()),
                       2, ts.getID(),
                       3, topic.getID())
       .withResult(row("id", 1));
    expectInsert("INSERT INTO Event");
    expectSql("SELECT FROM Event WHERE date time_slot_id topic_id")
    .withParameters(1, new java.sql.Date(date1.getTime()),
                    2, ts.getID(),
                    3, topic.getID())
    .withResult(row("id", 2));

    Collection<Event> events = topic.addAllEvents(startDate, finishDate, ts, auditorium);
    assertEquals(events.size(), 2);
    
    for (Event event : events) {
      assertTrue(event.getStartDate().after(startDate));
      assertTrue(event.getStartDate().before(finishDate));
    }
  }
  
  @Test
  public void testAddAttendee() {
    TopicExtent topicExtent = new TopicExtentImpl();
    Topic topic = topicExtent.createTopic("CS101-2012", Type.LECTURE_COURSE, "Computer Science intro course");
    TimeInstant start = new TimeInstant(9, 30);
    TimeInstant finish = new TimeInstant(11, 5);
    TimeSlotExtent tsExtent = new TimeSlotExtentImpl();
    TimeSlot ts = tsExtent.create("first double class", start, finish, 2, EvenOddWeek.ALL);
    Date date = new Date();
    Auditorium aud = new AuditoriumImpl("1", 1);

    Event event = new EventSqlImpl(1, date, ts, aud, topic);

    Attendee student = new AttendeeSqlImpl(1, "student", "Test1", Attendee.Type.STUDENT, null, new AttendeeExtentSqlImpl());

    expectSql("INSERT EventAttendee SET attendee_uid event_id")
       .withParameters(1, student.getID(),
                       2, event.getID());                             
    event.addAttendee(student);    
  }
  
  @Test
  public void testGetAttendees() {  
    TopicExtent topicExtent = new TopicExtentImpl();
    Topic topic = topicExtent.createTopic("CS101-2012", Type.LECTURE_COURSE, "Computer Science intro course");
    TimeInstant start = new TimeInstant(9, 30);
    TimeInstant finish = new TimeInstant(11, 5);
    TimeSlotExtent tsExtent = new TimeSlotExtentImpl();
    TimeSlot ts = tsExtent.create("first double class", start, finish, 2, EvenOddWeek.ALL);
    Date date = new Date();
    Auditorium aud = new AuditoriumImpl("1", 1);

    Event event = new EventSqlImpl(1, date, ts, aud, topic);
    Attendee student1 = new AttendeeSqlImpl(1, "student1", "Test1", Attendee.Type.STUDENT, null, new AttendeeExtentSqlImpl());
    Attendee student2 = new AttendeeSqlImpl(2, "student2", "Test2", Attendee.Type.STUDENT, null, new AttendeeExtentSqlImpl());
    
    expectSql("SELECT FROM Attendee a JOIN EventAttendee ea ON ea.attendee_uid = a.uid WHERE ea.event_id")
     .withParameters(1, event.getID())
     .withResult(
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

    List<Attendee> attendeesFromDb = (List<Attendee>) event.getAttendees();
    List<Attendee> attendees = Arrays.asList(student1, student2);
    assertEquals(attendees, attendeesFromDb);
  }
}
