package ru.spbu.math.baobab.server.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import com.google.appengine.api.rdbms.AppEngineDriver;
import com.google.common.collect.Lists;

/**
 * Encapsulates JDBC connection stuff and provides interface for transforming 
 * an SQL script to a list of {@link PreparedStatement} instances.
 * 
 * @author dbarashev
 */
public class SqlApi {
  private static final Logger LOGGER = Logger.getLogger("SqlService");
  private static final String CONNECTION_SPEC = "jdbc:google:rdbms://barashev.net:baobab:baobab/";
  private static final Pattern PATTERN_COMMENT = Pattern.compile("^\\p{Blank}*--[^\\n]+(\\n|$)");
  private static final Pattern PATTERN_WHITESPACE = Pattern.compile("^\\p{Blank}*(\\n|$)");
  private static final Pattern PATTERN_STMT_END = Pattern.compile(".*;\\p{Blank}*(\\n|$)");

  static {
    try {
      DriverManager.registerDriver(new AppEngineDriver());
    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, "Failed to initialize JDBC driver", e);
    }    
  }

  private final Connection myConnection;

  /**
   * Creates an instance with default database and username parameters
   */
  public SqlApi() {
    this("prod", "frontend");
  }
  
  /**
   * Creates an instance which connects to the given database as the given user.
   * 
   * @param database database name
   * @param username user name
   */
  public SqlApi(String database, String username) {
    Connection c = null;
    try {
      c = DriverManager.getConnection(CONNECTION_SPEC + database, username, null);
    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, "Failed to create connection", e);
    }
    myConnection = c;
  }
  
  /**
   * Disposes this API instance and releases all resources.
   */
  public void dispose() {
    if (myConnection != null) {
      try {
        myConnection.close();
      } catch (SQLException e) {
        LOGGER.log(Level.SEVERE, "Failed to close connection", e);
      }
    }
  }
  
  /**
   * Splits the given text to a list of SQL statements, striping out comments, and creates
   * {@link PreparedStatement} instance for each statement.
   * 
   * @param text SQL script with statements separated by semicolon + linebreak pairs
   * @return a list of ready-to-run statements
   * 
   * @throws SQLException
   */
  public List<PreparedStatement> prepareScript(String text) throws SQLException {
    return createPreparedStatements(splitScript(text));
  }  

  static List<String> splitScript(String text) {
    List<String> stmts = Lists.newArrayList();
    StringBuilder stmtBuilder = new StringBuilder();
    for (String line : text.split("\\n")) {
      if (PATTERN_COMMENT.matcher(line).matches()) {
        continue;
      }
      if (PATTERN_WHITESPACE.matcher(line).matches()) {
        continue;
      }
      stmtBuilder.append(line);
      if (PATTERN_STMT_END.matcher(line).matches()) {
        stmts.add(stmtBuilder.toString());
        stmtBuilder = new StringBuilder();
      }
    }
    if (stmtBuilder.length() > 0) {
      stmts.add(stmtBuilder.toString());
    }    
    return stmts;
  }
  
  private List<PreparedStatement> createPreparedStatements(List<String> stmts) throws SQLException {
    List<PreparedStatement> result = Lists.newArrayList();
    for (String s : stmts) {
      result.add(myConnection.prepareCall(s));
    }    
    return result;
  }
}
