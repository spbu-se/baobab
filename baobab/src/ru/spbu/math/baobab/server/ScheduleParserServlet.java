package ru.spbu.math.baobab.server;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ru.spbu.math.baobab.model.AttendeeExtent;
import ru.spbu.math.baobab.model.AuditoriumExtent;
import ru.spbu.math.baobab.model.TimeSlotExtent;
import ru.spbu.math.baobab.model.TopicExtent;

public class ScheduleParserServlet extends HttpServlet {
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    process(request, response);
  }

  private void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    request.setCharacterEncoding("UTF-8");
    RequestDispatcher scriptForm = request.getRequestDispatcher("/schedule_parser.jsp");
    scriptForm.forward(request, response);
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String url = request.getParameter("url");
    String result = "";
    if (!url.isEmpty()) {
      TopicExtent topicExtent = new TopicExtentImpl();
      AuditoriumExtent auditoriumExtent = new AuditoriumExtentImpl();
      AttendeeExtent attendeeExtent = new AttendeeExtentImpl();
      TimeSlotExtent tsExtent = new TimeSlotExtentImpl();
      ScheduleParser parser = new ScheduleParser(auditoriumExtent, attendeeExtent, topicExtent, tsExtent);
      parser.Parse(url);
      result = parser.result();
    }
    System.out.println(url);
    request.setAttribute("script_text", result);
    process(request, response);
  }
}
