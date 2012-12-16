package ru.spbu.math.baobab.server;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ru.spbu.math.baobab.model.Attendee;
import ru.spbu.math.baobab.model.Event;

import com.google.common.base.Function;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Lists;

/**
 * Get schedule of exams for all groups
 * 
 * @author agudulin
 */
public class ExamScheduleServlet extends HttpServlet {
  private static final Logger LOGGER = Logger.getLogger("ExamScheduleService");
  
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    TestData data = new TestData();
    LinkedListMultimap<Attendee, Event> schedule = (LinkedListMultimap<Attendee, Event>) data.getExamSchedule();
    LinkedListMultimap<String, String> groups = getGroupList(schedule.keySet());
    System.out.println("Groups List is " + groups);

    request.setCharacterEncoding("UTF-8");
    request.setAttribute("groups_list", groups);
    RequestDispatcher view = request.getRequestDispatcher("/exam_schedule.jsp");
    view.forward(request, response);
  }

  private LinkedListMultimap<String, String> getGroupList(Set<Attendee> attendeeList) {
    List<String> attendeeNameList = getAttendeeList(Attendee.Type.ACADEMIC_GROUP, attendeeList);
    if (attendeeNameList.isEmpty()) {
      return null;
    }

    LinkedListMultimap<String, String> groups = LinkedListMultimap.<String, String>create();
    String prefix = attendeeNameList.get(0).substring(0, 1);
    for (String name : attendeeNameList) {
      if (name.startsWith(prefix)) {
        if (groups.containsKey(prefix)) {
          groups.get(prefix).add(name);
        } else {
          groups.put(prefix, name);
        }
      }
      prefix = name.substring(0, 1);
    }

    return groups;
  }
  
  private List<String> getAttendeeList(Attendee.Type type, Set<Attendee> attendeeList) {
    List<Attendee> groups = Lists.newArrayList();
    for (Attendee a : attendeeList) {
      if (a.getType() == type) {
        groups.add(a);
      }
    }
    List<String> names = Lists.newArrayList(Lists.transform(groups, new Function<Attendee, String>() {
      @Override
      public String apply(Attendee a) {
        return a.getName();
      }
    }));
    Collections.sort(names);
    return names;  
  }

}
