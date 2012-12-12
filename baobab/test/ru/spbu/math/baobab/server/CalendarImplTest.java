package ru.spbu.math.baobab.server;

import java.sql.SQLException;
import java.util.List;

import ru.spbu.math.baobab.model.Calendar;
import ru.spbu.math.baobab.model.CalendarExtent;
import ru.spbu.math.baobab.model.Topic;
import ru.spbu.math.baobab.server.sql.SqlTestCase;
import ru.spbu.math.baobab.server.sql.TopicSqlImpl;

import com.google.common.collect.Lists;

/**
 * Tests for CalendarImpl
 * 
 * @author vloginova
 */
public class CalendarImplTest extends SqlTestCase {
  public void testAddTopicAndGetAllTopics() throws SQLException {
    CalendarExtent calendarExtent = new CalendarExtentImpl();
    Calendar calendar = calendarExtent.create("1");

    List<Topic> topics = Lists.newArrayList();
    Topic topic1 = new TopicSqlImpl("_1", Topic.Type.EXAM, "math", null, null);
    Topic topic2 = new TopicSqlImpl("_2", Topic.Type.LECTURE_COURSE, "algebra", null, null);
    topics.add(topic1);
    topics.add(topic2);

    calendar.addTopic(topic1);
    calendar.addTopic(topic2);

    List<Topic> dbTopics = (List<Topic>) calendar.getAllTopics();
    assertEquals(topics, dbTopics);
  }
}
