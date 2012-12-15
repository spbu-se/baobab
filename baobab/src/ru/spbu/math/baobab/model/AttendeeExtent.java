package ru.spbu.math.baobab.model;

import java.util.Collection;

/**
 * Attendee extent is a set of all available attendees.
 * 
 * @author dbarashev
 */
public interface AttendeeExtent {
  /**
   * Creates a new attendee
   * @param id attendee ID
   * @param name attendee name
   * @param type attendee type
   * @return new attendee instance
   * @throws IllegalArgumentException if attendee with the given ID already exists
   */
  Attendee create(String id, String name, Attendee.Type type);
  
  /**
   * Finds attendee by its ID
   * @param id id
   * @return found attendee or {@code null} if no attendee with such ID exists
   */
  Attendee find(String id);
  
  /**
   * @return collection of all available attendees
   */
  Collection<Attendee> getAll();
}
