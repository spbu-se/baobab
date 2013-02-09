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

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XMLExamScheduleServlet extends HttpServlet {
  private CalendarExtent myCalendarExtent = new CalendarExtentSqlImpl();

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    Multimap<Attendee, Event> schedule = getSchedule(myCalendarExtent, request);

    try {
      XMLResponseBuilder builder = new XMLResponseBuilder();

      Document document;

      String groupReq = request.getParameter("group");

      if (groupReq == null) {
        document = builder.buildAttendeeNameList(schedule.keySet(), "Groups");
      } else {
        Attendee group = null;
        for (Attendee g : schedule.keySet()) {
          if (g.getName().equals(groupReq)) {
            group = g;
            break;
          }
        }

        if (group != null) {
          document = builder.buildEventList(schedule.get(group), group.getName());
        } else {
          document = builder.buildError("Requested group does not exist", "Cannot find " + groupReq);
        }
      }

      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      Transformer transformer = transformerFactory.newTransformer();
      DOMSource source = new DOMSource(document);
      StreamResult result = new StreamResult(response.getOutputStream());

      response.setContentType("text/xml");

      transformer.transform(source, result);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  static Multimap<Attendee, Event> getSchedule(CalendarExtent calendarExtent, HttpServletRequest req) {
    Multimap<Attendee, Event> schedule;
    if (DevMode.USE_TEST_DATA) {
      schedule = new TestData().getExamSchedule();
    } else {
      Calendar calendar = calendarExtent.find("exams-winter-2013");
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
