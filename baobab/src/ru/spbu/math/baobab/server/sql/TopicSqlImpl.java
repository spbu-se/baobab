package ru.spbu.math.baobab.server.sql;

import java.util.Collection;
import java.util.Date;

import com.google.common.base.Objects;

import ru.spbu.math.baobab.model.Attendee;
import ru.spbu.math.baobab.model.Auditorium;
import ru.spbu.math.baobab.model.Event;
import ru.spbu.math.baobab.model.TimeSlot;
import ru.spbu.math.baobab.model.Topic;
import ru.spbu.math.baobab.server.TimeSlotImpl;

/**
 * SQL-based implementation of Topic
 * 
 * @author agudulin
 */
public class TopicSqlImpl implements Topic {

  private final String myId;
  private final Type myType;
  private final String myName;

  public TopicSqlImpl(String id, Type type, String name) {
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
  public Event addEvent(Date date, TimeSlot timeSlot, Auditorium auditorium) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Collection<Event> addAllEvents(Date start, Date finish, TimeSlot timeSlot, Auditorium auditorium) {
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

  @Override
  public int hashCode() {
    return Objects.hashCode(myId, myType, myName);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof TopicSqlImpl == false) {
      return false;
    }

    TopicSqlImpl other = (TopicSqlImpl) obj;
    return Objects.equal(myId, other.myId)
        && Objects.equal(myType, other.myType)
        && Objects.equal(myName, other.myName);
  }
}
