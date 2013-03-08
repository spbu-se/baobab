package ru.spbu.math.baobab.server.sql;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import com.google.common.collect.Lists;
import ru.spbu.math.baobab.model.Auditorium;
import ru.spbu.math.baobab.model.AuditoriumExtent;

/**
 * SQL-based implementation of AuditoriumExtent
 * 
 * @author vloginova
 */
public class AuditoriumExtentSqlImpl implements AuditoriumExtent {

  @Override
  public Auditorium create(String id, int capacity) {
    if (find(id) != null) {
      throw new IllegalArgumentException("Auditorium with the given ID already exists.");
    }
    SqlApi sqlApi = SqlApi.create();
    try {
      CallableStatement stmt = sqlApi.prepareScript("INSERT INTO Auditorium SET num=?, capacity=?;").get(0);
      stmt.setString(1, id);
      stmt.setInt(2, capacity);
      stmt.execute();
      Auditorium auditorium = new AuditoriumSqlImpl(id, capacity);
      return auditorium;
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      sqlApi.dispose();
    }
    return null;
  }

  @Override
  public Collection<Auditorium> getAll() {
    List<Auditorium> auditoriums = Lists.newArrayList();
    SqlApi sqlApi = SqlApi.create();
    try {
      List<CallableStatement> stmts = sqlApi.prepareScript("SELECT * FROM Auditorium ORDER BY num;");
      ResultSet rs = stmts.get(0).executeQuery();
      for (boolean hasRow = rs.next(); hasRow; hasRow = rs.next()) {
        String id = rs.getString("num");
        int capacity = rs.getInt("capacity");
        Auditorium auditorium = new AuditoriumSqlImpl(id, capacity);
        auditoriums.add(auditorium);
      }
      return auditoriums;
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      sqlApi.dispose();
    }
    return null;
  }

  @Override
  public Auditorium find(String id) {
    SqlApi con = SqlApi.create();
    try {
      CallableStatement stmt = con.prepareScript("SELECT capacity FROM Auditorium WHERE num=?").get(0);
      stmt.setString(1, id);
      ResultSet resultFind = stmt.executeQuery();
      if (resultFind.next()) {
        int capacity = resultFind.getInt("capacity");
        return new AuditoriumSqlImpl(id, capacity);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      con.dispose();
    }
    return null;
  }
}
