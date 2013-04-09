package ru.spbu.math.baobab.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import ru.spbu.math.baobab.lang.AttendeeCommandParser;
import ru.spbu.math.baobab.lang.AuditoriumCommandParser;
import ru.spbu.math.baobab.lang.EventDeclareCommandParser;
import ru.spbu.math.baobab.lang.TimeSlotCommandParser;
import ru.spbu.math.baobab.model.Attendee;
import ru.spbu.math.baobab.model.AttendeeExtent;
import ru.spbu.math.baobab.model.Auditorium;
import ru.spbu.math.baobab.model.AuditoriumExtent;
import ru.spbu.math.baobab.model.EvenOddWeek;
import ru.spbu.math.baobab.model.TimeInstant;
import ru.spbu.math.baobab.model.TimeSlot;
import ru.spbu.math.baobab.model.TimeSlotExtent;
import ru.spbu.math.baobab.model.Topic;
import ru.spbu.math.baobab.model.TopicExtent;
import ru.spbu.math.baobab.model.Topic.Type;

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
  private StringBuilder myCommands;
  private static final Pattern myLabsMatch1 = Pattern.compile("(?=.*?(?:\\(пр\\.?[ ]*(з\\.?)?\\)?)).*");
  private static final Pattern myLabsMatch2 = Pattern.compile("(?=.*?(?:практикум)).*");
  private static final Pattern myLabsSplit = Pattern.compile("\\(пр\\.?[ ]*(з\\.?)?\\)?");
  private static final Pattern myLectureMatch = Pattern.compile("(?=.*?(?:\\((лек[ц]?\\.?)|(лекция)\\))).*");
  private static final Pattern myLectureSplit = Pattern.compile("\\((лек[ц]?\\.?)|(лекция)\\)");
  private static final Pattern myNumeratorDenominator = Pattern.compile("([^_]*)[_]+(.*)");
  private static final Pattern myAuditorium = Pattern.compile("(?=.*?(?:(\\d+([\\/-]\\d+)?))).*");
  private static final String myWordEndsWithDot = "[а-яёА-ЯЁ]+\\.";

  private final AttendeeExtentImpl myBufferAttendeeExtent;
  private final TopicExtentImpl myBufferTopicExtent;
  private final AuditoriumExtentImpl myBufferAuditoriumExtent;
  private final TimeSlotExtentImpl myBufferTimeSlotExtent;

  ScheduleParser(AuditoriumExtent auditoriumExtent, AttendeeExtent attendeeExtent, TopicExtent topicExtent, TimeSlotExtent tsExtent) {
    myBufferAttendeeExtent = new AttendeeExtentImpl();
    for (Attendee att : attendeeExtent.getAll()) {
      myBufferAttendeeExtent.create(att.getID(), att.getName(), att.getType());
    }
    myBufferAuditoriumExtent = new AuditoriumExtentImpl();
    for (Auditorium aud : auditoriumExtent.getAll()) {
      myBufferAuditoriumExtent.create(aud.getID(), aud.getCapacity());
    }
    myBufferTopicExtent = new TopicExtentImpl();
    for (Topic t : topicExtent.getAll()) {
      Topic bufferedTopic = myBufferTopicExtent.createTopic(t.getID(), t.getType(), t.getName());
      for (Attendee att : t.getOwners()) {
        bufferedTopic.addOwner(myBufferAttendeeExtent.find(att.getID()));
      }
    }
    myBufferTimeSlotExtent = new TimeSlotExtentImpl();
    for (TimeSlot ts : tsExtent.getAll()) {
      myBufferTimeSlotExtent.create(ts.getName(), ts.getStart(), ts.getFinish(), ts.getDayOfWeek(), ts.getEvenOddWeek());
    }
    myCommands = new StringBuilder();
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
    for (int day = 1; day < 7; ++day) {
      oneDaySchedule(tr, groups, day);
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
   * 
   * @param tr table row iterator
   * @param groups list of groups names
   * @param day day number
   */
  private void oneDaySchedule(Iterator<Element> tr, ArrayList<String> groups, int day) {
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
    System.out.println("\n======== " + firstCell.text() + " ========");
    myCommands.append("-- " + firstCell.text() + " --").append("\n");
    for (int i = 1; i < classesCount+1; ++i) {
      td.next(); // skip class number and time info
      System.out.println("\n======== " + i + "-я пара ========");
      myCommands.append("-- " + i + "-я пара --").append("\n");
      int columnNumber = 0;
      while (td.hasNext()) {
        Element _td = td.next();
        // ignore the last column because it's always empty
        if (!td.hasNext()) {
          break;
        }

        // TODO: check empty day in another way
        if (_td.text().length() <= 3) {
          System.out.println("ОКНО");
          columnNumber++;
          continue;
        }

        Integer colspan = _td.attr("colspan").isEmpty() ? 0 : Integer.parseInt(_td.attr("colspan"));
        List<String> groupsEvent = new ArrayList<String>();
        String commonPrefix = groups.get(columnNumber);
        for (int x = 0; x < colspan; ++x) {
          commonPrefix = Strings.commonPrefix(commonPrefix, groups.get(columnNumber));
          System.out.println(groups.get(columnNumber));
          groupsEvent.add(groups.get(columnNumber));
          columnNumber++;
        }
        String prefix = "-" + commonPrefix + "x";
        if (colspan == 0) {
          prefix = "";
          System.out.println(groups.get(columnNumber));
          groupsEvent.add(groups.get(columnNumber));
          columnNumber++;
        }

        // Create topics for numerator/denominator if it is
        Matcher m = myNumeratorDenominator.matcher(_td.text());
        if (m.find()) {
          String numerator = m.group(1).trim();
          String denomenator = m.group(2).trim();
          if (!numerator.isEmpty()) {
            System.out.println("Числитель");
            Auditorium auditorium = importAuditorium(numerator);
            String timeSlotName = importTimeSlot(i, day, EvenOddWeek.EVEN);
            List<Attendee> teachers = importAttendees(numerator);
            Topic topic = importTopic(numerator, prefix, teachers);
            createEvent(topic, auditorium, timeSlotName, groupsEvent);
          }
          if (!denomenator.isEmpty()) {
            System.out.println("Знаменатель");
            Auditorium auditorium = importAuditorium(denomenator);
            String timeSlotName = importTimeSlot(i, day, EvenOddWeek.ODD);
            List<Attendee> teachers = importAttendees(denomenator);
            Topic topic = importTopic(denomenator, prefix, teachers);
            createEvent(topic, auditorium, timeSlotName, groupsEvent);
          }
        } else {
          Auditorium auditorium = importAuditorium(_td.text());
          String timeSlotName = importTimeSlot(i, day, EvenOddWeek.ALL);
          List<Attendee> teachers = importAttendees(_td.text());
          Topic topic = importTopic(_td.text(), prefix, teachers);
          createEvent(topic, auditorium, timeSlotName, groupsEvent);
        }
      }
      if (i < classesCount) {
        td = tr.next().select("td").iterator();
      }
    }
  }

  private void createEvent(Topic topic, Auditorium auditorium, String timeSlotName, List<String> groups) {
    String auditoriumID = "";
    if (auditorium != null) {
      auditoriumID = auditorium.getID();
    }
    String topicID = "";
    if (topic != null) {
      topicID = topic.getID();
    }
    String command = String.format("событие \"%s\" состоится на \"%s\" в \"%s\" для %s", topicID, timeSlotName,
        auditoriumID, createStringList(groups));
    if (topicID.isEmpty() || auditoriumID.isEmpty() || groups.size() == 0 || timeSlotName.isEmpty()) {
      myCommands.append("<font color='red'>" + command + "</font>").append("\n\n");
    } else {
      myCommands.append(command).append("\n\n");
    }
    System.out.println(command);
  }

  private static final Pattern pTeacher = Pattern.compile("([А-Яа-яёЁ]+ ([А-ЯЁ]\\.){2})");

  /**
   * Searches for existing timeslot and generates
   * commands to create timeslot which are not yet defined
   */
  private String importTimeSlot(int classesNumber, int day, EvenOddWeek flashing) {
    TimeInstant start = new TimeInstant(9, 30);
    TimeInstant finish = new TimeInstant(11, 5);

    switch(classesNumber) {
    case 1:
      break;
    case 2:
      start = new TimeInstant(11, 15);
      finish = new TimeInstant(12, 50);
      break;
    case 3:
      start = new TimeInstant(13, 40);
      finish = new TimeInstant(15, 15);
      break;
    case 4:
      start = new TimeInstant(15, 25);
      finish = new TimeInstant(17, 0);
      break;
    case 5:
      start = new TimeInstant(17, 10);
      finish = new TimeInstant(18, 45);
      break;
    case 6:
      start = new TimeInstant(18, 55);
      finish = new TimeInstant(20, 30);
      break;
    default:
      break;
    }

    String evenOdd = "";
    switch(flashing) {
    case EVEN:
      evenOdd = "четный";
      break;
    case ODD:
      evenOdd = "нечетный";
      break;
    case ALL: default:
      evenOdd = "";
      break;
    }

    String dayOfWeek = "";
    switch(day) {
    case 1:
      dayOfWeek = "пн";
      break;
    case 2:
      dayOfWeek = "вт";
      break;
    case 3:
      dayOfWeek = "ср";
      break;
    case 4:
      dayOfWeek = "чт";
      break;
    case 5:
      dayOfWeek = "пт";
      break;
    case 6:
      dayOfWeek = "сб";
      break;
    case 7:
      dayOfWeek = "вс";
      break;
    default:
      break;
    }

    String name = String.format("%d-я пара", classesNumber);
    for (TimeSlot ts : myBufferTimeSlotExtent.getAll()) {
      if (ts.getName().equals(name) && (ts.getDayOfWeek() == day) &&
          ts.getEvenOddWeek().equals(flashing) && ts.getStart().equals(start)) {
        return name;
      }
    }

    int minutes = start.getMinute();
    String _start = String.format("%s:%s", start.getHour(), minutes < 10 ? "0" + minutes : minutes);
    minutes = finish.getMinute();
    String _finish = String.format("%s:%s", finish.getHour(), minutes < 10 ? "0" + minutes : minutes);
    String command = String.format("определить интервал \"%s\" от %s до %s в %s %s", name, _start, _finish,
        evenOdd, dayOfWeek);

    if (new TimeSlotCommandParser(myBufferTimeSlotExtent).parse(command)) {
      myCommands.append(command).append("\n");
      System.out.println(command);
      return name;
    } else {
      myCommands.append("<font color='red'>" + command + "</font>").append("\n");
    }
    return null;
  }

  /**
   * Searches for existing attendees and generates
   * commands to create attendees which are not yet defined
   * 
   * @param text text contains attendee names and other trash
   */
  private List<Attendee> importAttendees(String text) {
    List<Attendee> result = Lists.newArrayList();

    Matcher m = pTeacher.matcher(text);
    while (m.find()) {
      String teacher = m.group(1);

      Attendee attendee = myBufferAttendeeExtent.find(teacher);
      if (attendee != null) {
        result.add(attendee);
        continue;
      }

      String command = String.format("определить участника \"%s\" как преподавателя", teacher);
      if (new AttendeeCommandParser(myBufferAttendeeExtent).parse(command)) {
        myCommands.append(command).append("\n");
      } else {
        myCommands.append("<font color='red'>" + command + "</font>").append("\n");
      }
      attendee = myBufferAttendeeExtent.find(teacher);
      assert attendee != null;
      result.add(attendee);
    }
    return result;
  }

  /**
   * Create topic from table cell text
   * 
   * @param text a string from schedule cell 
   * @param prefix short string of common group prefix
   */
  public Topic importTopic(String text, String prefix, List<Attendee> teachers) {
    if (myLectureMatch.matcher(text).matches()) {
      String topicName = myLectureSplit.split(text)[0].trim().replaceAll("[^а-яА-ЯёЁ\\w\\s]", "");
      return topicByType(topicName, teachers, prefix, Type.LECTURE_COURSE, true);
    }
    if (myLabsMatch1.matcher(text).matches()) {
      String topicName = myLabsSplit.split(text)[0].trim().replaceAll("[^а-яА-ЯёЁ\\w\\s]", "");
      return topicByType(topicName, teachers, prefix, Type.LABS_COURSE, true);
    }
    if (myLabsMatch2.matcher(text.toLowerCase()).matches()) {
      String topicName = "";
      if (teachers.size() > 0) {
        Attendee t = teachers.get(0);
        System.out.println("FIRST TEACHER: " + t.getName());
        topicName = text.split(t.getName())[0].replaceAll(myWordEndsWithDot, "").trim().replaceAll("[^а-яА-ЯёЁ\\w\\s]", "");
      } else {
        topicName = text.replaceAll(myWordEndsWithDot, "").trim().replaceAll("[^а-яА-ЯёЁ\\w\\s]", "");
      }
      return topicByType(topicName, teachers, prefix, Type.LABS_COURSE, true);
    } else {
      String topicName = text.trim();
      return topicByType(topicName, teachers, prefix, Type.LABS_COURSE, false);
    }
  }

  /**
   * Create topic with defined type
   * 
   * @param topicName name of the topic
   * @param teachers list of attendees to check if topic already exists
   * @param groupPrefix short string of common group prefix
   * @param type type of the topic (lecture, labs, etc)
   * @return
   */
  private Topic topicByType(String topicName, List<Attendee> teachers, String groupPrefix, Type type, boolean noDoubt) {
    String typeTag = "", typeName = "";
    switch (type) {
    case LECTURE_COURSE:
      typeTag = "-lect";
      typeName = "лекция";
      break;
    case LABS_COURSE:
      typeTag = "-labs";
      typeName = "практика";
      break;
    default:
      assert false;
    }

    for (Topic t : myBufferTopicExtent.getAll()) {
      if (t.getName().equals(topicName) && Sets.newHashSet(t.getOwners()).equals(teachers)) {
        return t;
      }
    }

    String abbr = createAbbreviation(topicName);
    String suffix = groupPrefix + typeTag;
    char c = 'a';
    while (myBufferTopicExtent.find(abbr + suffix) != null) {
      suffix = groupPrefix + typeTag + c;
      c++;
    }
    String id = abbr + suffix;

    System.out.println(typeName.toUpperCase() + ":: " + id + " : " + topicName);

    String owners = "";
    if (teachers.size() > 0) {
      owners = String.format("владельцы %s", createAttendeeList(teachers));
    }
    String command = String.format("определить %s \"%s\" \"%s\" %s", typeName, id, topicName, owners);
    System.out.println(command);
    if (noDoubt && new EventDeclareCommandParser(myBufferTopicExtent, myBufferAttendeeExtent).parse(command)) {
      myCommands.append(command).append("\n");
      return myBufferTopicExtent.find(id);
    } else {
      myCommands.append("<font color='red'>" + command + "</font>").append("\n");
    }

    return null;
  }

  /**
   * Searches for existing auditorium and generates a command to create a new one if 
   * auditorium is not yet defined
   */
  private Auditorium importAuditorium(String line) {
    Matcher m = myAuditorium.matcher(line);
    if (m.find()) {
      String auditorium = m.group(1);
      Auditorium result = myBufferAuditoriumExtent.find(auditorium);
      if (result == null) {
        String command = String.format("определить помещение \"%s\"", auditorium);
        if (new AuditoriumCommandParser(myBufferAuditoriumExtent).parse(command)) {
          myCommands.append(command).append("\n");
          result = myBufferAuditoriumExtent.find(auditorium);
        } else {
          myCommands.append("<font color='red'>" + command + "</font>").append("\n");
        }
      }
      assert result != null;
      return result;
    }
    return null;
  }

  private static Pattern ABBR_PATTERN = Pattern.compile("^[a-zA-Zа-яА-Я]{4}.*");
  private static Pattern VOWEL_PATTERN = Pattern.compile("[aeiouAEIOUаеёиоуыэюяАЕЁИОУЫЭЮЯ]"); // XXX
  /**
   * Generates abbreviation consisting of lowercased first characters of words in the given string
   * E.g. "Математический анализ -> матан", "Геометрия и топология" -> "геомитоп", "Дискретный анализ -> дисан"
   */
  private String createAbbreviation(String line) {
    StringBuilder result = new StringBuilder();
    for (String word : line.split("\\s+")) {
      if (!word.isEmpty()) {
        if (ABBR_PATTERN.matcher(word).matches()) {
          result.append(word.charAt(0));
          String c2 = Character.toString(word.charAt(1));
          result.append(c2);
          if (VOWEL_PATTERN.matcher(c2).matches()) {
            String c3 = Character.toString(word.charAt(2));
            if (!VOWEL_PATTERN.matcher(c3).matches()) {
              result.append(c3);
            } else {
              String c4 = Character.toString(word.charAt(3)); 
              if (!VOWEL_PATTERN.matcher(c4).matches()) {
                result.append(c3);
                result.append(c4);
              }
            }
          } else {
            String c3 = Character.toString(word.charAt(2));
            if (!VOWEL_PATTERN.matcher(c3).matches()) {
              result.append(c3);
            }
          }
        } else {
          result.append(word);
        }
      }
    }
    return result.toString().toLowerCase();
  }

  public String result() {
    return myCommands.toString();
  }

  private static <T> String createIDList(Collection<T> objects, Function<T, String> getId) {
    return Joiner.on(',').join(Collections2.transform(objects, getId));
  }
  /**
   * Creates a list of comma-separated quoted attendee IDs
   */
  private String createAttendeeList(Collection<Attendee> attendees) {
    return createIDList(attendees, new Function<Attendee, String>() {
      @Override
      public String apply(Attendee a) {
        return "\"" + a.getID() + "\"";
      }
    });
  }
  private String createStringList(Collection<String> attendees) {
    return createIDList(attendees, new Function<String, String>() {
      @Override
      public String apply(String a) {
        return "\"" + a + "\"";
      }
    });
  }
}
