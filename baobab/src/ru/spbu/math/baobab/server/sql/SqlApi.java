package ru.spbu.math.baobab.server.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;

import com.google.appengine.api.rdbms.AppEngineDriver;
import com.google.common.collect.Lists;

public class SqlApi {
  private static final String CONNECTION_SPEC = "jdbc:google:rdbms://barashev.net:baobab:baobab/";
  private static final Pattern PATTERN_COMMENT = Pattern.compile("^\\p{Blank}*--[^\\n]+(\\n|$)");
  private static final Pattern PATTERN_WHITESPACE = Pattern.compile("^\\p{Blank}*(\\n|$)");
  private static final Pattern PATTERN_STMT_END = Pattern.compile(".*;\\p{Blank}*(\\n|$)");

  static {
    try {
      DriverManager.registerDriver(new AppEngineDriver());
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }    
  }

  private final Connection myConnection;

  public SqlApi() {
    this("prod", "frontend");
  }
  
  public SqlApi(String database, String username) {
    Connection c = null;
    try {
      c = DriverManager.getConnection(CONNECTION_SPEC + database, username, "");
    } catch (SQLException e) {
      e.printStackTrace();
    }
    myConnection = c;
  }
  
  public List<PreparedStatement> prepareScript(String text) throws SQLException {
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
    
    List<PreparedStatement> result = Lists.newArrayList();
    for (String s : stmts) {
      result.add(myConnection.prepareCall(s));
    }
    
    return result;
  }  
}
