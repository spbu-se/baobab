package ru.spbu.math.baobab.server.sql;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import ru.spbu.math.baobab.model.Event;
import ru.spbu.math.baobab.model.Topic.Type;
import ru.spbu.math.baobab.model.EvenOddWeek;
import ru.spbu.math.baobab.model.TimeInstant;
import ru.spbu.math.baobab.model.TimeSlot;
import ru.spbu.math.baobab.model.TimeSlotExtent;
import ru.spbu.math.baobab.model.Topic;
import ru.spbu.math.baobab.model.TopicExtent;

/**
 * Tests for SQL-based implementation of Event
 * 
 * @author dageev
 */
public class EventSqlImplTest extends SqlTestCase {

  @Test
  public void testAddEvent() {
    TopicExtent topicExtent = new TopicExtentSqlImpl();
    expectSql("SELECT Topic WHERE uid").withParameters(1, "CS101-2012");
    expectSql("INSERT Topic uid name type").withParameters(1, "CS101-2012", 2,
        "Computer Science introduction course in year 2012", 3, Type.LECTURE_COURSE.ordinal());
    Topic topic = topicExtent.createTopic("CS101-2012", Type.LECTURE_COURSE,
        "Computer Science introduction course in year 2012");

    TimeSlotExtent timeSlotExtent = new TimeSlotExtentSqlImpl();
    TimeInstant start = new TimeInstant(9, 30);
    TimeInstant finish = new TimeInstant(11, 5);
    expectSql("SELECT TimeSlot WHERE name day is_odd is_odd").withParameters(1, "first double class", 2, 2, 3,
        EvenOddWeek.ODD.ordinal(), 4, EvenOddWeek.ALL.ordinal());
    expectSql("INSERT TimeSlot name start_min finish_min day is_odd").withParameters(1, "first double class", 2,
        start.getDayMinute(), 3, finish.getDayMinute(), 4, 2, 5, EvenOddWeek.ODD.ordinal());
    TimeSlot ts1 = timeSlotExtent.create("first double class", start, finish, 2, EvenOddWeek.ODD);

    Calendar cal = Calendar.getInstance();
    Date date = cal.getTime();

    expectSql("SELECT FROM TimeSlot WHERE name").withParameters(2, ts1.getName()).withResult(
        row("id", 0, "name", ts1.getName(), "start_min", start.getDayMinute(), "finish_min", finish.getDayMinute(),
            "day", 2, "is_odd", EvenOddWeek.ODD.ordinal()));

    expectSql("SELECT FROM Topic WHERE uid").withParameters(2, topic.getID()).withResult(
        row("id", 0, "uid", topic.getID(), "name", topic.getName(), "type", Type.LECTURE_COURSE.ordinal()));
    expectInsert("INSERT INTO Event date time_slot_id topic_id auditorium_num");

    Event event = topic.addEvent(date, ts1, null);
    Event event1 = new EventSqlImpl(date, ts1, null, topic);
    assertEquals(event, event1);
  }

  @Test
  public void testGetEvents() {
    TopicExtent topicExtent = new TopicExtentSqlImpl();
    expectSql("SELECT Topic WHERE uid").withParameters(1, "CS101-2012");
    expectSql("INSERT Topic uid name type").withParameters(1, "CS101-2012", 2,
        "Computer Science introduction course in year 2012", 3, Type.LECTURE_COURSE.ordinal());
    Topic topic = topicExtent.createTopic("CS101-2012", Type.LECTURE_COURSE,
        "Computer Science introduction course in year 2012");

    TimeSlotExtent timeSlotExtent = new TimeSlotExtentSqlImpl();
    TimeInstant start = new TimeInstant(9, 30);
    TimeInstant finish = new TimeInstant(11, 5);
    expectSql("SELECT TimeSlot WHERE name day is_odd is_odd").withParameters(1, "first double class", 2, 2, 3,
        EvenOddWeek.ODD.ordinal(), 4, EvenOddWeek.ALL.ordinal());
    expectSql("INSERT TimeSlot name start_min finish_min day is_odd").withParameters(1, "first double class", 2,
        start.getDayMinute(), 3, finish.getDayMinute(), 4, 2, 5, EvenOddWeek.ODD.ordinal());
    TimeSlot ts1 = timeSlotExtent.create("first double class", start, finish, 2, EvenOddWeek.ODD);

    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.DAY_OF_WEEK, 3);
    Date date = cal.getTime();

    expectSql("SELECT FROM TimeSlot WHERE name").withParameters(2, ts1.getName()).withResult(
        row("id", 0, "name", ts1.getName(), "start_min", start.getDayMinute(), "finish_min", finish.getDayMinute(),
            "day", 2, "is_odd", EvenOddWeek.ODD.ordinal()));

    expectSql("SELECT FROM Topic WHERE uid").withParameters(2, topic.getID()).withResult(
        row("id", 0, "uid", topic.getID(), "name", topic.getName(), "type", Type.LECTURE_COURSE.ordinal()));
    expectInsert("INSERT INTO date time_slot_id topic_id auditorium_num");
    Event eventFromDb1 = topic.addEvent(date, ts1, null);

    expectSql("SELECT FROM TimeSlot WHERE name").withParameters(2, ts1.getName()).withResult(
        row("id", 0, "name", ts1.getName(), "start_min", start.getDayMinute(), "finish_min", finish.getDayMinute(),
            "day", 2, "is_odd", EvenOddWeek.ODD.ordinal()));

    expectSql("SELECT FROM Topic WHERE uid").withParameters(2, topic.getID()).withResult(
        row("id", 0, "uid", topic.getID(), "name", topic.getName(), "type", Type.LECTURE_COURSE.ordinal()));
    expectInsert("INSERT INTO Event date time_slot_id topic_id auditorium_num");

    cal.set(Calendar.DAY_OF_WEEK, 4);
    Date date1 = cal.getTime();
    Event eventFromDb2 = topic.addEvent(date1, ts1, null);

    Event event = new EventSqlImpl(date, ts1, null, topic);
    Event event1 = new EventSqlImpl(date1, ts1, null, topic);
    List<Event> events = Arrays.asList(event, event1);
    java.sql.Date sqlDate1 = new java.sql.Date(eventFromDb1.getStartDate().getTime());
    java.sql.Date sqlDate2 = new java.sql.Date(eventFromDb2.getStartDate().getTime());

    expectQuery("SELECT * FROM Event", row("date", sqlDate1, "time_slot_id", 0, "topic_id", 0, "auditorium_num", ""),
        row("date", sqlDate2, "time_slot_id", 0, "topic_id", 0, "auditorium_num", ""));

    expectSql("SELECT FROM TimeSlot WHERE id").withParameters(1, 0).withResult(
        row("name", ts1.getName(), "start_min", ts1.getStart().getDayMinute(), "finish_min", ts1.getFinish()
            .getDayMinute(), "day", ts1.getDayOfWeek(), "is_odd", EvenOddWeek.ODD.ordinal()));

    expectSql("SELECT FROM Auditorium WHERE num").withParameters(1, "").withResult(row("num", "", "capacity", 15));

    expectSql("SELECT FROM Topic WHERE id").withParameters(1, 0).withResult(
        row("uid", topic.getID(), "name", topic.getName(), "type", Type.LECTURE_COURSE.ordinal()));

    expectSql("SELECT FROM TimeSlot WHERE id").withParameters(1, 0).withResult(
        row("name", ts1.getName(), "start_min", start.getDayMinute(), "finish_min", finish.getDayMinute(), "day", 2,
            "is_odd", EvenOddWeek.ODD.ordinal()));
    
    expectSql("SELECT FROM Auditorium WHERE num").withParameters(1, "").withResult(row("num", "", "capacity", 15));
    
    expectSql("SELECT FROM Topic WHERE id").withParameters(1, 0).withResult(
        row("uid", topic.getID(), "name", topic.getName(), "type", Type.LECTURE_COURSE.ordinal()));

    List<Event> eventsFromDb = (List<Event>) topic.getEvents();
    assertEquals(events, eventsFromDb);
  }
}
