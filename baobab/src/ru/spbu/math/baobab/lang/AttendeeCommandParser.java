package ru.spbu.math.baobab.lang;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import ru.spbu.math.baobab.model.Attendee;
import ru.spbu.math.baobab.model.AttendeeExtent;
import ru.spbu.math.baobab.model.impl.AttendeeTypeConverter;

/**
 * Parser for define attendee command
 * 
 * @author vloginova
 */
public class AttendeeCommandParser extends Parser {

  private static final Pattern PATTERN_ENG = Pattern.compile(String.format(
      "^\\s*define\\s+attendee\\s+(%s)\\s+beeng\\s+(%s)\\s*$", ID_PATTERN, ATTENDEE_PATTERN_ENG));
  private static final Pattern PATTERN_RUS = Pattern.compile(String.format(
      "^\\s*определить\\s+участника\\s+(%s)\\s+как\\s+(%s)\\s*$", ID_PATTERN, ATTENDEE_PATTERN_RUS));

  private final AttendeeExtent myAttendeeExtent;

  public AttendeeCommandParser(AttendeeExtent attendeeExtent) {
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
   * defines time slot using AttendeeExtent
   * 
   * @param match matcher with matched command string
   */
  private void execute(Matcher match) {
    String id = unquote(match.group(1));
    Attendee.Type type = AttendeeTypeConverter.convertToAttendeeType(match.group(2));
    myAttendeeExtent.create(id, id, type);
  }
}
