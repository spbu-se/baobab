package ru.spbu.math.baobab.lang;

import java.util.Arrays;
import java.util.List;

/**
 * Abstract class for all command parsers
 * 
 * @author vloginova
 */
public abstract class Parser {
  // baobab language literals: days of week
  public static final List<String> MONDAY = Arrays.asList("Monday", "Mo", "понедельник", "пн");
  public static final List<String> TUESDAY = Arrays.asList("Tuesday", "Tu", "вторник", "вт");
  public static final List<String> WEDNESDAY = Arrays.asList("Wednesday", "We", "среда", "ср");
  public static final List<String> THURSDAY = Arrays.asList("Thursday", "Th", "четверг", "чт");
  public static final List<String> FRIDAY = Arrays.asList("Friday", "Fr", "пятница", "пт");
  public static final List<String> SATURDAY = Arrays.asList("Saturday", "Sa", "суббота", "сб");
  public static final List<String> SUNDAY = Arrays.asList("Sunday", "Su", "воскресенье", "вс");

  // baobab language literals: flashing
  public static final List<String> ODD = Arrays.asList("odd", "нечетный");
  public static final List<String> EVEN = Arrays.asList("even", "четный");

  // baobab language literals: attendee types
  public static final List<String> STUDENT = Arrays.asList("student", "студента");
  public static final List<String> TEACHER = Arrays.asList("teacher", "преподавателя");
  public static final List<String> ACADEMIC_GROUP = Arrays.asList("academic group", "учебную группу");
  public static final List<String> CHAIR = Arrays.asList("chair", "кафедру");
  public static final List<String> FREE_FORM_GROUP = Arrays.asList("free form group", "коллектив");

  // group patterns
  protected final static String ID_PATTERN = "[\\wа-яА-Я]+";
  protected final static String TIME_PATTERN = "\\d{1,2}:\\d{2}";
  protected final static String EVEN_ODD_PATTERN_ENG = String.format("%s|%s", ODD.get(0), EVEN.get(0));
  protected final static String EVEN_ODD_PATTERN_RUS = String.format("%s|%s", ODD.get(1), EVEN.get(1));
  protected final static String WEEKDAY_PATTERN_ENG = String.format("%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s",
      MONDAY.get(0), MONDAY.get(1), TUESDAY.get(0), TUESDAY.get(1), WEDNESDAY.get(0), WEDNESDAY.get(1),
      THURSDAY.get(0), THURSDAY.get(1), FRIDAY.get(0), FRIDAY.get(1), SATURDAY.get(0), SATURDAY.get(1), SUNDAY.get(0),
      SUNDAY.get(1));
  protected final static String WEEKDAY_PATTERN_RUS = String.format("%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s",
      MONDAY.get(2), MONDAY.get(3), TUESDAY.get(2), TUESDAY.get(3), WEDNESDAY.get(2), WEDNESDAY.get(3),
      THURSDAY.get(2), THURSDAY.get(3), FRIDAY.get(2), FRIDAY.get(3), SATURDAY.get(2), SATURDAY.get(3), SUNDAY.get(2),
      SUNDAY.get(3));
  protected final static String ATTENDEE_PATTERN_ENG = String.format("%s|%s|%s|%s|%s", STUDENT.get(0), TEACHER.get(0),
      ACADEMIC_GROUP.get(0), CHAIR.get(0), FREE_FORM_GROUP.get(0));
  protected final static String ATTENDEE_PATTERN_RUS = String.format("%s|%s|%s|%s|%s", STUDENT.get(1), TEACHER.get(1),
      ACADEMIC_GROUP.get(1), CHAIR.get(1), FREE_FORM_GROUP.get(1));

  /**
   * parses command in baobab language and executes it
   * 
   * @param command command in baobab language
   * @return true in case of success parsing
   */
  public abstract boolean parse(String command);
}
