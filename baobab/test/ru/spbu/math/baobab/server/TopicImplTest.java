package ru.spbu.math.baobab.server;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;

import org.junit.Test;

import com.google.common.collect.Lists;

import ru.spbu.math.baobab.model.Attendee;
import ru.spbu.math.baobab.model.EvenOddWeek;
import ru.spbu.math.baobab.model.Event;
import ru.spbu.math.baobab.model.TimeInstant;
import ru.spbu.math.baobab.model.TimeSlot;
import ru.spbu.math.baobab.model.TimeSlotExtent;
import ru.spbu.math.baobab.model.Topic;
import ru.spbu.math.baobab.model.TopicExtent;
import ru.spbu.math.baobab.model.Topic.Type;

/**
 * Tests for Event implementation
 * 
 * @author agudulin
 */
public class TopicImplTest {

  @Test
  public void testAddEvent() {
    TopicExtent topicExtent = new TopicExtentImpl();
    Topic topic = topicExtent.createTopic("CS101-2012", Type.LECTURE_COURSE, "Computer Science intro course");

    TimeInstant start = new TimeInstant(9, 30);
    TimeInstant finish = new TimeInstant(11, 5);
    TimeSlotExtent tsExtent = new TimeSlotExtentImpl();
    TimeSlot ts = tsExtent.create("first double class", start, finish, 2, EvenOddWeek.ALL);
    TimeSlot ts1 = tsExtent.create("double class", start, finish, 4, EvenOddWeek.EVEN);
    Date date = new Date();

    Event event = topic.addEvent(date, ts, null);
    Event event1 = topic.addEvent(date, ts1, null);
    Collection<Event> events = Lists.newArrayList(event, event1);

    assertEquals(topic.getEvents(), events);
  }

  @Test
  public void testAddAllEvents() {
    TopicExtent topicExtent = new TopicExtentImpl();
    Topic topic = topicExtent.createTopic("CS101-2012", Type.LECTURE_COURSE, "Computer Science intro course");

    TimeInstant start = new TimeInstant(9, 30);
    TimeInstant finish = new TimeInstant(11, 5);
    TimeSlotExtent tsExtent = new TimeSlotExtentImpl();
    TimeSlot ts = tsExtent.create("first double class", start, finish, 2, EvenOddWeek.ODD);

    Calendar cal = Calendar.getInstance(new Locale("ru", "RU"));
    cal.set(2012, Calendar.NOVEMBER, 11);
    Date startDate = cal.getTime();
    cal.set(2012, Calendar.DECEMBER, 3);
    Date finishDate = cal.getTime();

    Collection<Event> events = topic.addAllEvents(startDate, finishDate, ts, null);
    assertEquals(events.size(), 2);
    
    for (Event event : events) {
      assertTrue(event.getStartDate().after(startDate));
      assertTrue(event.getStartDate().before(finishDate));
    }
  }

  @Test
  public void testGetAttendees() {
    Topic topic = new TopicImpl("CS101-2012", Type.LECTURE_COURSE, "Computer Science intro course");
    AttendeeExtentImpl attendeeExtent = new AttendeeExtentImpl();
    ru.spbu.math.baobab.model.Attendee.Type type = ru.spbu.math.baobab.model.Attendee.Type.STUDENT;
    Attendee student1 = attendeeExtent.create("1", "Иван Иванов", type);
    Attendee student2 = attendeeExtent.create("2", "Петр Петров", type);
    Attendee student3 = attendeeExtent.create("3", "Андрей Андреев", type);
    topic.addAttendee(student1);
    topic.addAttendee(student2);
    topic.addAttendee(student3);
    Collection<Attendee> attendees = Lists.newArrayList(student1, student2, student3);
    assertEquals(attendees, topic.getAttendees());
  }

  @Test
  public void testGetOwners() {
    Topic topic = new TopicImpl("CS101-2012", Type.LECTURE_COURSE, "Computer Science intro course");
    AttendeeExtentImpl attendeeExtent = new AttendeeExtentImpl();
    ru.spbu.math.baobab.model.Attendee.Type type = ru.spbu.math.baobab.model.Attendee.Type.TEACHER;
    Attendee owner1 = attendeeExtent.create("1", "Денис Денисов", type);
    Attendee owner2 = attendeeExtent.create("2", "Яков Яковлев", type);
    Attendee owner3 = attendeeExtent.create("3", "Антон Антонов", type);
    topic.addOwner(owner1);
    topic.addOwner(owner2);
    topic.addOwner(owner3);
    Collection<Attendee> owners = Lists.newArrayList(owner1, owner2, owner3);
    assertEquals(owners, topic.getOwners());
  }
}
