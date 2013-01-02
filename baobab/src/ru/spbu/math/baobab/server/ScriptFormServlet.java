package ru.spbu.math.baobab.server;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ru.spbu.math.baobab.lang.AttendeeCommandParser;
import ru.spbu.math.baobab.lang.AuditoriumCommandParser;
import ru.spbu.math.baobab.lang.CalendarCommandParser;
import ru.spbu.math.baobab.lang.EventBindCommandParser;
import ru.spbu.math.baobab.lang.EventDeclareCommandParser;
import ru.spbu.math.baobab.lang.Parser;
import ru.spbu.math.baobab.lang.ScriptInterpreter;
import ru.spbu.math.baobab.lang.TimeSlotCommandParser;
import ru.spbu.math.baobab.model.Attendee;
import ru.spbu.math.baobab.model.AttendeeExtent;
import ru.spbu.math.baobab.model.Auditorium;
import ru.spbu.math.baobab.model.AuditoriumExtent;
import ru.spbu.math.baobab.model.CalendarExtent;
import ru.spbu.math.baobab.model.TimeSlot;
import ru.spbu.math.baobab.model.TimeSlotExtent;
import ru.spbu.math.baobab.model.Topic;
import ru.spbu.math.baobab.model.TopicExtent;
import ru.spbu.math.baobab.server.sql.AttendeeExtentSqlImpl;
import ru.spbu.math.baobab.server.sql.AuditoriumExtentSqlImpl;
import ru.spbu.math.baobab.server.sql.CalendarExtentSqlImpl;
import ru.spbu.math.baobab.server.sql.TimeSlotExtentSqlImpl;
import ru.spbu.math.baobab.server.sql.TopicExtentSqlImpl;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

/**
 * Simple script form handler
 * Get script source from the web interface and try to execute it
 * 
 * @author agudulin
 */
public class ScriptFormServlet extends HttpServlet {
  private static final Logger LOGGER = Logger.getLogger("ScriptFormService");

  private final AttendeeExtent myAttendeeExtent = new AttendeeExtentSqlImpl();
  private final AuditoriumExtent myAuditoriumExtent = new AuditoriumExtentSqlImpl();
  private final TimeSlotExtent myTimeSlotExtent = new TimeSlotExtentSqlImpl();
  private final TopicExtent myTopicExtent = new TopicExtentSqlImpl(myTimeSlotExtent, myAuditoriumExtent);
  private final CalendarExtent myCalendarExtent = new CalendarExtentSqlImpl();
  
  
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    process(request, response, "");
  }

  private void process(HttpServletRequest request, HttpServletResponse response, String result) throws ServletException, IOException {
    request.setCharacterEncoding("UTF-8");
    request.setAttribute("result", result);
    request.setAttribute("group_list", getGroupList());
    request.setAttribute("teacher_list", getTeacherList());
    request.setAttribute("topic_list", getTopicList());
    request.setAttribute("auditorium_list", getAuditoriumList());
    request.setAttribute("time_slot_list", getTimeSlotList());
    request.setAttribute("calendarList", myCalendarExtent.getAll());
    request.setAttribute("placeholders", Parser.placeholders());
    RequestDispatcher scriptForm = request.getRequestDispatcher("/script_form.jsp");
    scriptForm.forward(request, response);
  }

  private Collection<String> getTopicList() {
    return Collections2.transform(myTopicExtent.getAll(), new Function<Topic, String>() {
      @Override
      public String apply(Topic topic) {
        return String.format("%s (%s)", topic.getName(), topic.getID());
      }
    });
  }
  private String getGroupList() {
    return getAttendeeList(Attendee.Type.ACADEMIC_GROUP);
  }

  private String getTeacherList() {
    return getAttendeeList(Attendee.Type.TEACHER);
  }

  private String getAttendeeList(Attendee.Type type) {
    List<Attendee> groups = Lists.newArrayList();
    for (Attendee a : myAttendeeExtent.getAll()) {
      if (a.getType() == type) {
        groups.add(a);
      }
    }
    List<String> names = Lists.newArrayList(Lists.transform(groups, new Function<Attendee, String>() {
      @Override
      public String apply(Attendee a) {
        return a.getName();
      }
    }));
    Collections.sort(names);
    return Joiner.on(", ").join(names);    
  }

  private String getTimeSlotList() {
    List<TimeSlot> timeSlots = Lists.newArrayList(myTimeSlotExtent.getAll());
    List<String> names = Lists.transform(timeSlots, new Function<TimeSlot, String>() {
      @Override
      public String apply(TimeSlot ts) {
        return String.format("%s (%s %02d:%02d - %02d:%02d)", ts.getName(), Parser.DAYS_LONG_RU[ts.getDayOfWeek() - 1], 
            ts.getStart().getHour(), ts.getStart().getMinute(), ts.getFinish().getHour(), ts.getFinish().getMinute());
      }
    });
    return Joiner.on(", ").join(names);
  }

  private String getAuditoriumList() {
    List<Auditorium> auditoria = Lists.newArrayList(myAuditoriumExtent.getAll());
    List<String> names = Lists.newArrayList(Lists.transform(auditoria, new Function<Auditorium, String>() {
      @Override
      public String apply(Auditorium a) {
        return a.getID();
      }
    }));
    Collections.sort(names);
    return Joiner.on(", ").join(names);
  }

  private static void loadProperties(Properties result, String resource) {
    URL url = ScriptFormServlet.class.getResource(resource);
    if (url == null) {
      return;
    }
    try {
      result.load(url.openStream());
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, "Failed to load properties", e);
    }
  }

  private boolean checkPassword(String password) {
    Properties properties = new Properties();
    loadProperties(properties, "/auth.secret.properties");
    String[] passwords = properties.getProperty("script.passwords", "").split(";");
    for (String p : passwords) {
      if (p.equals(password)) {
        return true;
      }
    }
    return false;
  }

  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String password = request.getParameter("password");
    String scriptText = request.getParameter("script");
    String result = "";

    if (!checkPassword(password)) {
      request.setAttribute("script_text", scriptText);
      result = "Неправильный пароль";
    }
    else {
      if (scriptText.startsWith("#")) {
        LegacyExamScheduleImporter importer = new LegacyExamScheduleImporter(myAuditoriumExtent, myAttendeeExtent, myTopicExtent);
        try {
          String schedule = importer.importSchedule(scriptText, "exams-winter-2013");
          request.setAttribute("script_text", schedule);
          result = "Данные импортированы успешно";
        } catch (Throwable e) {
          LOGGER.log(Level.SEVERE, "Failed to import data", e);
          LOGGER.log(Level.SEVERE, scriptText);
          request.setAttribute("script_text", scriptText);
          result = String.format("Ошибка при импорте:\n %s", e.getMessage());
        }
      } else {
        ScriptInterpreter interpreter = new ScriptInterpreter(Lists.<Parser>newArrayList(
            new TimeSlotCommandParser(myTimeSlotExtent), new AttendeeCommandParser(myAttendeeExtent), new AuditoriumCommandParser(myAuditoriumExtent), 
            new EventDeclareCommandParser(myTopicExtent, myAttendeeExtent), new EventBindCommandParser(myTopicExtent, myAttendeeExtent, myAuditoriumExtent, myTimeSlotExtent),
            new CalendarCommandParser(myCalendarExtent, myTopicExtent)));
  
        result = "Все завершилось прекрасно";
        for (String command : Splitter.on('\n').omitEmptyStrings().split(scriptText)) {
          try {
            if (!interpreter.process(command)) {
              request.setAttribute("script_text", scriptText);
              result = String.format("Не удалось разобрать команду %s", command);
              break;            
            }
          } catch (Throwable e) {
            LOGGER.log(Level.SEVERE, "Failed to execute script", e);
            request.setAttribute("script_text", scriptText);
            result = String.format("Ошибка при выполнении команды %s:%s\n", command, e.getMessage());
            break;
          }
        }
      }
    }
    process(request, response, result);
  }
}
