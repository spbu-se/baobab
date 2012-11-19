package ru.spbu.math.baobab.model;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Time slot population is a set of all available time slots. One can create a new time slot,
 * get all available time slots or find a time slot by its name.
 * 
 * @author dbarashev
 */
public interface TimeSlotExtent {
  /**
   * @return collection of all available time slots
   */
  Collection<TimeSlot> getAll();
  
  /**
   * Searches for all time slots defined for the given week day. Names or flashing modes do not matter. 
   * Returned list is ordered by time slot start moments in ascending order.   
   * 
   * @param day week day in [1..7] range
   * @return all applicable time slots
   */
  List<TimeSlot> findByWeekDay(int day);
  
  /**
   * Searches for all time slots defined for the given date, taking into account flashing mode.
   * Returned list is ordered by time slot start moments in ascending order.   
   * 
   * @param date calendar date
   * @return all applicable time slots
   */
  List<TimeSlot> findByDate(Date date);
  
  /**
   * Creates a new time slot with the given parameters. Throws a runtime exception if time slot
   * with the specified name already exists. 
   * 
   * @param name time slot name
   * @param start time slot start instant
   * @param finish time slot finish instant
   * @param day week day in [1..7] range
   * @param flashing flashing mode
   * @return newly created time slot
   */
  TimeSlot create(String id, String name, TimeInstant start, TimeInstant finish, int day, EvenOddWeek flashing);
  
  /**
   * Finds time slot by its ID
   * @param id id
   * @return found time slot or {@code null} if no time slot with such ID exists
   */
  TimeSlot findById(String id);
}
