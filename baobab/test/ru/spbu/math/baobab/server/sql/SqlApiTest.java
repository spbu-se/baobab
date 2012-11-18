package ru.spbu.math.baobab.server.sql;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import com.google.common.collect.ImmutableList;

/**
 * Some tests for SqlApi
 * 
 * @author dbarashev
 */
public class SqlApiTest extends SqlTestCase {
  public void testSplitSingleStatement() {
    assertEquals(ImmutableList.of("CREATE TABLE FOO(a INT);"), SqlApi.splitScript("CREATE TABLE FOO(a INT);"));
  }
  
  public void testSplitTwoStatements() {
    assertEquals(ImmutableList.of("CREATE TABLE FOO(a INT);", "CREATE TABLE BAR(b INT);"), 
        SqlApi.splitScript("CREATE TABLE FOO(a INT);\nCREATE TABLE BAR(b INT);"));
  }
  
  public void testMultilineStatement() {
    assertEquals(ImmutableList.of("CREATE TABLE FOO(a INT,b INT);"), SqlApi.splitScript("CREATE TABLE FOO(a INT,\nb INT);"));
  }
  
  public void testStripComments() {
    assertEquals(ImmutableList.of("CREATE TABLE FOO(a INT,b INT);"), 
        SqlApi.splitScript("--this is a comment\nCREATE TABLE FOO(a INT,\n-- b is another attribute\nb INT);"));
  }
  
  public void testMockSqlApi() throws SQLException {
    SqlApi sqlApi = SqlApi.create();
    expectInsert("INSERT INTO Foo");
    expectQuery("SELECT * FROM Foo", 
        row("id", "1", "value", "val1"),
        row("id", "2", "value", "val2"),
        row("id", "3", "value", "val3")
    );
    expectSql("SELECT FROM Foo WHERE id").withParameters(1, 4);
    expectSql("SELECT FROM Foo WHERE id").withParameters(1, "qwe");
    
    List<PreparedStatement> script = sqlApi.prepareScript(
        "INSERT INTO Foo(id, value) VALUES (1, 'val1');\n" + 
        "SELECT * FROM Foo;\n" + 
        "SELECT * FROM Foo WHERE id=?;\n" + 
        "SELECT * FROM Foo WHERE id=?;");
    script.get(0).execute();

    int rowNum = 1;
    ResultSet resultSet = script.get(1).executeQuery();
    for (boolean hasNext = resultSet.next(); hasNext; hasNext = resultSet.next()) {
      assertEquals(rowNum, resultSet.getInt("id"));
      assertEquals("val" + rowNum, resultSet.getString("value"));
      rowNum++;
    }
    
    script.get(2).setInt(1, 4);
    assertFalse(script.get(2).executeQuery().next());
    
    {
      boolean failed = false;
      try {
        script.get(3).setInt(1, 4);
      } catch (AssertionError e) {
        failed = true;
      }
      if (!failed) {
        fail("setInt(1, 4) should've failed since we expected 'qwe' as parameter 1 value");
      }
    }
    {
      boolean failed = false;
      try {
        expectInsert("INSERT Bar");
        sqlApi.prepareScript("INSERT INTO Foo(id, value) VALUES (1, 'val1')");
      } catch (AssertionError e) {
        failed = true;
      }
      if (!failed) {
        fail("Should've failed because actual SQL statement doesn't match the expected pattern");
      }      
    }
  }
  
  public void testMockNullValues() throws SQLException {
    SqlApi sqlApi = SqlApi.create();
    expectQuery("SELECT * FROM Foo", row("id", null, "value1", null, "value2", "foobar"));
    List<PreparedStatement> script = sqlApi.prepareScript("SELECT * FROM Foo;");
    ResultSet resultSet = script.get(0).executeQuery();
    assertTrue(resultSet.next());
    assertEquals(0, resultSet.getInt("id"));
    assertTrue(resultSet.wasNull());
    assertNull(resultSet.getString("value1"));
    assertTrue(resultSet.wasNull());
    assertEquals("foobar", resultSet.getString("value2"));
    assertFalse(resultSet.wasNull());
  }
  
  public void testDateValue() throws SQLException {
    SqlApi sqlApi = SqlApi.create();
    expectQuery("SELECT * FROM Foo", row("date", "2012-11-14"));
    List<PreparedStatement> script = sqlApi.prepareScript("SELECT * FROM Foo;");
    ResultSet resultSet = script.get(0).executeQuery();
    assertTrue(resultSet.next());
    
    Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    c.clear();
    c.set(Calendar.YEAR, 2012);
    c.set(Calendar.MONTH, Calendar.NOVEMBER);
    c.set(Calendar.DAY_OF_MONTH, 14);
    
    assertEquals(new Date(c.getTime().getTime()), resultSet.getDate("date"));    
  }
}
