package ru.spbu.math.baobab.server;

import ru.spbu.math.baobab.model.Auditorium;
import junit.framework.TestCase;

/**
 * Some tests for AuditoriumImpl
 * 
 * @author aoool
 */
public class AuditoriumImplTest extends TestCase {

  public void testGetID() {
    Auditorium auditorium = new AuditoriumImpl("1", 20);
    assertEquals(auditorium.getID(), "1");
  }

  public void testGetCapacity() {
    Auditorium auditorium1 = new AuditoriumImpl("1", 20);
    assertEquals(auditorium1.getCapacity(), 20);
    Auditorium auditorium2 = new AuditoriumImpl("2", 0);
    assertEquals(auditorium2.getCapacity(), 0);
  }
}
