package ru.spbu.math.baobab.lang;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ru.spbu.math.baobab.model.Calendar;
import ru.spbu.math.baobab.model.CalendarExtent;
import ru.spbu.math.baobab.model.Topic;
import ru.spbu.math.baobab.model.TopicExtent;

import com.google.common.base.Joiner;

/**
 * Parses command which adds topics to calendars.
 * 
 * @author dbarashev
 */
public class CalendarCommandParser extends Parser {
  private static final Pattern CALENDAR_PATTERN_RUS = Pattern.compile(Joiner.on("\\s*").join(
      "", "добавить в календарь ", group(ID_PATTERN), " события ", group(ID_LIST_PATTERN), ""));

  private static final Pattern CALENDAR_PATTERN_ENG = Pattern.compile(Joiner.on("\\s*").join(
      "", "fill calendar ", group(ID_PATTERN), " with events ", group(ID_LIST_PATTERN), ""));

  private CalendarExtent myCalendarExtent;

  private TopicExtent myTopicExtent;

  public CalendarCommandParser(CalendarExtent calendarExtent, TopicExtent topicExtent) {
    myCalendarExtent = calendarExtent;
    myTopicExtent = topicExtent;
  }
  
  @Override
  public boolean parse(String command) {
    return tryParse(CALENDAR_PATTERN_RUS, command) || tryParse(CALENDAR_PATTERN_ENG, command);
  }
  
  private boolean tryParse(Pattern pattern, String command) {
    Matcher matcher = pattern.matcher(command);
    if (matcher.matches()) {
      String calId = unquote(matcher.group(1));
      String topicList = matcher.group(2);
      
      Calendar c = myCalendarExtent.find(calId);
      if (c == null) {
        c = myCalendarExtent.create(calId);
      }
      
      for (String quotedId : topicList.split(",")) {
        String topicId = unquote(quotedId.trim());
        Topic topic = myTopicExtent.find(topicId);
        if (topic != null) {
          c.addTopic(topic);
        } else {
          throw new IllegalArgumentException("Can't find topic " + topicId);
        }
      }
      return true;
    }
    return false;
  }
}
