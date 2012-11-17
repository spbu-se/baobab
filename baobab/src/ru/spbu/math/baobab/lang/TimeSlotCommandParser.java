package ru.spbu.math.baobab.lang;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import ru.spbu.math.baobab.model.impl.OddEvenWeekConverter;
import ru.spbu.math.baobab.model.impl.TimeInstantConverter;
import ru.spbu.math.baobab.model.impl.WeekDayConverter;
import ru.spbu.math.baobab.model.TimeSlotExtent;
import ru.spbu.math.baobab.model.TimeInstant;
import ru.spbu.math.baobab.model.EvenOddWeek;

/**
 * Parser for define timeslot command
 * 
 * @author vloginova
 */
public class TimeSlotCommandParser extends Parser {

  private static final Pattern PATTERN_ENG = Pattern.compile(String.format(
      "^\\s?define\\s+timeslot\\s+(%s)\\s+(%s)\\s+to\\s+(%s)\\s+on\\s+(%s)\\s+(%s)\\s?$", ID_PATTERN, TIME_PATTERN,
      TIME_PATTERN, EVEN_ODD_PATTERN_ENG, WEEKDAY_PATTERN_ENG));
  private static final Pattern PATTERN_RUS = Pattern.compile(String.format(
      "^\\s?определить\\s+интервал\\s+(%s)\\s+от\\s+(%s)\\s+до\\s+(%s)\\s+в\\s+(%s)\\s+(%s)\\s?$", ID_PATTERN, TIME_PATTERN,
      TIME_PATTERN, EVEN_ODD_PATTERN_RUS, WEEKDAY_PATTERN_RUS));

  private final TimeSlotExtent myTimeSlotExtent;

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
   * @param match matcher with matched command string
   */
  private void execute(Matcher match) {
    String id = match.group(1);
    TimeInstant start = TimeInstantConverter.convertToTimeInstant(match.group(2));
    TimeInstant finish = TimeInstantConverter.convertToTimeInstant(match.group(3));
    EvenOddWeek flashing = OddEvenWeekConverter.convertToOddEvenWeek(match.group(4));
    Integer weekday = WeekDayConverter.convertToInt(match.group(5));
    myTimeSlotExtent.create(id, start, finish, weekday, flashing);
  }
}
