package ru.spbu.math.baobab.server;

import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ru.spbu.math.baobab.model.Attendee;
import ru.spbu.math.baobab.model.TimeSlot;

/**
 * Calendar Of Attendee Servlet
 * 
 * @author aoool
 */
@SuppressWarnings("serial")
public abstract class CalendarOfAttendeeServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    request.setCharacterEncoding("UTF-8");
    CalendarOfAttendee table = new CalendarOfAttendee(createTimeSlots());
    request.setAttribute("AttendeeID", createAttendee().getID());
    request.setAttribute("tableRows", table.getTableRows());
    RequestDispatcher view = request.getRequestDispatcher("/calendar_of_attendee.jsp");
    view.forward(request, response);
  }

  protected abstract List<TimeSlot> createTimeSlots();
  protected abstract Attendee createAttendee();
}
