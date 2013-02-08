package ru.spbu.math.baobab.model;

import java.util.Collection;

/**
 * CalendarExtent is a set of all available calendars. One can create a new calendar
 * or find an existing one by its ID.
 * 
 * @author dbarashev
 */
public interface CalendarExtent {
  /**
   * Creates a new calendar with the specified ID. 
   * Throws RuntimeException is such calendar already exists.
   * 
   * @param uid calendar id
   * @return new calendar instance
   */
  Calendar create(String uid);
  
  
  /**
   * Finds calendar by its id 
   * 
   * @param uid calendar id
   * @return existing calendar instance or {@code null} if there is no such calendar 
   */
  Calendar find(String uid);

  /**
   * @return all defined calendars
   */
  Collection<Calendar> getAll();
}
