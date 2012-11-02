package ru.spbu.math.baobab.server.sql;

import static org.junit.Assert.*;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collection;
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
public class TimeSlotExtentSqlImplTest {
  static void deleteFromTimeSLot() throws SQLException {
    SqlApi sqlApi = new SqlApi();
    List<PreparedStatement> stmts = sqlApi.prepareScript("DELETE FROM TimeSlot;");
    stmts.get(0).execute();
  };

  @Test
  public void testGetAll() throws SQLException {
    deleteFromTimeSLot();

    TimeInstant start = new TimeInstant(9, 30);
    TimeInstant finish = new TimeInstant(11, 5);
    TimeInstant start1 = new TimeInstant(13, 40);
    TimeInstant finish1 = new TimeInstant(15, 15);

    TimeSlotExtent timeSlotExtentSql = new TimeSlotExtentSqlImpl();
    TimeSlot ts1 = timeSlotExtentSql.create("first double class", start, finish, 2, EvenOddWeek.ALL);
    TimeSlot ts2 = timeSlotExtentSql.create("second double class", start1, finish1, 2, EvenOddWeek.EVEN);
    TimeSlot ts3 = timeSlotExtentSql.create("third double class", start1, finish1, 2, EvenOddWeek.ODD);

    Collection<TimeSlot> timeSlots = Lists.newArrayList();
    timeSlots.add(ts1);
    timeSlots.add(ts2);
    timeSlots.add(ts3);

    Collection<TimeSlot> timeSlotsFromDb = timeSlotExtentSql.getAll();

    // XXX: can't understand why this test doesn't pass
    assertEquals(timeSlots, timeSlotsFromDb);
  }

  @Test
  public void testFindByWeekDay() throws SQLException {
    deleteFromTimeSLot();

    TimeSlotExtent timeSlotExtent = new TimeSlotExtentSqlImpl();

    TimeInstant start = new TimeInstant(9, 30);
    TimeInstant finish = new TimeInstant(11, 5);
    TimeInstant start1 = new TimeInstant(13, 40);
    TimeInstant finish1 = new TimeInstant(15, 15);

    timeSlotExtent.create("first double class", start, finish, 2, EvenOddWeek.ALL);
    timeSlotExtent.create("second double class", start, finish, 3, EvenOddWeek.ALL);
    timeSlotExtent.create("third double class", start1, finish1, 4, EvenOddWeek.ALL);
    timeSlotExtent.create("fourth double class", start, finish, 4, EvenOddWeek.ALL);

    List<TimeSlot> list = timeSlotExtent.findByWeekDay(4);

    assertEquals(list.get(0).getName(), "fourth double class");
    assertEquals(list.get(1).getName(), "third double class");
  }

  @Test
  public void testFindByDate() throws SQLException {
    deleteFromTimeSLot();

    TimeSlotExtent timeSlotExtent = new TimeSlotExtentSqlImpl();

    TimeInstant start = new TimeInstant(9, 30);
    TimeInstant finish = new TimeInstant(11, 5);
    TimeInstant start1 = new TimeInstant(13, 40);
    TimeInstant finish1 = new TimeInstant(15, 15);

    timeSlotExtent.create("first double class", start1, finish1, 4, EvenOddWeek.ALL);
    timeSlotExtent.create("second double class", start, finish, 4, EvenOddWeek.EVEN);
    timeSlotExtent.create("third double class", start1, finish1, 4, EvenOddWeek.ODD);

    Calendar calendar = new GregorianCalendar();
    calendar.set(Calendar.WEEK_OF_YEAR, 1);
    calendar.set(Calendar.DAY_OF_WEEK, 4);
    Date date1 = calendar.getTime();
    calendar.set(Calendar.WEEK_OF_YEAR, 3);
    Date date2 = calendar.getTime();

    List<TimeSlot> list = timeSlotExtent.findByDate(date1);
    List<TimeSlot> list1 = timeSlotExtent.findByDate(date2);

    for (int i = 0; i < list.size(); ++i) {
      assertEquals(list.get(i).getName(), list1.get(i).getName());
    }

    calendar.set(Calendar.WEEK_OF_YEAR, 2);
    list1 = timeSlotExtent.findByDate(calendar.getTime());

    for (int i = 0; i < list.size(); ++i) {
      assertFalse(list.get(i).getName().equals(list1.get(i).getName()));
    }
  }

  @Test
  public void testCreate() throws SQLException {
    deleteFromTimeSLot();

    TimeSlotExtent timeSlotExtent = new TimeSlotExtentSqlImpl();

    TimeInstant start = new TimeInstant(9, 30);
    TimeInstant finish = new TimeInstant(11, 5);
    timeSlotExtent.create("first double class", start, finish, 2, EvenOddWeek.ALL);

    TimeInstant start1 = new TimeInstant(13, 40);
    TimeInstant finish1 = new TimeInstant(15, 15);

    try {
      timeSlotExtent.create("first double class", start1, finish1, 5, EvenOddWeek.ODD);
      fail("Expected IllegalArgumentException");
    } catch (IllegalStateException e) {
      // can't create with existing name
    }
  }
}
