package ru.spbu.math.baobab.server.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import ru.spbu.math.baobab.model.Attendee;
import ru.spbu.math.baobab.model.AttendeeExtent;
import ru.spbu.math.baobab.model.Auditorium;
import ru.spbu.math.baobab.model.TimeSlot;
import ru.spbu.math.baobab.model.Topic;
import ru.spbu.math.baobab.server.AbstractEvent;
import ru.spbu.math.baobab.server.AttendeeExtentImpl;
import com.google.common.collect.Lists;

/**
 * Implementation of Event
 * 
 * @author dageev
 */
public class EventSqlImpl extends AbstractEvent {

  public EventSqlImpl(int id, Date date, TimeSlot timeSlot, Auditorium auditorium, Topic topic) {
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
      return fetchAttendees(rs);
      
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      sqlApi.dispose();
    }
    return null;
  }

  private Collection<Attendee> fetchAttendees(ResultSet rs) throws SQLException {
    List<Attendee> attendees = Lists.newArrayList();
    AttendeeExtent attExtent = new AttendeeExtentImpl();
    for (boolean hasRow = rs.next(); hasRow; hasRow = rs.next()) {
      int id = rs.getInt("id");
      String uid = rs.getString("uid");
      String name = rs.getString("name");
      int group_id = rs.getInt("group_id");
      int type = rs.getInt("type");
      Attendee attendee = new AttendeeSqlImpl(id, uid, name, Attendee.Type.values()[type], group_id, attExtent);
      attendees.add(attendee);
    }
    return attendees;
  }
}
