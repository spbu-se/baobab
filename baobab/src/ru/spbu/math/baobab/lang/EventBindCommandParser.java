package ru.spbu.math.baobab.lang;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ru.spbu.math.baobab.model.Attendee;
import ru.spbu.math.baobab.model.AttendeeExtent;
import ru.spbu.math.baobab.model.Auditorium;
import ru.spbu.math.baobab.model.AuditoriumExtent;
import ru.spbu.math.baobab.model.TimeSlot;
import ru.spbu.math.baobab.model.TimeSlotExtent;
import ru.spbu.math.baobab.model.Topic;
import ru.spbu.math.baobab.model.TopicExtent;
import ru.spbu.math.baobab.model.impl.AttendeeListConverter;
import ru.spbu.math.baobab.model.impl.DateConverter;
import ru.spbu.math.baobab.model.impl.TimeSlotConverter;

/**
 * Parser for define course command
 * 
 * @author vloginova
 */
public class EventBindCommandParser extends Parser {
  private static final Pattern PATTERN_ENG = Pattern.compile(String.format(
      "^\\s*event\\s+(%s)\\s+holds on\\s+(%s)from\\s+(%s)till\\s+(%s)at\\s+(%s)(\\s+for\\s+(%s))?\\s*$", ID_PATTERN, TIMESLOT_KEY_ENG_PATTERN,
      DATE_PATTERN, DATE_PATTERN, ID_PATTERN, OWNERS_PATTERN));
  private static final Pattern PATTERN_RUS = Pattern.compile(String.format(
      "^\\s*событие\\s+(%s)\\s+состоится на\\s+(%s)с\\s+(%s)по\\s+(%s)в\\s+(%s)(\\s+for\\s+(%s))?\\s*$", ID_PATTERN, TIMESLOT_KEY_RUS_PATTERN,
      DATE_PATTERN, DATE_PATTERN, ID_PATTERN, OWNERS_PATTERN));
  private static final Pattern PATTERN_ENG_REPEATED = Pattern.compile(String.format(
      "^\\s*event\\s+(%s)\\s+holds on\\s+(%s)\\s+(%s)at\\s+(%s)(\\s+for\\s+(%s))?\\s*$", ID_PATTERN, ID_PATTERN,
      DATE_PATTERN, ID_PATTERN, OWNERS_PATTERN));
  private static final Pattern PATTERN_RUS_REPEATED = Pattern.compile(String.format(
      "^\\s*событие\\s+(%s)\\s+состоится на\\s+(%s)\\s+(%s)в\\s+(%s)(\\s+for\\s+(%s))?\\s*$", ID_PATTERN, ID_PATTERN,
      DATE_PATTERN, ID_PATTERN, OWNERS_PATTERN));

  private final TopicExtent myTopicExtent;
  private final AttendeeExtent myAttendeeExtent;
  private final AuditoriumExtent myAuditoriumExtent;
  private final TimeSlotExtent myTimeSlotExtent;

  public EventBindCommandParser(TopicExtent topicExtent, AttendeeExtent attendeeExtent,  AuditoriumExtent auditoriumExtent, TimeSlotExtent timeSlotExtent) {
    myTopicExtent = topicExtent;
    myAttendeeExtent = attendeeExtent;
    myAuditoriumExtent = auditoriumExtent;
    myTimeSlotExtent = timeSlotExtent;
  }

  @Override
  public boolean parse(String command) {
    return tryParse(PATTERN_RUS, command) || tryParse(PATTERN_ENG, command) ||
        tryParseNotRepeated(PATTERN_ENG_REPEATED, command) || tryParseNotRepeated(PATTERN_RUS_REPEATED, command);
  }

  private boolean tryParse(Pattern pattern, String command) {
    Matcher commandMatch = pattern.matcher(command);
    if (commandMatch.matches()) {
      execute(commandMatch);
      return true;
    }
    return false;
  }
  
  private boolean tryParseNotRepeated(Pattern pattern, String command) {
    Matcher commandMatch = pattern.matcher(command);
    if (commandMatch.matches()) {
      executeNotRepeated(commandMatch);
      return true;
    }
    return false;
  }

  /**
   * defines time slot using AttendeeExtent
   * 
   * @param match matcher with matched command string
   */
  private void execute(Matcher match) {
    String id = match.group(1);
    TimeSlot timeSlot = TimeSlotConverter.convertToTimeSlot(match.group(2), myTimeSlotExtent);
    Date from = DateConverter.convertToDate(match.group(3));
    Date to = DateConverter.convertToDate(match.group(3));
    Auditorium auditorium = myAuditoriumExtent.find(match.group(2));
    List<Attendee> participants = AttendeeListConverter.convertToList(match.group(6), myAttendeeExtent);
    Topic topic = findById(id, myTopicExtent.getAll());
    for (Attendee att : participants) {
      topic.addAttendee(att);
    }
    topic.addAllEvents(from, to, timeSlot, auditorium);
  }
  
  private void executeNotRepeated(Matcher match) {
    String id = match.group(1);
    Date date = DateConverter.convertToDate(match.group(3));
    TimeSlot timeSlot = TimeSlotConverter.convertToTimeSlot(match.group(2), date, myTimeSlotExtent);
    Auditorium auditorium = myAuditoriumExtent.find(match.group(4));
    List<Attendee> participants = AttendeeListConverter.convertToList(match.group(6), myAttendeeExtent);
    Topic topic = findById(id, myTopicExtent.getAll());
    for (Attendee att : participants) {
      topic.addAttendee(att);
    }
    topic.addEvent(date, timeSlot, auditorium);
  }

  private Topic findById(String id, Collection<Topic> topics) {
    for (Topic topic : topics) {
      if (topic.getID().equals(id)) {
        return topic;
      }
    }
    throw new RuntimeException("The Topic with this ID doesn't exist");
  }
}
