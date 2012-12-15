package ru.spbu.math.baobab.lang;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ru.spbu.math.baobab.model.AuditoriumExtent;

/**
 * Parser for define auditorium command
 * 
 * @author dageev
 */
public class AuditoriumCommandParser extends Parser {

  private static final Pattern PATTERN_ENG = Pattern.compile(String.format(
      "^\\s*define\\s+auditorium\\s+(%s)(\\s+within\\s+(%s))?\\s*$", ID_PATTERN, ID_PATTERN));
  private static final Pattern PATTERN_RUS = Pattern.compile(String.format(
      "^\\s*определить\\s+помещение\\s+(%s)(\\s+на\\s+площадке\\s+(%s))?\\s*$", ID_PATTERN, ID_PATTERN));

  private final AuditoriumExtent myAuditoriumExtent;

  public AuditoriumCommandParser(AuditoriumExtent auditoriumExtent) {
    myAuditoriumExtent = auditoriumExtent;
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
   * defines auditorium using AuditoriumExtent
   * 
   * @param match matcher with matched command string
   */
  private void execute(Matcher match) {
    String auditorium_num = unquote(match.group(1));
    myAuditoriumExtent.create(auditorium_num, 0);
  }
}