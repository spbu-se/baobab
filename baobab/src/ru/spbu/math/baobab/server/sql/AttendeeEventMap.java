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
      "SELECT att.uid AS att_uid, att.name AS att_name, att.type AS att_type, "
      + "ts.id AS ts_id, ts.name AS ts_name, ts.start_min AS ts_start_min, "
      + "ts.finish_min AS ts_finish_min, ts.day AS ts_day, ts.is_odd AS ts_is_odd, "
      + "au.num AS au_num, au.capacity AS au_capacity, "
      + "top.uid AS topic_uid, top.type AS topic_type, top.name AS topic_name, "
      + "att2.uid AS owner_uid, att2.name AS owner_name, att2.type AS owner_type, "
      + "ev.date AS ev_date "
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
      // Extents initialization
      AttendeeExtent attendeeExtent = new AttendeeExtentImpl();
      AttendeeExtent ownerExtent = new AttendeeExtentImpl();
      TimeSlotExtent timeSlotExtent = new TimeSlotExtentImpl();
      AuditoriumExtent auditoriumExtent = new AuditoriumExtentImpl();
      TopicExtent topicExtent = new TopicExtentImpl();
      // Map filling
      while (result.next()) {
        Attendee attendee = attendeeExtent.find(result.getString("att_uid"));
        if (attendee == null) {
          attendee = attendeeExtent.create(result.getString("att_uid"), result.getString("att_name"),
              Attendee.Type.values()[result.getInt("att_type")]);
        }
        TimeSlot timeSlot = timeSlotExtent.findById(result.getInt("ts_id"));
        if (timeSlot == null) {
          timeSlot = timeSlotExtent.create(result.getString("ts_name"), new TimeInstant(result.getInt("ts_start_min")),
              new TimeInstant(result.getInt("ts_finish_min")), result.getInt("ts_day"),
              EvenOddWeek.values()[result.getInt("ts_is_odd")]);
        }
        Auditorium auditorium = auditoriumExtent.find(result.getString("au_num"));
        if (auditorium == null) {
          auditorium = auditoriumExtent.create(result.getString("au_num"), result.getInt("au_capacity"));
        }
        Topic topic = topicExtent.find(result.getString("topic_uid"));
        if (topic == null) {
          topic = topicExtent.createTopic(result.getString("topic_uid"), Topic.Type.values()[result.getInt("topic_type")],
              result.getString("topic_name"));
        }
        Attendee owner = ownerExtent.find(result.getString("owner_uid"));
        if (owner == null) {
          owner = ownerExtent.create(result.getString("owner_uid"), result.getString("owner_name"),
              Attendee.Type.values()[result.getInt("owner_type")]);
        }
        topic.addOwner(owner); // adding topic owner
        topic.addEvent(result.getDate("ev_date"), timeSlot, auditorium);
        myAttendeeEvent.put(attendee, topic.addEvent(result.getDate("ev_date"), timeSlot, auditorium));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      con.dispose();
    }
  }
}
