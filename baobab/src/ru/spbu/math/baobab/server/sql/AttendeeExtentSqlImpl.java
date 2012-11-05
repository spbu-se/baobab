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
    SqlApi con = new SqlApi();
    try {
      // insert new Attendee into Attendee table
      List<PreparedStatement> stmt = con.prepareScript("INSERT INTO Attendee SET uid=?, name=?, type=?");
      stmt.get(0).setString(1, id);
      stmt.get(0).setString(2, name);
      stmt.get(0).setInt(3, type.ordinal());
      stmt.get(0).executeQuery();
      // set unique id for created Attendee
      stmt = con.prepareScript("SELECT id FROM Attendee WHERE uid=?");
      stmt.get(0).setString(1, id);
      int intID = stmt.get(0).executeQuery().getInt(1);
      Attendee myAttendee = new AttendeeSqlImpl(intID, id, name, type, this);
      // set group_id for group members
      if (myAttendee.isGroup()) {
        stmt = con.prepareScript("INSERT INTO AttendeeGroup SET id=?; \n "
            + "UPDATE Attendee SET group_id = ? WHERE id = ?;");
        stmt.get(0).setInt(1, intID);
        stmt.get(0).executeQuery();
        stmt.get(1).setInt(1, intID);
        stmt.get(1).setInt(2, intID);
        stmt.get(1).executeQuery();
      }
      return myAttendee;
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      con.dispose();
    }
    return null;
  }

  @Override
  public Attendee find(String id) {
    SqlApi con = new SqlApi();
    try {
      PreparedStatement stmt = con.prepareScript("SELECT id, name, type FROM Attendee WHERE uid=?").get(0);
      stmt.setString(1, id);
      ResultSet resultFind = stmt.executeQuery();
      if (resultFind.next()) {
        Attendee myAttendee;
        switch (resultFind.getInt(3)) {
        case 0:
          myAttendee = new AttendeeSqlImpl(resultFind.getInt(1), id, resultFind.getString(2), Type.STUDENT, this);
          return myAttendee;
        case 1:
          myAttendee = new AttendeeSqlImpl(resultFind.getInt(1), id, resultFind.getString(2), Type.TEACHER, this);
          return myAttendee;
        case 2:
          myAttendee = new AttendeeSqlImpl(resultFind.getInt(1), id, resultFind.getString(2), Type.ACADEMIC_GROUP, this);
          return myAttendee;
        case 3:
          myAttendee = new AttendeeSqlImpl(resultFind.getInt(1), id, resultFind.getString(2), Type.CHAIR, this);
          return myAttendee;
        case 4:
          myAttendee = new AttendeeSqlImpl(resultFind.getInt(1), id, resultFind.getString(2), Type.FREE_FORM_GROUP,
              this);
          return myAttendee;
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
