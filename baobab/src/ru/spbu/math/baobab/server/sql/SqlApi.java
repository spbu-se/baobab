package ru.spbu.math.baobab.server.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import ru.spbu.math.baobab.server.DevMode;

import com.google.appengine.api.rdbms.AppEngineDriver;
import com.google.common.collect.Lists;

/**
 * Encapsulates JDBC connection stuff and provides interface for transforming 
 * an SQL script to a list of {@link CallableStatement} instances.
 * 
 * @author dbarashev
 */
public abstract class SqlApi {
  private static final Logger LOGGER = Logger.getLogger("SqlService");
  private static final String CONNECTION_SPEC = "jdbc:google:rdbms://barashev.net:baobab:baobab/";
  public static final Pattern PATTERN_COMMENT = Pattern.compile("^\\p{Blank}*--[^\\n]+(\\n|\\r|$)");
  public static final Pattern PATTERN_WHITESPACE = Pattern.compile("^\\p{Blank}*(\\n|\\r|$)");
  private static final Pattern PATTERN_STMT_END = Pattern.compile(".*;\\p{Blank}*(\\n|$)");

  static {
    try {
      DriverManager.registerDriver(new AppEngineDriver());
    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, "Failed to initialize JDBC driver", e);
    }    
  }

  public static interface Factory {
    SqlApi create();
  }
  
  private static Factory ourFactory = new Factory() {
    @Override
    public SqlApi create() {
      return new SqlApi() {
        private final Connection myConnection = createConnection("prod", "frontend");
        
        @Override
        protected CallableStatement prepareCall(String stmt) throws SQLException {
          return myConnection.prepareCall(stmt);
        }
        
        public void dispose() {
          if (myConnection != null) {
            try {
              myConnection.close();
            } catch (SQLException e) {
              LOGGER.log(Level.SEVERE, "Failed to close connection", e);
            }
          }
        }
      };
    }
  };
  
  protected static void setFactory(Factory factory) {
    ourFactory = factory;
  }
  
  public static SqlApi create() {
    assert ourFactory != null;
    return ourFactory.create();
  }
  
  /**
   * Creates an instance with default database and username parameters
   */
  protected SqlApi() {
  }
  
  /**
   * Creates an instance which connects to the given database as the given user.
   * 
   * @param database database name
   * @param username user name
   */
  private static Connection createConnection(String database, String username) {
    Connection c = null;
    try {
      c = DriverManager.getConnection(CONNECTION_SPEC + database + (DevMode.IS_ENABLED ? "&useUnicode=yes&characterEncoding=UTF-8" : ""), username, null);
    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, "Failed to create connection", e);
    }
    return c;
  }
  
  /**
   * Disposes this API instance and releases all resources.
   */
  public abstract void dispose();
  
  /**
   * Splits the given text to a list of SQL statements, striping out comments, and creates
   * {@link CallableStatement} instance for each statement.
   * 
   * @param text SQL script with statements separated by semicolon + linebreak pairs
   * @return a list of ready-to-run statements
   * 
   * @throws SQLException
   */
  public List<CallableStatement> prepareScript(String text) throws SQLException {
    return createCallableStatements(splitScript(text));
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
  
  private List<CallableStatement> createCallableStatements(List<String> stmts) throws SQLException {
    List<CallableStatement> result = Lists.newArrayList();
    for (String s : stmts) {
      result.add(prepareCall(s));
    }    
    return result;
  }
  
  protected abstract CallableStatement prepareCall(String stmt) throws SQLException;  
}
