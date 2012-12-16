package ru.spbu.math.baobab.server.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

import ru.spbu.math.baobab.model.Attendee;
import ru.spbu.math.baobab.model.AttendeeExtent;
import ru.spbu.math.baobab.model.Auditorium;
import ru.spbu.math.baobab.model.AuditoriumExtent;
import ru.spbu.math.baobab.model.Calendar;
import ru.spbu.math.baobab.model.EvenOddWeek;
import ru.spbu.math.baobab.model.Event;
import ru.spbu.math.baobab.model.TimeInstant;
import ru.spbu.math.baobab.model.TimeSlot;
import ru.spbu.math.baobab.model.TimeSlotExtent;
import ru.spbu.math.baobab.model.Topic;
import ru.spbu.math.baobab.model.TopicExtent;
import ru.spbu.math.baobab.server.AttendeeExtentImpl;
import ru.spbu.math.baobab.server.AttendeeImpl;
import ru.spbu.math.baobab.server.AuditoriumExtentImpl;
import ru.spbu.math.baobab.server.AuditoriumImpl;
import ru.spbu.math.baobab.server.EventImpl;
import ru.spbu.math.baobab.server.TimeSlotExtentImpl;
import ru.spbu.math.baobab.server.TimeSlotImpl;
import ru.spbu.math.baobab.server.TopicExtentImpl;
import ru.spbu.math.baobab.server.TopicImpl;

/**
 * AttendeeEventMap represents map Attendee <-> Event
 * 
 * @author aoool
 */
public class AttendeeEventMap {

  private final Calendar myCalendar;
  private Multimap<Attendee, Event> myAttendeeEvent = LinkedListMultimap.create();
  private final static String GET_TABLE_INFO_QUERY = 
      "SELECT att.uid, att.name, att.type, "
      + "ts.id, ts.name, ts.start_min, ts.finish_min, ts.day, ts.is_odd, "
      + "au.num, au.capacity, " 
      + "top.uid, top.type, top.name, "
      + "att2.uid, att2.name, att2.type, " 
      + "ev.id, ev.date " 
      + "FROM CalendarTopic ct "
      + "JOIN Topic top ON (top.uid = ct.topic_uid) " 
      + "JOIN Event ev ON (ev.topic_id = ct.topic_uid) "
      + "JOIN TopicOwner topow ON (ev.topic_id = topow.topic_id) "
      + "JOIN Attendee att2 ON (topow.attendee_id = att2.id) " 
      + "JOIN EventAttendee ea ON (ea.event_id = ev.id) "
      + "JOIN Attendee att ON (ea.attendee_uid = att.uid) " 
      + "JOIN TimeSlot ts ON (ts.id = ev.time_slot_id) "
      + "JOIN Auditorium au ON (au.num = ev.auditorium_num) " 
      + "WHERE ct.calendar_uid = ? "
      + "ORDER BY att.id, ev.date, ts.start_min;";

  public AttendeeEventMap(Calendar calendar) {
    myCalendar = calendar;
    initializeMyAttendeeTables();
  }

  public Multimap<Attendee, Event> getAttendeeEventMap() {
    return myAttendeeEvent;
  }

  private void initializeMyAttendeeTables() {
    SqlApi con = SqlApi.create();
    try {
      PreparedStatement stmt = con.prepareScript(GET_TABLE_INFO_QUERY).get(0);
      stmt.setString(1, myCalendar.getID());
      ResultSet result = stmt.executeQuery();
      //Extents initialization
      AttendeeExtent attendeeExtent = new AttendeeExtentImpl();
      AttendeeExtent ownerExtent = new AttendeeExtentImpl();
      TimeSlotExtent timeSlotExtent = new TimeSlotExtentImpl();
      AuditoriumExtent auditoriumExtent = new AuditoriumExtentImpl();
      TopicExtent topicExtent = new TopicExtentImpl();
      //Instances of Attendee, TimeSlot, etc.
      Attendee attendee = new AttendeeImpl(null, null, null, attendeeExtent);
      TimeSlot timeSlot = new TimeSlotImpl(0, null, null, null, 0, null, timeSlotExtent);
      Auditorium auditorium = new AuditoriumImpl(null, 0);
      Topic topic = new TopicImpl(null, null, null);
      Attendee owner = new AttendeeImpl(null, null, null, attendeeExtent);
      Event event = new EventImpl(0, null, null, null, null);
      //Map filling
      while(result.next()) {
        if(attendeeExtent.find(result.getString(1) /*attendee uid*/ ) == null){
          attendee = attendeeExtent.create(
              result.getString(1) /*attendee uid*/ , 
              result.getString(2) /*attendee name*/ , 
              Attendee.Type.values()[result.getInt(3)] /*attendee type*/
                  );
        } else {
          attendee = attendeeExtent.find(result.getString(1));
        }
        if((timeSlotExtent.findById(result.getInt(4)) == null) &&
            (timeSlotExtent.findByWeekDay(result.getInt(8)) == null)){
          timeSlot = timeSlotExtent.create(
              result.getString(5) /*timeslot name*/ , 
              new TimeInstant(result.getInt(6)) /*timeslot start*/ , 
              new TimeInstant(result.getInt(7)) /*timeslot finish*/ , 
              result.getInt(8) /*timeslot day*/ , 
              EvenOddWeek.values()[result.getInt(9)] /*timeslot EvenOddWeek*/
                  );
        } else {
          timeSlot = timeSlotExtent.findById(result.getInt(4) /*timeslot id*/);
        }
        if(auditoriumExtent.find(result.getString(10)) == null) {
          auditorium = auditoriumExtent.create(
              result.getString(10) /*auditorium num*/ , 
              result.getInt(11) /*auditorium capacity*/
                  );
        } else {
          auditorium = auditoriumExtent.find(result.getString(10));
        }
        if(!topicExtent.getAll().contains(topic)){
          topic = topicExtent.createTopic(
              result.getString(12) /*topic uid*/ , 
              Topic.Type.values()[result.getInt(13)] /*topic type*/ ,
              result.getString(14) /*topic name*/
                  );
        }
        if(ownerExtent.find(result.getString(15) /*owner uid*/ ) == null) {
          owner = ownerExtent.create(
              result.getString(15) /*owner uid*/ , 
              result.getString(16) /*owner name*/ ,
              Attendee.Type.values()[result.getInt(17)] /*owner type*/ 
                  );
        } else {
          owner = ownerExtent.find(result.getString(15) /*owner uid*/ );
        }
        topic.addOwner(owner); //adding topic owner (there will be a problem when more then one person owns topic)
        event = new EventImpl(
            result.getInt(18) /*event id*/ ,
            result.getDate(19) /*event date*/ ,
            timeSlot /*event timeslot*/ ,
            auditorium /*event auditorium*/ ,
            topic /*event topic*/
                );
        myAttendeeEvent.put(attendee, event);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      con.dispose();
    }
  }
}