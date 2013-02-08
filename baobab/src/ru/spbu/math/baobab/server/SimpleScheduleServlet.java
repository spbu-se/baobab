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
 * Simple Schedule Servlet
 * 
 * @author dageev
 */
public abstract class SimpleScheduleServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    request.setCharacterEncoding("UTF-8");
    Table table = new Table(createTimeSlots(), createAttendees());
    request.setAttribute("tableRows", table.getTableRows());
    RequestDispatcher view = request.getRequestDispatcher("/simple_schedule.jsp");
    view.forward(request, response);
  }

  protected abstract List<TimeSlot> createTimeSlots();

  protected abstract List<Attendee> createAttendees();
}
