package ru.spbu.math.baobab.server.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;

import ru.spbu.math.baobab.model.Attendee;
import ru.spbu.math.baobab.model.Auditorium;
import ru.spbu.math.baobab.model.TimeSlot;
import ru.spbu.math.baobab.model.Topic;
import ru.spbu.math.baobab.server.AbstractEvent;

/**
 * Implementation of Event
 * 
 * @author dageev
 */
public class EventSqlImpl extends AbstractEvent {

  public EventSqlImpl(int id, Date date, TimeSlot timeSlot, @Nullable Auditorium auditorium, Topic topic) {
    super(id, date, timeSlot, auditorium, topic);
  }

  @Override
  public void addAttendee(Attendee att) {
    SqlApi sqlApi = SqlApi.create();
    try {
      PreparedStatement stmt = sqlApi.prepareScript("INSERT INTO EventAttendee SET attendee_uid=?, event_id=?;").get(0);
      stmt.setString(1, att.getID());
      stmt.setInt(2, this.getID());

      stmt.execute();

    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      sqlApi.dispose();
    }
  }

  @Override
  public Collection<Attendee> getAttendees() {
    SqlApi sqlApi = SqlApi.create();
    try {
      PreparedStatement stmt = sqlApi.prepareScript(
          "SELECT * FROM Attendee a JOIN EventAttendee ea ON ea.attendee_uid = a.uid " + "WHERE ea.event_id=?;").get(0);
      stmt.setInt(1, this.getID());

      ResultSet rs = stmt.executeQuery();
      
      return new AttendeeExtentSqlImpl().fetchAttendees(rs);
      
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      sqlApi.dispose();
    }
    return null;
  }
}
