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
import ru.spbu.math.baobab.model.TimeSlot.Utils;
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
      PreparedStatement stmt = sqlApi.prepareScript(
          "INSERT INTO Event SET date=?,  time_slot_id=?,  topic_id=?,  auditorium_num=?;").get(0);
      java.sql.Date sqlDate = new java.sql.Date(date.getTime());
      stmt.setDate(1, sqlDate);
      stmt.setInt(2, timeSlot.getID());
      stmt.setString(3, this.getID());
      // stmt.setString(4, auditorium.getID());
      stmt.execute();

      Event event = new EventSqlImpl(date, timeSlot, auditorium, this);
      return event;
    } catch (SQLException e) {
    } finally {
      sqlApi.dispose();
    }
    return null;
  }

  @Override
  public Collection<Event> addAllEvents(Date start, Date finish, TimeSlot timeSlot, Auditorium auditorium) {
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
      List<PreparedStatement> stmts = sqlApi.prepareScript("SELECT * FROM Event WHERE topic_id=?" +
        "INNER JOIN TimeSlot ON (Event.timeslot_id = TimeSlot.id)" +
        "INNER JOIN Topic ON (Event.topic_id = Topic.uid);");
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

  private void fetchEvents(ResultSet rs, List<Event> events) throws SQLException {
    SqlApi sqlApi = SqlApi.create();
    try {
      for (boolean hasRow = rs.next(); hasRow; hasRow = rs.next()) {
        Date date = new Date(rs.getDate("date").getTime());
        String name = rs.getString("name");
        Integer startInMinutes = rs.getInt("start_min");
        TimeInstant start = new TimeInstant(startInMinutes / 60, startInMinutes % 60);
        Integer finishInMinutes = rs.getInt("finish_min");
        TimeInstant finish = new TimeInstant(finishInMinutes / 60, finishInMinutes % 60);
        Integer day = rs.getInt("day");
        EvenOddWeek flashing = EvenOddWeek.values()[rs.getInt("is_odd")];
        TimeSlotExtent tsExtent = new TimeSlotExtentImpl();
        TimeSlot ts = tsExtent.create(name, start, finish, day, flashing);
        //String num = rs.getString("auditorium_num");
        //int capacity = rs.getInt("capacity");
        // AuditoriumExtent audExtent = new AuditoriumExtentImpl();
        // auditorium = audExtent.create(num, capacity);
        Event event = new EventSqlImpl(date, ts, null, this);
        events.add(event);   
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      sqlApi.dispose();
    }
  }
}
