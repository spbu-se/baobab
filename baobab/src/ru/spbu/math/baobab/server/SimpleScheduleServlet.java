package ru.spbu.math.baobab.server;

import java.util.Collection;
import javax.servlet.http.HttpServlet;
import ru.spbu.math.baobab.model.Attendee;
import ru.spbu.math.baobab.model.TimeSlotExtent;

/**
 * Simple Schedule Servlet
 * 
 * @author dageev
 */
public abstract class SimpleScheduleServlet extends HttpServlet {

  abstract TimeSlotExtent createTimeSlots();

  abstract Collection<Attendee> createAttendees();
}
