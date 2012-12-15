package ru.spbu.math.baobab.server;

import java.util.List;

import com.google.common.collect.Lists;

import ru.spbu.math.baobab.model.Attendee;

import ru.spbu.math.baobab.model.TimeSlot;

/**
 * Class Table, which can create simple table with existing TimeSlots and Attendees
 * 
 * @author dageev
 */
public class Table {
  private final List<TimeSlot> myVertHeaders;
  private final List<Attendee> myHorHeaders;
  private final static TableCell EMPTY_CELL = new TableCell("");

  public Table(List<TimeSlot> timeslot, List<Attendee> attendees) {
    myVertHeaders = timeslot;
    myHorHeaders = attendees;
  }

  private List<TableCell> getDayOfWeek() {
    List<TableCell> firstColumn = Lists.newArrayList();
    List<TimeSlot> timeSlots = myVertHeaders;
    int dayOfWeek = 0;
    String[] days = { "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" };
    for (TimeSlot timeslot : timeSlots) {
      if (timeslot.getDayOfWeek() != dayOfWeek) {
        firstColumn.add(new TableCell(days[dayOfWeek++]));
      } else {
        firstColumn.add(EMPTY_CELL);
      }
    }
    return firstColumn;
  }

  public List<TableRow> getTableRows() {
    List<TableRow> table = Lists.newArrayList();
    TableRow row = new TableRow();
    row.addCell(EMPTY_CELL);
    row.addCell(EMPTY_CELL);
    for (Attendee attendee : myHorHeaders) {
      row.addCell(new TableCell(attendee.getName()));
    }
    table.add(row);
    for (int i = 0; i < myVertHeaders.size(); i++) {
      row = new TableRow();
      row.addCell(getDayOfWeek().get(i));
      row.addCell(new TableCell(myVertHeaders.get(i).getName()));
      for (int j = 0; j < myHorHeaders.size(); j++) {
        row.addCell(EMPTY_CELL);
      }
      table.add(row);
    }
    return table;
  }
}
