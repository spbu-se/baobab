package ru.spbu.math.baobab.server;

import java.util.Collection;
import java.util.HashSet;

import ru.spbu.math.baobab.model.Auditorium;
import ru.spbu.math.baobab.model.AuditoriumExtent;
import junit.framework.TestCase;

/**
 * Some tests for AuditoriumExtentImpl
 * 
 * @author aoool
 */
public class AuditoriumExtentImplTest extends TestCase {

  public void testCreate() {
    AuditoriumExtent extent = new AuditoriumExtentImpl();
    extent.create("1", 20);
    try {
      extent.create("1", 20);
      fail("Expected RuntimeException.");
    } catch (RuntimeException e) {
      // OK: can't create new Auditorium with ID which already exists.
    }
    try {
      extent.create("3", -1);
      fail("Expected IllegalArgumentException.");
    } catch (IllegalArgumentException e) {
      // OK: can't create new Auditorium with capacity < 0.
    }
  }

  public void testGetAll() {
    AuditoriumExtent extent = new AuditoriumExtentImpl();
    Collection<Auditorium> auditoriums = new HashSet<Auditorium>();
    auditoriums.add(extent.create("1", 20));
    auditoriums.add(extent.create("2", 15));
    auditoriums.add(extent.create("3", 2));
    assertTrue(auditoriums.containsAll(extent.getAll()));
  }

  public void testFind() {
    AuditoriumExtent extent = new AuditoriumExtentImpl();
    extent.create("1", 20);
    extent.create("2", 30);
    Auditorium auditorium = extent.create("3", 12);
    assertEquals(extent.find("3"), auditorium);
    assertNull(extent.find("4"));
  }
}
