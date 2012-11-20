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
    expectSql("SELECT TimeSlot WHERE name day is_odd is_odd")
    .withParameters(1, "first double class", 2, 2, 3, EvenOddWeek.ODD.ordinal(), 4, EvenOddWeek.ALL.ordinal())
    .withResult(
        row(1, 1)
        );
    TimeSlot ts1 = timeSlotExtent.create("first double class", start, finish, 2, EvenOddWeek.ODD);

    Calendar cal = Calendar.getInstance();
    Date date = cal.getTime();
    java.sql.Date sqlDate = new java.sql.Date(date.getTime());
     
    expectSql("INSERT Event SET date time_slot_id topic_id auditorium_num").withParameters(1, sqlDate , 2,
        ts1.getID(), 3, topic.getID()); //4, null);
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
    expectSql("SELECT TimeSlot WHERE name day is_odd is_odd")
    .withParameters(1, "first double class", 2, 2, 3, EvenOddWeek.ODD.ordinal(), 4, EvenOddWeek.ALL.ordinal())
    .withResult(
        row(1, 1)
        );
    TimeSlot ts1 = timeSlotExtent.create("first double class", start, finish, 2, EvenOddWeek.ODD);

    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.DAY_OF_WEEK, 3);
    Date date = cal.getTime();
    java.sql.Date sqlDate = new java.sql.Date(date.getTime());
     
    expectSql("INSERT Event SET date time_slot_id topic_id auditorium_num").withParameters(1, sqlDate , 2,
        ts1.getID(), 3, topic.getID()); //4, null);
    topic.addEvent(date, ts1, null);
    
    cal.set(Calendar.DAY_OF_WEEK, 5);
    Date date1 = cal.getTime();
    java.sql.Date sqlDate1 = new java.sql.Date(date1.getTime());
     
    expectSql("INSERT Event SET date time_slot_id topic_id auditorium_num").withParameters(1, sqlDate1 , 2,
        ts1.getID(), 3, topic.getID()); //4, null);
    topic.addEvent(date1, ts1, null);
    Event event = new EventSqlImpl(date, ts1, null, topic);
    Event event1 = new EventSqlImpl(date1, ts1, null, topic);
    List<Event> events = Arrays.asList(event, event1);
    expectSql("SELECT Event WHERE topic_id")
    .withParameters(1, topic.getID())
    .withResult(row(
                 "date", sqlDate,
                 "name", ts1.getName(),
                 "start_min", ts1.getStart().getDayMinute(),
                 "finish_min", ts1.getFinish().getDayMinute(),
                 "day", ts1.getDayOfWeek(),
                 "is_odd", ts1.getEvenOddWeek().ordinal()
               ),
               row(
                 "date", sqlDate1,
                 "name", ts1.getName(),
                 "start_min", ts1.getStart().getDayMinute(),
                 "finish_min", ts1.getFinish().getDayMinute(),
                 "day", ts1.getDayOfWeek(),
                 "is_odd", ts1.getEvenOddWeek().ordinal()                
                ));
    assertEquals(events, topic.getEvents());      
  }
}
