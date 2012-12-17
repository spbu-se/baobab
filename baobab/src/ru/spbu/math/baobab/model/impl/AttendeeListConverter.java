package ru.spbu.math.baobab.model.impl;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Lists;

import ru.spbu.math.baobab.lang.Parser;
import ru.spbu.math.baobab.model.Attendee;
import ru.spbu.math.baobab.model.AttendeeExtent;

public class AttendeeListConverter {
  private final static Pattern TIME_PATTERN = Pattern.compile(String.format("(%s),", Parser.ID_PATTERN));

  public static List<Attendee> convertToList(String value, AttendeeExtent extent) {
    List<Attendee> attendees = Lists.newArrayList();
    value += ",";
    Matcher commandMatch = TIME_PATTERN.matcher(value);
    while (commandMatch.find()) {
      String attendeeId = Parser.unquote(commandMatch.group(1));
      attendees.add(extent.find(attendeeId));
    }
    return attendees;
  }
}
