package ru.spbu.math.baobab.server.sql;

import java.sql.SQLException;
import java.util.List;

import ru.spbu.math.baobab.model.Calendar;
import ru.spbu.math.baobab.model.CalendarExtent;
import ru.spbu.math.baobab.model.Topic;

import com.google.common.collect.Lists;

/**
 * Tests for CalendarSqlImpl
 * 
 * @author vloginova
 */
public class CalendarSqlImplTest extends SqlTestCase {
  public void testAddTopicAndGetAllTopics() throws SQLException {
    CalendarExtent calendarExtent = new CalendarExtentSqlImpl();
    expectSql("SELECT Calendar WHERE uid").withParameters(1, "1");
    expectSql("INSERT Calendar uid").withParameters(1, "1");
    Calendar calendar = calendarExtent.create("1");

    List<Topic> topics = Lists.newArrayList();
    Topic topic1 = new TopicSqlImpl("_1", Topic.Type.EXAM, "math", null, null);
    Topic topic2 = new TopicSqlImpl("_2", Topic.Type.LECTURE_COURSE, "algebra", null, null);
    topics.add(topic1);
    topics.add(topic2);

    expectSql("SELECT CalendarTopic WHERE calendar_uid topic_uid").withParameters(1, "1", 2, "_1");
    expectSql("INSERT CalendarTopic calendar_uid topic_uid").withParameters(1, "1", 2, "_1");
    expectSql("SELECT CalendarTopic WHERE calendar_uid topic_uid").withParameters(1, "1", 2, "_2").withResult(
        row("calendar_uid", "1", "topic_uid", "_2"));

    calendar.addTopic(topic1);
    calendar.addTopic(topic2);
    expectQuery("SELECT FROM CalendarTopic ct JOIN Topic t ON ct.topic_uid = t.uid WHERE calendar_uid",
        row("calendar_uid", "1", "uid", "_1", "name", "math", "type", 2),
        row("calendar_uid", "1", "uid", "_2", "name", "algebra", "type", 0));
    List<Topic> dbTopics = (List<Topic>) calendar.getAllTopics();
    assertEquals(topics, dbTopics);
  }
}
