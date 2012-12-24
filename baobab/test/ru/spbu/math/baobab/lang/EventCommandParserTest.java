package ru.spbu.math.baobab.lang;

import java.util.Collection;

import com.google.common.collect.Sets;

import ru.spbu.math.baobab.model.Attendee;
import ru.spbu.math.baobab.model.Attendee.Type;
import ru.spbu.math.baobab.model.AttendeeExtent;
import ru.spbu.math.baobab.model.AuditoriumExtent;
import ru.spbu.math.baobab.model.Event;
import ru.spbu.math.baobab.model.TimeSlotExtent;
import ru.spbu.math.baobab.model.Topic;
import ru.spbu.math.baobab.model.TopicExtent;
import ru.spbu.math.baobab.server.AttendeeExtentImpl;
import ru.spbu.math.baobab.server.AuditoriumExtentImpl;
import ru.spbu.math.baobab.server.TimeSlotExtentImpl;
import ru.spbu.math.baobab.server.TopicExtentImpl;
import junit.framework.TestCase;

public class EventCommandParserTest extends TestCase {
  public void testBindAndDeclareCommandCorrectnessEng() {
    TopicExtent topicExtent = new TopicExtentImpl();
    AttendeeExtent attendeeExtent = new AttendeeExtentImpl();
    AuditoriumExtent auditoriumExtent = new AuditoriumExtentImpl();
    TimeSlotExtent timeSlotExtent = new TimeSlotExtentImpl();
    TimeSlotCommandParser timeSlotParser = new TimeSlotCommandParser(timeSlotExtent);

    Attendee teacher1 = attendeeExtent.create("Teacher1", "Teacher1", Type.TEACHER);
    Attendee student1 = attendeeExtent.create("Student1", "Student1", Type.STUDENT);
    Attendee student2 = attendeeExtent.create("Student2", "Student2", Type.STUDENT);
    timeSlotParser.parse("define timeslot BP 12:12 to 12:40 on Monday");
    auditoriumExtent.create("06", 100);

    EventBindCommandParser parserBind = new EventBindCommandParser(topicExtent, attendeeExtent, auditoriumExtent,
        timeSlotExtent);
    EventDeclareCommandParser parserDeclare = new EventDeclareCommandParser(topicExtent, attendeeExtent);
    assertTrue(parserDeclare.parse("define exam Math"));
    Topic math = topicExtent.find("Math");
    assertNotNull(math);
    assertTrue(math.getAttendees().isEmpty());
    
    assertTrue(parserDeclare.parse("define lectures Algebra for Student1,Student2 owned by Teacher1"));
    Topic algebra = topicExtent.find("Algebra");
    assertNotNull(algebra);
    assertEquals(Sets.newHashSet(teacher1), Sets.newHashSet(algebra.getOwners()));
    assertEquals(Sets.newHashSet(student1, student2), Sets.newHashSet(algebra.getAttendees()));
    
    assertTrue(parserBind
        .parse("event Math holds on BP Monday from 2012-11-11 till 2012-12-12 at 06 for Student1,Student2"));
    Collection<Event> mathEvents = math.getEvents();
    assertFalse(mathEvents.isEmpty());
    for (Event mathEvent : mathEvents) {
      assertEquals(Sets.newHashSet(student1, student2), Sets.newHashSet(mathEvent.getAttendees()));
    }
    assertTrue(parserBind.parse("event Algebra holds on BP 2012-12-17 at 06"));
  }

  public void testBindAndDeclareCommandCorrectnessRus() {
    TopicExtent topicExtent = new TopicExtentImpl();
    AttendeeExtent attendeeExtent = new AttendeeExtentImpl();
    AuditoriumExtent auditoriumExtent = new AuditoriumExtentImpl();
    TimeSlotExtent timeSlotExtent = new TimeSlotExtentImpl();
    TimeSlotCommandParser timeSlotParser = new TimeSlotCommandParser(timeSlotExtent);

    attendeeExtent.create("Teacher1", "Teacher1", Type.TEACHER);
    attendeeExtent.create("Teacher2", "Teacher2", Type.TEACHER);
    attendeeExtent.create("Student1", "Student1", Type.STUDENT);
    attendeeExtent.create("Student2", "Student2", Type.STUDENT);
    timeSlotParser.parse("define timeslot BP 12:12 to 12:40 on Monday");
    auditoriumExtent.create("06", 100);

    EventBindCommandParser parserBind = new EventBindCommandParser(topicExtent, attendeeExtent, auditoriumExtent,
        timeSlotExtent);
    EventDeclareCommandParser parserDeclare = new EventDeclareCommandParser(topicExtent, attendeeExtent);
    assertTrue(parserDeclare.parse("определить экзамен Math"));
    assertTrue(parserDeclare.parse("определить экзамен \"exam01\" \"Дифференциальные уравнения\" владельцы \"Teacher1\""));
    assertTrue(parserDeclare.parse("определить лекция Algebra для Student1,Student2 владельцы Teacher1"));
    assertTrue(parserBind.parse("событие Math состоится на BP пн с 2012-11-11 по 2012-12-12 в 06 для Student1,Student2"));
    assertTrue(parserBind.parse("событие Algebra состоится на BP 2012-12-17 в 06"));
  }
  
  public void testBindAndDeclareQuotedCommandCorrectnessEng() {
    TopicExtent topicExtent = new TopicExtentImpl();
    AttendeeExtent attendeeExtent = new AttendeeExtentImpl();
    AuditoriumExtent auditoriumExtent = new AuditoriumExtentImpl();
    TimeSlotExtent timeSlotExtent = new TimeSlotExtentImpl();
    TimeSlotCommandParser timeSlotParser = new TimeSlotCommandParser(timeSlotExtent);

    attendeeExtent.create("Teacher1", "Teacher1", Type.TEACHER);
    attendeeExtent.create("Student1", "Student1", Type.STUDENT);
    attendeeExtent.create("Student2", "Student2", Type.STUDENT);
    timeSlotParser.parse("define timeslot \"BP\" 12:12 to 12:40 on Monday");
    auditoriumExtent.create("06", 100);

    EventBindCommandParser parserBind = new EventBindCommandParser(topicExtent, attendeeExtent, auditoriumExtent,
        timeSlotExtent);
    EventDeclareCommandParser parserDeclare = new EventDeclareCommandParser(topicExtent, attendeeExtent);
    assertTrue(parserDeclare.parse("define exam \"Math\""));
    assertTrue(parserDeclare.parse("define lectures \"Algebra\" for \"Student1\",\"Student2\" owned by \"Teacher1\""));
    assertTrue(parserBind
        .parse("event \"Math\" holds on \"BP\" Monday from 2012-11-11 till 2012-12-12 at \"06\" for \"Student1\",\"Student2\""));
    assertTrue(parserBind.parse("event \"Algebra\" holds on \"BP\" 2012-12-17 at \"06\""));
  }
}
