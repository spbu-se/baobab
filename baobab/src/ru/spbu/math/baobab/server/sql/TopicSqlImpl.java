package ru.spbu.math.baobab.server.sql;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;

import ru.spbu.math.baobab.model.Attendee;
import ru.spbu.math.baobab.model.Auditorium;
import ru.spbu.math.baobab.model.AuditoriumExtent;
import ru.spbu.math.baobab.model.Event;
import ru.spbu.math.baobab.model.TimeSlot;
import ru.spbu.math.baobab.model.TimeSlotExtent;
import ru.spbu.math.baobab.model.Topic;
import ru.spbu.math.baobab.model.TimeSlot.Utils;
import ru.spbu.math.baobab.server.EventImpl;

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
  private final TimeSlotExtent myTimeSlotExtent;
  private final AuditoriumExtent myAuditoriumExtent;

  public TopicSqlImpl(String id, Type type, String name, TimeSlotExtent tsExtent, AuditoriumExtent auditExtent) {
    myId = id;
    myType = type;
    myName = name;
    myTimeSlotExtent = tsExtent;
    myAuditoriumExtent = auditExtent;
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
      PreparedStatement stmt = sqlApi.prepareScript(
          "INSERT INTO Event SET date=?,  time_slot_id=?,  topic_id=?,  auditorium_num=?;").get(0);
      java.sql.Date sqlDate = new java.sql.Date(date.getTime());
      stmt.setDate(1, sqlDate);
      stmt.setInt(2, timeSlot.getID());
      stmt.setString(3, this.getID());
      stmt.setString(4, auditorium.getID());
      stmt.execute();
      
      stmt = sqlApi.prepareScript("SELECT id FROM Event WHERE date=? AND time_slot_id=? AND topic_id=?").get(0);
      stmt.setDate(1, sqlDate);
      stmt.setInt(2, timeSlot.getID());
      stmt.setString(3, this.getID());

      ResultSet rs = stmt.executeQuery();
      if (!rs.next()) {
        throw new IllegalStateException("Event identified by this date: " + date.toString() +
        		", timeslot: " + timeSlot.getName() + ",  topic: " + this.getName() + " does not exist");
      } 
      int id = rs.getInt("id");
      if (rs.next()) {
        throw new IllegalStateException("There are too many events identified by this date: " + sqlDate.toString() +
                ", timeslot: " + timeSlot.getName() + ",  topic: " + this.getName());
      } 
     
      Event event = new EventSqlImpl(id, date, timeSlot, auditorium, this);
      return event;
    } catch (SQLException e) {
      throw new RuntimeException(String.format("Ошибка при добавлении события %s в дату %s:\n%s", myId, date.toString(), e.getMessage()), e);
    } finally {
      sqlApi.dispose();
    }
  }

  @Override
  public Collection<Event> addAllEvents(Date start, Date finish, TimeSlot timeSlot, @Nullable Auditorium auditorium) {
    Collection<Event> events = Lists.newArrayList();

    for (Date date : Utils.getFilteredRangeOfDates(Utils.datesRange(start, finish), timeSlot)) {
      Event event = addEvent(date, timeSlot, auditorium);
      events.add(event);
    }

    return events;
  }

  @Override
  public Collection<Event> getEvents() {
    List<Event> events = Lists.newArrayList();
    SqlApi sqlApi = SqlApi.create();
    try {
      List<PreparedStatement> stmts = sqlApi.prepareScript("SELECT * FROM Event WHERE topic_id=?");
      stmts.get(0).setString(1, this.getID());
      ResultSet rs = stmts.get(0).executeQuery();
      fetchEvents(rs, events);
      return events;
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      sqlApi.dispose();
    }
    return null;
  }

  private void insertAttendee(Attendee att, String tableName) {
    SqlApi sqlApi = SqlApi.create();
    try {
      PreparedStatement stmt = sqlApi.prepareScript(
          "INSERT INTO " + tableName + " SET topic_id=?, attendee_id=(SELECT id FROM Attendee WHERE uid=?);").get(0);
      stmt.setString(1, this.getID());
      stmt.setString(2, att.getID());
      stmt.execute();

    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      sqlApi.dispose();
    }
  }

  private Collection<Attendee> selectAttendee(String tableName) {
    List<Attendee> attendees = Lists.newArrayList();
    SqlApi sqlApi = SqlApi.create();
    try {
      PreparedStatement stmt = sqlApi.prepareScript(
          "SELECT * FROM Attendee a JOIN " + tableName + " ta ON ta.attendee_id = a.id " + "WHERE ta.topic_id=?;").get(0);
      stmt.setString(1, this.getID());
      ResultSet rs = stmt.executeQuery();
      
      return new AttendeeExtentSqlImpl().fetchAttendees(rs);
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      sqlApi.dispose();
    }
    return null;
  }

  @Override
  public void addAttendee(Attendee att) {
    insertAttendee(att, "TopicAttendee");
  }

  @Override
  public Collection<Attendee> getAttendees() {
    return selectAttendee("TopicAttendee");
  }

  @Override
  public void addOwner(Attendee owner) {
    insertAttendee(owner, "TopicOwner");
  }

  @Override
  public Collection<Attendee> getOwners() {
    return selectAttendee("TopicOwner");
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
    return Objects.equal(myId, other.myId)
        && Objects.equal(myType, other.myType)
        && Objects.equal(myName, other.myName);
  }

  private void fetchEvents(ResultSet rs, List<Event> events) throws SQLException {
    SqlApi sqlApi = SqlApi.create();
    try {
      for (boolean hasRow = rs.next(); hasRow; hasRow = rs.next()) {
        int id = rs.getInt("id");
        Date date = new Date(rs.getDate("date").getTime());
        TimeSlot ts = myTimeSlotExtent.findById(rs.getInt("time_slot_id"));
        Auditorium auditorium = myAuditoriumExtent.find(rs.getString("auditorium_num"));
        Event event = new EventSqlImpl(id, date, ts, auditorium, this);
        events.add(event);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      sqlApi.dispose();
    }
  }
  
  public void setUrl(String url) {
    SqlApi sqlApi = SqlApi.create();
    try {
      PreparedStatement stmt = sqlApi.prepareScript(
          "UPDATE Topic SET url=? WHERE uid=?;").get(0);
      stmt.setString(1, this.getID());
      stmt.setString(2, url);
      stmt.execute();
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      sqlApi.dispose();
    }
  }
  
  public String getUrl() {
    SqlApi sqlApi = SqlApi.create();
    try {
      PreparedStatement stmt = sqlApi.prepareScript(
          "SELECT url FROM Topic WHERE uid=?;").get(0);
      stmt.setString(1, this.getID());
      ResultSet rs = stmt.executeQuery();
      if (!rs.next()) {
        throw new IllegalStateException("This Topic: " + this.getName() + " does not containt a list of questions");
      } 
      String url = rs.getString("url");
      return url;
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      sqlApi.dispose();
    }
    return null;
  }
}
