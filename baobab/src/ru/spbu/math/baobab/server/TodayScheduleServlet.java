package ru.spbu.math.baobab.server;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

import ru.spbu.math.baobab.model.Attendee;
import ru.spbu.math.baobab.model.CalendarExtent;
import ru.spbu.math.baobab.model.Event;
import ru.spbu.math.baobab.server.sql.CalendarExtentSqlImpl;

public class TodayScheduleServlet extends HttpServlet {
  private static final SimpleDateFormat DF = new SimpleDateFormat("yy-MM-dd", new Locale("ru", "RU"));
  private static final SimpleDateFormat DF2 = new SimpleDateFormat("dd MMM", new Locale("ru", "RU"));
  private static final Calendar ourCalendar = (Calendar) Calendar.getInstance(TimeZone.getTimeZone("UTC+04"), new Locale("ru", "RU")).clone();
  private CalendarExtent myCalendarExtent = new CalendarExtentSqlImpl();

  public static class JspEntry {
    private final String myTitle;
    private final String myAttendees;
    private final String myAuditorium;
    
    JspEntry(Event e, Collection<Attendee> attendees) {
      myAuditorium = e.getAuditorium().getID();
      myAttendees = Joiner.on(", ").join(Collections2.transform(attendees, new Function<Attendee, String>() {
        @Override
        public String apply(Attendee a) {
          return a.getID();
        }
      }));
      myTitle = e.getTopic().getName();
    }
    
    public String getAuditorium() {
      return myAuditorium;
    }
    
    public String getTitle() {
      return myTitle;
    }
    
    public String getAttendees() {
      return myAttendees;
    }
  }
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    Multimap<Attendee, Event> schedule = ExamScheduleServlet.getSchedule(myCalendarExtent, req);
    String date = DF.format(getDate(req));

    Multimap<Event, Attendee> events = LinkedListMultimap.create();
    for (Attendee a : schedule.keySet()) {
      for (Event e : schedule.get(a)) {
        String eventDate = DF.format(e.getStartDate());
        if (date.equals(eventDate)) {
          events.put(e, a);
        }
      }
    }
    
    List<JspEntry> entries = Lists.newArrayList();
    for (Event e : events.keySet()) {
      entries.add(new JspEntry(e, events.get(e)));
    }
    Collections.sort(entries, new Comparator<JspEntry>() {
      @Override
      public int compare(JspEntry o1, JspEntry o2) {
        return o1.myAuditorium.compareTo(o2.myAuditorium);
      }
    });
    req.setAttribute("calendarList", myCalendarExtent.getAll());
    req.setAttribute("entries", entries);
    RequestDispatcher view = req.getRequestDispatcher("/exam_today.jsp");
    view.forward(req, resp);

  }

  private Date getDate(HttpServletRequest req) {
    Date result = ourCalendar.getTime();
    String dateParam = req.getParameter("date");
    if (dateParam != null) {
      try {
        result = DF.parse(dateParam);
        req.setAttribute("scheduleDate", DF2.format(result));
      } catch (ParseException e) {
        e.printStackTrace();
      }
    } else {
      req.setAttribute("scheduleDate", "сегодня");
    }
    return result;
  }

}
