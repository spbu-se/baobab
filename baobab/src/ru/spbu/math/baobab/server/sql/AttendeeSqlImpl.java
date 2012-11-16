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
  private Integer myGroupId;
  private final AttendeeExtent myExtent;
  private static final String query = "SELECT A_member.id, A_member.uid, A_member.name, A_member.type, A_member.group_id "
      + "FROM Attendee A_group JOIN AttendeeGroup AG ON (A_group.group_id = AG.id) "
      + "JOIN GroupMember GM ON (GM.group_id = AG.id) JOIN Attendee A_member "
      + "ON (A_member.id = GM.attendee_id) WHERE A_group.id = ?;";

  // constructor
  public AttendeeSqlImpl(int id, String uid, String name, Type type, Integer group_id, AttendeeExtent extent) {
    myID = id;
    myUID = uid;
    myName = name;
    myType = type;
    myGroupId = group_id;
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
    if (!isGroup()) {
      return null;
    }
    SqlApi con = SqlApi.create();
    try {
      Collection<Attendee> members = new ArrayList<Attendee>();
      // find out data about members of this group
      List<PreparedStatement> stmt = con.prepareScript(query);
      stmt.get(0).setInt(1, myID);
      ResultSet result = stmt.get(0).executeQuery();
      while (result.next()) {
        int intID = result.getInt("A_member.id");
        String uid = result.getString("A_member.uid");
        String name = result.getString("A_member.name");
        Type type = Type.values()[result.getInt("A_member.type")];
        int group_id = result.getInt("A_member.group_id");
        if (result.wasNull()) {
          Attendee groupAttendee = new AttendeeSqlImpl(intID, uid, name, type, null, myExtent);
          members.add(groupAttendee);
        } else {
          Attendee groupAttendee = new AttendeeSqlImpl(intID, uid, name, type, group_id, myExtent);
          members.add(groupAttendee);
        }
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
    if (!isGroup()) {
      throw new IllegalStateException("The attendee is not a group.");
    }
    SqlApi con = SqlApi.create();
    try {
      List<PreparedStatement> stmt = con.prepareScript("SELECT id FROM Attendee WHERE uid = ?; \n"
          + "INSERT INTO GroupMember SET group_id=?, attendee_id = ?;");
      stmt.get(0).setString(1, member.getID());
      // adding a new group member
      ResultSet result = stmt.get(0).executeQuery();
      result.next();
      stmt.get(1).setInt(1, myGroupId); // set group_id
      stmt.get(1).setInt(2, result.getInt("id")); // set attendee_id
      stmt.get(1).execute();
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      con.dispose();
    }
  }

  @Override
  public int hashCode() {
    return (myGroupId == null ? myID * 31 : myGroupId * 13 + myID * 29);
  }

  @Override
  public boolean equals(Object instance) {
    if (instance instanceof Attendee) {
      Attendee attendee = (Attendee) instance;
      if ((getID() == attendee.getID()) && (getName() == attendee.getName()) && (getType() == attendee.getType())
          && (hashCode() == attendee.hashCode())) {
        return true;
      }
    }
    return false;
  }
}
