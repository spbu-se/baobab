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

  // constructor
  public AttendeeSqlImpl(int id, String uid, String name, Type type, AttendeeExtent extent) {
    myID = id;
    myUID = uid;
    myName = name;
    myType = type;
    myExtent = extent;
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
    SqlApi con = SqlApi.create();
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
    SqlApi con = SqlApi.create();
    try {
      Collection<Attendee> members = new ArrayList<Attendee>();
      // find out data about members of this group
      List<PreparedStatement> stmt = con.prepareScript("SELECT id, uid, name, type FROM Attendee JOIN "
          + "GroupMember ON GroupMember.group_id=? AND GroupMember.attendee_id = id");
      stmt.get(0).setInt(1, myID); // group_id == myID for attendee who is
      // a group
      ResultSet result = stmt.get(0).executeQuery();
      while (result.next()) {
        Attendee groupAttendee = new AttendeeSqlImpl(result.getInt(1), result.getString(2), result.getString(3),
            Type.values()[result.getInt(4)], myExtent);
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
    SqlApi con = SqlApi.create();
    try {
      List<PreparedStatement> stmt = con.prepareScript("SELECT id FROM Attendee WHERE uid = ?; \n "
          + "INSERT INTO GroupMember SET group_id=?, attendee_id = ?;");
      stmt.get(0).setString(1, member.getID());
      // adding a new group member
      ResultSet result = stmt.get(0).executeQuery();
      result.next();
      stmt.get(1).setInt(1, myID); // group_id == myID for attendee who is a group
      stmt.get(1).setInt(2, result.getInt(1)); // set attendee_id
      stmt.get(1).execute();
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      con.dispose();
    }
  }
}
