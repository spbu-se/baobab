package ru.spbu.math.baobab.server;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ru.spbu.math.baobab.model.Attendee;
import ru.spbu.math.baobab.model.AuditoriumExtent;
import ru.spbu.math.baobab.model.CalendarExtent;
import ru.spbu.math.baobab.model.Event;
import ru.spbu.math.baobab.model.TimeSlotExtent;
import ru.spbu.math.baobab.model.Topic;
import ru.spbu.math.baobab.model.TopicExtent;
import ru.spbu.math.baobab.server.sql.AuditoriumExtentSqlImpl;
import ru.spbu.math.baobab.server.sql.CalendarExtentSqlImpl;
import ru.spbu.math.baobab.server.sql.TimeSlotExtentSqlImpl;
import ru.spbu.math.baobab.server.sql.TopicExtentSqlImpl;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

/**
 * Home page of exam
 * 
 * @author dageev
 */
public class ViewOfExamServlet extends HttpServlet {
  private final TestData myTestData = new TestData();
  private final AuditoriumExtent myAuditoriumExtent = DevMode.USE_TEST_DATA ? myTestData.getAuditoriumExtent() : new AuditoriumExtentSqlImpl();
  private final TimeSlotExtent myTimeSlotExtent = DevMode.USE_TEST_DATA ? myTestData.getTimeSlotExtent() : new TimeSlotExtentSqlImpl();
  private final TopicExtent myTopicExtent = DevMode.USE_TEST_DATA ? myTestData.getTopicExtent() : new TopicExtentSqlImpl(myTimeSlotExtent, myAuditoriumExtent);
  private final CalendarExtent myCalendarExtent = DevMode.USE_TEST_DATA ? myTestData.getCalendarExtent() : new CalendarExtentSqlImpl();
  
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    request.setCharacterEncoding("UTF-8");
    String id = request.getParameter("id");
    Topic topic = getTopic(id);
    if (topic != null) {    
      request.setAttribute("exam_name", topic.getName());
      request.setAttribute("owners", getOwnerList(topic));
      request.setAttribute("attendees", getAttendees(topic));
      request.setAttribute("url", topic.getUrl());
      request.setAttribute("calendarList", myCalendarExtent.getAll());
    }
    RequestDispatcher scriptForm = request.getRequestDispatcher("/view_of_exam.jsp");
    scriptForm.forward(request, response);
  }

  private Topic getTopic(String id) {
    return myTopicExtent.find(id);
  }

  private String getOwnerList(Topic topic) {
    List<String> names = Lists.newArrayList();
    for (Attendee att : topic.getOwners()) {
      names.add(att.getName());
    }
    return Joiner.on(", ").join(names);
  }

  private List<String> getAttendees(Topic topic) {
    SimpleDateFormat dateformat = new SimpleDateFormat("dd MMM", new Locale("ru", "RU"));
    List<Event> events = (List<Event>) topic.getEvents();
    List<String> names = Lists.newArrayList();
    for (Event ev : events) {
      for (Attendee att : ev.getAttendees()) {
        names.add(String.format("%s группа %s, ауд. %s", dateformat.format(ev.getStartDate()), 
            att.getName(), ev.getAuditorium().getID()));
      }
    }
    Collections.sort(names);
    return names;
  }
}
