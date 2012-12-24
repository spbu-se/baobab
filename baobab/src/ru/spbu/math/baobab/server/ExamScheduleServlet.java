package ru.spbu.math.baobab.server;

import java.io.IOException;
import java.util.Set;

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

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

/**
 * Get schedule of exams for all groups
 * 
 * @author agudulin
 */
public class ExamScheduleServlet extends HttpServlet {
  private static final String CALENDAR_COMPONENT = "calendar";
  private CalendarExtent myCalendarExtent = new CalendarExtentSqlImpl();
  
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    Multimap<Attendee, Event> schedule;
    if (DevMode.USE_TEST_DATA) {
      schedule = new TestData().getExamSchedule(); 
    } else {
      Calendar calendar = myCalendarExtent.find(getCalendarFromPath(request));
      if (calendar != null) {
        request.setAttribute("calendarID", calendar.getID());
        AttendeeEventMap data = new AttendeeEventMap(calendar);
        schedule = data.getAttendeeEventMap();      
      } else {
        schedule = LinkedListMultimap.create();
      }
    }
    Multimap<String, Attendee> groups = getGroupList(schedule.keySet());

    request.setCharacterEncoding("UTF-8");
    request.setAttribute("calendarList", myCalendarExtent.getAll());
    request.setAttribute("groupsList", groups.asMap());
    request.setAttribute("schedule", schedule.asMap());
    RequestDispatcher view = request.getRequestDispatcher("/exam_schedule.jsp");
    view.forward(request, response);
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
  
  static String getCalendarFromPath(HttpServletRequest req) {
    String[] path = req.getRequestURI().split("/");
    return (path.length >= 2 && CALENDAR_COMPONENT.equals(path[1])) ? path[2] : "exams-winter-2013";
  }
}
