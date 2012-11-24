package ru.spbu.math.baobab.server.sql;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;

import ru.spbu.math.baobab.model.EvenOddWeek;
import ru.spbu.math.baobab.model.TimeInstant;
import ru.spbu.math.baobab.model.TimeSlot;
import ru.spbu.math.baobab.model.TimeSlotExtent;

/**
 * Some tests for TimeSlotExtentSqlImpl
 * 
 * @author agudulin
 */
public class TimeSlotExtentSqlImplTest extends SqlTestCase {
  @Test
  public void testGetAll() throws SQLException {
    TimeInstant start = new TimeInstant(9, 30);
    TimeInstant finish = new TimeInstant(11, 5);
    TimeInstant start1 = new TimeInstant(13, 40);
    TimeInstant finish1 = new TimeInstant(15, 15);

    expectQuery("SELECT * FROM TimeSlot");
    expectInsert("INSERT INTO TimeSlot");
    expectSql("SELECT TimeSlot WHERE name day is_odd")
    .withParameters(1, "first double class", 2, 2, 3, EvenOddWeek.ALL.ordinal())
    .withResult(
        row(1, 1)
        );
    expectQuery("SELECT * FROM TimeSlot");
    expectInsert("INSERT INTO TimeSlot");
    expectSql("SELECT TimeSlot WHERE name day is_odd")
    .withParameters(1, "second double class", 2, 2, 3, EvenOddWeek.EVEN.ordinal())
    .withResult(
        row(1, 2)
        );
    expectQuery("SELECT * FROM TimeSlot");
    expectInsert("INSERT INTO TimeSlot");
    expectSql("SELECT TimeSlot WHERE name day is_odd")
    .withParameters(1, "third double class", 2, 2, 3, EvenOddWeek.ODD.ordinal())
    .withResult(
        row(1, 3)
        );
    TimeSlotExtent timeSlotExtentSql = new TimeSlotExtentSqlImpl();
    TimeSlot ts1 = timeSlotExtentSql.create("first double class", start, finish, 2, EvenOddWeek.ALL);
    TimeSlot ts2 = timeSlotExtentSql.create("second double class", start1, finish1, 2, EvenOddWeek.EVEN);
    TimeSlot ts3 = timeSlotExtentSql.create("third double class", start1, finish1, 2, EvenOddWeek.ODD);

    List<TimeSlot> timeSlots = Lists.newArrayList();
    timeSlots.add(ts1);
    timeSlots.add(ts2);
    timeSlots.add(ts3);

    expectQuery("SELECT * FROM TimeSlot", 
        row(
            "name", "first double class", 
            "start_min", ts1.getStart().getDayMinute(), 
            "finish_min", ts1.getFinish().getDayMinute(), 
            "day", ts1.getDayOfWeek(), 
            "is_odd", ts1.getEvenOddWeek().ordinal()),
        row(
            "name", ts2.getName(), 
            "start_min", ts2.getStart().getDayMinute(), 
            "finish_min", ts2.getFinish().getDayMinute(), 
            "day", ts2.getDayOfWeek(), 
            "is_odd", ts2.getEvenOddWeek().ordinal()),
        row(
            "name", ts3.getName(), 
            "start_min", ts3.getStart().getDayMinute(), 
            "finish_min", ts3.getFinish().getDayMinute(), 
            "day", ts3.getDayOfWeek(), 
            "is_odd", ts3.getEvenOddWeek().ordinal())
    );
    List<TimeSlot> timeSlotsFromDb = (List<TimeSlot>) timeSlotExtentSql.getAll();

    assertEquals(timeSlots, timeSlotsFromDb);
  }

  @Test
  public void testFindByWeekDay() throws SQLException {
    TimeSlotExtent timeSlotExtent = new TimeSlotExtentSqlImpl();

    TimeInstant start = new TimeInstant(9, 30);
    TimeInstant finish = new TimeInstant(11, 5);
    TimeInstant start1 = new TimeInstant(13, 40);
    TimeInstant finish1 = new TimeInstant(15, 15);

    expectSql("SELECT TimeSlot WHERE day ORDER BY start_min")
        .withParameters(1, 4)
        .withResult(
            row("name", "fourth double class", 
                "start_min", start.getDayMinute(), 
                "finish_min", finish.getDayMinute(), 
                "day", 4, 
                "is_odd", EvenOddWeek.ALL.ordinal()),
            row("name", "third double class", 
                "start_min", start1.getDayMinute(), 
                "finish_min", finish1.getDayMinute(), 
                "day", 4, 
                "is_odd", EvenOddWeek.ALL.ordinal()));
    List<TimeSlot> list = timeSlotExtent.findByWeekDay(4);

    assertEquals("fourth double class", list.get(0).getName());
    assertEquals("third double class", list.get(1).getName());
  }

  @Test
  public void testFindByDate() throws SQLException {
    TimeSlotExtent timeSlotExtent = new TimeSlotExtentSqlImpl();

    TimeInstant start = new TimeInstant(9, 30);
    TimeInstant finish = new TimeInstant(11, 5);
    TimeInstant start1 = new TimeInstant(13, 40);
    TimeInstant finish1 = new TimeInstant(15, 15);

//    timeSlotExtent.create("first double class", start1, finish1, 4, EvenOddWeek.ALL);
//    timeSlotExtent.create("second double class", start, finish, 4, EvenOddWeek.EVEN);
//    timeSlotExtent.create("third double class", start1, finish1, 4, EvenOddWeek.ODD);

    Calendar calendar = new GregorianCalendar();
    calendar.set(Calendar.WEEK_OF_YEAR, 1);
    calendar.set(Calendar.DAY_OF_WEEK, 4);
    Date date1 = calendar.getTime();
    calendar.set(Calendar.WEEK_OF_YEAR, 3);
    Date date2 = calendar.getTime();

    expectSql("SELECT TimeSlot WHERE day is_odd ORDER BY start_min")
        .withParameters(1, 4, 2, EvenOddWeek.ODD.ordinal())
        .withResult(
            row("name", "third double class", 
                "start_min", start1.getDayMinute(), 
                "finish_min", finish1.getDayMinute(), 
                "day", 4, 
                "is_odd", EvenOddWeek.ALL.ordinal()));
    List<TimeSlot> list = timeSlotExtent.findByDate(date1);
    
    expectSql("SELECT TimeSlot WHERE day is_odd ORDER BY start_min")
        .withParameters(1, 4, 2, EvenOddWeek.ODD.ordinal())
        .withResult(
            row("name", "third double class", 
                "start_min", start1.getDayMinute(), 
                "finish_min", finish1.getDayMinute(), 
                "day", 4, 
                "is_odd", EvenOddWeek.ALL.ordinal()));    
    List<TimeSlot> list1 = timeSlotExtent.findByDate(date2);

    for (int i = 0; i < list.size(); ++i) {
      assertEquals(list.get(i).getName(), list1.get(i).getName());
    }

    calendar.set(Calendar.WEEK_OF_YEAR, 2);
    expectSql("SELECT TimeSlot WHERE day is_odd ORDER BY start_min")
        .withParameters(1, 4, 2, EvenOddWeek.EVEN.ordinal())
        .withResult(
            row("name", "second double class", 
                "start_min", start1.getDayMinute(), 
                "finish_min", finish1.getDayMinute(), 
                "day", 4, 
                "is_odd", EvenOddWeek.ALL.ordinal()));    
    list1 = timeSlotExtent.findByDate(calendar.getTime());

    for (int i = 0; i < list.size(); ++i) {
      assertFalse(list.get(i).getName().equals(list1.get(i).getName()));
    }
  }

  @Test
  public void testCreate() throws SQLException {
    TimeSlotExtent timeSlotExtent = new TimeSlotExtentSqlImpl();

    TimeInstant start = new TimeInstant(9, 30);
    TimeInstant finish = new TimeInstant(11, 5);
    
    expectSql("SELECT TimeSlot WHERE name day is_odd is_odd")
        .withParameters(1, "first double class", 2, 2, 3, EvenOddWeek.ODD.ordinal(), 4, EvenOddWeek.ALL.ordinal());
    expectSql("INSERT TimeSlot name start_min finish_min day is_odd")
        .withParameters(1, "first double class", 2, start.getDayMinute(), 3, finish.getDayMinute(), 4, 2, 5, EvenOddWeek.ODD.ordinal());
    expectSql("SELECT TimeSlot WHERE name day is_odd")
    .withParameters(1, "first double class", 2, 2, 3, EvenOddWeek.ODD.ordinal(), 4, EvenOddWeek.ALL.ordinal())
    .withResult(
        row(1, 1)
        );
    timeSlotExtent.create("first double class", start, finish, 2, EvenOddWeek.ODD);

    TimeInstant start1 = new TimeInstant(13, 40);
    TimeInstant finish1 = new TimeInstant(15, 15);

    try {
      expectSql("SELECT TimeSlot WHERE name day is_odd is_odd")
          .withParameters(1, "first double class", 2, 2, 3, EvenOddWeek.ODD.ordinal(), 4, EvenOddWeek.ALL.ordinal())
          .withResult(row(
              "name", "first double class", 
              "start_min", start.getDayMinute(), 
              "finish_min", finish.getDayMinute(), 
              "day", 2, 
              "is_odd", EvenOddWeek.ODD.ordinal()));
      timeSlotExtent.create("first double class", start1, finish1, 2, EvenOddWeek.ODD);
      fail("Expected IllegalArgumentException");
    } catch (IllegalStateException e) {
      // can't create with existing name
    }
  }
  
  @Test
  public void testFindId() throws SQLException {
    TimeSlotExtent timeSlotExtent = new TimeSlotExtentSqlImpl();

    TimeInstant start = new TimeInstant(9, 30);
    TimeInstant finish = new TimeInstant(11, 5);
    TimeInstant start1 = new TimeInstant(13, 40);
    TimeInstant finish1 = new TimeInstant(15, 15);

    expectSql("SELECT TimeSlot WHERE id")
        .withParameters(1, 1)
        .withResult(
            row("name", "fourth double class", 
                "start_min", start.getDayMinute(), 
                "finish_min", finish.getDayMinute(), 
                "day", 4, 
                "is_odd", EvenOddWeek.ALL.ordinal()));
    TimeSlot ts = timeSlotExtent.findById(1);
    assertEquals("fourth double class", ts.getName());
  }
}