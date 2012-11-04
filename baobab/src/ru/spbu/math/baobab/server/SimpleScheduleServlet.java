package ru.spbu.math.baobab.server;

import java.io.IOException;
import java.util.Collection;
import com.google.common.collect.Lists;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ru.spbu.math.baobab.model.Attendee;
import ru.spbu.math.baobab.model.AttendeeExtent;
import ru.spbu.math.baobab.model.EvenOddWeek;
import ru.spbu.math.baobab.model.TimeInstant;
import ru.spbu.math.baobab.model.TimeSlotExtent;
import ru.spbu.math.baobab.model.Attendee.Type;

public class SimpleScheduleServlet extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    request.setCharacterEncoding("UTF-8");
    Table table = new Table(TimeSlotsCreate(), AttendeeCreate());
    request.setAttribute("table", table);
    RequestDispatcher view = request.getRequestDispatcher("simple_schedule.jsp");
    view.forward(request, response);
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    request.setCharacterEncoding("UTF-8");
  }

  public TimeSlotExtent TimeSlotsCreate() {
    TimeInstant start = new TimeInstant(9, 30);
    TimeInstant finish = new TimeInstant(11, 5);
    TimeInstant start1 = new TimeInstant(11, 15);
    TimeInstant finish1 = new TimeInstant(12, 50);
    TimeInstant start2 = new TimeInstant(13, 40);
    TimeInstant finish2 = new TimeInstant(15, 15);
    TimeSlotExtent timeSlotExtent = new TimeSlotExtentImpl();
    timeSlotExtent.create("1 pair", start, finish, 1, EvenOddWeek.ALL);
    timeSlotExtent.create("2 pair", start1, finish1, 1, EvenOddWeek.ALL);
    timeSlotExtent.create("3 pair", start2, finish2, 2, EvenOddWeek.ALL);
    timeSlotExtent.create("4 pair", start2, finish2, 2, EvenOddWeek.ALL);
    timeSlotExtent.create("5 pair", start2, finish2, 3, EvenOddWeek.ALL);
    timeSlotExtent.create("6 pair", start2, finish2, 3, EvenOddWeek.ALL);
    return timeSlotExtent;
  }

  public Collection<Attendee> AttendeeCreate() {
    Collection<Attendee> attendees = Lists.newArrayList();
    AttendeeExtent attendeeExtent = new AttendeeExtentImpl();
    attendees.add(attendeeExtent.create("1", "141", Type.ACADEMIC_GROUP));
    attendees.add(attendeeExtent.create("2", "142", Type.ACADEMIC_GROUP));
    attendees.add(attendeeExtent.create("3", "143", Type.ACADEMIC_GROUP));
    attendees.add(attendeeExtent.create("4", "144", Type.ACADEMIC_GROUP));
    attendees.add(attendeeExtent.create("5", "241", Type.ACADEMIC_GROUP));
    attendees.add(attendeeExtent.create("6", "242", Type.ACADEMIC_GROUP));
    attendees.add(attendeeExtent.create("7", "243", Type.ACADEMIC_GROUP));
    attendees.add(attendeeExtent.create("8", "244", Type.ACADEMIC_GROUP));

    return attendees;
  }
}
