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
import ru.spbu.math.baobab.model.EvenOddWeek;
import ru.spbu.math.baobab.model.TimeInstant;
import ru.spbu.math.baobab.model.TimeSlot;
import ru.spbu.math.baobab.model.TimeSlotExtent;
import ru.spbu.math.baobab.model.Topic;
import ru.spbu.math.baobab.model.TopicExtent;
import ru.spbu.math.baobab.server.AuditoriumExtentImpl;
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
    TimeSlotExtent timeSlotExtent = new TimeSlotExtentSqlImpl();
    TimeInstant start = new TimeInstant(9, 30);
    TimeInstant finish = new TimeInstant(11, 5);
    expectSql("SELECT TimeSlot WHERE name day is_odd is_odd").withParameters(1, "first double class", 2, 2, 3,
        EvenOddWeek.ODD.ordinal(), 4, EvenOddWeek.ALL.ordinal());
    expectSql("INSERT TimeSlot name start_min finish_min day is_odd").withParameters(1, "first double class", 2,
        start.getDayMinute(), 3, finish.getDayMinute(), 4, 2, 5, EvenOddWeek.ODD.ordinal());
    expectSql("SELECT TimeSlot WHERE name day is_odd")
    .withParameters(1, "first double class", 2, 2, 3, EvenOddWeek.ODD.ordinal(), 4, EvenOddWeek.ALL.ordinal())
    .withResult(
        row(1, 1)
        );
    TimeSlot ts1 = timeSlotExtent.create("first double class", start, finish, 2, EvenOddWeek.ODD);
    
    AuditoriumExtent auditoriumExtent = new AuditoriumExtentImpl();
    Auditorium auditorium = auditoriumExtent.create("1", 20);
    
    TopicExtent topicExtent = new TopicExtentSqlImpl(timeSlotExtent, auditoriumExtent);
    expectSql("SELECT Topic WHERE uid").withParameters(1, "CS101-2012");
    expectSql("INSERT Topic uid name type").withParameters(1, "CS101-2012", 2,
        "Computer Science introduction course in year 2012", 3, Type.LECTURE_COURSE.ordinal());
    Topic topic = topicExtent.createTopic("CS101-2012", Type.LECTURE_COURSE,
        "Computer Science introduction course in year 2012");

    Calendar cal = Calendar.getInstance();
    Date date = cal.getTime();
    java.sql.Date sqlDate = new java.sql.Date(date.getTime());
     
    expectSql("INSERT Event SET date time_slot_id topic_id auditorium_num").withParameters(1, sqlDate , 2,
        ts1.getID(), 3, topic.getID(), 4, auditorium.getID());
    Event event = topic.addEvent(date, ts1, auditorium);
    Event event1 = new EventImpl(date, ts1, auditorium, topic);
    assertEquals(event, event1);
  }

  @Test
  public void testGetEvents() {
    TimeSlotExtent timeSlotExtent = new TimeSlotExtentSqlImpl();
    TimeInstant start = new TimeInstant(9, 30);
    TimeInstant finish = new TimeInstant(11, 5);
    expectSql("SELECT TimeSlot WHERE name day is_odd is_odd").withParameters(1, "first double class", 2, 2, 3,
        EvenOddWeek.ODD.ordinal(), 4, EvenOddWeek.ALL.ordinal());
    expectSql("INSERT TimeSlot name start_min finish_min day is_odd").withParameters(1, "first double class", 2,
        start.getDayMinute(), 3, finish.getDayMinute(), 4, 2, 5, EvenOddWeek.ODD.ordinal());
    expectSql("SELECT TimeSlot WHERE name day is_odd")
    .withParameters(1, "first double class", 2, 2, 3, EvenOddWeek.ODD.ordinal(), 4, EvenOddWeek.ALL.ordinal())
    .withResult(
        row(1, 1)
        );
    TimeSlot ts1 = timeSlotExtent.create("first double class", start, finish, 2, EvenOddWeek.ODD);
    
    AuditoriumExtent auditoriumExtent = new AuditoriumExtentImpl();
    Auditorium auditorium = auditoriumExtent.create("1", 20);
 
    TopicExtent topicExtent = new TopicExtentSqlImpl(timeSlotExtent, auditoriumExtent);
    expectSql("SELECT Topic WHERE uid").withParameters(1, "CS101-2012");
    expectSql("INSERT Topic uid name type").withParameters(1, "CS101-2012", 2,
        "Computer Science introduction course in year 2012", 3, Type.LECTURE_COURSE.ordinal());
    Topic topic = topicExtent.createTopic("CS101-2012", Type.LECTURE_COURSE,
        "Computer Science introduction course in year 2012");
    
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.DAY_OF_WEEK, 3);
    Date date = cal.getTime();
    java.sql.Date sqlDate = new java.sql.Date(date.getTime());
     
    expectSql("INSERT Event SET date time_slot_id topic_id auditorium_num").withParameters(1, sqlDate , 2,
        ts1.getID(), 3, topic.getID(), 4, auditorium.getID());
    topic.addEvent(date, ts1, auditorium);
    
    cal.set(Calendar.DAY_OF_WEEK, 5);
    Date date1 = cal.getTime();
    java.sql.Date sqlDate1 = new java.sql.Date(date1.getTime());
     
    expectSql("INSERT Event SET date time_slot_id topic_id auditorium_num").withParameters(1, sqlDate1 , 2,
        ts1.getID(), 3, topic.getID(), 4, auditorium.getID());
    topic.addEvent(date1, ts1, auditorium);
    Event event = new EventImpl(date, ts1, auditorium, topic);
    Event event1 = new EventImpl(date1, ts1, auditorium, topic);
    List<Event> events = Arrays.asList(event, event1);
    expectSql("SELECT Event WHERE topic_id")
    .withParameters(1, topic.getID())
    .withResult(row(
                 "date", sqlDate,
                 "timeslot_id", ts1.getID(),
                 "auditorium_num", auditorium.getID()
                ),
                row(
                 "date", sqlDate1,
                 "timeslot_id", ts1.getID(),
                 "auditorium_num", auditorium.getID()             
                ));
    expectSql("SELECT TimeSlot WHERE id")
    .withParameters(1, ts1.getID())
    .withResult(
            row("name", ts1.getName(), 
                "start_min", ts1.getStart().getDayMinute(), 
                "finish_min", ts1.getFinish().getDayMinute(), 
                "day", 2, 
                "is_odd", EvenOddWeek.ODD.ordinal())
           );
    expectSql("SELECT TimeSlot WHERE id")
    .withParameters(1, ts1.getID())
    .withResult(
            row("name", ts1.getName(), 
                "start_min", ts1.getStart().getDayMinute(), 
                "finish_min", ts1.getFinish().getDayMinute(), 
                "day", 2, 
                "is_odd", EvenOddWeek.ODD.ordinal())
           );
    List<Event> eventsFromDb = (List<Event>) topic.getEvents();
    assertEquals(events, eventsFromDb);      
  }
  
  @Test
  public void testAddAllEvents() {
    TimeSlotExtent timeSlotExtent = new TimeSlotExtentSqlImpl();
    TimeInstant start = new TimeInstant(9, 30);
    TimeInstant finish = new TimeInstant(11, 5);
    expectSql("SELECT TimeSlot WHERE name day is_odd is_odd").withParameters(1, "first double class", 2, 2, 3,
        EvenOddWeek.ODD.ordinal(), 4, EvenOddWeek.ALL.ordinal());
    expectSql("INSERT TimeSlot name start_min finish_min day is_odd").withParameters(1, "first double class", 2,
        start.getDayMinute(), 3, finish.getDayMinute(), 4, 2, 5, EvenOddWeek.ODD.ordinal());
    expectSql("SELECT TimeSlot WHERE name day is_odd")
    .withParameters(1, "first double class", 2, 2, 3, EvenOddWeek.ODD.ordinal(), 4, EvenOddWeek.ALL.ordinal())
    .withResult(
        row(1, 1)
        );
    TimeSlot ts = timeSlotExtent.create("first double class", start, finish, 2, EvenOddWeek.ODD);
    
    AuditoriumExtent auditoriumExtent = new AuditoriumExtentImpl();
    Auditorium auditorium = auditoriumExtent.create("1", 20);
    
    TopicExtent topicExtent = new TopicExtentSqlImpl(timeSlotExtent, auditoriumExtent);
    expectSql("SELECT Topic WHERE uid").withParameters(1, "CS101-2012");
    expectSql("INSERT Topic uid name type").withParameters(1, "CS101-2012", 2,
        "Computer Science introduction course in year 2012", 3, Type.LECTURE_COURSE.ordinal());
    Topic topic = topicExtent.createTopic("CS101-2012", Type.LECTURE_COURSE,
        "Computer Science introduction course in year 2012");

    Calendar cal = Calendar.getInstance(new Locale("ru", "RU"));
    cal.set(2012, Calendar.NOVEMBER, 11);
    Date startDate = cal.getTime();
    cal.set(2012, Calendar.DECEMBER, 3);
    Date finishDate = cal.getTime();
    expectInsert("INSERT INTO Event");
    expectInsert("INSERT INTO Event");

    Collection<Event> events = topic.addAllEvents(startDate, finishDate, ts, auditorium);
    assertEquals(events.size(), 2);
    
    for (Event event : events) {
      assertTrue(event.getStartDate().after(startDate));
      assertTrue(event.getStartDate().before(finishDate));
    }
  } 
}
