package ru.spbu.math.baobab.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import ru.spbu.math.baobab.model.AuditoriumExtent;
import ru.spbu.math.baobab.model.TimeSlotExtent;
import ru.spbu.math.baobab.model.Topic;
import ru.spbu.math.baobab.model.TopicExtent;
import ru.spbu.math.baobab.model.Topic.Type;
import ru.spbu.math.baobab.server.sql.TopicExtentSqlImpl;

/**
 * Parser gets an html file exported from Excel. There is only one table inside the file.
 * Then grabs some data from every cell of table.
 * E.g.
 *   Математический анализ (пр. з.) ст. пр. Голузина М.Г. 2520
 *   Практикум на ЭВМ ст. пр. Киреев И.В. 2410
 *   Математическая логика и теория множеств (лекц.) доц. Всемирнов М.А. 36
 *   
 * With every string creates a topic.
 * 
 * @author agudulin
 *
 */
public class ScheduleParser {
  private final TopicExtent topicExtent;
  private static final Pattern pLabsMatch1 = Pattern.compile("(?=.*?(?:\\(пр\\.?[ ]*(з\\.?)?\\)?)).*",Pattern.CASE_INSENSITIVE);
  private static final Pattern pLabsMatch2 = Pattern.compile("(?=.*?(?:практикум)).*", Pattern.CASE_INSENSITIVE);
  private static final Pattern pLabsSplit = Pattern.compile("\\(пр\\.?[ ]*(з\\.?)?\\)?", Pattern.CASE_INSENSITIVE);
  private static final Pattern pLectureMatch = Pattern.compile("(?=.*?(?:\\(лек[ц]?\\.?\\))).*", Pattern.CASE_INSENSITIVE);
  private static final Pattern pLectureSplit = Pattern.compile("\\(лек[ц]?\\.?\\)", Pattern.CASE_INSENSITIVE);
  private static final Pattern pLabsLectureMatch = Pattern.compile("(?=.*?(?:(\\(лек[ц]?\\.?,[ ]*пр\\.?\\))|(\\(пр\\.?,[ ]*лек[ц]?\\.?\\)))).*", Pattern.CASE_INSENSITIVE);
  private static final Pattern pLabsLectureSplit = Pattern.compile("(\\(лек[ц]?\\.?,[ ]*пр\\.?\\))|(\\(пр\\.?,[ ]*лек[ц]?\\.?\\))", Pattern.CASE_INSENSITIVE);

  ScheduleParser(TimeSlotExtent timeSlotExtent, AuditoriumExtent auditoriumExtent) {
    topicExtent = new TopicExtentSqlImpl(timeSlotExtent, auditoriumExtent);
  }

  /**
   * Parse html file of schedule
   * 
   * @param url URL of file to parse
   * @throws IOException
   */
  public void Parse(String url) throws IOException {
    Document doc = Jsoup.connect(url).get();
    Element table = doc.select("table").first();
    Iterator<Element> tr = table.select("tr").iterator();

    /* 
     * Get a list of count of groups per speciality
     * Don't think it is necessary, but why not
     * 
     * Look at the first <tr>
     * It contains cells like
     *    <td colspan=3 ...>...</td>
     * or
     *    <td ...>..</td>
     */
    Element row = tr.next();

    ArrayList<Integer> countOfGroupsPerSpeciality = new ArrayList<Integer>();
    Iterator<Element> td = row.select("td").iterator();
    skipTwoItems(td);
    while (td.hasNext()) {
      Element e = td.next();
      if (!td.hasNext()) {
        break;
      }
      // Look at <td> colspan param to get information about groups count
      String colspan = e.attr("colspan");
      Integer tmp = colspan.isEmpty() ? 1 : Integer.parseInt(colspan);
      countOfGroupsPerSpeciality.add(tmp);
      System.out.println(tmp);
    }

    /* 
     * Get the groups list
     * 
     * Look at the 2nd <tr>
     * There are groups numbers here
     */
    row = tr.next();
    td = row.select("td").iterator();
    skipTwoItems(td);
    ArrayList<String> groups = new ArrayList<String>();
    while (td.hasNext()) {
      Element e = td.next();
      if (!td.hasNext()) {
        break;
      }
      String groupName = e.text();
      groups.add(groupName);
      System.out.println(groupName);
    }

    /* 
     * Get the specialities list
     */
    row = tr.next();
    td = row.select("td").iterator();
    skipTwoItems(td);
    ArrayList<String> specialities = new ArrayList<String>();
    while (td.hasNext()) {
      Element e = td.next();
      if (!td.hasNext()) {
        break;
      }
      String specialityName = e.text();
      specialities.add(specialityName);
      System.out.println(specialityName);
    }

    /*
     * Get departments list
     * TODO: subj. and make this function optional,
     *       because 1st and 2nd courses haven't departments
     */ 

    /*
     * Get weekly schedule
     */
    for (int day = 0; day < 6; ++day) {
      oneDaySchedule(tr);
    }
  }

  /**
   * Skip first two empty <td> (colspan attribute mb equals 2)
   */
  private void skipTwoItems(Iterator<Element> td) {
    if (td.next().attr("colspan").equals("")) {
      td.next();
    }
  }

  /**
   * Get schedule for one day of week
   * @param tr table row iterator
   */
  private void oneDaySchedule(Iterator<Element> tr) {
    /*
     * Get the places list
     */
    Element row = tr.next();
    Iterator<Element> td = row.select("td").iterator();
    skipTwoItems(td);
    // get places list
    ArrayList<String> places = new ArrayList<String>();
    while (td.hasNext()) {
      Element e = td.next();
      if (!td.hasNext()) {
        break;
      }
      String place = e.text();
      places.add(place);
      System.out.println(place);
    }

    /* 
     * Get classes
     */
    row = tr.next();
    td = row.select("td").iterator();
    Element firstCell = td.next();
    Integer classesCount = Integer.parseInt(firstCell.attr("rowspan"));
    System.out.println(firstCell.text());
    for (int i = 0; i < classesCount; ++i) {
      td.next(); // skip class number and time info
      System.out.println((i + 1) + "-я пара");
      while (td.hasNext()) {
        Element _td = td.next();
        // ignore the last column because it's always empty
        if (!td.hasNext()) {
          break;
        }

        // TODO: check empty day in another way
        if (_td.text().length() <= 3) {
          System.out.println("ОКНО");
          continue;
        }

        if (_td.attr("colspan").equals("")) {
          topicFromString(_td.text());
        } else {
          Integer colspan = Integer.parseInt(_td.attr("colspan"));
          for (int j = 0; j < colspan; ++j) {
            topicFromString(_td.text());
          }
        }        
      }
      if (i < classesCount-1) {
        td = tr.next().select("td").iterator();
      }
    }
  }

  /**
   * Parse classes text and create topic
   * @param text a string from schedule cell 
   */
  public Topic topicFromString(String text) {
    Topic topic = null;
    String id;

    // TODO: generate cool ID for every topic
    // TODO: parse numerator and denominator situation
    if (pLectureMatch.matcher(text).matches()) {
      System.out.println("ЛЕКЦИЯ:: " + pLectureSplit.split(text)[0]);
      id = pLectureSplit.split(text)[0];
      if (topicExtent.find(id) == null) {
        topic = topicExtent.createTopic(id, Type.LECTURE_COURSE, id);
      }
      return topic;
    }
    if (pLabsLectureMatch.matcher(text).matches()) {
      System.out.println("ЛЕКЦИЯ+ПРАКТИКА:: " + pLabsLectureSplit.split(text)[0]);
      id = pLabsLectureSplit.split(text)[0];
      if (topicExtent.find(id) == null) {
        topic = topicExtent.createTopic(id, Type.LECTURE_COURSE, id);
//        topic = topicExtent.createTopic(id2, Type.LABS_COURSE, id);
      }
      return topic;
    }
    if (pLabsMatch1.matcher(text).matches()) {
      System.out.println("ПРАКТИКА:: " + pLabsSplit.split(text)[0]);
      id = pLabsSplit.split(text)[0];
      if (topicExtent.find(id) == null) {
        topic = topicExtent.createTopic(id, Type.LABS_COURSE, id);
      }
      return topic;
    }
    if (pLabsMatch2.matcher(text.toLowerCase()).matches()) {
      System.out.println("ПРАКТИКА:: " + text);
      id = text.substring(0, text.length() > 10 ? 10 : text.length()); // TODO: generate ID
      if (topicExtent.find(id) == null) {
        topic = topicExtent.createTopic(id, Type.LABS_COURSE, id);
      }
    } else {
      System.out.println("НИРЫБАНИМЯСО:: " + text);
      id = text.substring(0, text.length() > 10 ? 10 : text.length()); // TODO: generate ID
      if (topicExtent.find(id) == null) {
        topic = topicExtent.createTopic(id, Type.LABS_COURSE, id);
      }
    }
    return topic;
  }
}
