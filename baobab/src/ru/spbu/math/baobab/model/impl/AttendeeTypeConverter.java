package ru.spbu.math.baobab.model.impl;

import java.util.Arrays;
import java.util.List;
import ru.spbu.math.baobab.model.Attendee;

/**
 * Converts attendee type on Baobab language to convenient type
 * 
 * @author vloginova
 */
public class AttendeeTypeConverter {
  private static final List<String> STUDENT = Arrays.asList("student", "студента");
  private static final List<String> TEACHER = Arrays.asList("teacher", "преподавателя");
  private static final List<String> ACADEMIC_GROUP = Arrays.asList("academic group", "учебную группу");
  private static final List<String> CHAIR = Arrays.asList("chair", "кафедру");
  private static final List<String> FREE_FORM_GROUP = Arrays.asList("free form group", "коллектив");

  /**
   * converts string to Attendee.Type
   * 
   * @param value string to convert
   * @return converted Attendee.Type
   */
  public static Attendee.Type convertToAttendeeType(String value) {
    if (STUDENT.contains(value)) {
      return Attendee.Type.STUDENT;
    }
    if (TEACHER.contains(value)) {
      return Attendee.Type.TEACHER;
    }
    if (ACADEMIC_GROUP.contains(value)) {
      return Attendee.Type.ACADEMIC_GROUP;
    }
    if (CHAIR.contains(value)) {
      return Attendee.Type.CHAIR;
    }
    if (FREE_FORM_GROUP.contains(value)) {
      return Attendee.Type.FREE_FORM_GROUP;
    }
    throw new IllegalArgumentException("convertToAttendeeType: Incorrect value");
  }
}
