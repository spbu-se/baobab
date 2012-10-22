package ru.spbu.math.baobab.server;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import ru.spbu.math.baobab.model.EvenOddWeek;
import ru.spbu.math.baobab.model.TimeInstant;
import ru.spbu.math.baobab.model.TimeSlot;
import ru.spbu.math.baobab.model.TimeSlotExtent;
/**
 * Implementation of TimeSlotExtent interface
 * 
 * @author dageev
 */
public class TimeSlotExtentImpl implements TimeSlotExtent {
  private final Collection<TimeSlot> myTimeSlot = com.google.common.collect.Lists
      .newArrayList();

  @Override
  public Collection<TimeSlot> getAll() {
    return myTimeSlot;
  }

  @Override
  public List<TimeSlot> findByWeekDay(int day) {
    List<TimeSlot> list = new ArrayList<TimeSlot>();
    for (TimeSlot ts : myTimeSlot) {
      if (ts.getDayOfWeek() == day)
        list.add(ts);
    }
    Collections.sort(list, DateComparator);
    return list;
  }

  @Override
  public List<TimeSlot> findByDate(Date date){
    List<TimeSlot> list = new ArrayList<TimeSlot>();
    Calendar calendar = GregorianCalendar.getInstance();
    calendar.setTime(date);
    int day = calendar.get(Calendar.DAY_OF_WEEK);
    int evenorodd = calendar.get(Calendar.WEEK_OF_YEAR) / 2;
    for (TimeSlot ts : myTimeSlot) {
      if (ts.getDayOfWeek() == day) {
        boolean add = true;
        if (ts.getEvenOddWeek() == EvenOddWeek.ODD && evenorodd == 1) {
          add = false;
        }

        if (ts.getEvenOddWeek() == EvenOddWeek.EVEN && evenorodd == 0) {
          add = false;
        }
        if (add) {
          list.add(ts);
        }
      }
    }
    Collections.sort(list, DateComparator);
    return list;
  }

  @Override
  public TimeSlot create(String name, TimeInstant start, TimeInstant finish,
      int day, EvenOddWeek flashing) {
    boolean err = false;
    for (TimeSlot ts : myTimeSlot) {
      if (ts.getName() == name)
        err = true;
    }
    if (err)
      throw new IllegalStateException(
          "The TimeSlot with this name is already exist");
    TimeSlot timeslot = new TimeSlotImpl(name, start, finish, day, flashing,
        this);
    myTimeSlot.add(timeslot);
    return timeslot;

  }

  private static Comparator<TimeSlot> DateComparator = new Comparator<TimeSlot>() {
    public int compare(TimeSlot o1, TimeSlot o2) {
      TimeInstant timeStart1 = o1.getStart();
      TimeInstant timeStart2 = o2.getStart();
      int val1 = timeStart1.getHour() * 60 + timeStart1.getMinute();
      int val2 = timeStart2.getHour() * 60 + timeStart2.getMinute();
      return (val1 - val2);
    }

  };

}
