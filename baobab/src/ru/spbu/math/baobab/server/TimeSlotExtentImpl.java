package ru.spbu.math.baobab.server;

import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import com.google.common.collect.Lists;

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
  private final Collection<TimeSlot> myTimeSlots = Lists.newArrayList();

  @Override
  public Collection<TimeSlot> getAll() {
    return myTimeSlots;
  }

  @Override
  public List<TimeSlot> findByWeekDay(int day) {
    List<TimeSlot> list = Lists.newArrayList();
    for (TimeSlot ts : myTimeSlots) {
      if (ts.getDayOfWeek() == day) {
        list.add(ts);
      }
    }
    Collections.sort(list, DATE_COMPARATOR);
    return list;
  }

  @Override
  public List<TimeSlot> findByDate(Date date) {
    List<TimeSlot> list = Lists.newArrayList();
    Calendar calendar = GregorianCalendar.getInstance();
    calendar.setTime(date);
    int day = calendar.get(Calendar.DAY_OF_WEEK);
    boolean isEven = false;
    if (calendar.get(Calendar.WEEK_OF_YEAR) % 2 == 1) {
      isEven = true;
    }
    for (TimeSlot ts : myTimeSlots) {
      if (ts.getDayOfWeek() == day) {
        boolean add = true;
        if (ts.getEvenOddWeek() == EvenOddWeek.ODD && !isEven) {
          add = false;
        }
        if (ts.getEvenOddWeek() == EvenOddWeek.EVEN && isEven) {
          add = false;
        }
        if (add) {
          list.add(ts);
        }
      }
    }
    Collections.sort(list, DATE_COMPARATOR);
    return list;
  }

  @Override
  public TimeSlot create(String id, String name, TimeInstant start, TimeInstant finish, int day, EvenOddWeek flashing) {
    for (TimeSlot ts : myTimeSlots) {
      if (ts.getName().equals(name)) {
        throw new IllegalStateException("The TimeSlot with this name is already exist");
      }
      if (ts.getID().equals(id)) {
        throw new IllegalStateException("The TimeSlot with this ID is already exist");
      }
    }
    TimeSlot timeslot = new TimeSlotImpl(id, name, start, finish, day, flashing, this);
    myTimeSlots.add(timeslot);
    return timeslot;

  }

  private static Comparator<TimeSlot> DATE_COMPARATOR = new Comparator<TimeSlot>() {
    @Override
    public int compare(TimeSlot o1, TimeSlot o2) {
      TimeInstant timeStart1 = o1.getStart();
      TimeInstant timeStart2 = o2.getStart();
      int val1 = timeStart1.getHour() * 60 + timeStart1.getMinute();
      int val2 = timeStart2.getHour() * 60 + timeStart2.getMinute();
      return (val1 - val2);
    }
  };

  @Override
  public TimeSlot findById(String id) {
    for (TimeSlot ts : myTimeSlots) {
      if (ts.getID().equals(id)) {
        return ts;
      }
    }
    return null;
  }
}
