package ru.spbu.math.baobab.server.sql;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.Multimap;

import ru.spbu.math.baobab.model.Attendee;
import ru.spbu.math.baobab.model.CalendarExtent;
import ru.spbu.math.baobab.model.Event;

/**
 * This servlet does a smoke test of database connection. It runs a single select and
 * prints how many rows it returned
 * 
 * @author dbarashev
 */
public class TestSqlServlet extends HttpServlet {
  private static final Logger LOGGER = Logger.getLogger("SqlService");
  
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    SqlApi sqlApi = SqlApi.create();
    try {
      List<PreparedStatement> stmts = sqlApi.prepareScript("SELECT * FROM TimeSlot;");
      assert stmts.size() == 1;
      
      int rowCount = 0;
      ResultSet resultSet = stmts.get(0).executeQuery();
      for (boolean hasRow = resultSet.next(); hasRow; hasRow = resultSet.next()) {
        rowCount++;
      }
      resp.getWriter().println("Success. " + rowCount + " rows fetched");
      CalendarExtent extent =  new CalendarExtentSqlImpl();
      AttendeeEventMap attev = new AttendeeEventMap(extent.find("TESTCALENDAR"));
      Multimap<Attendee, Event> map = attev.getAttendeeEventMap();
      resp.getWriter().println(map.size());
    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, "Failed to execute SQL", e);
      resp.getWriter().println("Failure. " + e.getMessage());
    }
  }
  
}
