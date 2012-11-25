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
    assertTrue(attendee.getType() == Attendee.Type.FREE_FORM_GROUP);
  }

  public void testCommandCorrectnessRus() {
    AttendeeExtent attendeeExtent = new AttendeeExtentImpl();
    AttendeeCommandParser parser = new AttendeeCommandParser(attendeeExtent);
    assertTrue(parser.parse("определить участника att1 как студента"));
    Attendee attendee = attendeeExtent.find("att1");
    assertTrue(attendee.getType() == Attendee.Type.STUDENT);
  }

  public void testIncorrectCommand() {
    AttendeeExtent attendeeExtent = new AttendeeExtentImpl();
    AttendeeCommandParser parser = new AttendeeCommandParser(attendeeExtent);
    assertFalse(parser.parse("define attendee beeng free form group"));
  }
}
