package ru.spbu.math.baobab.server.sql;

import com.google.common.collect.ImmutableList;

import junit.framework.TestCase;

/**
 * Some tests for SqlApi
 * 
 * @author dbarashev
 */
public class SqlApiTest extends TestCase {
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
}
