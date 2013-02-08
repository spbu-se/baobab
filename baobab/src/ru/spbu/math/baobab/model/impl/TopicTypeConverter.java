package ru.spbu.math.baobab.model.impl;

import ru.spbu.math.baobab.lang.Parser;
import ru.spbu.math.baobab.model.Topic.Type;

/**
 * Converts topic type lexeme on Baobab language to convenient type
 * 
 * @author vloginova
 */
public class TopicTypeConverter {
  /**
   * converts string to Topic.Type
   * 
   * @param value string to convert
   * @return converted Topic.Type
   */
  public static Type convertToTopicType(String value) {
    if (Parser.LABS_COURSE.contains(value)) {
      return Type.LABS_COURSE;
    }
    if (Parser.LECTURE_COURSE.contains(value)) {
      return Type.LECTURE_COURSE;
    }
    if (Parser.EXAM.contains(value)) {
      return Type.EXAM;
    }
    if (Parser.INVITED_LECTURE.contains(value)) {
      return Type.INVITED_LECTURE;
    }
    if (Parser.THESIS_DEFENSE.contains(value)) {
      return Type.THESIS_DEFENSE;
    }
    if (Parser.TEAM_MEETING.contains(value)) {
      return Type.TEAM_MEETING;
    }
    if (Parser.OFFICE_HOURS.contains(value)) {
      return Type.OFFICE_HOURS;
    }
    throw new IllegalArgumentException("convertToTopicType: Incorrect value");
  }
}
