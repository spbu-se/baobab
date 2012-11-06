package ru.spbu.math.baobab.server.sql;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;

import ru.spbu.math.baobab.model.Topic;
import ru.spbu.math.baobab.model.TopicExtent;
import ru.spbu.math.baobab.model.Topic.Type;

/**
 * Tests for SQL-based implementation of TopicExtent
 * 
 * @author agudulin
 */
public class TopicExtentSqlImplTest {

  @Test
  public void testCreateTopic() {
    TopicExtent topicExtent = new TopicExtentSqlImpl();
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
    TopicExtent topicExtent = new TopicExtentSqlImpl();

    topicExtent.createTopic("CS101-2012", Type.LECTURE_COURSE, "Computer Science introduction course in year 2012");
    topicExtent.createTopic("CS102-2012", Type.LECTURE_COURSE, "Computer Science introduction course in year 2012");

    Topic topic1 = new TopicSqlImpl("CS101-2012", Type.LECTURE_COURSE,
        "Computer Science introduction course in year 2012");
    Topic topic2 = new TopicSqlImpl("CS102-2012", Type.LECTURE_COURSE,
        "Computer Science introduction course in year 2012");

    List<Topic> topicsFromDb = (List<Topic>) topicExtent.getAll();
    List<Topic> topics = Lists.newArrayList();
    topics.add(topic1);
    topics.add(topic2);

    assertEquals(topics, topicsFromDb);
  }
}
