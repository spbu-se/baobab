package ru.spbu.math.baobab.lang;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import ru.spbu.math.baobab.model.impl.*;
import ru.spbu.math.baobab.model.*;

/**
 * Parser for define timeslot command
 * 
 * @author Vita
 */
public class TimeSlotCommandParser extends Parser {

  private static final Pattern PATTERN_RUS = Pattern.compile(String.format(
      "^define\\stimeslot\\s(%s)\\s(%s)\\sto\\s(%s)\\son\\s(%s)\\s(%s)$", ID_PATTERN, TIME_PATTERN, TIME_PATTERN,
      EVEN_ODD_PATTERN_ENG, WEEKDAY_PATTERN_ENG));
  private static final Pattern PATTERN_ENG = Pattern.compile(String.format(
      "^определить\\sинтервал\\s(%s)\\sот\\s(%s)\\sдо\\s(%s)\\sв\\s(%s)\\s(%s)$", ID_PATTERN, TIME_PATTERN,
      TIME_PATTERN, EVEN_ODD_PATTERN_RUS, WEEKDAY_PATTERN_RUS));

  private TimeSlotExtent myTimeSlotExtent;

  public TimeSlotCommandParser(TimeSlotExtent timeSlotExtent) {
    myTimeSlotExtent = timeSlotExtent;
  }

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
   * defines time slot using TimeSlotExtent
   * 
   * @param match
   *          matcher with matched command string
   */
  private void execute(Matcher match) {
    String id = match.group(1);
    TimeInstant start = TimeInstantConverter.convertToTimeInstant(match.group(2));
    TimeInstant finish = TimeInstantConverter.convertToTimeInstant(match.group(3));
    EvenOddWeek flashing = OddEvenWeekConverter.convertToOddEvenWeek(match.group(4));
    Integer weekday = WeekdayConverter.convertToInt(match.group(5));
    myTimeSlotExtent.create(id, start, finish, weekday, flashing);
  }
}
