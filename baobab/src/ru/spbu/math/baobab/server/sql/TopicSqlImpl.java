package ru.spbu.math.baobab.server.sql;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;

import ru.spbu.math.baobab.model.Attendee;
import ru.spbu.math.baobab.model.Auditorium;
import ru.spbu.math.baobab.model.EvenOddWeek;
import ru.spbu.math.baobab.model.Event;
import ru.spbu.math.baobab.model.TimeInstant;
import ru.spbu.math.baobab.model.TimeSlot;
import ru.spbu.math.baobab.model.TimeSlotExtent;
import ru.spbu.math.baobab.model.Topic;
import ru.spbu.math.baobab.server.TimeSlotExtentImpl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.annotation.Nullable;

/**
 * SQL-based implementation of Topic
 * 
 * @author agudulin
 */
public class TopicSqlImpl implements Topic {

  private final String myId;
  private final Type myType;
  private final String myName;

  public TopicSqlImpl(String id, Type type, String name) {
    myId = id;
    myType = type;
    myName = name;
  }

  @Override
  public String getID() {
    return myId;
  }

  @Override
  public Type getType() {
    return myType;
  }

  @Override
  public String getName() {
    return myName;
  }

  @Override
  public Event addEvent(Date date, TimeSlot timeSlot, @Nullable Auditorium auditorium) {
    SqlApi sqlApi = SqlApi.create();
    try {
      PreparedStatement stmt = sqlApi.prepareScript("SELECT * FROM TimeSlot WHERE name=?").get(0);
      stmt.setString(2, timeSlot.getName());
      ResultSet rs = stmt.executeQuery();
      int timeSlotId = 100;
      for (boolean hasRow = rs.next(); hasRow; hasRow = rs.next()) {
        timeSlotId = rs.getInt("id");
      }

      stmt = sqlApi.prepareScript("SELECT FROM Topic WHERE uid=?;").get(0);
      stmt.setString(2, this.getID());
      rs = stmt.executeQuery();
      int topicId = 100;
      for (boolean hasRow = rs.next(); hasRow; hasRow = rs.next()) {
        topicId = rs.getInt("id");
      }

      Event event = new EventSqlImpl(date, timeSlot, auditorium, this);
      stmt = sqlApi.prepareScript("INSERT INTO Event SET date=?,  time_slot_id=?,  topic_id=?,  auditorium_num=?;")
          .get(0);
      java.sql.Date sqlDate = new java.sql.Date(date.getTime());
      stmt.setDate(1, sqlDate);
      stmt.setInt(2, timeSlotId);
      stmt.setInt(3, topicId);
      stmt.setString(4, "");
      stmt.execute();

      return event;
    } catch (SQLException e) {
    } finally {
      sqlApi.dispose();
    }
    return null;
  }

  @Override
  public Collection<Event> addAllEvents(Date start, Date finish, TimeSlot timeSlot, Auditorium auditorium) {
    return null;
  }

  @Override
  public Collection<Event> getEvents() {
    List<Event> events = Lists.newArrayList();
    SqlApi sqlApi = SqlApi.create();
    try {
      List<PreparedStatement> stmts = sqlApi.prepareScript("SELECT * FROM Event");
      ResultSet rs = stmts.get(0).executeQuery();
      for (boolean hasRow = rs.next(); hasRow; hasRow = rs.next()) {
        java.sql.Date sqlDate = rs.getDate("date");
        Date date = new Date(sqlDate.getTime());
        int timeSlotId = rs.getInt("time_slot_id");
        int topicId = rs.getInt("topic_id");
        String auditorium_num = rs.getString("auditorium_num");
        Event event = new EventSqlImpl(date, fetchTimeSlot(timeSlotId), fetchAuditorium(auditorium_num),
            fetchTopic(topicId));
        events.add(event);
      }

      return events;
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      sqlApi.dispose();
    }
    return null;
  }

  @Override
  public void addAttendee(Attendee att) {
    // TODO Auto-generated method stub

  }

  @Override
  public Collection<Attendee> getAttendees() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void addOwner(Attendee owner) {
    // TODO Auto-generated method stub
  }

  @Override
  public Collection<Attendee> getOwners() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(myId, myType, myName);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof TopicSqlImpl == false) {
      return false;
    }

    TopicSqlImpl other = (TopicSqlImpl) obj;
    return Objects.equal(myId, other.myId) && Objects.equal(myType, other.myType)
        && Objects.equal(myName, other.myName);
  }

  private TimeSlot fetchTimeSlot(int timeslot_id) throws SQLException {
    SqlApi sqlApi = SqlApi.create();
    try {
      PreparedStatement stmt = sqlApi.prepareScript("SELECT * FROM TimeSlot WHERE id=?").get(0);
      stmt.setInt(1, timeslot_id);
      ResultSet rs = stmt.executeQuery();
      for (boolean hasRow = rs.next(); hasRow; hasRow = rs.next()) {
        String name = rs.getString("name");
        Integer startInMinutes = rs.getInt("start_min");
        TimeInstant start = new TimeInstant(startInMinutes / 60, startInMinutes % 60);
        Integer finishInMinutes = rs.getInt("finish_min");
        TimeInstant finish = new TimeInstant(finishInMinutes / 60, finishInMinutes % 60);
        Integer day = rs.getInt("day");
        EvenOddWeek flashing = EvenOddWeek.values()[rs.getInt("is_odd")];
        TimeSlotExtent tsExtent = new TimeSlotExtentImpl();
        TimeSlot ts = tsExtent.create(name, start, finish, day, flashing);
        return ts;
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      sqlApi.dispose();
    }
    return null;
  }

  private Auditorium fetchAuditorium(String num) throws SQLException {

    SqlApi sqlApi = SqlApi.create();
    try {
      PreparedStatement stmt = sqlApi.prepareScript("SELECT * FROM Auditorium WHERE num=?").get(0);
      stmt.setString(1, num);
      ResultSet rs = stmt.executeQuery();
      for (boolean hasRow = rs.next(); hasRow; hasRow = rs.next()) {
        int capacity = rs.getInt("capacity");
        // AuditoriumExtent audExtent = new AuditoriumExtentImpl();
        // auditorium = audExtent.create(auditorium_num, capacity);
        // return auditorium;
      }
    } catch (SQLException e) {
    } finally {
      sqlApi.dispose();
    }
    return null;
  }

  private Topic fetchTopic(int topic_id) throws SQLException {
    SqlApi sqlApi = SqlApi.create();
    try {
      PreparedStatement stmt = sqlApi.prepareScript("SELECT * FROM Topic WHERE id=?").get(0);
      stmt.setInt(1, topic_id);
      ResultSet rs = stmt.executeQuery();
      for (boolean hasRow = rs.next(); hasRow; hasRow = rs.next()) {
        String id = rs.getString("uid");
        Type type = Type.values()[rs.getInt("type")];
        String name = rs.getString("name");
        Topic topic = new TopicSqlImpl(id, type, name);
        return topic;
      }
    } catch (SQLException e) {
    } finally {
      sqlApi.dispose();
    }
    return null;
  }
}
