package ru.spbu.math.baobab.server.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import com.google.common.collect.Lists;

import ru.spbu.math.baobab.model.EvenOddWeek;
import ru.spbu.math.baobab.model.TimeInstant;
import ru.spbu.math.baobab.model.TimeSlot;
import ru.spbu.math.baobab.model.TimeSlotExtent;
import ru.spbu.math.baobab.server.TimeSlotImpl;

/**
 * SQL-based implementation of TimeSlotExtent
 * 
 * @author agudulin
 */
public class TimeSlotExtentSqlImpl implements TimeSlotExtent {

  private void fetchTimeSlots(ResultSet rs, List<TimeSlot> timeSlots) throws SQLException {
    for (boolean hasRow = rs.next(); hasRow; hasRow = rs.next()) {
      int id = rs.getInt("id");
      String name = rs.getString("name");

      Integer startInMinutes = rs.getInt("start_min");
      TimeInstant start = new TimeInstant(startInMinutes / 60, startInMinutes % 60);

      Integer finishInMinutes = rs.getInt("finish_min");
      TimeInstant finish = new TimeInstant(finishInMinutes / 60, finishInMinutes % 60);

      Integer day = rs.getInt("day");

      EvenOddWeek flashing = EvenOddWeek.values()[rs.getInt("is_odd")];

      TimeSlot ts = new TimeSlotImpl(id, name, start, finish, day, flashing, this);
      timeSlots.add(ts);
    }
  }

  @Override
  public Collection<TimeSlot> getAll() {
    List<TimeSlot> timeSlots = Lists.newArrayList();
    SqlApi sqlApi = SqlApi.create();

    try {
      List<PreparedStatement> stmts = sqlApi.prepareScript("SELECT * FROM TimeSlot ORDER BY start_min;");

      ResultSet rs = stmts.get(0).executeQuery();
      fetchTimeSlots(rs, timeSlots);
      return timeSlots;

    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      sqlApi.dispose();
    }

    return null;
  }

  @Override
  public List<TimeSlot> findByWeekDay(int day) {
    List<TimeSlot> timeSlots = Lists.newArrayList();
    SqlApi sqlApi = SqlApi.create();

    try {
      List<PreparedStatement> stmts = sqlApi.prepareScript("SELECT * FROM TimeSlot WHERE day=? ORDER BY start_min");
      stmts.get(0).setInt(1, day);

      ResultSet rs = stmts.get(0).executeQuery();
      fetchTimeSlots(rs, timeSlots);
      return timeSlots;

    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      sqlApi.dispose();
    }

    return null;
  }

  @Override
  public List<TimeSlot> findByDate(Date date) {
    List<TimeSlot> timeSlots = Lists.newArrayList();
    SqlApi sqlApi = SqlApi.create();

    Calendar calendar = GregorianCalendar.getInstance();
    calendar.setTime(date);
    int day = calendar.get(Calendar.DAY_OF_WEEK);
    boolean isOdd = calendar.get(Calendar.WEEK_OF_YEAR) % 2 == 1;

    try {
      List<PreparedStatement> stmts = sqlApi
          .prepareScript("SELECT * FROM TimeSlot WHERE day=? AND is_odd=? ORDER BY start_min");
      stmts.get(0).setInt(1, day);
      stmts.get(0).setInt(2, (isOdd) ? 1 : 2);

      ResultSet rs = stmts.get(0).executeQuery();
      fetchTimeSlots(rs, timeSlots);
      return timeSlots;

    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      sqlApi.dispose();
    }

    return null;
  }

  @Override
  public TimeSlot create(String name, TimeInstant start, TimeInstant finish, int day, EvenOddWeek flashing) {
    SqlApi sqlApi = SqlApi.create();

    try {
      PreparedStatement stmt = sqlApi.prepareScript(
          "SELECT * FROM TimeSlot WHERE name=? AND day=? AND (is_odd=? OR is_odd=?);").get(0);
      stmt.setString(1, name);
      stmt.setInt(2, day);
      stmt.setInt(3, flashing.ordinal());
      stmt.setInt(4, 0);
      int rowCount = 0;
      ResultSet resultSet = stmt.executeQuery();
      for (boolean hasRow = resultSet.next(); hasRow; hasRow = resultSet.next()) {
        rowCount++;
      }

      if (rowCount > 0) {
        throw new IllegalStateException("The TimeSlot with this name is already exists");
      }

      stmt = sqlApi.prepareScript("INSERT INTO TimeSlot SET name=?, start_min=?, finish_min=?, day=?, is_odd=?;")
          .get(0);
      stmt.setString(1, name);
      stmt.setInt(2, start.getHour() * 60 + start.getMinute());
      stmt.setInt(3, finish.getHour() * 60 + finish.getMinute());
      stmt.setInt(4, day);
      stmt.setInt(5, flashing.ordinal());

      stmt.execute();
      stmt = sqlApi.prepareScript("SELECT id FROM TimeSlot WHERE name=? AND day=? AND is_odd=?;").get(0);
      stmt.setString(1, name);
      stmt.setInt(2, day);
      stmt.setInt(3, flashing.ordinal());
      resultSet = stmt.executeQuery();
      int id = -1;
      int rowcount = 0;
      for (boolean hasRow = resultSet.next(); hasRow; hasRow = resultSet.next()) {
        id = resultSet.getInt(1);
        rowcount++;
      }
      if (rowcount == 0) {
        throw new IllegalStateException("The TimeSlot with this name is not exist");
      }
      if (rowcount > 1) {
        throw new IllegalStateException("There are more than one TimeSlot with this name");
      }

      TimeSlot ts = new TimeSlotImpl(id, name, start, finish, day, flashing, this);
      return ts;

    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      sqlApi.dispose();
    }

    return null;
  }

  @Override
  public TimeSlot findById(int id) {
    SqlApi sqlApi = SqlApi.create();

    try {
      List<PreparedStatement> stmts = sqlApi.prepareScript("SELECT * FROM TimeSlot WHERE id=?;");
      stmts.get(0).setInt(1, id);

      ResultSet rs = stmts.get(0).executeQuery();
      for (boolean hasRow = rs.next(); hasRow; hasRow = rs.next()) {
        String name = rs.getString("name");

        Integer startInMinutes = rs.getInt("start_min");
        TimeInstant start = new TimeInstant(startInMinutes / 60, startInMinutes % 60);

        Integer finishInMinutes = rs.getInt("finish_min");
        TimeInstant finish = new TimeInstant(finishInMinutes / 60, finishInMinutes % 60);

        Integer day = rs.getInt("day");

        EvenOddWeek flashing = EvenOddWeek.values()[rs.getInt("is_odd")];

        TimeSlot ts = new TimeSlotImpl(id, name, start, finish, day, flashing, this);
        return ts;
      }

    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      sqlApi.dispose();
    }
    
    return null;
  }
}
