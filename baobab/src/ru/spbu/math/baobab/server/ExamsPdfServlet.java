package ru.spbu.math.baobab.server;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ru.spbu.math.baobab.model.Attendee;
import ru.spbu.math.baobab.model.Calendar;
import ru.spbu.math.baobab.model.CalendarExtent;
import ru.spbu.math.baobab.model.Event;
import ru.spbu.math.baobab.model.TimeSlot;
import ru.spbu.math.baobab.server.sql.AttendeeEventMap;
import ru.spbu.math.baobab.server.sql.CalendarExtentSqlImpl;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfFonts;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;

/**
 * PDF view of all exams grouped by academic groups
 * 
 * @author dbarashev
 */
public class ExamsPdfServlet extends HttpServlet {
  private static final Logger LOGGER = Logger.getLogger("PdfService");

  
  
  private static final String EXAM_START_MESSAGE = "Экзамены начинаются в %02d:%02d, если явно не указано иное";

  private static final String AUDITORIUM_NUM_MESSAGE = "ауд. %s";
  
  private static final TestData myTestData = new TestData();

  private final CalendarExtent myCalendarExtent = new CalendarExtentSqlImpl();
  
  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    resp.setContentType("application/pdf");
    Document document = new Document(PageSize.A4, 30f, 30f, 30f, 30f);

    try {
      Multimap<Attendee, Event> schedule = getSchedule(req);
      Multimap<Collection<Event>, Attendee> invertedMap = HashMultimap.create();
      for (Attendee a : schedule.keySet()) {
        invertedMap.put(schedule.get(a), a);
      }
      final TimeSlot typicalTimeSlot = getTypicalTimeSlot(invertedMap.keySet());
      PdfWriter writer = PdfWriter.getInstance(document, resp.getOutputStream());
      writer.setPageEvent(new PdfPageEventHelper() {
        @Override
        public void onEndPage(PdfWriter writer, Document document) {
          Rectangle pageSize = writer.getPageSize();
          ColumnText.showTextAligned(writer.getDirectContent(),
              Element.ALIGN_CENTER, new Phrase(String.format(
                  EXAM_START_MESSAGE, typicalTimeSlot.getStart().getHour(), typicalTimeSlot.getStart().getMinute()), 
                  PdfFonts.SMALL_FONT),
              (pageSize.getLeft() + pageSize.getRight()) / 2,  10, 0);
          LineSeparator line = new LineSeparator();
          line.setLineWidth(0.5f);
          line.setLineColor(PdfFonts.GREY);
          line.drawLine(writer.getDirectContent(), 10f, writer.getPageSize().getRight() - 10f, 15f + PdfFonts.SMALL_FONT.getSize());
        }
        
      });
      document.open();

      PdfPTable table = new PdfPTable(new float[] {1, 6});
      table.setWidthPercentage(100f);
      table.getDefaultCell().setPaddingBottom(12.0f);
      
      int row = 0;
      for (Collection<Event> events : schedule.asMap().values()) {
        Collection<Attendee> attendees = invertedMap.removeAll(events);
        if (attendees.isEmpty()) {
          // we have already processed this group
          continue;
        }
        if (row % 2 == 1) {
          table.getDefaultCell().setBackgroundColor(new BaseColor(0xF8F8F8));
        } else {
          table.getDefaultCell().setBackgroundColor(new BaseColor(0xFFFFFF));
        }
        table.getDefaultCell().setBorderWidth(0);
        table.getDefaultCell().setBorderWidthRight(1.0f);
        table.getDefaultCell().setBorderColorRight(new BaseColor(0, 0, 0));
        table.getDefaultCell().setRowspan(2);
        table.getDefaultCell().setVerticalAlignment(Element.ALIGN_TOP);
        table.getDefaultCell().setPaddingRight(6f);
        table.addCell(createAttendeeTable(attendees, typicalTimeSlot));

        table.getDefaultCell().setBorderWidthRight(0f);
        table.addCell(createEventTable(events));        
        
        row++;
      }
      
      document.add(table);
      document.close();
    } catch (DocumentException e) {
      LOGGER.log(Level.WARNING, "Failed to create PDF", e);
      resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
  }
  
  private Multimap<Attendee, Event> getSchedule(HttpServletRequest req) {
    Multimap<Attendee, Event> schedule;
    Calendar calendar = myCalendarExtent.find(ExamScheduleServlet.getCalendarFromPath(req));
    if (calendar != null) {
      AttendeeEventMap data = AttendeeEventMap.create(calendar);
      schedule = data.getAttendeeEventMap();      
    } else {
      schedule = DevMode.USE_TEST_DATA ? myTestData.getExamSchedule() : LinkedListMultimap.<Attendee, Event>create();
    }
    return schedule;
  }

  private TimeSlot getTypicalTimeSlot(Collection<Collection<Event>> allEvents) {
    Multiset<TimeSlot> timeSlots = HashMultiset.create();
    TimeSlot typicalTimeSlot = null;
    for (Collection<Event> events : allEvents) {
      for (Event e : events) {
        timeSlots.add(e.getTimeSlot());
        if (typicalTimeSlot == null || timeSlots.count(e.getTimeSlot()) > timeSlots.count(typicalTimeSlot)) {
          typicalTimeSlot = e.getTimeSlot();
        }
      }
    }
    return typicalTimeSlot;
  }
  
  private PdfPTable createAttendeeTable(Collection<Attendee> unsortedAttendees, TimeSlot typicalTimeSlot) {
    List<Attendee> attendees = Lists.newArrayList(unsortedAttendees);
    Collections.sort(attendees, new Comparator<Attendee>() {
      @Override
      public int compare(Attendee o1, Attendee o2) {
        return o1.getName().compareTo(o2.getName());
      }
    });
    PdfPTable table = new PdfPTable(new float[] {1f});
    table.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
    table.getDefaultCell().setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
    for (Attendee a : attendees) {
      table.addCell(new Phrase(a.getName(), PdfFonts.BIG_FONT));
    }
    return table;
  }
  
  private PdfPTable createEventTable(Collection<Event> events) {
    PdfPTable table = new PdfPTable(new float[] {2, 8, 4});
    table.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
    table.getDefaultCell().setPadding(0);
    for (Event e : events) {
      table.getDefaultCell().setPaddingTop(6.0f);
      table.getDefaultCell().setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
      table.addCell(new Phrase(ExamScheduleServlet.DATE_FORMAT.format(e.getStartDate()), PdfFonts.MEDIUM_FONT));
      
      table.getDefaultCell().setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
      table.addCell(new Phrase(e.getTopic().getName(), PdfFonts.MEDIUM_FONT));
      
      table.getDefaultCell().setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      String owners = Joiner.on(", ").join(Collections2.transform(e.getTopic().getOwners(), new Function<Attendee, String>() {
        @Override
        public String apply(Attendee att) {
          return att.getID();
        }
      }));
      table.addCell(new Phrase(owners, PdfFonts.TEACHER_LABEL));
      table.getDefaultCell().setPaddingTop(1.0f);
      table.getDefaultCell().setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
      table.addCell("");
      
      PdfPTable footerTable = new PdfPTable(new float[] {1f, 4f});
      footerTable.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
      PdfPCell audCell = new PdfPCell(new Phrase(String.format(
          AUDITORIUM_NUM_MESSAGE, e.getAuditorium().getID()), PdfFonts.SMALL_FONT));
      audCell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
      audCell.setBorder(PdfPCell.NO_BORDER);
      footerTable.addCell(audCell);
      
      PdfPCell consultLabel = new PdfPCell(new Phrase("", PdfFonts.CONSULT_LABEL));
      consultLabel.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      consultLabel.setBorder(PdfPCell.NO_BORDER);
      footerTable.addCell(consultLabel);
      
      PdfPCell footerCell = new PdfPCell(footerTable);
      footerCell.setBorder(PdfPCell.NO_BORDER);
      footerCell.setColspan(2);
      table.addCell(footerCell);
    }
    return table;
  }
}
