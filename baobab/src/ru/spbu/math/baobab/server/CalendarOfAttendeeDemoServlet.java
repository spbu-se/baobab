package ru.spbu.math.baobab.server;

import java.util.List;

import com.google.common.collect.Lists;

import ru.spbu.math.baobab.model.Attendee;
import ru.spbu.math.baobab.model.AttendeeExtent;
import ru.spbu.math.baobab.model.EvenOddWeek;
import ru.spbu.math.baobab.model.TimeInstant;
import ru.spbu.math.baobab.model.TimeSlot;
import ru.spbu.math.baobab.model.TimeSlotExtent;
import ru.spbu.math.baobab.model.Attendee.Type;

/**
 * Calendar Of Attendee Demo Servlet
 * 
 * @author aoool
 */
@SuppressWarnings("serial")
public class CalendarOfAttendeeDemoServlet extends CalendarOfAttendeeServlet {

  @Override
  protected List<TimeSlot> createTimeSlots() {
    List<TimeSlot> timeSlots = Lists.newArrayList();
    TimeInstant start = new TimeInstant(9, 30);
    TimeInstant finish = new TimeInstant(11, 5);
    TimeInstant start1 = new TimeInstant(11, 15);
    TimeInstant finish1 = new TimeInstant(12, 50);
    TimeInstant start2 = new TimeInstant(13, 40);
    TimeInstant finish2 = new TimeInstant(15, 15);
    TimeSlotExtent timeSlotExtent = new TimeSlotExtentImpl();
    timeSlots.add(timeSlotExtent.create("1 pair", start, finish, 1, EvenOddWeek.ALL));
    timeSlots.add(timeSlotExtent.create("2 pair", start1, finish1, 1, EvenOddWeek.ALL));
    timeSlots.add(timeSlotExtent.create("3 pair", start2, finish2, 5, EvenOddWeek.ALL));
    timeSlots.add(timeSlotExtent.create("4 pair", start2, finish2, 2, EvenOddWeek.ALL));
    timeSlots.add(timeSlotExtent.create("5 pair", start2, finish2, 7, EvenOddWeek.ALL));
    timeSlots.add(timeSlotExtent.create("6 pair", start2, finish2, 3, EvenOddWeek.ALL));
    return timeSlots;
  }

  protected Attendee getAttendee() {
    AttendeeExtent extent = new AttendeeExtentImpl();
    return extent.create("Ivan", "Ivan Petrov", Type.STUDENT);
  }
}
