package ru.spbu.math.baobab.model;

/**
 * Time instant is a moment of a day, with minute precision. It repeats every day.
 * E.g. 9:30 is a time instant.
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

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + myHour;
    result = prime * result + myMinute;
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
    TimeInstant other = (TimeInstant) obj;
    if (myHour != other.myHour)
      return false;
    if (myMinute != other.myMinute)
      return false;
    return true;
  }
}
