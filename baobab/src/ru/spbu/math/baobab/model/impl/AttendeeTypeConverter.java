package ru.spbu.math.baobab.model.impl;

import ru.spbu.math.baobab.model.Attendee;
import ru.spbu.math.baobab.lang.Parser;

/**
 * Converts attendee type on Baobab language to convenient type
 * 
 * @author vloginova
 */
public class AttendeeTypeConverter {
  /**
   * converts string to Attendee.Type
   * 
   * @param value string to convert
   * @return converted Attendee.Type
   */
  public static Attendee.Type convertToAttendeeType(String value) {
    if (Parser.STUDENT.contains(value)) {
      return Attendee.Type.STUDENT;
    }
    if (Parser.TEACHER.contains(value)) {
      return Attendee.Type.TEACHER;
    }
    if (Parser.ACADEMIC_GROUP.contains(value)) {
      return Attendee.Type.ACADEMIC_GROUP;
    }
    if (Parser.CHAIR.contains(value)) {
      return Attendee.Type.CHAIR;
    }
    if (Parser.FREE_FORM_GROUP.contains(value)) {
      return Attendee.Type.FREE_FORM_GROUP;
    }
    throw new IllegalArgumentException("convertToAttendeeType: Incorrect value");
  }
}
