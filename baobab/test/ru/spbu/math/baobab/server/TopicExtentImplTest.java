package ru.spbu.math.baobab.server;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import com.google.common.collect.Lists;
import ru.spbu.math.baobab.model.Topic;
import ru.spbu.math.baobab.model.TopicExtent;
import ru.spbu.math.baobab.model.Topic.Type;

/**
 * Tests for in-memory implementation of TopicExtent
 * 
 * @author dageev
 */
public class TopicExtentImplTest {

  @Test
  public void testCreateTopic() {
    TopicExtent topicExtent = new TopicExtentImpl();
    topicExtent.createTopic("CS101-2012", Type.LECTURE_COURSE, "Computer Science introduction course in year 2012");
    try {
      topicExtent.createTopic("CS101-2012", Type.EXAM, "Computer Science introduction exam");
      fail("Expected RuntimeException");
    } catch (RuntimeException e) {
      // can't create another topic with existing ID
    }
  }

  @Test
  public void testGetAll() {
    TopicExtent topicExtent = new TopicExtentImpl();
    List<Topic> topics = Lists.newArrayList();
    Topic topic1 = topicExtent.createTopic("CS101-2012", Type.EXAM, "Computer Science introduction exam. Part one");
    Topic topic2 = topicExtent.createTopic("CS102-2012", Type.EXAM, "Computer Science introduction exam. Part two");
    topics.add(topic1);
    topics.add(topic2);
    assertEquals(topics, topicExtent.getAll());
  }
}
