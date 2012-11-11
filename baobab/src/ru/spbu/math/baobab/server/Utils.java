package ru.spbu.math.baobab.server;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import com.google.common.collect.Lists;

/**
 * Small stuff here
 * @author dluciv
 *
 */
public class Utils {
  /**
   * Escape an html string. Escaping data received from the client helps to
   * prevent cross-site script vulnerabilities.
   * org.apache.commons.lang.StringEscapeUtils does the same
   * @param html the html string to escape
   * @return the escaped string
   */
  public static String escapeHtml(String html) {
    if (html == null) {
      return null;
    }
    return html.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");
  }
  
  /**
   * Get ordered set of dates
   * @param start start date
   * @param finish stop date
   * @param step step to increase start date value, have to be greater than 0
   * @return ordered dates range
   */
  public static Collection<Date> datesRange(Date start, Date finish, int step) {
    if (step <= 0) {
      throw new RuntimeException("Step arg should be greater than 0");
    }
    Collection<Date> dates = Lists.newArrayList();
    Calendar calStart = Calendar.getInstance();
    Calendar calFinish = Calendar.getInstance();

    calStart.setTime(start);
    calFinish.setTime(finish);
    for (; !calStart.after(calFinish); calStart.add(Calendar.DATE, step)) {
      dates.add(calStart.getTime());
    }

    return dates;
  }
}
