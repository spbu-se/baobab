package ru.spbu.math.baobab.server.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import ru.spbu.math.baobab.model.Calendar;
import ru.spbu.math.baobab.model.CalendarExtent;

/**
 * SQL-based implementation of CalendarExtent
 * 
 * @author vloginova
 */
public class CalendarExtentSqlImpl implements CalendarExtent {

  @Override
  public Calendar create(String uid) {
    if (find(uid) != null) {
      throw new RuntimeException("Calendar with the given UID already exists.");
    }
    SqlApi sqlApi = SqlApi.create();
    try {
      PreparedStatement stmt = sqlApi.prepareScript("INSERT INTO Calendar SET uid=?;").get(0);
      stmt.setString(1, uid);
      stmt.execute();
      Calendar calendar = new CalendarSqlImpl(uid);
      return calendar;
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      sqlApi.dispose();
    }
    return null;
  }

  @Override
  public Calendar find(String uid) {
    SqlApi con = SqlApi.create();
    try {
      PreparedStatement stmt = con.prepareScript("SELECT * FROM Calendar WHERE uid=?").get(0);
      stmt.setString(1, uid);
      ResultSet resultFind = stmt.executeQuery();
      if (resultFind.next()) {
        return new CalendarSqlImpl(uid);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      con.dispose();
    }
    return null;
  }
}
