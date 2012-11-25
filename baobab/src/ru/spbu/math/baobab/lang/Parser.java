package ru.spbu.math.baobab.lang;

/**
 * Abstract class for all command parsers
 * 
 * @author vloginova
 */
public abstract class Parser {
  // group patterns
  protected final static String ID_PATTERN = "\\w+";
  protected final static String TIME_PATTERN = "\\d{1,2}:\\d{2}";
  protected final static String EVEN_ODD_PATTERN_ENG = "odd|even";
  protected final static String EVEN_ODD_PATTERN_RUS = "четный|нечетный";
  protected final static String WEEKDAY_PATTERN_ENG = "Monday|Tuesday|Wednesday|Thursday|Friday|Saturday|Sunday|Mo|Tu|We|Th|Fr|Sa|Su";
  protected final static String WEEKDAY_PATTERN_RUS = "понедельник|вторник|среда|четверг|пятница|суббота|воскресенье|пн|вт|ср|чт|пт|сб|вс";
  protected final static String ATTENDEE_PATTERN_ENG = "chair|free form group|student|teacher|academic group";
  protected final static String ATTENDEE_PATTERN_RUS = "студента|преподавателя|учебную группу|кафедру|коллектив";

  /**
   * parses command in baobab language and executes it
   * 
   * @param command command in baobab language
   * @return true in case of success parsing
   */
  public abstract boolean parse(String command);
}
