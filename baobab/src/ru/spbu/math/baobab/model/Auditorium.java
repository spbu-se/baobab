package ru.spbu.math.baobab.model;

/**
 * Auditorium is a physical room which is able to accommodate a certain number of 
 * people. Auditorium has an ID which is human-distinguishable room identifier (e.g. "hall 03" or "405").
 * 
 * @author dbarashev
 */
public interface Auditorium {
  /**
   * @return auditorium ID
   */
  String getID();
  
  /**
   * @return non-negative auditorium capacity; 0 if capacity is not yet specified 
   */
  int getCapacity();
}
