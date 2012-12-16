package ru.spbu.math.baobab.server;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ru.spbu.math.baobab.model.Attendee;
import ru.spbu.math.baobab.model.Event;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfFonts;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * PDF view of all exams grouped by academic groups
 * 
 * @author dbarashev
 */
public class ExamsPdfServlet extends HttpServlet {
  private static final Logger LOGGER = Logger.getLogger("PdfService");

  private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd MMM", new Locale("ru", "RU"));
  
  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    resp.setContentType("application/pdf");
    Document document = new Document(PageSize.A4);

    try {
      TestData testData = new TestData();
      PdfWriter.getInstance(document, resp.getOutputStream());

      document.open();

      PdfPTable table = new PdfPTable(new float[] {1, 6});
      table.setWidthPercentage(100f);
      table.getDefaultCell().setPaddingBottom(12.0f);
      
      int row = 0;
      for (Attendee a : testData.getExamSchedule().keySet()) {
        Collection<Event> events = testData.getExamSchedule().get(a);

        if (row % 2 == 1) {
          table.getDefaultCell().setBackgroundColor(new BaseColor(0xEEEEEE));
        } else {
          table.getDefaultCell().setBackgroundColor(new BaseColor(0xFFFFFF));
        }
        table.getDefaultCell().setBorderWidth(0);
        table.getDefaultCell().setBorderWidthRight(1.0f);
        table.getDefaultCell().setBorderColorRight(new BaseColor(0, 0, 0));
        table.getDefaultCell().setRowspan(2);
        table.getDefaultCell().setVerticalAlignment(Element.ALIGN_TOP);
        table.addCell(new Phrase(a.getName(), PdfFonts.BIG_FONT));

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
  
  private PdfPTable createEventTable(Collection<Event> events) {
    PdfPTable table = new PdfPTable(new float[] {2, 8, 4});
    table.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
    table.getDefaultCell().setPadding(0);
    for (Event e : events) {
      table.getDefaultCell().setPaddingTop(6.0f);
      table.getDefaultCell().setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
      table.addCell(new Phrase(DATE_FORMAT.format(e.getStartDate()), PdfFonts.MEDIUM_FONT));
      
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
      table.addCell(new Phrase(String.format("ауд. %s", e.getAuditorium().getID()), PdfFonts.SMALL_FONT));
      
      PdfPCell consultLabel = new PdfPCell(new Phrase("консультация в 15:00", PdfFonts.CONSULT_LABEL));
      consultLabel.setBorder(PdfPCell.NO_BORDER);
      consultLabel.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
      consultLabel.setColspan(2);
      table.addCell(consultLabel);
    }
    return table;
  }
}
