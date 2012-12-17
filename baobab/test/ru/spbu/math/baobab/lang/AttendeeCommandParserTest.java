package ru.spbu.math.baobab.lang;

import ru.spbu.math.baobab.model.Attendee;
import ru.spbu.math.baobab.model.AttendeeExtent;
import ru.spbu.math.baobab.server.AttendeeExtentImpl;
import junit.framework.TestCase;

/**
 * Tests for AttendeeCommandParser
 * 
 * @author vloginova
 */
public class AttendeeCommandParserTest extends TestCase {
  public void testCommandCorrectnessEng() {
    AttendeeExtent attendeeExtent = new AttendeeExtentImpl();
    AttendeeCommandParser parser = new AttendeeCommandParser(attendeeExtent);
    assertTrue(parser.parse("define attendee att1  beeng free form group"));
    Attendee attendee = attendeeExtent.find("att1");
    assertEquals(attendee.getType(), Attendee.Type.FREE_FORM_GROUP);
  }

  public void testCommandCorrectnessRus() {
    AttendeeExtent attendeeExtent = new AttendeeExtentImpl();
    AttendeeCommandParser parser = new AttendeeCommandParser(attendeeExtent);
    assertTrue(parser.parse("определить участника участник1 как студента"));
    Attendee attendee = attendeeExtent.find("участник1");
    assertEquals(attendee.getType(), Attendee.Type.STUDENT);
  }

  public void testQuotedIdentifier() {
    AttendeeExtent attendeeExtent = new AttendeeExtentImpl();
    AttendeeCommandParser parser = new AttendeeCommandParser(attendeeExtent);
    assertTrue(parser.parse("определить участника \"Барашев Д.В.\" как преподавателя"));
    Attendee attendee = attendeeExtent.find("Барашев Д.В.");
    assertNotNull(attendee);
    assertEquals(Attendee.Type.TEACHER, attendee.getType());
  }

  public void testIncorrectCommand() {
    AttendeeExtent attendeeExtent = new AttendeeExtentImpl();
    AttendeeCommandParser parser = new AttendeeCommandParser(attendeeExtent);
    assertFalse(parser.parse("define attendee beeng free form group"));
  }
  
}
