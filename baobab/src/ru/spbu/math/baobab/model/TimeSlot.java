package ru.spbu.math.baobab.model;

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
   * @return time slot start time instant
   */
  TimeInstant getStart();

  /**
   * @return time slot finish time instant
   */
  TimeInstant getFinish();
}
