package ru.spbu.math.baobab.model;

import java.util.Collection;

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
   * @param name time slot name
   * @return time slot with the given name or {@code null} if no such time slot exists
   */
  TimeSlot findByName(String name);
  
  /**
   * Creates a new time slot with the given parameters. Throws a runtime exception if time slot
   * with the specified name already exists. 
   * 
   * @param name time slot name
   * @param start time slot start instant
   * @param finish time slot finish instant
   * @return newly created time slot
   */
  TimeSlot create(String name, TimeInstant start, TimeInstant finish);
}
