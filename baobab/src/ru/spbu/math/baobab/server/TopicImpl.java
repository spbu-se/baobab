package ru.spbu.math.baobab.server;

import java.util.Collection;
import java.util.Date;

import javax.annotation.Nullable;

import com.google.common.collect.Sets;
import com.google.common.collect.Lists;

import ru.spbu.math.baobab.model.Attendee;
import ru.spbu.math.baobab.model.Auditorium;
import ru.spbu.math.baobab.model.Event;
import ru.spbu.math.baobab.model.TimeSlot;
import ru.spbu.math.baobab.model.TimeSlot.Utils;
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
  private final Collection<Attendee> myAttendees = Sets.newLinkedHashSet();
  private final Collection<Attendee> myOwners = Sets.newLinkedHashSet();
  private String myUrl;

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
    Event event = new EventImpl(myEvents.size() + 1, date, timeSlot, auditorium, this);
    myEvents.add(event);
    return event;
  }

  @Override
  public Collection<Event> addAllEvents(Date start, Date finish, TimeSlot timeSlot, @Nullable Auditorium auditorium) {
    Collection<Event> events = Lists.newArrayList();

    for (Date date : Utils.getFilteredRangeOfDates(Utils.datesRange(start, finish), timeSlot)) {
      Event event = addEvent(date, timeSlot, auditorium);
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
      myAttendees.add(att);
  }

  @Override
  public Collection<Attendee> getAttendees() {
    return myAttendees;
  }

  @Override
  public void addOwner(Attendee owner) {
      myOwners.add(owner);
  }

  @Override
  public Collection<Attendee> getOwners() {
    return myOwners;
  }

  @Override
  public void setUrl(String url) {
    myUrl = url;
  }

  @Override
  public String getUrl() {
    return myUrl;
  }
}
