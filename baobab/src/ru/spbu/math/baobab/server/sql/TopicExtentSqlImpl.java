package ru.spbu.math.baobab.server.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;

import ru.spbu.math.baobab.model.AuditoriumExtent;
import ru.spbu.math.baobab.model.TimeSlotExtent;
import ru.spbu.math.baobab.model.Topic;
import ru.spbu.math.baobab.model.Topic.Type;
import ru.spbu.math.baobab.model.TopicExtent;

/**
 * SQL-based implementation of TopicExtent
 * 
 * @author agudulin
 */
public class TopicExtentSqlImpl implements TopicExtent {
  private final TimeSlotExtent myTimeSlotExtent;
  private final AuditoriumExtent myAuditoriumExtent;

  public TopicExtentSqlImpl(TimeSlotExtent timeSlotExtent, AuditoriumExtent auditoriumExtent) {
    myTimeSlotExtent = timeSlotExtent;
    myAuditoriumExtent = auditoriumExtent;
  }

  @Override
  public Topic createTopic(String id, Type type, String name) {
    SqlApi sqlApi = SqlApi.create();

    try {
      PreparedStatement stmt = sqlApi.prepareScript("SELECT * FROM Topic WHERE uid=?;").get(0);
      stmt.setString(1, id);

      int rowCount = 0;
      ResultSet resultSet = stmt.executeQuery();
      for (boolean hasRow = resultSet.next(); hasRow; hasRow = resultSet.next()) {
        rowCount++;
      }

      if (rowCount > 0) {
        throw new RuntimeException("The Topic with this ID already exists");
      }

      stmt = sqlApi.prepareScript("INSERT INTO Topic SET uid=?, name=?, type=?;").get(0);
      stmt.setString(1, id);
      stmt.setString(2, name);
      stmt.setInt(3, type.ordinal());

      stmt.execute();

      Topic topic = new TopicSqlImpl(id, type, name, myTimeSlotExtent, myAuditoriumExtent);
      return topic;

    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      sqlApi.dispose();
    }

    return null;
  }

  @Override
  public Collection<Topic> getAll() {
    List<Topic> topics = Lists.newArrayList();
    SqlApi sqlApi = SqlApi.create();

    try {
      List<PreparedStatement> stmts = sqlApi.prepareScript("SELECT * FROM Topic;");

      ResultSet rs = stmts.get(0).executeQuery();
      fetchTopic(rs, topics);
      
      return topics;
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      sqlApi.dispose();
    }

    return null;
  }

  @Override
  public Topic find(String id) {
    SqlApi sqlApi = SqlApi.create();

    try {
      List<Topic> topics = Lists.newArrayList();
      List<PreparedStatement> stmts = sqlApi.prepareScript("SELECT * FROM Topic WHERE uid=?;");
      stmts.get(0).setString(1, id);
      
      ResultSet rs = stmts.get(0).executeQuery();
      fetchTopic(rs, topics);
      return topics.isEmpty() ? null : topics.get(0);
      
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      sqlApi.dispose();
    }
    
    return null;
  }
  
  private void fetchTopic(ResultSet rs, List<Topic> topics) throws SQLException {
    for (boolean hasRow = rs.next(); hasRow; hasRow = rs.next()) {
      String id = rs.getString("uid");
      Type type = Type.values()[rs.getInt("type")];
      String name = rs.getString("name");

      Topic topic = new TopicSqlImpl(id, type, name, myTimeSlotExtent, myAuditoriumExtent);
      topics.add(topic);
    }      
  }
}
