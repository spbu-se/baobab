package ru.spbu.math.baobab.server.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
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
  private final AttendeeExtent myExtent;

  public AttendeeSqlImpl(int ID, AttendeeExtent extent) {
    myID = ID;
    myExtent = extent;
  }

  @Override
  public String getName() {
    try {
      SqlApi con = new SqlApi();
      List<PreparedStatement> stmt = con.prepareScript("SELECT name FROM Attendee WHERE uid=?");
      Iterator<PreparedStatement> it = stmt.iterator();
      while (it.hasNext()) {
        PreparedStatement value = (PreparedStatement) it.next();
        value.setString(1, this.getID());
        ResultSet resultFind = value.executeQuery();
        return resultFind.getString(1);
      }
      con.dispose();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  // should be modified because returned result is not as in the description
  public String getID() {
    try {
      SqlApi con = new SqlApi();
      List<PreparedStatement> stmt;
      stmt = con.prepareScript("SELECT uid FROM Attendee WHERE id=?");
      Iterator<PreparedStatement> it = stmt.iterator();
      while (it.hasNext()) {
        PreparedStatement value = (PreparedStatement) it.next();
        value.setInt(1, myID);
        ResultSet resultFind = value.executeQuery();
        return resultFind.getString(1);
      }
      con.dispose();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public void setID(String id) {
    if (myExtent.find(id) != null) {
      throw new IllegalArgumentException("Attendee with the given ID already exists");
    }
    try {
      SqlApi con = new SqlApi();
      List<PreparedStatement> stmt;
      stmt = con.prepareScript("UPDATE Attendee SET uid = ? WHERE id = ?");
      Iterator<PreparedStatement> it = stmt.iterator();
      while (it.hasNext()) {
        PreparedStatement value = (PreparedStatement) it.next();
        value.setString(1, id);
        value.setInt(2, myID);
        value.executeQuery();
      }
      con.dispose();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Override
  public Type getType() {
    int typeNumber = -1;
    try {
      SqlApi con = new SqlApi();
      List<PreparedStatement> stmt;
      stmt = con.prepareScript("SELECT type FROM Attendee WHERE id=?");
      Iterator<PreparedStatement> it = stmt.iterator();
      while (it.hasNext()) {
        PreparedStatement value = (PreparedStatement) it.next();
        value.setInt(1, myID);
        ResultSet resultFind = value.executeQuery();
        typeNumber = resultFind.getInt(1);
      }
      con.dispose();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    switch (typeNumber) {
    case 0:
      return Type.STUDENT;
    case 1:
      return Type.TEACHER;
    case 2:
      return Type.ACADEMIC_GROUP;
    case 3:
      return Type.CHAIR;
    case 4:
      return Type.FREE_FORM_GROUP;
    default:
      return null;
    }
  }

  @Override
  public boolean isGroup() {
    if ((getType() == Type.ACADEMIC_GROUP) || (getType() == Type.CHAIR) || (getType() == Type.FREE_FORM_GROUP)) {
      return true;
    }
    return false;
  }

  @Override
  public Collection<Attendee> getGroupMembers() {
    try {
      Collection<Attendee> members = new ArrayList<Attendee>();
      SqlApi con = new SqlApi();
      // find out group_id of current Attendee
      List<PreparedStatement> stmt1;
      stmt1 = con.prepareScript("SELECT group_id FROM Attendee WHERE id=?");
      Iterator<PreparedStatement> it1 = stmt1.iterator();
      ResultSet resultFind1;
      int group_id = 0;
      while (it1.hasNext()) {
        PreparedStatement value1 = (PreparedStatement) it1.next();
        value1.setInt(1, myID);
        resultFind1 = value1.executeQuery();
        group_id = resultFind1.getInt(1);
      }
      // find out group members
      List<PreparedStatement> stmt2;
      stmt2 = con.prepareScript("SELECT attendee_id FROM GroupMember WHERE group_id = ?");
      Iterator<PreparedStatement> it2 = stmt2.iterator();
      ResultSet resultFind2;
      while (it2.hasNext()) {
        PreparedStatement value2 = (PreparedStatement) it2.next();
        value2.setInt(1, group_id);
        resultFind2 = value2.executeQuery();
        while (resultFind2.next()) {
          Attendee groupAttendee = new AttendeeSqlImpl(resultFind2.getInt(1), myExtent);
          members.add(groupAttendee);
        }
      }
      con.dispose();
      return members;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public void addGroupMember(Attendee member) {
    if (!member.isGroup()) {
      throw new IllegalStateException("The attendee is not a group.");
    }
    try {
      SqlApi con = new SqlApi();
      // find out group_id of current Attendee
      List<PreparedStatement> stmt1;
      stmt1 = con.prepareScript("SELECT group_id FROM Attendee WHERE id=?");
      Iterator<PreparedStatement> it1 = stmt1.iterator();
      ResultSet resultFind1;
      int group_id = 0;
      while (it1.hasNext()) {
        PreparedStatement value1 = (PreparedStatement) it1.next();
        value1.setInt(1, myID);
        resultFind1 = value1.executeQuery();
        group_id = resultFind1.getInt(1);
      }
      // find out attendee_id of member
      List<PreparedStatement> stmt2;
      stmt2 = con.prepareScript("SELECT id FROM Attendee WHERE uid = ?");
      Iterator<PreparedStatement> it2 = stmt2.iterator();
      ResultSet resultFind2;
      int attendee_id = 0;
      while (it2.hasNext()) {
        PreparedStatement value2 = (PreparedStatement) it2.next();
        value2.setString(1, member.getID());
        resultFind2 = value2.executeQuery();
        attendee_id = resultFind2.getInt(1);
      }
      // adding a new group member
      List<PreparedStatement> stmt3;
      stmt3 = con.prepareScript("INSERT INTO GroupMember SET group_id=?, attendee_id = ?");
      Iterator<PreparedStatement> it3 = stmt3.iterator();
      while (it3.hasNext()) {
        PreparedStatement value3 = (PreparedStatement) it3.next();
        value3.setInt(1, group_id);
        value3.setInt(2, attendee_id);
        value3.executeQuery();
      }
      con.dispose();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
