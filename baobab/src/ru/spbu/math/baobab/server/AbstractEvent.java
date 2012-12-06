package ru.spbu.math.baobab.server;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import javax.annotation.Nullable;

import ru.spbu.math.baobab.model.Attendee;
import ru.spbu.math.baobab.model.Auditorium;
import ru.spbu.math.baobab.model.Event;
import ru.spbu.math.baobab.model.TimeInstant;
import ru.spbu.math.baobab.model.TimeSlot;
import ru.spbu.math.baobab.model.Topic;

import com.google.common.base.Objects;

/**
 * Implementation of Event
 * 
 * @author dageev
 */
public abstract class AbstractEvent implements Event {
  protected final Date myDate;
  protected final TimeSlot myTimeSlot;
  protected Auditorium myAuditorium;
  protected final Topic myTopic;
  protected final int myID;

  public AbstractEvent(int id, Date date, TimeSlot timeSlot, @Nullable Auditorium auditorium, Topic topic) {
    myID = id;
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
    if (obj instanceof AbstractEvent == false) {
      return false;
    }
    
    // we ignore ID because we actually want compare data fields here rather than IDs
    AbstractEvent other = (AbstractEvent) obj;
    return Objects.equal(this.getStartDate(), other.getStartDate())       
        && Objects.equal(myTimeSlot, other.myTimeSlot)
        && Objects.equal(myTopic, other.myTopic);
  }
  
  @Override
  public int getID() {
    return myID;
  }

  @Override
  public abstract void addAttendee(Attendee att);
 
  @Override
  public abstract Collection<Attendee> getAttendees();
}
