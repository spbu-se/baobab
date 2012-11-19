package ru.spbu.math.baobab.server;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;

import org.junit.Test;

import com.google.common.collect.Lists;

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
}
