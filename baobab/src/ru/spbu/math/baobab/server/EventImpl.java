package ru.spbu.math.baobab.server;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.collect.Sets;

import ru.spbu.math.baobab.model.Attendee;
import ru.spbu.math.baobab.model.Auditorium;
import ru.spbu.math.baobab.model.Event;
import ru.spbu.math.baobab.model.TimeInstant;
import ru.spbu.math.baobab.model.TimeSlot;
import ru.spbu.math.baobab.model.Topic;

/**
 * Implementation of Event
 * 
 * @author agudulin
 */
public class EventImpl implements Event {
  private final Date myDate;
  private final TimeSlot myTimeSlot;
  private Auditorium myAuditorium;
  private final Topic myTopic;
  private final Collection<Attendee> myAttendees = Sets.newLinkedHashSet();

  public EventImpl(Date date, TimeSlot timeSlot, @Nullable Auditorium auditorium, Topic topic) {
    myDate = date;
    myTimeSlot = timeSlot;
    myAuditorium = auditorium;
    myTopic = topic;
  }

  private Date dateWithTime(TimeInstant t) {
    Calendar cal = Calendar.getInstance();

    cal.setTime(myDate);
    cal.set(Calendar.HOUR_OF_DAY, t.getHour());
    cal.set(Calendar.MINUTE, t.getMinute());
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);

    return cal.getTime();
  }

  @Override
  public Date getStartDate() {
    return dateWithTime(myTimeSlot.getStart());
  }

  @Override
  public Date getFinishDate() {
    return dateWithTime(myTimeSlot.getFinish());
  }

  @Override
  public TimeSlot getTimeSlot() {
    return myTimeSlot;
  }

  @Override
  public Auditorium getAuditorium() {
    return myAuditorium;
  }

  @Override
  public void setAuditorium(Auditorium auditorium) {
    myAuditorium = auditorium;
  }

  @Override
  public Topic getTopic() {
    return myTopic;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(myDate, myTimeSlot, myTopic);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof EventImpl == false) {
      return false;
    }

    EventImpl other = (EventImpl) obj;
    return Objects.equal(this.getStartDate(), other.getStartDate())       
        && Objects.equal(myTimeSlot, other.myTimeSlot)
        && Objects.equal(myTopic, other.myTopic);
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
