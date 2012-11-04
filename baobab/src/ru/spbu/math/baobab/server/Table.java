package ru.spbu.math.baobab.server;

import java.util.Collection;

import ru.spbu.math.baobab.model.Attendee;

import ru.spbu.math.baobab.model.TimeSlot;
import ru.spbu.math.baobab.model.TimeSlotExtent;

public class Table {
  private Collection<TableRow> myTable;
  private Collection<TimeSlot> myVertHeaders;
  private Collection<Attendee> myHorHeaders;

  public Table(TimeSlotExtent timeslot, Collection<Attendee> attendees) {
    myVertHeaders = timeslot.getAll();
    myHorHeaders = attendees;
  }

  public Collection<TimeSlot> GetVertHeader() {
    return myVertHeaders;
  }

  public Collection<Attendee> GetHorHeaders() {
    return myHorHeaders;
  }
}
