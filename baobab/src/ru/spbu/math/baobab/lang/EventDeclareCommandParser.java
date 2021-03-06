package ru.spbu.math.baobab.lang;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Strings;

import ru.spbu.math.baobab.model.Attendee;
import ru.spbu.math.baobab.model.Topic;
import ru.spbu.math.baobab.model.Topic.Type;
import ru.spbu.math.baobab.model.AttendeeExtent;
import ru.spbu.math.baobab.model.TopicExtent;
import ru.spbu.math.baobab.model.impl.AttendeeListConverter;
import ru.spbu.math.baobab.model.impl.TopicTypeConverter;

/**
 * Parser for define topic command
 * 
 * @author vloginova
 */
public class EventDeclareCommandParser extends Parser {
  private static final Pattern PATTERN_ENG = Pattern.compile(String.format(
      "^\\s*define\\s+(%s)\\s+(%s)(\\s+(%s))?(\\s+for\\s+(%s))?(\\s+owned by\\s+(%s))?\\s*$", TOPIC_TYPE_PATTERN_ENG,
      ID_PATTERN, ID_PATTERN, ID_LIST_PATTERN, ID_LIST_PATTERN));
  private static final Pattern PATTERN_RUS = Pattern.compile(String.format(
      "^\\s*определить\\s+(%s)\\s+(%s)(\\s+(%s))?(\\s+для\\s+(%s))?(\\s+владельцы\\s+(%s))?\\s*$",
      TOPIC_TYPE_PATTERN_RUS, ID_PATTERN, ID_PATTERN, ID_LIST_PATTERN, ID_LIST_PATTERN));

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
   * defines topic using Extents
   * 
   * @param match matcher with matched command string
   */
  private void execute(Matcher match) {
    Type type = TopicTypeConverter.convertToTopicType(match.group(1));
    String id = unquote(match.group(2));
    String name = unquote(match.group(4));
    List<Attendee> participants = (Strings.isNullOrEmpty(match.group(6))) 
        ? Collections.<Attendee>emptyList() : AttendeeListConverter.convertToList(match.group(6), myAttendeeExtent);
    List<Attendee> owners = (Strings.isNullOrEmpty(match.group(8)))
        ? Collections.<Attendee>emptyList() : AttendeeListConverter.convertToList(match.group(8), myAttendeeExtent);
    Topic topic = myTopicExtent.createTopic(id, type, name == null ? id : name);
    for (Attendee att : participants) {
      topic.addAttendee(att);
    }
    for (Attendee att : owners) {
      topic.addOwner(att);
    }
  }
}
