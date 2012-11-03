package ru.spbu.math.baobab.model;

import java.util.Date;

/**
 * Event is something which happens either once or recurring (bi)weekly in some date interval. 
 * It is associated with a time slot which designates event time slot in a single day and recurring pattern
 * and with a topic which is going to be "discussed", this or that way, on the event. Normally it is also 
 * associated with auditorium.
 * 
 * @author dbarashev
 */
public interface Event {
  /**
   * @return date when event starts
   */
  Date getStartDate();
  
  /**
   * @return date when event finishes
   */
  Date getFinishDate();
  
  /**
   * @return time slot associated with this event
   */
  TimeSlot getTimeSlot();
  
  /**
   * @return auditorium associated with this event, or {@code null} if auditorium is undefined
   */
  Auditorium getAuditorium();
  
  /**
   * @return topic associated with this event
   */
  Topic getTopic();
}
