package ru.spbu.math.baobab.model;

import com.google.common.base.Objects;

/**
 * Time instant is a moment of a day, with minute precision. It repeats every day. E.g. 9:30 is a time instant.
 * 
 * @author dbarashev
 */
public class TimeInstant {
  private final int myHour;
  private final int myMinute;

  public TimeInstant(int hour, int minute) {
    assert hour >= 0 && hour <= 23 : "invalid hour value=" + hour;
    assert minute >= 0 && minute <= 59 : "invalid minute value=" + minute;
    myHour = hour;
    myMinute = minute;
  }

  public int getHour() {
    return myHour;
  }

  public int getMinute() {
    return myMinute;
  }

  public int getDayMinute() {
    return myHour * 60 + myMinute;
  }
  
  @Override
  public int hashCode() {
    return com.google.common.base.Objects.hashCode(myHour, myMinute);
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
    TimeInstant other = (TimeInstant) obj;
    return Objects.equal(myHour, other.myHour)
        && Objects.equal(myMinute, other.myMinute);
  }
}
