package ru.spbu.math.baobab.lang;

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
 * Parser for binding events to topic command
 * 
 * @author vloginova
 */
public class EventBindCommandParser extends Parser {
  private static final Pattern PATTERN_ENG = Pattern.compile(String.format(
      "^\\s*event\\s+(%s)\\s+holds on\\s+(%s)\\s+from\\s+(%s)\\s+till\\s+(%s)\\s+at\\s+(%s)(\\s+for\\s+(%s))?\\s*$",
      ID_PATTERN, TIMESLOT_KEY_ENG_PATTERN, DATE_PATTERN, DATE_PATTERN, ID_PATTERN, ATTENDEES_PATTERN));
  private static final Pattern PATTERN_RUS = Pattern.compile(String.format(
      "^\\s*событие\\s+(%s)\\s+состоится на\\s+(%s)\\s+с\\s+(%s)\\s+по\\s+(%s)\\s+в\\s+(%s)(\\s+для\\s+(%s))?\\s*$",
      ID_PATTERN, TIMESLOT_KEY_RUS_PATTERN, DATE_PATTERN, DATE_PATTERN, ID_PATTERN, ATTENDEES_PATTERN));
  private static final Pattern PATTERN_ENG_WITHOUT_REPEATING = Pattern.compile(String.format(
      "^\\s*event\\s+(%s)\\s+holds on\\s+(%s)\\s+(%s)\\s+at\\s+(%s)(\\s+for\\s+(%s))?\\s*$", ID_PATTERN, ID_PATTERN,
      DATE_PATTERN, ID_PATTERN, ATTENDEES_PATTERN));
  private static final Pattern PATTERN_RUS_WITHOUT_REPEATING = Pattern.compile(String.format(
      "^\\s*событие\\s+(%s)\\s+состоится на\\s+(%s)\\s+(%s)\\s+в\\s+(%s)(\\s+для\\s+(%s))?\\s*$", ID_PATTERN,
      ID_PATTERN, DATE_PATTERN, ID_PATTERN, ATTENDEES_PATTERN));

  private final TopicExtent myTopicExtent;
  private final AttendeeExtent myAttendeeExtent;
  private final AuditoriumExtent myAuditoriumExtent;
  private final TimeSlotExtent myTimeSlotExtent;

  public EventBindCommandParser(TopicExtent topicExtent, AttendeeExtent attendeeExtent,
      AuditoriumExtent auditoriumExtent, TimeSlotExtent timeSlotExtent) {
    myTopicExtent = topicExtent;
    myAttendeeExtent = attendeeExtent;
    myAuditoriumExtent = auditoriumExtent;
    myTimeSlotExtent = timeSlotExtent;
  }

  @Override
  public boolean parse(String command) {
    return tryParse(PATTERN_RUS, command) || tryParse(PATTERN_ENG, command)
        || tryParseNotRepeated(PATTERN_ENG_WITHOUT_REPEATING, command)
        || tryParseNotRepeated(PATTERN_RUS_WITHOUT_REPEATING, command);
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
      executeWithoutRepeating(commandMatch);
      return true;
    }
    return false;
  }

  /**
   * binds events to topics using Extents for repeating events
   * 
   * @param match matcher with matched command string
   */
  private void execute(Matcher match) {
    String id = match.group(1);
    TimeSlot timeSlot = TimeSlotConverter.convertToTimeSlot(match.group(2), myTimeSlotExtent);
    Date from = DateConverter.convertToDate(match.group(5));
    Date to = DateConverter.convertToDate(match.group(6));
    Auditorium auditorium = myAuditoriumExtent.find(match.group(7));
    List<Attendee> participants = AttendeeListConverter.convertToList(match.group(9), myAttendeeExtent);
    Topic topic = findTopicById(id, myTopicExtent);
    for (Attendee att : participants) {
      topic.addAttendee(att);
    }
    topic.addAllEvents(from, to, timeSlot, auditorium);
  }

  /**
   * binds events to topics using Extents for events without repeating
   * 
   * @param match matcher with matched command string
   */
  private void executeWithoutRepeating(Matcher match) {
    String id = match.group(1);
    Date date = DateConverter.convertToDate(match.group(3));
    TimeSlot timeSlot = TimeSlotConverter.convertToTimeSlot(match.group(2), date, myTimeSlotExtent);
    Auditorium auditorium = findAuditoriumById(match.group(4), myAuditoriumExtent);
    List<Attendee> participants = AttendeeListConverter.convertToList(match.group(6), myAttendeeExtent);
    Topic topic = findTopicById(id, myTopicExtent);
    for (Attendee att : participants) {
      topic.addAttendee(att);
    }
    topic.addEvent(date, timeSlot, auditorium);
  }

  /**
   * finds auditorium by Id using AuditoriumExtent. Throws RuntimeException in case of absence of it
   * 
   * @param id auditorium id
   * @param auditoriumExtent extent for searching
   * @return found auditorium
   */
  private Auditorium findAuditoriumById(String id, AuditoriumExtent auditoriumExtent) {
    Auditorium auditorium = myAuditoriumExtent.find(id);
    if (auditorium == null) {
      throw new RuntimeException("The  Auditorium with this ID doesn't exist");
    }
    return auditorium;
  }

  /**
   * finds topic by Id using TopicExtent. Throws RuntimeException in case of absence of it
   * 
   * @param id topic id
   * @param topicExtent extent for searching
   * @return found topic
   */
  private Topic findTopicById(String id, TopicExtent topicExtent) {
    for (Topic topic : topicExtent.getAll()) {
      if (topic.getID().equals(id)) {
        return topic;
      }
    }
    throw new RuntimeException("The Topic with this ID doesn't exist");
  }
}