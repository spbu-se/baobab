package ru.spbu.math.baobab.server;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import com.google.common.collect.Lists;
import ru.spbu.math.baobab.model.EvenOddWeek;
import ru.spbu.math.baobab.model.TimeInstant;
import ru.spbu.math.baobab.model.TimeSlot;
import ru.spbu.math.baobab.model.TimeSlotExtent;
import junit.framework.TestCase;

/**
 * Some Tests for TimeSlotExtent
 * 
 * @author dageev
 */
public class TimeSlotExtentImplTest extends TestCase {

  public void testGetAll() {
    TimeInstant start = new TimeInstant(9, 30);
    TimeInstant finish = new TimeInstant(11, 5);
    TimeInstant start1 = new TimeInstant(13, 40);
    TimeInstant finish1 = new TimeInstant(15, 15);
    TimeSlotExtent timeSlotExtent = new TimeSlotExtentImpl();
    TimeSlot ts1 = timeSlotExtent.create("first double class", start, finish, 2, EvenOddWeek.ALL);
    TimeSlot ts2 = timeSlotExtent.create("second double class", start1, finish1, 2, EvenOddWeek.ALL);
    TimeSlot ts3 = timeSlotExtent.create("third double class", start1, finish1, 2, EvenOddWeek.ALL);
    Collection<TimeSlot> timeSlots = Lists.newArrayList();
    timeSlots.add(ts1);
    timeSlots.add(ts2);
    timeSlots.add(ts3);
    assertEquals(timeSlots, timeSlotExtent.getAll());
  }

  public void testCreate() {
    TimeSlotExtent timeSlotExtent = new TimeSlotExtentImpl();
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

  public void testFindByWeekDay() {
    TimeSlotExtent timeSlotExtent = new TimeSlotExtentImpl();
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

  public void testFindByDate() {
    TimeSlotExtent timeSlotExtent = new TimeSlotExtentImpl();
    TimeInstant start = new TimeInstant(9, 30);
    TimeInstant finish = new TimeInstant(11, 5);
    TimeInstant start1 = new TimeInstant(13, 40);
    TimeInstant finish1 = new TimeInstant(15, 15);
    Calendar c = new GregorianCalendar();
    c.set(Calendar.WEEK_OF_YEAR, 1);
    c.set(Calendar.DAY_OF_WEEK, 4);
    Date date1 = c.getTime();
    c.set(Calendar.WEEK_OF_YEAR, 3);
    Date date2 = c.getTime();
    timeSlotExtent.create("first double class", start1, finish1, 4, EvenOddWeek.ALL);
    timeSlotExtent.create("second double class", start, finish, 4, EvenOddWeek.EVEN);
    timeSlotExtent.create("third double class", start1, finish1, 4, EvenOddWeek.ODD);
    List<TimeSlot> list = timeSlotExtent.findByDate(date1);
    List<TimeSlot> list1 = timeSlotExtent.findByDate(date2);
    assertEquals(list, list1);
    c.set(Calendar.WEEK_OF_YEAR, 2);
    date1 = c.getTime();
    list1 = timeSlotExtent.findByDate(date1);
    assertFalse(list.equals(list1));
  }
}