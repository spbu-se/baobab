package ru.spbu.math.baobab.server.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;

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

  @Override
  public Collection<Calendar> getAll() {
    SqlApi con = SqlApi.create();
    try {
      List<Calendar> result = Lists.newArrayList();
      PreparedStatement stmt = con.prepareScript("SELECT * FROM Calendar").get(0);
      ResultSet resultFind = stmt.executeQuery();
      while (resultFind.next()) {
        result.add(new CalendarSqlImpl(resultFind.getString("uid")));
      }
      return result;
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      con.dispose();
    }
    return null;
  }
}
