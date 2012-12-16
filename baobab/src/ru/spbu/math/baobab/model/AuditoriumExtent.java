package ru.spbu.math.baobab.model;

import java.util.Collection;

/**
 * AuditoriumExtent is a set of all available auditoria. One can create a new auditorium
 * or find an existing one by its ID.
 * 
 * @author dbarashev
 */
public interface AuditoriumExtent {
  /**
   * Creates a new auditorium with the specified ID and capacity. Throws a RuntimeException 
   * if auditorium with the given ID already exists.
   * 
   * @param id auditorium ID
   * @param capacity non-negative capacity
   * @return auditorium instance
   */
  Auditorium create(String id, int capacity);
  
  /**
   * @return collection of all available auditoria
   */
  Collection<Auditorium> getAll();
  
  /**
   * Finds auditorium by ID
   * @param id auditorium to search for
   * @return auditorium instance or {@code null} if such auditorium does not exist
   */
  Auditorium find(String id);
}
