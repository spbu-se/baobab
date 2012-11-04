package ru.spbu.math.baobab.server;

import java.util.Collection;

import ru.spbu.math.baobab.model.Attendee;

import ru.spbu.math.baobab.model.TimeSlot;
import ru.spbu.math.baobab.model.TimeSlotExtent;

/**
 * Class Table
 * 
 * @author dageev
 */
public class Table {
  private final Collection<TimeSlot> myVertHeaders;
  private final Collection<Attendee> myHorHeaders;

  public Table(TimeSlotExtent timeslot, Collection<Attendee> attendees) {
    myVertHeaders = timeslot.getAll();
    myHorHeaders = attendees;
  }

  public Collection<TimeSlot> getVertHeader() {
    return myVertHeaders;
  }

  public Collection<Attendee> getHorHeaders() {
    return myHorHeaders;
  }
}
