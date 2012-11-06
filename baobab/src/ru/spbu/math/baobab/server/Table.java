package ru.spbu.math.baobab.server;

import java.util.HashMap;
import java.util.List;

import com.google.common.collect.Lists;

import ru.spbu.math.baobab.model.Attendee;

import ru.spbu.math.baobab.model.TimeSlot;

/**
 * @author dageev
 */
public class Table {
  private final List<TimeSlot> myVertHeaders;
  private final List<Attendee> myHorHeaders;

  public Table(List<TimeSlot> timeslot, List<Attendee> attendees) {
    myVertHeaders = timeslot;
    myHorHeaders = attendees;
  }

  public HashMap<TimeSlot, String> getDayOfWeek() {
    HashMap<TimeSlot, String> daysofweek = new HashMap<TimeSlot, String>();
    List<TimeSlot> timeSlots = myVertHeaders;
    int dayOfWeek = 0;
    String[] days = { "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" };
    for (TimeSlot timeslot : timeSlots) {
      if (timeslot.getDayOfWeek() != dayOfWeek) {
        daysofweek.put(timeslot, days[dayOfWeek++]);
      } else {
        daysofweek.put(timeslot, "");
      }
    }
    return daysofweek;
  }

  public List<TableRow> getTableRows() {
    List<TableRow> table = Lists.newArrayList();
    TableRow row = new TableRow();
    TableCell cell = new TableCell("");
    TableCell cell0 = new TableCell("");
    row.addCell(cell);
    row.addCell(cell0);
    for (Attendee attendee : myHorHeaders) {
      TableCell cell1 = new TableCell(attendee.getName());
      row.addCell(cell1);
    }
    table.add(row);
    for (TimeSlot timeslot : myVertHeaders) {
      row = new TableRow();
      TableCell cell2 = new TableCell(getDayOfWeek().get(timeslot));
      row.addCell(cell2);
      TableCell cell3 = new TableCell(timeslot.getName());
      row.addCell(cell3);
      for (Attendee attendee : myHorHeaders) {
        row.addCell(cell);
      }
      table.add(row);
    }
    return table;
  }
}
