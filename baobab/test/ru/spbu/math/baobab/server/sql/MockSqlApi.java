package ru.spbu.math.baobab.server.sql;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import ru.spbu.math.baobab.server.sql.MockSqlApi.Expectations;

import junit.framework.Assert;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;

import static org.mockito.Mockito.*;

/**
 * Implements SqlApi which creates mocks of PreparedStatement and ResultSet,
 * with ability to verify SQL commands being issued to JDBC, and return
 * predefined results.
 * 
 * This class registers an instance of {@link SqlApi.Factory} so that code which
 * uses {@link SqlApi} gets mock instances automatically.
 * 
 * Typical usage: create an instance, add some expectations, run your code which
 * deals with SqlApi.
 * 
 * @author dbarashev
 */
public class MockSqlApi extends SqlApi {
  private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
  
  private static final Function<String, String> ESCAPE_REGEX = new Function<String, String>() {
    @Override
    public String apply(String value) {
      return value.replace("*", "\\*");
    }
  };

  /**
   * Expectations on SQL commands: command text pattern, expected parameter
   * values and user-defined result set.
   */
  public static class Expectations {
    final Pattern myStmtPattern;
    Map<Integer, Object> myParameters;
    List<Map<String, Object>> myResultSet;
    private PreparedStatement myMock;

    /**
     * Initializes expectations with command text pattern. Pattern is a sequence
     * of space-delimited words, and it is expected that all specified words
     * will be in the actually issued SQL statement in the same order as in the
     * pattern.
     * 
     * Example: if pattern is "SELECT FROM Foo WHERE id" then statement
     * "SELECT * FROM Foo WHERE id=2" will match this expectation while
     * "SELECT Foo FROM Bar" will not.
     * 
     * @param pattern
     */
    Expectations(String pattern) {
      this(pattern, true);
    }

    Expectations(String pattern, boolean hasResultSet) {
      myStmtPattern = Pattern.compile(Joiner.on(".*").join(
          Collections2.transform(Arrays.asList(pattern.split("\\s+")), ESCAPE_REGEX))
          + ".*");      
      myResultSet = hasResultSet ? Lists.<Map<String, Object>>newArrayList() : null;
    }
    /**
     * Adds parameter expectations. If SQL statement has placeholders, mock
     * SqlApi will check if actual parameter values match the expected ones.
     * 
     * @param parameters
     *          a list of pairs "position", "value" where position is an integer
     *          number of parameter, and "value" is the expected value
     * @return updated expectations instance
     */
    public Expectations withParameters(Object... parameters) {
      assert parameters.length % 2 == 0;
      myParameters = Maps.newHashMap();
      for (int i = 0; i < parameters.length / 2; i++) {
        myParameters.put(Integer.parseInt(String.valueOf(parameters[i * 2])), parameters[i * 2 + 1]);
      }
      return this;
    }

    /**
     * Adds mock result set data. Each row is a mapping of column name to value.
     * 
     * @param rows
     *          result set rows
     * @return updated expectations instance
     */
    public Expectations withResult(Map<String, Object>... rows) {
      myResultSet = Arrays.asList(rows);
      return this;
    }
    
    boolean hasResultSet() {
      return myResultSet != null;
    }

    void setMock(PreparedStatement mock) {
      myMock = mock;
    }
    
    void verify() {
      if (myMock != null && !hasResultSet()) {
        try {
          Mockito.verify(myMock).execute();
        } catch (SQLException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    }
  }

  static {
    DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
  }
  private final Queue<Expectations> myExpectedData = Queues.newArrayDeque();
  private final List<Expectations> myVerificationList = Lists.newArrayList();
  
  public MockSqlApi() {
    SqlApi.setFactory(new SqlApi.Factory() {
      @Override
      public SqlApi create() {
        return MockSqlApi.this;
      }
    });
  }

  /**
   * Adds an expectation.
   * 
   * @param pattern
   *          SQL command pattern
   * @return new expectation object
   */
  public Expectations addExpectation(String pattern) {
    Expectations result = new Expectations(pattern);
    myExpectedData.add(result);
    return result;
  }

  public Expectations addInsertExpectation(String pattern) {
    Expectations result = new Expectations(pattern, false);
    myExpectedData.add(result);
    return result;
  }

  @Override
  public void dispose() {
    for (Expectations e : myVerificationList) {
      e.verify();
    }
 }

  @Override
  protected PreparedStatement prepareCall(String stmt) throws SQLException {
    final Expectations expectedData = myExpectedData.poll();
    Assert.assertTrue("Expected that string=" + stmt + " would match pattern=" + expectedData.myStmtPattern.pattern(),
        expectedData.myStmtPattern.matcher(stmt).matches());
    PreparedStatement mock = mock(PreparedStatement.class);
    if (expectedData.myParameters != null) {
      Answer<Void> verifyParameter = new Answer<Void>() {
        @Override
        public Void answer(InvocationOnMock invocation) throws Throwable {
          org.junit.Assert.assertEquals(expectedData.myParameters.get(invocation.getArguments()[0]),
              invocation.getArguments()[1]);
          return null;
        }
      };
      doAnswer(verifyParameter).when(mock).setInt(anyInt(), anyInt());
      doAnswer(verifyParameter).when(mock).setString(anyInt(), anyString());
      doAnswer(verifyParameter).when(mock).setDate(anyInt(), any(Date.class));
    }
    if (expectedData.hasResultSet()) {
      ResultSet mockResultSet = mockResultSet(expectedData.myResultSet);
      when(mock.executeQuery()).thenReturn(mockResultSet);
    } else {
      expectedData.setMock(mock);
      myVerificationList.add(expectedData);
    }
    return mock;
  }

  private ResultSet mockResultSet(final List<Map<String, Object>> data) throws SQLException {
    final AtomicInteger idx = new AtomicInteger(-1);
    final AtomicReference<Object> lastValue = new AtomicReference<Object>(null);
    ResultSet mock = mock(ResultSet.class);
    when(mock.next()).then(new Answer<Boolean>() {
      @Override
      public Boolean answer(InvocationOnMock arg0) throws Throwable {
        int idxValue = idx.incrementAndGet();
        return idxValue < data.size();
      }
    });
    when(mock.getString(anyString())).thenAnswer(new Answer<String>() {
      @Override
      public String answer(InvocationOnMock invocation) throws Throwable {
        Object mockValue = data.get(idx.get()).get(String.valueOf(invocation.getArguments()[0]));
        lastValue.set(mockValue);
        return mockValue == null ? null : mockValue.toString();
      }
    });
    when(mock.getInt(anyString())).thenAnswer(new Answer<Integer>() {
      @Override
      public Integer answer(InvocationOnMock invocation) throws Throwable {
        Object mockValue = data.get(idx.get()).get(String.valueOf(invocation.getArguments()[0]));
        lastValue.set(mockValue);
        return mockValue == null ? 0 : Integer.parseInt(mockValue.toString());
      }
    });
    when(mock.getDate(anyString())).thenAnswer(new Answer<Date>() {
      @Override
      public Date answer(InvocationOnMock invocation) throws Throwable {
        Object mockValue = data.get(idx.get()).get(String.valueOf(invocation.getArguments()[0]));
        lastValue.set(mockValue);
        if (mockValue == null) {
          return null;
        }
        return new Date(DATE_FORMAT.parse(mockValue.toString()).getTime());
      }
    });
    when(mock.wasNull()).then(new Answer<Boolean>() {
      @Override
      public Boolean answer(InvocationOnMock arg0) throws Throwable {
        return lastValue.get() == null;
      }
    });
    return mock;
  }
}  
