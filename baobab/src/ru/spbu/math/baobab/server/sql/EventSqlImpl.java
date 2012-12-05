package ru.spbu.math.baobab.server.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;

import ru.spbu.math.baobab.model.Attendee;
import ru.spbu.math.baobab.model.Auditorium;
import ru.spbu.math.baobab.model.Event;
import ru.spbu.math.baobab.model.TimeInstant;
import ru.spbu.math.baobab.model.TimeSlot;
import ru.spbu.math.baobab.model.Topic;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;

/**
 * Implementation of Event
 * 
 * @author dageev
 */
public class EventSqlImpl implements Event {
  private final Date myDate;
  private final TimeSlot myTimeSlot;
  private Auditorium myAuditorium;
  private final Topic myTopic;

  public EventSqlImpl(Date date, TimeSlot timeSlot, @Nullable Auditorium auditorium, Topic topic) {
    myDate = date;
    myTimeSlot = timeSlot;
    myAuditorium = auditorium;
    myTopic = topic;
  }

  private Date dateWithTime(TimeInstant t) {
    Calendar cal = Calendar.getInstance();

    cal.setTime(myDate);
    cal.set(Calendar.HOUR_OF_DAY, t.getHour());
    cal.set(Calendar.MINUTE, t.getMinute());
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);

    return cal.getTime();
  }

  @Override
  public Date getStartDate() {
    return dateWithTime(myTimeSlot.getStart());
  }

  @Override
  public Date getFinishDate() {
    return dateWithTime(myTimeSlot.getFinish());
  }

  @Override
  public TimeSlot getTimeSlot() {
    return myTimeSlot;
  }

  @Override
  public Auditorium getAuditorium() {
    return myAuditorium;
  }

  @Override
  public void setAuditorium(Auditorium auditorium) {
    myAuditorium = auditorium;
  }

  @Override
  public Topic getTopic() {
    return myTopic;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(myDate, myTimeSlot, myTopic);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof EventSqlImpl == false) {
      return false;
    }

    EventSqlImpl other = (EventSqlImpl) obj;
    return Objects.equal(this.getStartDate(), other.getStartDate()) 
        && Objects.equal(myTimeSlot, other.myTimeSlot)
        && Objects.equal(myTopic, other.myTopic);
  }

  @Override
  public void addAttendee(Attendee att) {
    SqlApi sqlApi = SqlApi.create();
    try {
      PreparedStatement stmt = sqlApi
          .prepareScript(
              "INSERT INTO EventAttendee SET attendee_uid=?, event_id=(SELECT id FROM Event WHERE date=?, time_slot_id=?, topic_id=?, auditorium_num=?);")
          .get(0);
      stmt.setString(1, att.getID());
      stmt.setDate(2, new java.sql.Date(myDate.getTime()));
      stmt.setInt(3, myTimeSlot.getID());
      stmt.setString(4, myTopic.getID());
      stmt.setString(5, myAuditorium.getID());
      stmt.execute();

    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      sqlApi.dispose();
    }
  }

  @Override
  public Collection<Attendee> getAttendees() {
    List<Attendee> attendees = Lists.newArrayList();
    SqlApi sqlApi = SqlApi.create();
    try {
      PreparedStatement stmt = sqlApi
          .prepareScript(
              "SELECT * FROM Attendee a JOIN EventAttendee ea ON ea.attendee_uid = a.uid " +
                "WHERE ea.event_id=(SELECT id FROM Event WHERE date=?, time_slot_id=?, topic_id=?, auditorium_num=?);")
          .get(0);
      stmt.setDate(1, new java.sql.Date(myDate.getTime()));
      stmt.setInt(2, myTimeSlot.getID());
      stmt.setString(3, myTopic.getID());
      stmt.setString(4, myAuditorium.getID());

      ResultSet rs = stmt.executeQuery();

      for (boolean hasRow = rs.next(); hasRow; hasRow = rs.next()) {
        int id = rs.getInt("id");
        String uid = rs.getString("uid");
        String name = rs.getString("name");
        int group_id = rs.getInt("group_id");
        int type = rs.getInt("type");

        Attendee attendee = new AttendeeSqlImpl(id, uid, name, Attendee.Type.values()[type], group_id,
            new AttendeeExtentSqlImpl());
        attendees.add(attendee);
      }
      return attendees;
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      sqlApi.dispose();
    }
    return null;
  }
}
