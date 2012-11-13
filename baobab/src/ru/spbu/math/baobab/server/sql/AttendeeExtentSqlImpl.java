package ru.spbu.math.baobab.server.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

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
      result.next();
      int intID = result.getInt("id");
      if ((type == Type.ACADEMIC_GROUP) || (type == Type.CHAIR) || (type == Type.FREE_FORM_GROUP)) {
        // set group_id for group members
        stmt.get(2).setInt(1, intID);
        stmt.get(2).execute();
        stmt.get(3).setInt(1, intID);
        stmt.get(3).setInt(2, intID);
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
      if (resultFind.next()) {
        int intID = resultFind.getInt("id");
        String name = resultFind.getString("name");
        Type type = Type.values()[resultFind.getInt("type")];
        int group_id = resultFind.getInt("group_id");
        if ((group_id == 0) && (type != Type.ACADEMIC_GROUP) && (type == Type.CHAIR) && (type == Type.FREE_FORM_GROUP)) {
          return new AttendeeSqlImpl(intID, id, name, type, null, this);
        } else {
          return new AttendeeSqlImpl(intID, id, name, type, group_id, this);
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      con.dispose();
    }
    return null;
  }
}
