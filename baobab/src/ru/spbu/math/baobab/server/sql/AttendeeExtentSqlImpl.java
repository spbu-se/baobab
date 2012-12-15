package ru.spbu.math.baobab.server.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;

import ru.spbu.math.baobab.model.Attendee;
import ru.spbu.math.baobab.model.AttendeeExtent;
import ru.spbu.math.baobab.model.Attendee.Type;

/**
 * SQL-based implementation of AttendeeExtent
 * 
 * @author aoool
 */
public class AttendeeExtentSqlImpl implements AttendeeExtent {

  @Override
  public Attendee create(String id, String name, Attendee.Type type) {
    if (find(id) != null) {
      throw new IllegalArgumentException("Attendee with the given ID already exists.");
    }
    SqlApi con = SqlApi.create();
    try {
      List<PreparedStatement> stmt = con.prepareScript("INSERT INTO Attendee SET uid=?, name=?, type=?; \n"
          + "SELECT id FROM Attendee WHERE uid=?; \n" + "INSERT INTO AttendeeGroup SET id=?; \n"
          + "UPDATE Attendee SET group_id = ? WHERE id = ?;");
      // insert new Attendee into Attendee table
      stmt.get(0).setString(1, id);
      stmt.get(0).setString(2, name);
      stmt.get(0).setInt(3, type.ordinal());
      stmt.get(0).execute();
      // set unique id for created Attendee
      stmt.get(1).setString(1, id);
      ResultSet result = stmt.get(1).executeQuery();
      if (!result.next()) {
        throw new RuntimeException();
      }
      int intID = result.getInt("id");
      if ((type == Type.ACADEMIC_GROUP) || (type == Type.CHAIR) || (type == Type.FREE_FORM_GROUP)) {
        // set group_id for group members
        stmt.get(2).setInt(1, intID);
        stmt.get(3).setInt(1, intID);
        stmt.get(3).setInt(2, intID);
        stmt.get(2).execute();
        stmt.get(3).execute();
        return new AttendeeSqlImpl(intID, id, name, type, intID, this);
      } else {
        return new AttendeeSqlImpl(intID, id, name, type, null, this);
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      con.dispose();
    }
    return null;
  }

  @Override
  public Attendee find(String id) {
    SqlApi con = SqlApi.create();
    try {
      PreparedStatement stmt = con.prepareScript("SELECT id, name, type, group_id FROM Attendee WHERE uid=?").get(0);
      stmt.setString(1, id);
      ResultSet resultFind = stmt.executeQuery();
      List<Attendee> findedAttendees = (List<Attendee>) fetchAttendees(resultFind);
      if (findedAttendees.size() == 1) {
        return findedAttendees.get(0);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      con.dispose();
    }
    return null;
  }
  
  
  @Override
  public Collection<Attendee> getAll() {
    SqlApi con = SqlApi.create();
    try {
      PreparedStatement stmt = con.prepareScript("SELECT id, name, type, group_id FROM Attendee").get(0);
      ResultSet resultFind = stmt.executeQuery();
      return fetchAttendees(resultFind);
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      con.dispose();
    }
    return null;
  }

  public Collection<Attendee> fetchAttendees(ResultSet rs) throws SQLException {
    List<Attendee> attendees = Lists.newArrayList();
    for (boolean hasRow = rs.next(); hasRow; hasRow = rs.next()) {
      int id = rs.getInt("id");
      String uid = rs.getString("uid");
      String name = rs.getString("name");
      int group_id = rs.getInt("group_id");
      int type = rs.getInt("type");
      if (rs.wasNull()) {        
        attendees.add(new AttendeeSqlImpl(id, uid, name, Attendee.Type.values()[type], null, this));
      } else {
        attendees.add(new AttendeeSqlImpl(id, uid, name, Attendee.Type.values()[type], group_id, this));
      }    
    }
    return attendees;
  }
}
