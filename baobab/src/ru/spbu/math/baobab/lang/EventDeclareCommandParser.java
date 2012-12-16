package ru.spbu.math.baobab.lang;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ru.spbu.math.baobab.model.Attendee;
import ru.spbu.math.baobab.model.Topic;
import ru.spbu.math.baobab.model.Topic.Type;
import ru.spbu.math.baobab.model.AttendeeExtent;
import ru.spbu.math.baobab.model.TopicExtent;
import ru.spbu.math.baobab.model.impl.AttendeeListConverter;
import ru.spbu.math.baobab.model.impl.TopicTypeConverter;

/**
 * Parser for define action command
 * 
 * @author vloginova
 */
public class EventDeclareCommandParser extends Parser {
  private static final Pattern PATTERN_ENG = Pattern.compile(String.format(
      "^\\s*define\\s+(%s)\\s+(%s)(\\s+(%s))?(\\s+for\\s+(%s))?(\\s+owned by\\s+(%s))?\\s*$", TOPIC_TYPE_PATTERN_ENG,
      ID_PATTERN, ID_PATTERN, OWNERS_PATTERN, OWNERS_PATTERN));
  private static final Pattern PATTERN_RUS = Pattern.compile(String.format(
      "^\\s*определить\\s+(%s)\\s+(%s)(\\s+(%s))?(\\s+для\\s+(%s))?(\\s+владельцы\\s+(%s))?\\s*$", TOPIC_TYPE_PATTERN_RUS,
      ID_PATTERN, ID_PATTERN, OWNERS_PATTERN, OWNERS_PATTERN));

  private final TopicExtent myTopicExtent;
  private final AttendeeExtent myAttendeeExtent;

  public EventDeclareCommandParser(TopicExtent topicExtent, AttendeeExtent attendeeExtent) {
    myTopicExtent = topicExtent;
    myAttendeeExtent = attendeeExtent;
  }

  @Override
  public boolean parse(String command) {
    return tryParse(PATTERN_RUS, command) || tryParse(PATTERN_ENG, command);
  }

  private boolean tryParse(Pattern pattern, String command) {
    Matcher commandMatch = pattern.matcher(command);
    if (commandMatch.matches()) {
      execute(commandMatch);
      return true;
    }
    return false;
  }

  /**
   * defines action using TopicExtent
   * 
   * @param match matcher with matched command string
   */
  private void execute(Matcher match) {
    Type type = TopicTypeConverter.convertToTopicType(match.group(1));
    String id = match.group(2);
    String name = match.group(4);
    List<Attendee> participants = AttendeeListConverter.convertToList(match.group(6), myAttendeeExtent);
    List<Attendee> owners = AttendeeListConverter.convertToList(match.group(8), myAttendeeExtent);
    Topic topic = myTopicExtent.createTopic(id, type, name == null ? id : name);
    for (Attendee att : participants) {
      topic.addAttendee(att);
    }
    for (Attendee att : owners) {
      topic.addOwner(att);
    }
  }
}
