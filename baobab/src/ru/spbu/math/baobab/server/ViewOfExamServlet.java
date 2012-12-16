package ru.spbu.math.baobab.server;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ru.spbu.math.baobab.model.Attendee;
import ru.spbu.math.baobab.model.Event;
import ru.spbu.math.baobab.model.Topic;
import ru.spbu.math.baobab.model.TopicExtent;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

/**
 * Simple script form handler Get script source from the web interface and try to execute it
 * 
 * @author agudulin
 */
public class ViewOfExamServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    process(request, response, "");
  }

  private void process(HttpServletRequest request, HttpServletResponse response, String result)
      throws ServletException, IOException {
    request.setCharacterEncoding("UTF-8");
    TestData testd = new TestData();
    String id = request.getParameter("id");
    // Topic topic = getTopic(id) do not work
    Topic topic = getTopic("гит-03-exam");
    request.setAttribute("exam_name", topic.getName());
    request.setAttribute("owners", getOwnerList(topic));
    request.setAttribute("events", getAttendees(topic));
    request.setAttribute("url", topic.getUrl());

    RequestDispatcher scriptForm = request.getRequestDispatcher("/view_of_exam.jsp");
    scriptForm.forward(request, response);
  }

  private Topic getTopic(String id) {
    TestData testData = new TestData();
    TopicExtent topicExtent = testData.getTopicExtent();
    return topicExtent.find(id);
  }

  private String getOwnerList(Topic topic) {
    List<String> names = Lists.newArrayList();
    for (Attendee att : topic.getOwners()) {
      names.add(att.getName());
    }
    return Joiner.on(", ").join(names);
  }

  private List<String> getAttendees(Topic topic) {
    final List<String> months = Arrays.asList("янв", "ферв", "марта", "апр", "мая", "июня", "июля", "авг", "сент",
        "окт", "ноябр", "дек");
    List<Event> events = (List<Event>) topic.getEvents();
    List<String> names = Lists.newArrayList();
    for (Event ev : events) {
      for (Attendee att : ev.getAttendees()) {
        names.add(String.format("%s %s группа %s, ауд. %s", ev.getStartDate().getDate(),
            months.get(ev.getStartDate().getMonth()), att.getName(), ev.getAuditorium().getID()));
      }
    }
    return names;
  }
}
