package ru.spbu.math.baobab.server;

import java.util.Collection;
import java.util.Date;

import javax.annotation.Nullable;

import ru.spbu.math.baobab.model.Attendee;
import ru.spbu.math.baobab.model.Auditorium;
import ru.spbu.math.baobab.model.Event;
import ru.spbu.math.baobab.model.TimeSlot;
import ru.spbu.math.baobab.model.Topic;

/**
 * In-memory implementation of Topic
 * 
 * @author dageev
 */
public class TopicImpl implements Topic {
  private final String myName;
  private final Type myType;
  private final String myId;

  public TopicImpl(String id, Type type, String name) {
    myId = id;
    myType = type;
    myName = name;
  }

  @Override
  public String getID() {
    return myId;
  }

  @Override
  public Type getType() {
    return myType;
  }

  @Override
  public String getName() {
    return myName;
  }

  @Override
  public Event addEvent(Date date, TimeSlot timeSlot, @Nullable Auditorium auditorium) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Collection<Event> addAllEvents(Date start, Date finish, TimeSlot timeSlot, @Nullable Auditorium auditorium) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Collection<Event> getEvents() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void addAttendee(Attendee att) {
    // TODO Auto-generated method stub
  }

  @Override
  public Collection<Attendee> getAttendees() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void addOwner(Attendee owner) {
    // TODO Auto-generated method stub
  }

  @Override
  public Collection<Attendee> getOwners() {
    // TODO Auto-generated method stub
    return null;
  }
}
