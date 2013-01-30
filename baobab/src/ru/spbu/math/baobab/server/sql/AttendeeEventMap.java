package ru.spbu.math.baobab.server.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

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
import ru.spbu.math.baobab.server.AuditoriumExtentImpl;
import ru.spbu.math.baobab.server.TimeSlotExtentImpl;
import ru.spbu.math.baobab.server.TopicExtentImpl;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

/**
 * AttendeeEventMap represents map Attendee <-> Event
 * 
 * @author aoool
 */
public class AttendeeEventMap {
  private static final Map<Calendar, AttendeeEventMap> ourCalendarScheduleMap = Maps.newHashMap();
  
  private final Calendar myCalendar;
  private Multimap<Attendee, Event> myAttendeeEvent = LinkedListMultimap.create();
  private final static String GET_TABLE_INFO_QUERY = 
      "SELECT * FROM ExamsView WHERE calendar_uid = ? "
      + "ORDER BY att_uid, ev_date, ts_start_min, topic_name;";

  private AttendeeEventMap(Calendar calendar) {
    myCalendar = Preconditions.checkNotNull(calendar);
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
        TimeSlot timeSlot = getOrCreateTimeSlot(timeSlotExtent, result.getString("ts_name"), new TimeInstant(result.getInt("ts_start_min")),
            new TimeInstant(result.getInt("ts_finish_min")), result.getInt("ts_day"),
            EvenOddWeek.values()[result.getInt("ts_is_odd")]); 
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

  private TimeSlot getOrCreateTimeSlot(TimeSlotExtent timeSlotExtent, String name, TimeInstant start, TimeInstant finish, int day, EvenOddWeek flashing) {
    for (TimeSlot ts : timeSlotExtent.getAll()) {
      if (Objects.equal(name, ts.getName()) && ts.getDayOfWeek() == day && ts.getEvenOddWeek() == flashing) {
        return ts;
      }
    }
    return timeSlotExtent.create(name, start, finish, day, flashing);
  }

  public static AttendeeEventMap create(Calendar calendar) {
    AttendeeEventMap schedule = ourCalendarScheduleMap.get(calendar);
    if (schedule == null) {
      schedule = new AttendeeEventMap(calendar);
      ourCalendarScheduleMap.put(calendar, schedule);
    }
    return schedule;
  }
  
  public static void clearCaches() {
    ourCalendarScheduleMap.clear();
  }
}
