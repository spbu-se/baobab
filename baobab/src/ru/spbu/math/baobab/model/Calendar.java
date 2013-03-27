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
   * Adds a topic to this calendar. Does nothing if topic is already there
   * 
   * @param topic topic instance
   */
  void addTopic(Topic topic);
  
  /**
   * @return this caledar ID
   */
  String getID();
}
