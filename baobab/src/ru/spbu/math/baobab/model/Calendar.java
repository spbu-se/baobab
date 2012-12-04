package ru.spbu.math.baobab.model;

import java.util.Collection;

/**
 * Calendar is just a collection of topics.
 *  
 * @author dbarashev
 */
public interface Calendar {
  /**
   * @return all topics in this calendar
   */
  Collection<Topic> getAllTopics();
  
  /**
   * @return this caledar ID
   */
  String getID();
}
