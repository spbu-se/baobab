package ru.spbu.math.baobab.server.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;

import ru.spbu.math.baobab.model.Calendar;
import ru.spbu.math.baobab.model.Topic;
import ru.spbu.math.baobab.model.Topic.Type;
import ru.spbu.math.baobab.server.sql.TopicSqlImpl;

/**
 * SQL-based implementation of Calendar
 * 
 * @author vloginova
 */
public class CalendarSqlImpl implements Calendar {

  private final String myID;

  public CalendarSqlImpl(String id) {
    myID = id;
  }

  public Collection<Topic> fetchTopics(ResultSet resultFind) throws SQLException {
    List<Topic> topics = Lists.newArrayList();
    for (boolean hasRow = resultFind.next(); hasRow; hasRow = resultFind.next()) {
      String id = resultFind.getString("uid");
      Type type = Type.values()[resultFind.getInt("type")];
      String name = resultFind.getString("name");
      topics.add(new TopicSqlImpl(id, type, name, null, null));
    }
    return topics;
  }

  @Override
  public Collection<Topic> getAllTopics() {
    SqlApi sqlApi = SqlApi.create();
    try {
      PreparedStatement stmt = sqlApi.prepareScript(
          "SELECT * FROM CalendarTopic ct JOIN Topic t ON ct.topic_uid = t.uid WHERE calendar_uid=?").get(0);
      stmt.setString(1, this.getID());
      ResultSet resultFind = stmt.executeQuery();
      return fetchTopics(resultFind);

    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      sqlApi.dispose();
    }
    return null;
  }

  @Override
  public void addTopic(Topic topic) {
    SqlApi sqlApi = SqlApi.create();

    try {
      PreparedStatement stmt = sqlApi
          .prepareScript("SELECT * FROM CalendarTopic WHERE calendar_uid=? AND topic_uid=?;").get(0);
      stmt.setString(1, myID);
      stmt.setString(2, topic.getID());

      ResultSet resultSet = stmt.executeQuery();
      if (resultSet.next()) {
        return;
      }

      stmt = sqlApi.prepareScript("INSERT INTO CalendarTopic SET calendar_uid=?, topic_uid=?;").get(0);
      stmt.setString(1, myID);
      stmt.setString(2, topic.getID());
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      sqlApi.dispose();
    }
  }

  @Override
  public String getID() {
    return myID;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(myID);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof Calendar == false) {
      return false;
    }
    Calendar calendar = (Calendar) obj;
    if (this.getID().equals(calendar.getID())) {
      return true;
    }
    return false;
  }
}
