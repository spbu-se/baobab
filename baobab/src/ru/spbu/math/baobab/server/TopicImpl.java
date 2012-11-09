package ru.spbu.math.baobab.server;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

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
  private final Collection<Event> myEvents = Lists.newArrayList();

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
    Event event = new EventImpl(date, timeSlot, auditorium, this);
    myEvents.add(event);
    return event;
  }

  @Override
  public Collection<Event> addAllEvents(Date start, Date finish, TimeSlot timeSlot, @Nullable Auditorium auditorium) {
    Calendar calStart = Calendar.getInstance();
    Calendar calFinish = Calendar.getInstance();
    
    calStart.setTime(start);
    calFinish.setTime(finish);
    
    Collection<Event> events = Lists.newArrayList();
    
    for (; !calStart.after(calFinish); calStart.add(Calendar.DATE, 1)) {
      Date current = calStart.getTime();
      Event event = addEvent(current, timeSlot, auditorium);
      events.add(event);
    }
    
    return events;
  }

  @Override
  public Collection<Event> getEvents() {
    return myEvents;
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
