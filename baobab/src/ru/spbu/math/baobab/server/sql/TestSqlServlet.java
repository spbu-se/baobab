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

public class TestSqlServlet extends HttpServlet {
  private static final Logger LOGGER = Logger.getLogger("SqlService");
  
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    SqlApi sqlApi = new SqlApi();
    try {
      List<PreparedStatement> stmts = sqlApi.prepareScript("SELECT * FROM TimeSlot;");
      assert stmts.size() == 1;
      
      int rowCount = 0;
      ResultSet resultSet = stmts.get(0).executeQuery();
      for (boolean hasRow = resultSet.next(); hasRow; hasRow = resultSet.next()) {
        rowCount++;
      }
      resp.getWriter().println("Success. " + rowCount + " rows fetched");
    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, "Failed to execute SQL", e);
      resp.getWriter().println("Failure. " + e.getMessage());
    }
  }
  
}
