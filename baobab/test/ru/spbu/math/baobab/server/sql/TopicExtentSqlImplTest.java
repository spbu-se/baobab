package ru.spbu.math.baobab.server.sql;

import java.sql.SQLException;
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
public class TopicExtentSqlImplTest extends SqlTestCase {

  @Test
  public void testCreateTopic() throws SQLException {
    TopicExtent topicExtent = new TopicExtentSqlImpl();

    expectSql("SELECT Topic WHERE uid").withParameters(1, "CS101-2012");
    expectSql("INSERT Topic uid name type")
        .withParameters(1, "CS101-2012", 2, "Computer Science introduction course in year 2012", 3, Type.LECTURE_COURSE.ordinal());
    topicExtent.createTopic("CS101-2012", Type.LECTURE_COURSE, "Computer Science introduction course in year 2012");

    try {
      expectSql("SELECT Topic WHERE uid")
          .withParameters(1, "CS101-2012")
          .withResult(row(
              "uid", "CS101-2012",
              "name", "Computer Science introduction course in year 2012",
              "type", Type.LECTURE_COURSE.ordinal()));

      topicExtent.createTopic("CS101-2012", Type.EXAM, "Computer Science introduction exam");
      fail("Expected RuntimeException");
    } catch (RuntimeException e) {
      // can't create another topic with existing ID
    }
  }

  @Test
  public void testGetAll() throws SQLException {
    TopicExtent topicExtent = new TopicExtentSqlImpl();

    expectQuery("SELECT * FROM Topic");
    expectInsert("INSERT INTO Topic");
    expectQuery("SELECT * FROM Topic");
    expectInsert("INSERT INTO Topic");
    topicExtent.createTopic("CS101-2012", Type.LECTURE_COURSE, "Computer Science introduction course in year 2012");
    topicExtent.createTopic("CS102-2012", Type.LECTURE_COURSE, "Computer Science introduction course in year 2012");

    Topic topic1 = new TopicSqlImpl("CS101-2012", Type.LECTURE_COURSE,
        "Computer Science introduction course in year 2012");
    Topic topic2 = new TopicSqlImpl("CS102-2012", Type.LECTURE_COURSE,
        "Computer Science introduction course in year 2012");

    expectQuery(
        "SELECT * FROM Topic",
        row(
            "uid", "CS101-2012",
            "name", "Computer Science introduction course in year 2012",
            "type", Type.LECTURE_COURSE.ordinal()),
        row(
            "uid", "CS102-2012",
            "name", "Computer Science introduction course in year 2012",
            "type", Type.LECTURE_COURSE.ordinal())
    );

    List<Topic> topicsFromDb = (List<Topic>) topicExtent.getAll();
    List<Topic> topics = Lists.newArrayList();
    topics.add(topic1);
    topics.add(topic2);

    assertEquals(topics, topicsFromDb);
  }
}
