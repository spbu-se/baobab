package ru.spbu.math.baobab.server;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ru.spbu.math.baobab.lang.TimeSlotCommandParser;
import ru.spbu.math.baobab.model.TimeSlotExtent;

/**
 * Simple script form handler
 * Get script source from the web interface and try to execute it
 * 
 * @author agudulin
 */
public class ScriptFormServlet extends HttpServlet {
  private static final Logger LOGGER = Logger.getLogger("ScriptFormService");

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    process(request, response, "");
  }

  private void process(HttpServletRequest request, HttpServletResponse response, String result) throws ServletException, IOException {
    request.setCharacterEncoding("UTF-8");
    request.setAttribute("result", result);
    RequestDispatcher scriptForm = request.getRequestDispatcher("/script_form.jsp");
    scriptForm.forward(request, response);
  }

  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    try {
      String scriptText = request.getParameter("script");

      TimeSlotExtent timeSlotExtent = new TimeSlotExtentImpl();
      TimeSlotCommandParser parser = new TimeSlotCommandParser(timeSlotExtent);
      String result = (parser.parse(scriptText)) ? ":-)" : ":-(";
      process(request, response, result);

    } catch (IllegalArgumentException e) {
      LOGGER.log(Level.WARNING, "Failed to execute the query", e);
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
  }
}
