package ru.spbu.math.baobab.server;

import java.util.Arrays;
import java.util.List;

import ru.spbu.math.baobab.model.Attendee;
import ru.spbu.math.baobab.model.AttendeeExtent;
import ru.spbu.math.baobab.model.EvenOddWeek;
import ru.spbu.math.baobab.model.TimeInstant;
import ru.spbu.math.baobab.model.TimeSlot;
import ru.spbu.math.baobab.model.TimeSlotExtent;
import ru.spbu.math.baobab.model.Attendee.Type;

import com.google.common.collect.Lists;

import junit.framework.TestCase;

public class ScheduleTest extends TestCase {
  private final TableCell EMPTY_CELL = new TableCell("");

  public void testFirstRow() {
    Table table = new Table(createTimeSlots(), createAttendees());
    TableRow row = new TableRow();
    row.addCell(EMPTY_CELL);
    row.addCell(EMPTY_CELL);
    row.addCell(new TableCell("141"));
    row.addCell(new TableCell("142"));
    row.addCell(new TableCell("143"));
    row.addCell(new TableCell("144"));
    row.addCell(new TableCell("241"));
    row.addCell(new TableCell("242"));
    row.addCell(new TableCell("243"));
    row.addCell(new TableCell("244"));
    assertEquals(table.getTableRows().get(0), row);
  }

  public void testFirstColumn() {
    List<String> column = Arrays.asList("", "Mon", "", "Tue", "", "Wed", "");
    List<String> column1 = Lists.newArrayList();
    Table table = new Table(createTimeSlots(), createAttendees());
    for (TableRow row : table.getTableRows()) {
      column1.add(row.getCells().get(0).getValue());
    }
    assertEquals(column1, column);
  }

  public void testSecondColumn() {
    List<String> column = Arrays.asList("", "1 pair", "2 pair", "3 pair", 
        "4 pair", "5 pair", "6 pair");
    List<String> column1 = Lists.newArrayList();
    Table table = new Table(createTimeSlots(), createAttendees());
    for (TableRow row : table.getTableRows()) {
      column1.add(row.getCells().get(1).getValue());
    }
    assertEquals(column1, column);
  }

  private List<TimeSlot> createTimeSlots() {
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
    timeSlots.add(timeSlotExtent.create("3 pair", start2, finish2, 2, EvenOddWeek.ALL));
    timeSlots.add(timeSlotExtent.create("4 pair", start2, finish2, 2, EvenOddWeek.ALL));
    timeSlots.add(timeSlotExtent.create("5 pair", start2, finish2, 3, EvenOddWeek.ALL));
    timeSlots.add(timeSlotExtent.create("6 pair", start2, finish2, 3, EvenOddWeek.ALL));
    return timeSlots;
  }

  private List<Attendee> createAttendees() {
    List<Attendee> attendees = Lists.newArrayList();
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
