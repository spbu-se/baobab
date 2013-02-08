package ru.spbu.math.baobab.lang;

import java.util.Arrays;
import java.util.List;

import ru.spbu.math.baobab.model.AttendeeExtent;
import ru.spbu.math.baobab.model.AuditoriumExtent;
import ru.spbu.math.baobab.model.TimeSlotExtent;
import ru.spbu.math.baobab.model.TopicExtent;
import ru.spbu.math.baobab.server.AttendeeExtentImpl;
import ru.spbu.math.baobab.server.AuditoriumExtentImpl;
import ru.spbu.math.baobab.server.TimeSlotExtentImpl;
import ru.spbu.math.baobab.server.TopicExtentImpl;
import ru.spbu.math.baobab.server.sql.SqlApi;

/**
 * Script interpreter processes the lines satisfying specifications of the Baobab language and executes them
 * 
 * @author ragozina.anastasiya, vloginova
 */
public class ScriptInterpreter {
  private final List<Parser> myParsers;

  public ScriptInterpreter() {
    this(getNewParserList());
  }

  public ScriptInterpreter(List<Parser> parsers) {
    myParsers = parsers;
  }

  private static List<Parser> getNewParserList() {
    TopicExtent topicExtent = new TopicExtentImpl();
    AttendeeExtent attendeeExtent = new AttendeeExtentImpl();
    AuditoriumExtent auditoriumExtent = new AuditoriumExtentImpl();
    TimeSlotExtent timeSlotExtent = new TimeSlotExtentImpl();

    return Arrays.asList(new TimeSlotCommandParser(timeSlotExtent), new AuditoriumCommandParser(auditoriumExtent),
        new EventDeclareCommandParser(topicExtent, attendeeExtent), new EventBindCommandParser(topicExtent,
            attendeeExtent, auditoriumExtent, timeSlotExtent));
  }

  /**
   * Receive command and process it
   * 
   * @param command some command in baobab language
   */
  public boolean process(String command) {
    if (SqlApi.PATTERN_WHITESPACE.matcher(command).matches() || SqlApi.PATTERN_COMMENT.matcher(command).matches()) {
      return true;
    }
    for (Parser p : myParsers) {
      if (p.parse(command)) {
        return true;
      }
    }
    return false;
  }
}
