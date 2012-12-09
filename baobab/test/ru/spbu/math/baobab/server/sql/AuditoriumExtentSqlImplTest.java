package ru.spbu.math.baobab.server.sql;

import java.sql.SQLException;
import java.util.List;
import com.google.common.collect.Lists;
import ru.spbu.math.baobab.model.Auditorium;
import ru.spbu.math.baobab.model.AuditoriumExtent;

/**
 * Tests for AuditoriumExtendSqlImpl
 * 
 * @author vloginova
 */
public class AuditoriumExtentSqlImplTest extends SqlTestCase {

  public void testCreate() {
    AuditoriumExtent auditoriumExtent = new AuditoriumExtentSqlImpl();
    expectSql("SELECT Auditorium WHERE num").withParameters(1, "404");
    expectSql("INSERT Auditorium num capacity").withParameters(1, "404", 2, 40);
    Auditorium auditorium = auditoriumExtent.create("404", 40);
    assertEquals(auditorium.getID(), "404");
    assertEquals(auditorium.getCapacity(), 40);

    try {
      expectSql("SELECT Auditorium WHERE num").withParameters(1, "404").withResult(row("num", "404", "capacity", 40));
      auditoriumExtent.create("404", 30);
      fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      // can't create with existing name
    }
  }

  public void testFind() {
    AuditoriumExtent attendeeExtent = new AuditoriumExtentSqlImpl();
    Auditorium auditorium = new AuditoriumSqlImpl("404", 35);
    // test find method when auditorium with the given id doen't exist
    expectSql("SELECT capacity FROM Auditorium WHERE num").withParameters(1, "555");
    assertNull(attendeeExtent.find("555"));
    // test find method when auditorium with the given id exists
    expectSql("SELECT capacity FROM Auditorium WHERE num").withParameters(1, "404").withResult(
        row("num", "404", "capacity", 35));
    assertTrue(attendeeExtent.find("404").equals(auditorium));
  }

  public void testGetAll() throws SQLException {
    expectSql("SELECT Auditorium WHERE num").withParameters(1, "111");
    expectSql("INSERT Auditorium num capacity").withParameters(1, "111", 2, 30);
    expectSql("SELECT Auditorium WHERE num").withParameters(1, "222");
    expectSql("INSERT Auditorium num capacity").withParameters(1, "222", 2, 50);

    AuditoriumExtent auditoriumExtent = new AuditoriumExtentSqlImpl();
    List<Auditorium> list = Lists.newArrayList();
    list.add(auditoriumExtent.create("111", 30));
    list.add(auditoriumExtent.create("222", 50));

    expectQuery("SELECT * FROM Auditorium", row("num", "111", "capacity", 30), row("num", "222", "capacity", 50));
    List<Auditorium> dbList = (List<Auditorium>) auditoriumExtent.getAll();

    assertEquals(list, dbList);
  }
}
