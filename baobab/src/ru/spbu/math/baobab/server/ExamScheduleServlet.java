package ru.spbu.math.baobab.server;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ru.spbu.math.baobab.model.Attendee;
import ru.spbu.math.baobab.model.Calendar;
import ru.spbu.math.baobab.model.CalendarExtent;
import ru.spbu.math.baobab.model.Event;
import ru.spbu.math.baobab.server.sql.AttendeeEventMap;
import ru.spbu.math.baobab.server.sql.CalendarExtentSqlImpl;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

/**
 * Get schedule of exams for all groups
 * 
 * @author agudulin
 */
public class ExamScheduleServlet extends HttpServlet {
  static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd MMM", new Locale("ru", "RU"));
  private static final SimpleDateFormat DF = new SimpleDateFormat("yy-MM-dd", new Locale("ru", "RU"));
  private CalendarExtent myCalendarExtent = new CalendarExtentSqlImpl();
  
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    Multimap<Attendee, Event> schedule = getSchedule(myCalendarExtent, request);
    Multimap<String, Attendee> groups = getGroupList(schedule.keySet());

    java.util.Calendar c = (java.util.Calendar) java.util.Calendar.getInstance(TimeZone.getTimeZone("UTC+04"), new Locale("ru", "RU")).clone();
    c.add(java.util.Calendar.DAY_OF_YEAR, 1);
    Date result = c.getTime();
    request.setAttribute("tomorrowDate", DF.format(result));
    request.setCharacterEncoding("UTF-8");
    request.setAttribute("calendarList", myCalendarExtent.getAll());
    request.setAttribute("groupsList", getJspAttendees(groups));
    
    Multimap<String, ExamJsp> scheduleView = LinkedListMultimap.create();
    for (Entry<Attendee, Collection<Event>> entry : schedule.asMap().entrySet()) {
      Map<String, Event> exam2oh = Maps.newHashMap();
      for (Event e : entry.getValue()) {
        switch (e.getTopic().getType()) {
        case OFFICE_HOURS:
          exam2oh.put(e.getTopic().getName(), e);
          break;
        case EXAM:
          Event oh = exam2oh.get(e.getTopic().getID());
          scheduleView.put(entry.getKey().getID(), new ExamJsp(e, oh));
        }
      }
    }
    request.setAttribute("schedule", scheduleView.asMap());
    RequestDispatcher view = request.getRequestDispatcher("/exam_schedule.jsp");
    view.forward(request, response);
  }

  public static class ExamJsp {
    public final Date examDate;
    public final String topicID;
    public final String topicName;
    public final String owners;
    public final String auditorium;
    public final String comment;
    
    ExamJsp(Event examEvent, Event officeHoursEvent) {
      this.examDate = examEvent.getStartDate();
      this.topicID = examEvent.getTopic().getID();
      this.topicName = examEvent.getTopic().getName();
      this.auditorium = examEvent.getAuditorium().getID();
      List<String> ownerNames = Lists.newArrayList();
      for (Attendee a : examEvent.getTopic().getOwners()) {
        if (a != null && a.getName() != null) {
          ownerNames.add(a.getName());
        }
      }
      this.owners = Joiner.on(", ").join(ownerNames);
      if (officeHoursEvent == null) {
        this.comment = null;
      } else {
        this.comment = String.format("консультация %s в %s", DATE_FORMAT.format(officeHoursEvent.getStartDate()), officeHoursEvent.getAuditorium().getID());
      }
    }

    public Date getExamDate() {
      return examDate;
    }

    public String getTopicID() {
      return topicID;
    }

    public String getTopicName() {
      return topicName;
    }

    public String getOwners() {
      return owners;
    }

    public String getAuditorium() {
      return auditorium;
    }

    public String getComment() {
      return comment;
    }
  }
  
  public static class AttendeeJsp {
    public final String name;
    public final String fullName;
    public final String anchor;
    public final String key;
    
    AttendeeJsp(Attendee key) {
      this.key = key.getID();
      this.name = key.getName();
      if (key.getType() == Attendee.Type.ACADEMIC_GROUP) {
        this.fullName = name + " группа"; 
      } else {
        this.fullName = name;
      }
      this.anchor = key.getName().replace(' ', '_');
    }
    
    public String getName() {
      return name;
    }

    public String getFullName() {
      return this.fullName;
    }
    
    public String getAnchor() {
      return anchor;
    }
    
    public String getKey() {
      return key;
    }
  }
  
  private Map<String, Collection<AttendeeJsp>> getJspAttendees(Multimap<String, Attendee> attendeeMultimap) {
    Multimap<String, AttendeeJsp> result = LinkedListMultimap.create();
    for (Entry<String, Collection<Attendee>> entry : attendeeMultimap.asMap().entrySet()) {
      for (Attendee a : entry.getValue()) {
        result.put(entry.getKey(), new AttendeeJsp(a));
      }
    }
    return result.asMap();
  }
  
  private Multimap<String, Attendee> getGroupList(Set<Attendee> attendeeList) {
    if (attendeeList.isEmpty()) {
      return LinkedListMultimap.create();
    }

    Multimap<String, Attendee> groups = LinkedListMultimap.<String, Attendee> create();
    for (Attendee a : attendeeList) {
      groups.put(a.getName().substring(0, 1), a);
    }

    return groups;
  }

  private static final Set<String> CALENDAR_COMPONENTS = Sets.newHashSet("calendar", "export", "today");
  
  static String getCalendarFromPath(HttpServletRequest req) {
    String[] path = req.getRequestURI().split("/");
    try {
      return (path.length > 2 && CALENDAR_COMPONENTS.contains(path[1])) ? URLDecoder.decode(path[2], Charsets.UTF_8.name()) : "exams-winter-2013";
    } catch (UnsupportedEncodingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return "exams-winter-2013";
    }
  }
  
  static Multimap<Attendee, Event> getSchedule(CalendarExtent calendarExtent, HttpServletRequest req) {
    Multimap<Attendee, Event> schedule;
    if (DevMode.USE_TEST_DATA) {
      schedule = new TestData().getExamSchedule(); 
    } else {
      Calendar calendar = calendarExtent.find(getCalendarFromPath(req));
      if (calendar != null) {
        req.setAttribute("calendarID", calendar.getID());
        AttendeeEventMap data = AttendeeEventMap.create(calendar);
        schedule = data.getAttendeeEventMap();      
      } else {
        schedule = LinkedListMultimap.create();
      }
    }
    return schedule;
  }
}
