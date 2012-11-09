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

  // constructor
  public AttendeeSqlImpl(int id, String uid, String name, Type type, AttendeeExtent extent) {
    myID = id;
    myUID = uid;
    myName = name;
    myType = type;
    myGroupId = null;
    myExtent = extent;
    // set group_id for group members
    if (this.isGroup()) {
      SqlApi con = SqlApi.create();
      try {
        List<PreparedStatement> stmt = con.prepareScript("INSERT INTO AttendeeGroup SET id=?; \n "
            + "UPDATE Attendee SET group_id = ? WHERE id = ?;");
        stmt.get(0).setInt(1, myID);
        stmt.get(0).execute();
        stmt.get(1).setInt(1, myID);
        stmt.get(1).setInt(2, myID);
        stmt.get(1).execute();
        myGroupId = myID;
      } catch (SQLException e) {
        e.printStackTrace();
      } finally {
        con.dispose();
      }
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
      String query = "SELECT A_member.id, A_member.uid, A_member.name, A_member.type "
          + "FROM Attendee A_group JOIN AttendeeGroup AG ON (A_group.group_id = AG.id) "
          + "JOIN GroupMember GM ON (GM.group_id = AG.id) JOIN Attendee A_member "
          + "ON (A_member.id = GM.attendee_id) WHERE A_group.id = ?;";
      List<PreparedStatement> stmt = con.prepareScript(query);
      stmt.get(0).setInt(1, myID);
      ResultSet result = stmt.get(0).executeQuery();
      while (result.next()) {
        int id = result.getInt(1);
        String uid = result.getString(2);
        String name = result.getString(3);
        Type type = Type.values()[result.getInt(4)];
        Attendee groupAttendee = new AttendeeSqlImpl(id, uid, name, type, myExtent);
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
      stmt.get(1).setInt(1, myGroupId); // set group_id
      stmt.get(1).setInt(2, result.getInt(1)); // set attendee_id
      stmt.get(1).execute();
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      con.dispose();
    }
  }
}
