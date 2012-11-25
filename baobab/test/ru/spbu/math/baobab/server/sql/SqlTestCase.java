package ru.spbu.math.baobab.server.sql;

import java.util.Map;

import junit.framework.TestCase;

import com.google.common.collect.Maps;

/**
 * Base class for SQL related tests. It creates and registers an instance of {@link MockSqlApi}
 * and provides some helper methods to add expectations.
 * 
 * @author dbarashev
 */
public class SqlTestCase extends TestCase {
  private MockSqlApi myMockSqlApi;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    myMockSqlApi = new MockSqlApi();
  }
  
  protected MockSqlApi.Expectations expectSql(String pattern) {
    return myMockSqlApi.addExpectation(pattern);
  }
  
  protected void expectInsert(String pattern) {
    myMockSqlApi.addExpectation(pattern);
  }

  protected void expectQuery(String pattern, Map<String, Object>... expectedResultSet) {
    myMockSqlApi.addExpectation(pattern).withResult(expectedResultSet);
  }

  /**
   * Builds a map from a flat array of (name, value) pairs where 'name' is a column name
   * and 'value' is a value of that column in the row being built.
   * 
   * @param pairs untyped array of (name, value) pairs
   * @return typed map of column names to values
   */
  protected static Map<String, Object> row(Object... pairs) {
    assert pairs.length % 2 == 0;
    Map<String, Object> result = Maps.newHashMap();
    for (int i = 0; i < pairs.length / 2; i++) {
      result.put(String.valueOf(pairs[i * 2]), pairs[i * 2 + 1]);
    }
    return result;
  }
}
