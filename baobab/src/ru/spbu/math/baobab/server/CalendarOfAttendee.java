package ru.spbu.math.baobab.server;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;

import ru.spbu.math.baobab.model.TimeSlot;

/**
 * CalendarOfAttendee can create table which represents a calendar of a single attendee
 * 
 * @author aoool
 */
public class CalendarOfAttendee {
  private final List<TimeSlot> myHorHeaders;
  private final List<String> myVertHeaders;
  private final static String[] myDaysOfWeek = { "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun" };
  private final static TableCell EMPTY_CELL = new TableCell("");

  public CalendarOfAttendee(List<TimeSlot> timeSlots) {
    myHorHeaders = timeSlots;
    List<Integer> arrayOfDays = Lists.newArrayList();
    for (TimeSlot timeslot : myHorHeaders) {
      if (!arrayOfDays.contains(timeslot.getDayOfWeek())) {
        arrayOfDays.add(timeslot.getDayOfWeek());
      }
    }
    myVertHeaders = getSortedDaysOfWeek(arrayOfDays);
  }

  private List<String> getSortedDaysOfWeek(List<Integer> arrayOfDays) {
    List<String> sortedDaysOfWeek = Lists.newArrayList();
    Collections.sort(arrayOfDays);
    for (int i = 0; i < arrayOfDays.size(); i++) {
      sortedDaysOfWeek.add(myDaysOfWeek[arrayOfDays.get(i) - 1]);
    }
    return sortedDaysOfWeek;
  }

  /**
   * @return List of {@link TableRow} which represents a calendar of a single attendee as a table.
   */
  public List<TableRow> getTableRows() {
    List<TableRow> table = Lists.newArrayList();
    TableRow row = new TableRow();
    row.addCell(EMPTY_CELL);
    for (int i = 0; i < myVertHeaders.size(); i++) {
      row.addCell(new TableCell(myVertHeaders.get(i)));
    }
    table.add(row);
    for (int i = 0; i < myHorHeaders.size(); i++) {
      row = new TableRow();
      row.addCell(new TableCell(myHorHeaders.get(i).getName()));
      for (int j = 0; j < myVertHeaders.size(); j++) {
        row.addCell(EMPTY_CELL);
      }
      table.add(row);
    }
    return table;
  }
}
