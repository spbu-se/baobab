package ru.spbu.math.baobab.server.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
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
		if(find(id)==null) {
			throw new IllegalArgumentException("Attendee with the given ID already exists");
		}
		try {
		  SqlApi con = new SqlApi(); 
		  //insert new Attendee into Attendee table
		  List<PreparedStatement> stmt = con.prepareScript("INSERT INTO Attendee SET uid=?, name=?, type=?");
		  Iterator<PreparedStatement> it=stmt.iterator();
	      while(it.hasNext()) {
	        PreparedStatement value=(PreparedStatement)it.next();
	        value.setString(1, id);
	        value.setString(2, name);
	        if(type==Type.STUDENT) {
	      	  value.setInt(3, 0);
	      	  value.setNull(4, java.sql.Types.NULL);
	        } else if(type==Type.TEACHER) {
	        	value.setInt(3, 1);
	      	    value.setNull(4, java.sql.Types.NULL);
	        } else if(type==Type.ACADEMIC_GROUP) {
	            value.setInt(3, 2);
	        	value.setNull(4, java.sql.Types.NULL);
	        } else if(type==Type.CHAIR) {
	        	value.setInt(3, 3);
	        	value.setNull(4, java.sql.Types.NULL);
	        } else if(type==Type.FREE_FORM_GROUP) {
	            value.setInt(3, 4);
	        	value.setNull(4, java.sql.Types.NULL);
	        }
	        value.executeQuery();
	      }
	      //set unique id for created Attendee
	      List<PreparedStatement> stmtID = con.prepareScript("SELECT id FROM Attendee WHERE uid=?");
	      it = stmtID.iterator();
	      while(it.hasNext()) {
	        PreparedStatement value=(PreparedStatement)it.next();
	        value.setString(1, id);
	        ResultSet resultFind = value.executeQuery();
	        int ID = resultFind.getInt(1);
	        Attendee myAttendee = new AttendeeSqlImpl(ID,this);
	        //set group_id for group members
	        if(myAttendee.isGroup()) {
	          List<PreparedStatement> stmtG = con.prepareScript("INSERT INTO AttendeeGroup SET id=?; " +
	          		"UPDATE Attendee SET group_id = ? WHERE id = ?;");
	          Iterator<PreparedStatement> itG=stmtG.iterator();
	          PreparedStatement valueG=(PreparedStatement)itG.next();
	          valueG.setInt(1, ID);
	          valueG.setInt(2, ID);
	          valueG.setInt(3, ID);
	          valueG.executeQuery();
	          valueG = (PreparedStatement)itG.next();
	          valueG.executeQuery();
	        }
	      con.dispose();
	      return myAttendee;
	      } 
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Attendee find(String id) {
		try {
		  SqlApi con = new SqlApi();
		  List<PreparedStatement> stmt = con.prepareScript("SELECT id FROM Attendee WHERE uid=?");
		  Iterator<PreparedStatement> it=stmt.iterator();
		  while(it.hasNext()) {
			PreparedStatement value=(PreparedStatement)it.next();
		    value.setString(1, id);
			ResultSet resultFind = value.executeQuery();
			int ID;
			if(resultFind.getRow()!=0) {
			  ID = resultFind.getInt(1);
			  Attendee myAttendee = new AttendeeSqlImpl(ID, this);
			  con.dispose();
			  return myAttendee;
			}
		  }
		} catch (SQLException e){
		    e.printStackTrace();
		}
		return null;
	}
}
