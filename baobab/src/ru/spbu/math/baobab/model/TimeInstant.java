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
    myHour = hour;
    myMinute = minute;
  }
  
  public int getHour() {
    return myHour;
  }
  
  public int getMinute() {
    return myMinute;
  }
}
