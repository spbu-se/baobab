package ru.spbu.math.baobab.model;

import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import junit.framework.TestCase;

import org.junit.Test;

import com.google.common.collect.Lists;

import ru.spbu.math.baobab.model.TimeSlot.Utils;
import ru.spbu.math.baobab.server.TimeSlotExtentImpl;

/**
 * Test case for TimeSlot utils
 * 
 * @author agudulin
 */
public class TimeSlotUtilsTest extends TestCase {

  @Test
  public void testDatesRange() {
    Calendar cal = Calendar.getInstance(new Locale("ru", "RU"));
    cal.set(2012, Calendar.JANUARY, 1);
    Date startDate = cal.getTime();
    cal.set(2012, Calendar.JANUARY, 7);
    Date finishDate = cal.getTime();

    List<Date> dates = Lists.newArrayList(Utils.datesRange(startDate, finishDate));
    List<Date> datesSorted = Lists.newArrayList(dates);
    Collections.sort(datesSorted);
    assertEquals(datesSorted, dates);

    assertEquals(dates.size(), 7);

    List<Date> datesToTest = Lists.newArrayList();
    for (int i = 1; i <= dates.size(); ++i) {
      cal.set(2012, Calendar.JANUARY, i);
      datesToTest.add(cal.getTime());
    }

    assertEquals(dates, datesToTest);
  }

  @Test
  public void testGetFilteredRangeOfDates() {
    Calendar cal = Calendar.getInstance(new Locale("ru", "RU"));
    cal.set(2012, Calendar.JANUARY, 1);
    Date startDate = cal.getTime();
    cal.set(2012, Calendar.DECEMBER, 31);
    Date finishDate = cal.getTime();

    // Get series of dates: <Sun, 1 Jan 2012> -- <Mon, 31 Dec 2012>
    Collection<Date> dates = Utils.datesRange(startDate, finishDate);

    TimeInstant start = new TimeInstant(9, 30);
    TimeInstant finish = new TimeInstant(11, 5);
    TimeSlotExtent tsExtent = new TimeSlotExtentImpl();

    // Create time slot: every week, every Monday, 9:30 - 11:05
    TimeSlot timeSlot = tsExtent.create("1", start, finish, 1, EvenOddWeek.ALL);
    List<Date> filteredDates = Lists.newArrayList(Utils.getFilteredRangeOfDates(dates, timeSlot));
    assertEquals(filteredDates.size(), 53);
    // In filtered range of dates:
    //    First day is Mon, 02 Jan 2012
    //    Last day is  Mon, 31 Dec 2012
    cal.set(2012, Calendar.JANUARY, 2);
    Date startTimeSlotDate = cal.getTime();
    Date finishTimeSlotDate = finishDate;
    assertEquals(filteredDates.get(0), startTimeSlotDate);
    assertEquals(filteredDates.get(filteredDates.size() - 1), finishTimeSlotDate);

    // Create time slot: every even week, every Tuesday, 9:30 - 11:05
    timeSlot = tsExtent.create("2", start, finish, 2, EvenOddWeek.EVEN);
    filteredDates = Lists.newArrayList(Utils.getFilteredRangeOfDates(dates, timeSlot));
    assertEquals(filteredDates.size(), 26);
    // In filtered range of dates:
    //    First day is Tue, 03 Jan 2012
    //    Last day is  Tue, 18 Dec 2012
    cal.set(2012, Calendar.JANUARY, 3);
    startTimeSlotDate = cal.getTime();
    cal.set(2012, Calendar.DECEMBER, 18);
    finishTimeSlotDate = cal.getTime();
    assertEquals(filteredDates.get(0), startTimeSlotDate);
    assertEquals(filteredDates.get(filteredDates.size() - 1), finishTimeSlotDate);

    // Create time slot: every odd week, every Sunday, 9:30 - 11:05
    timeSlot = tsExtent.create("3", start, finish, 7, EvenOddWeek.ODD);
    filteredDates = Lists.newArrayList(Utils.getFilteredRangeOfDates(dates, timeSlot));
    assertEquals(filteredDates.size(), 27);
    // In filtered range of dates:
    //    First day is Sun, 01 Jan 2012
    //    Last day is  Sun, 30 Dec 2012
    cal.set(2012, Calendar.JANUARY, 1);
    startTimeSlotDate = cal.getTime();
    cal.set(2012, Calendar.DECEMBER, 30);
    finishTimeSlotDate = cal.getTime();
    assertEquals(filteredDates.get(0), startTimeSlotDate);
    assertEquals(filteredDates.get(filteredDates.size() - 1), finishTimeSlotDate);
  }
}
