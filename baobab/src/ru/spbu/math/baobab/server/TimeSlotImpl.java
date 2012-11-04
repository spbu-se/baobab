package ru.spbu.math.baobab.server;

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
    final int prime = 31;
    int result = 1;
    result = prime * result + myDay;
    result = prime * result + ((myFinish == null) ? 0 : myFinish.hashCode());
    result = prime * result + ((myFlashing == null) ? 0 : myFlashing.hashCode());
    result = prime * result + ((myName == null) ? 0 : myName.hashCode());
    result = prime * result + ((myStart == null) ? 0 : myStart.hashCode());
    result = prime * result + ((myTimeSlotExtent == null) ? 0 : myTimeSlotExtent.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    TimeSlotImpl other = (TimeSlotImpl) obj;
    if (myDay != other.myDay)
      return false;
    if (myFinish == null) {
      if (other.myFinish != null)
        return false;
    } else if (!myFinish.equals(other.myFinish))
      return false;
    if (myFlashing != other.myFlashing)
      return false;
    if (myName == null) {
      if (other.myName != null)
        return false;
    } else if (!myName.equals(other.myName))
      return false;
    if (myStart == null) {
      if (other.myStart != null)
        return false;
    } else if (!myStart.equals(other.myStart))
      return false;
    if (myTimeSlotExtent == null) {
      if (other.myTimeSlotExtent != null)
        return false;
    } else if (!myTimeSlotExtent.equals(other.myTimeSlotExtent))
      return false;
    return true;
  }
}
