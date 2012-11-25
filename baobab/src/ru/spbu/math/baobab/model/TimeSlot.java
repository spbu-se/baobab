package ru.spbu.math.baobab.model;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

/**
 * TimeSlot is a predefined named time interval. Most of the events will take place at one of 
 * a handful of time slots (e.g. on "first double class", "lunch break", etc.)
 * 
 * @author dbarashev
 */
public interface TimeSlot {
  /**
   * @return time slot name
   */
  String getName();

  /**
   * @return day of week to which the slot applies
   */
  int getDayOfWeek();

  /**
   * @return whether slot applies to both / odd / even week
   */
  EvenOddWeek getEvenOddWeek();
  
  /**
   * @return time slot start time instant
   */
  TimeInstant getStart();

  /**
   * @return time slot finish time instant
   */
  TimeInstant getFinish();
  
  /**
   * @return extent associated with this time slot
   */
  TimeSlotExtent getExtent();
  
  /**
   * @return time slot id
   */
  int getID();
  
  /**
   * A set of tools to work with time slots
   * 
   * @author agudulin
   */
  public class Utils {
    /**
     * Get ordered set of dates
     * 
     * @param start start date
     * @param finish stop date
     * @return ordered dates range
     */
    public static Collection<Date> datesRange(Date start, Date finish) {
      Collection<Date> dates = Lists.newArrayList();
      Calendar calStart = Calendar.getInstance(new Locale("ru", "RU"));
      Calendar calFinish = (Calendar)calStart.clone();

      calStart.setTime(start);
      calFinish.setTime(finish);
      for (; !calStart.after(calFinish); calStart.add(Calendar.DATE, 1)) {
        dates.add(calStart.getTime());
      }

      return dates;
    }

    /**
     * Get series of dates whenever time slot is applicable from the range of dates
     * 
     * @param dates series of dates
     * @param timeSlot time slot
     * @return series of dates whenever time slot is applicable
     */
    public static Collection<Date> getFilteredRangeOfDates(Collection<Date> dates, final TimeSlot timeSlot) {
      final int day = timeSlot.getDayOfWeek();
      final boolean isEven = timeSlot.getEvenOddWeek().equals(EvenOddWeek.EVEN);
      final boolean isAll = timeSlot.getEvenOddWeek().equals(EvenOddWeek.ALL);
      final Calendar cal = Calendar.getInstance(new Locale("ru", "RU"));

      return Collections2.filter(dates, new Predicate<Date>() {

        @Override
        public boolean apply(Date date) {
          cal.setTime(date);

          int weekDay = (cal.get(Calendar.DAY_OF_WEEK) + 7 - cal.getFirstDayOfWeek()) % 7 + 1;
          if ((weekDay == day) &&
              (isAll ||
              isEven && cal.get(Calendar.WEEK_OF_YEAR) % 2 == 0 ||
              !isEven && cal.get(Calendar.WEEK_OF_YEAR) % 2 == 1)) {
            return true;
          }
          return false;
        }
      });
    }
  }
}
