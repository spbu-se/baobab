package ru.spbu.math.baobab.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Test;

import ru.spbu.math.baobab.model.Attendee;
import ru.spbu.math.baobab.model.Auditorium;
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
 * @author dageev
 */
public class EventImplTest {
  
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

    Event event = new EventImpl(1, date, ts, aud, topic);
    AttendeeExtentImpl attendeeExtent = new AttendeeExtentImpl();
    Attendee.Type type = Attendee.Type.STUDENT;
    Attendee student1 = attendeeExtent.create("1", "Иван Иванов", type);
    Attendee student2 = attendeeExtent.create("2", "Петр Петров", type);
    Attendee student3 = attendeeExtent.create("3", "Андрей Андреев", type);
    event.addAttendee(student1);
    event.addAttendee(student2);
    event.addAttendee(student3);
    event.addAttendee(student2);
    assertEquals(event.getAttendees().size(), 3);
    assertTrue(event.getAttendees().contains(student1));
    assertTrue(event.getAttendees().contains(student2));
    assertTrue(event.getAttendees().contains(student3));
  }
}
