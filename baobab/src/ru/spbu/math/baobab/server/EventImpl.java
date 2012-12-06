package ru.spbu.math.baobab.server;

import java.util.Collection;
import java.util.Date;

import com.google.common.collect.Sets;

import ru.spbu.math.baobab.model.Attendee;
import ru.spbu.math.baobab.model.Auditorium;
import ru.spbu.math.baobab.model.TimeSlot;
import ru.spbu.math.baobab.model.Topic;

/**
 * Implementation of Event
 * 
 * @author agudulin
 */
public class EventImpl extends AbstractEvent {
  private final Collection<Attendee> myAttendees = Sets.newLinkedHashSet();

  public EventImpl(int id, Date date, TimeSlot timeSlot, Auditorium auditorium, Topic topic) {
    super(id, date, timeSlot, auditorium, topic);
  }

  @Override
  public void addAttendee(Attendee att) {
    myAttendees.add(att);
  }

  @Override
  public Collection<Attendee> getAttendees() {
    return myAttendees;
  }
}
