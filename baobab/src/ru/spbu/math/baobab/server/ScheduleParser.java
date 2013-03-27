package ru.spbu.math.baobab.server;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * TODO: doc brief
 * Парсим HTML-файл с расписанием (экспорт из Excel)
 * 
 * @author agudulin
 *
 */
public class ScheduleParser {
  private final static String URL = "http://gudulin.ru/baobab/sheet001.htm";
  private File input;
  
  ScheduleParser(String inputFileName) {
    //TODO: check if file doesn't exist if it necessary
    input = new File(inputFileName);
  }
  
  public void Parse() throws IOException {
    Document doc = Jsoup.connect(URL).get();
    Element table = doc.select("table").first();
    Iterator<Element> tr = table.select("tr").iterator();

    /* 
     * Get a list of count of groups per speciality
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
     * Get weekly schedule
     */
    for (int day = 0; day < 6; ++day) {
      oneDaySchedule(tr);
    }
  }
  
  /**
   * Skip first two empty <td> (colspan mb equals 2)
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
      while (td.hasNext()) {
        System.out.println(td.next().text());
      }
      if (i < classesCount-1) {
        td = tr.next().select("td").iterator();
      }
    }
  }
}
