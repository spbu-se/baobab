package ru.spbu.math.baobab.server;

import java.util.Collection;
import java.util.HashMap;
import ru.spbu.math.baobab.model.Attendee;

import ru.spbu.math.baobab.model.TimeSlot;

/**
 * @author dageev
 */
public class Table {
  private final Collection<TimeSlot> myVertHeaders;
  private final Collection<Attendee> myHorHeaders;
  private Collection<TableRow> myTable;

  public Table(Collection<TimeSlot> timeslot, Collection<Attendee> attendees) {
    myVertHeaders = timeslot;
    myHorHeaders = attendees;
  }

  public Collection<TimeSlot> getVertHeader() {
    return myVertHeaders;
  }

  public Collection<Attendee> getHorHeaders() {
    return myHorHeaders;
  }

  public HashMap<TimeSlot, String> getDayOfWeek() {
    HashMap<TimeSlot, String> daysofweek = new HashMap<TimeSlot, String>();
    Collection<TimeSlot> timeSlots = myVertHeaders;
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
}
