package ru.spbu.math.baobab.server;

import java.util.Collection;
import com.google.common.collect.Lists;
import ru.spbu.math.baobab.model.Topic;
import ru.spbu.math.baobab.model.Topic.Type;
import ru.spbu.math.baobab.model.TopicExtent;

/**
 * In-memory implementation of TopicExtent
 * 
 * @author dageev
 */
public class TopicExtentImpl implements TopicExtent {
  private final Collection<Topic> myTopics = Lists.newArrayList();

  @Override
  public Topic createTopic(String id, Type type, String name) {
    for (Topic topic : myTopics) {
      if (topic.getID().equals(id)) {
        throw new RuntimeException("The Topic with this ID already exists");
      }
    }
    Topic topic = new TopicImpl(id, type, name);
    myTopics.add(topic);
    return topic;
  }

  @Override
  public Collection<Topic> getAll() {
    return myTopics;
  }

  @Override
  public Topic find(String id) {
    for (Topic topic : myTopics) {
      if (topic.getID() == id) {
        return topic;
      }
    }
    return null;
  }
}
