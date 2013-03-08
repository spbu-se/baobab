package ru.spbu.math.baobab.server;

import java.io.IOException;

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

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;

public class XMLExamScheduleServlet extends HttpServlet {
  private CalendarExtent myCalendarExtent = new CalendarExtentSqlImpl();

  static {
    System.setProperty("javax.xml.transform.TransformerFactory", "com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl");
  }
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
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
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
