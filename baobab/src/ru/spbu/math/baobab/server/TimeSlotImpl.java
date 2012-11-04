package ru.spbu.math.baobab.server;

import com.google.common.base.Objects;

import ru.spbu.math.baobab.model.EvenOddWeek;
import ru.spbu.math.baobab.model.TimeInstant;
import ru.spbu.math.baobab.model.TimeSlot;
import ru.spbu.math.baobab.model.TimeSlotExtent;

/**
 * Implementation of TimeSlot interface
 * 
 * @author dageev
 */
public class TimeSlotImpl implements TimeSlot {
  private final String myName;
  private final int myDay;
  private final EvenOddWeek myFlashing;
  private final TimeInstant myStart;
  private final TimeInstant myFinish;
  private final TimeSlotExtent myTimeSlotExtent;

  public TimeSlotImpl(String name, TimeInstant start, TimeInstant finish, int day, EvenOddWeek flashing,
      TimeSlotExtent timeslotextent) {
    myTimeSlotExtent = timeslotextent;
    myName = name;
    myDay = day;
    myFlashing = flashing;
    myStart = start;
    myFinish = finish;
  }

  @Override
  public String getName() {
    return myName;
  }

  @Override
  public int getDayOfWeek() {
    return myDay;
  }

  @Override
  public EvenOddWeek getEvenOddWeek() {
    return myFlashing;
  }

  @Override
  public TimeInstant getStart() {
    return myStart;
  }

  @Override
  public TimeInstant getFinish() {
    return myFinish;
  }

  @Override
  public TimeSlotExtent getExtent() {
    return myTimeSlotExtent;
  }

  @Override
  public int hashCode() {
    return com.google.common.base.Objects.hashCode(myDay, myFinish, myFlashing, myName, myStart, myTimeSlotExtent);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }

    TimeSlotImpl other = (TimeSlotImpl) obj;
    return com.google.common.base.Objects.equal(myDay, other.myDay)
        && com.google.common.base.Objects.equal(myFinish, other.myFinish)
        && com.google.common.base.Objects.equal(myFlashing, other.myFlashing)
        && com.google.common.base.Objects.equal(myName, other.myName)
        && com.google.common.base.Objects.equal(myStart, other.myStart)
        && com.google.common.base.Objects.equal(myTimeSlotExtent, other.myTimeSlotExtent);
  }
}
