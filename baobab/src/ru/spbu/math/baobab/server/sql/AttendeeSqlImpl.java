package ru.spbu.math.baobab.server.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ru.spbu.math.baobab.model.Attendee;
import ru.spbu.math.baobab.model.AttendeeExtent;

/**
 * SQL-based implementation of Attendee
 * 
 * @author aoool
 */
public class AttendeeSqlImpl implements Attendee {

  private final int myID; // unique id for every Attendee in Attendee table
  private String myUID;
  private String myName;
  private Type myType;
  private final AttendeeExtent myExtent;

  // constructor for AttendeeExtent
  public AttendeeSqlImpl(int id, String uid, String name, Type type, AttendeeExtent extent) {
    myID = id;
    myUID = uid;
    myName = name;
    myType = type;
    myExtent = extent;
  }

  // constructor for getGroupMembers()
  public AttendeeSqlImpl(int id, AttendeeExtent extent) {
    myID = id;
    myUID = null;
    myName = null;
    myType = null;
    myExtent = extent;
    SqlApi con = new SqlApi();
    try {
      // find out uid, name and type for current Attendee
      List<PreparedStatement> stmt = con.prepareScript("SELECT uid, name, type FROM Attendee WHERE id=?");
      stmt.get(0).setInt(1, myID);
      ResultSet result = stmt.get(0).executeQuery();
      myUID = result.getString(1);
      myName = result.getString(2);
      myType = Type.values()[result.getInt(3)];
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      con.dispose();
    }
  }

  @Override
  public String getName() {
    return myName;
  }

  @Override
  public String getID() {
    return myUID;
  }

  @Override
  public void setID(String id) {
    if (myExtent.find(id) != null) {
      throw new IllegalArgumentException("Attendee with the given ID already exists.");
    }
    SqlApi con = new SqlApi();
    try {
      PreparedStatement stmt = con.prepareScript("UPDATE Attendee SET uid = ? WHERE id = ?").get(0);
      stmt.setString(1, id);
      stmt.setInt(2, myID);
      stmt.execute();
      myUID = id;
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      con.dispose();
    }
  }

  @Override
  public Type getType() {
    return myType;
  }

  @Override
  public boolean isGroup() {
    if ((myType == Type.ACADEMIC_GROUP) || (myType == Type.CHAIR) || (myType == Type.FREE_FORM_GROUP)) {
      return true;
    }
    return false;
  }

  @Override
  public Collection<Attendee> getGroupMembers() {
    SqlApi con = new SqlApi();
    try {
      Collection<Attendee> members = new ArrayList<Attendee>();
      // find out group_id of current Attendee
      List<PreparedStatement> stmt = con.prepareScript("SELECT group_id FROM Attendee WHERE id=?; \n"
          + "SELECT attendee_id FROM GroupMember WHERE group_id = ?");
      stmt.get(0).setInt(1, myID);
      // find out group members
      stmt.get(1).setInt(1, stmt.get(0).executeQuery().getInt(1)); // set group_id
      ResultSet result = stmt.get(1).executeQuery();
      while (result.next()) {
        Attendee groupAttendee = new AttendeeSqlImpl(result.getInt(1), myExtent);
        members.add(groupAttendee);
      }
      return members;
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      con.dispose();
    }
    return null;
  }

  @Override
  public void addGroupMember(Attendee member) {
    if (!member.isGroup()) {
      throw new IllegalStateException("The attendee is not a group.");
    }
    SqlApi con = new SqlApi();
    try {
      List<PreparedStatement> stmt = con.prepareScript("SELECT group_id FROM Attendee WHERE id=?; \n"
          + "SELECT id FROM Attendee WHERE uid = ?; \n" + "INSERT INTO GroupMember SET group_id=?, attendee_id = ?;");
      stmt.get(0).setInt(1, myID);
      stmt.get(1).setString(1, member.getID());
      // adding a new group member
      stmt.get(2).setInt(1, stmt.get(0).executeQuery().getInt(1)); // set group_id
      stmt.get(2).setInt(2, stmt.get(1).executeQuery().getInt(1)); // set attendee_id
      stmt.get(2).execute();
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      con.dispose();
    }
  }
}
