package ru.spbu.math.baobab.server;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ru.spbu.math.baobab.model.Attendee;
import ru.spbu.math.baobab.model.TimeSlot;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * PDF view of a classic baobab calendar
 * 
 * @author agudulin
 */
public abstract class SimpleSchedulePdfServlet extends HttpServlet {
  private static final Logger LOGGER = Logger.getLogger("PdfService");

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    resp.setContentType("application/pdf");
    Document document = new Document(PageSize.A0);

    try {
      PdfWriter.getInstance(document, resp.getOutputStream());

      document.open();
      Paragraph title = new Paragraph("Simple Schedule", new Font(Font.FontFamily.COURIER, 16, Font.NORMAL));
      title.setAlignment(Element.ALIGN_CENTER);
      title.setSpacingAfter(15);
      document.add(title);

      Table tableContent = new Table(createTimeSlots(), createAttendees());
      List<TableRow> rows = tableContent.getTableRows();
      int tableWidth = (rows.size() > 1) ? rows.get(0).getCells().size() : 1;

      PdfPTable table = new PdfPTable(tableWidth);

      for (TableRow row : rows) {
        for (TableCell cell : row.getCells()) {
          PdfPCell cellToAdd = new PdfPCell(new Phrase(cell.getValue()));
          table.addCell(cellToAdd);
        }
      }

      document.add(table);
      document.close();
    } catch (DocumentException e) {
      LOGGER.log(Level.WARNING, "Failed to create PDF", e);
      resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
  }

  protected abstract List<TimeSlot> createTimeSlots();

  protected abstract List<Attendee> createAttendees();
}
