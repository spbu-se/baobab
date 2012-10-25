package ru.spbu.math.baobab.server;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * Test of creating a PDF file. If everything is OK, browser will recognize it as PDF and will open a PDF viewer.
 * 
 * @author agudulin
 */
public class TestPdfServlet extends HttpServlet {
  private static final Logger LOGGER = Logger.getLogger("PdfService");

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    resp.setContentType("application/pdf");
    Document document = new Document();

    try {
      PdfWriter.getInstance(document, resp.getOutputStream());

      document.open();
      Paragraph title = new Paragraph();
      title.add(new Paragraph("Baobab", new Font(Font.FontFamily.COURIER, 24, Font.BOLD)));
      document.add(title);
      document.close();
    } catch (DocumentException e) {
      LOGGER.log(Level.WARNING, "Failed to create PDF", e);
      resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
  }
}
