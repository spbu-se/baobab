package ru.spbu.math.baobab.model;

import java.util.Collection;
import java.util.Date;

import javax.annotation.Nullable;

/**
 * Topic is something which is the goal of holding an event or series of events. 
 * It may be a lecture course or a single invited lecture.
 * 
 * A topic is identified by its ID, which is an arbitrary string, probably composed
 * according to some conventions. For instance, for lecture courses it may be something like "CS101-2012",
 * which is "Computer Science introduction course in year 2012"
 * 
 * One topic may have many associated events, e.g. lectures may take place on tuesdays and odd thursdays.
 * A topic contains a list of Attendees which are supposed to attend all events associated with the topic.  
 * A topic may have a list of owners. Semantics of owner depends on topic type, e.g. for a lecture or labs course
 * owners are teachers who work on that course. 
 * 
 * @author dbarashev
 * @see http://en.wikipedia.org/wiki/Course_numbering_in_North_America
 */
public interface Topic {
  enum Type {
    LECTURE_COURSE, LABS_COURSE, EXAM, INVITED_LECTURE, TEAM_MEETING, THESIS_DEFENSE, OFFICE_HOURS
  }
  
  /**
   * @return topic ID
   */
  String getID();
  
  /**
   * @return topic type
   */
  Type getType();
  
  /**
   * @return topic name
   */
  String getName();
  
  /**
   * Creates a one-off event which occurs at the specified date
   *  
   * @param date event date
   * @param timeSlot event time slot
   * @param auditorium auditorium where event will take place
   * @return new event instance
   */
  Event addEvent(Date date, TimeSlot timeSlot, @Nullable Auditorium auditorium);
  
  /**
   * Creates a series of events in the specified date interval, whenever time slot is
   * applicable.
   *  
   * @param start event interval start date
   * @param finish event interval finish date
   * @param timeSlot event time slot
   * @param auditorium auditorium where events will take place
   * @return a collection of created events
   */
  Collection<Event> addAllEvents(Date start, Date finish, TimeSlot timeSlot, @Nullable Auditorium auditorium);
  
  /**
   * @return all events when this topic takes place
   */
  Collection<Event> getEvents();
    
  /**
   * Adds an attendee to this topic.
   * @param att new attendee
   */
  void addAttendee(Attendee att);
  
  /**
   * @return all attendees of this topic
   */
  Collection<Attendee> getAttendees();
  
  /**
   * Adds an owner of this topic. 
   * @param owner owner attendee
   */
  void addOwner(Attendee owner);
  
  /**
   * @return a list of all owners
   */
  Collection<Attendee> getOwners();
  
  /**
   * Set url of set of questions for topic
   * @param url url
   */
  void setUrl(String url);
  
  /**
   * @return topic url
   */
  String getUrl();
}
