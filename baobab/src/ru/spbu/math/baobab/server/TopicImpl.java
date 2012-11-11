package ru.spbu.math.baobab.server;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import ru.spbu.math.baobab.model.Attendee;
import ru.spbu.math.baobab.model.Auditorium;
import ru.spbu.math.baobab.model.EvenOddWeek;
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
    Collection<Date> dates = Utils.datesRange(start, finish, 1);
    Collection<Event> events = Lists.newArrayList();

    int day = timeSlot.getDayOfWeek();
    boolean isEven = timeSlot.getEvenOddWeek().equals(EvenOddWeek.EVEN);
    boolean isAll = timeSlot.getEvenOddWeek().equals(EvenOddWeek.ALL);

    for (Date date : dates) {
      Calendar cal = Calendar.getInstance();
      cal.setTime(date);

      // Calendar.DAY_OF_WEEK == 1:Sunday, 2:Monday, ...
      int weekDay = (cal.get(Calendar.DAY_OF_WEEK) + 6) % 7;
      if (weekDay == 0) {
        weekDay = 7;
      }
      if ((weekDay == day) &&
          (isAll ||
          isEven && cal.get(Calendar.WEEK_OF_YEAR) % 2 == 0 ||
          !isEven && cal.get(Calendar.WEEK_OF_YEAR) % 2 == 1)) {
        Event event = addEvent(date, timeSlot, auditorium);
        events.add(event);
      }
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
