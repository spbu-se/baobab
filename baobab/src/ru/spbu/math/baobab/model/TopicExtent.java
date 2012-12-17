package ru.spbu.math.baobab.model;

import java.util.Collection;

/**
 * TopicExtent is a set of all available topics.
 * 
 * @author dbarashev
 */
public interface TopicExtent {
  /**
   * Creates a new topic. Throws RuntimeException if topic with such ID already exists.
   * 
   * @param id topic ID
   * @param type topic type
   * @param name topic name
   * @return new topic instance
   */
  Topic createTopic(String id, Topic.Type type, String name);
  
  /**
   * @return all topics
   */
  Collection<Topic> getAll();
  
  /**
   * Finds topic by its ID
   * 
   * @param id id
   * @return found topic or {@code null} if no topic with such ID exists
   */
  Topic find(String id);
}
