package ru.spbu.math.baobab.server;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ru.spbu.math.baobab.lang.Parser;
import ru.spbu.math.baobab.lang.ScriptInterpreter;
import ru.spbu.math.baobab.lang.TimeSlotCommandParser;
import ru.spbu.math.baobab.model.TimeSlotExtent;
import ru.spbu.math.baobab.server.sql.TimeSlotExtentSqlImpl;

import com.google.common.collect.Lists;

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
    String scriptText = request.getParameter("script");

    TimeSlotExtent timeSlotExtent = new TimeSlotExtentSqlImpl();
    ScriptInterpreter interpreter = new ScriptInterpreter(Lists.<Parser>newArrayList(
        new TimeSlotCommandParser(timeSlotExtent)));
    
    String result = ":-)";
    try {
      interpreter.process(scriptText);
    } catch (Throwable e) {
      result = ":-( " + e.getMessage();
      LOGGER.log(Level.SEVERE, "Failed to execute script", e);
    }
    process(request, response, result);
  }
}
