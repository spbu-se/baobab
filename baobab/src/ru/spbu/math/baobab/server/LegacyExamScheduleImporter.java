package ru.spbu.math.baobab.server;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

import ru.spbu.math.baobab.lang.AttendeeCommandParser;
import ru.spbu.math.baobab.lang.AuditoriumCommandParser;
import ru.spbu.math.baobab.lang.EventDeclareCommandParser;
import ru.spbu.math.baobab.model.Attendee;
import ru.spbu.math.baobab.model.AttendeeExtent;
import ru.spbu.math.baobab.model.Auditorium;
import ru.spbu.math.baobab.model.AuditoriumExtent;
import ru.spbu.math.baobab.model.Event;
import ru.spbu.math.baobab.model.Topic;
import ru.spbu.math.baobab.model.TopicExtent;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;

/**
 * This class imports exam schedule from the following format: one file with
 * N exams of a single group or a list of groups. 
 * Exam is described by four lines (below semantics on the left, sample value on the right): 
 * 
 * #<attendee_id_list>    -- #191
 * <topic name>           -- Электричество и магнетизм        
 * <owner name>           -- Ст. пр. Дементьев А.В. 
 * <date>                 -- 10.01
 * <auditorium_id>        -- 4145
 *  
 * Thus, the number of lines in the file must be N*4 + 1.
 * Data in such format appear from our legacy RTF files with exam schedules by copying four 
 * columns from exam tables.
 * 
 * The output of this code is a sequence of commands in Baobab language.
 * 
 * When importing, this code strips first words from the owner name which end with dot. Then it tries to
 * find existing owner, auditorium, group and topic by topic name and owner list. If some
 * object is not found it generates a command which creates that object.
 *  
 * @author dbarashev
 */
class LegacyExamScheduleImporter {
  private final AttendeeExtentImpl myBufferAttendeeExtent;
  private final TopicExtentImpl myBufferTopicExtent;
  private final AuditoriumExtentImpl myBufferAuditoriumExtent;
  
  /**
   * Constructor copies existing data into in-memory buffers
   */
  LegacyExamScheduleImporter(AuditoriumExtent auditoriumExtent, AttendeeExtent attendeeExtent, TopicExtent topicExtent) {
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
  }

  /**
   * runs import process
   * 
   * @param text input text
   * @param calendar calendar ID to import to
   * @return script in Baobab language
   */
  String importSchedule(String text, String calendar) {
    StringBuilder result = new StringBuilder();
    List<Attendee> groups = null;
    List<String> lines = Lists.newArrayList();
    for (String s : text.split("[\n\r]+")) {
      if (!Strings.isNullOrEmpty(s.trim())) {
        lines.add(s.trim());
      }
    }
    if (lines.get(0).startsWith("#")) {
      groups = importAttendees(lines.get(0).substring(1), Attendee.Type.ACADEMIC_GROUP, result);
    }
    if (!groups.isEmpty()) {
      lines = lines.subList(1, lines.size());
    }
    
    assert lines.size() % 4 == 0;
    List<Topic> topics = Lists.newArrayList();
    for (int i = 0; i < lines.size() / 4; i++) {
      topics.add(process(groups, lines.get(i * 4), lines.get(i * 4 + 1), lines.get(i * 4 + 2), lines.get(i * 4 + 3), result));
    }
    result.append(String.format("добавить в календарь \"%s\" события %s", calendar, createIDList(topics, new Function<Topic, String>() {
      @Override
      public String apply(Topic t) {
        return "\"" + t.getID() + "\"";
      }
    })));
    return result.toString();
  }

  private Topic process(List<Attendee> groups, String topicLine, String teacherLine, String dateLine,
      String auditoriumLine, StringBuilder commands) {
    String commonPrefix = groups.get(0).getName();
    for (int i = 1; i < groups.size(); i++) {
      commonPrefix = Strings.commonPrefix(commonPrefix, groups.get(i).getName());
    }
    Auditorium aud = importAuditorium(auditoriumLine, commands);
    List<Attendee> teachers = importAttendees(extractName(teacherLine), Attendee.Type.TEACHER, commands);
    Topic topic = importTopic(topicLine, Sets.newHashSet(teachers), commonPrefix, commands);
    assert aud != null && !teachers.isEmpty() && topic != null;
    createEvent(topic, aud, groups, dateLine, commands);
    return topic;
  }

  private void createEvent(Topic topic, Auditorium auditorium, List<Attendee> groups, String dateLine,
      StringBuilder commands) {
    Preconditions.checkNotNull(topic, "Topic is undefined");
    Preconditions.checkNotNull(auditorium, "Auditorium is not defined");
    Preconditions.checkNotNull(groups, "Groups are not defined");
    Preconditions.checkArgument(!groups.isEmpty(), "Group list is empty");
    String command = String.format("событие \"%s\" состоится на \"экзамен\" %s в \"%s\" для %s", topic.getID(),
        convertDate(dateLine), auditorium.getID(), createAttendeeList(groups));
    commands.append(command).append("\n");
  }

  /**
   * Converts date from dd.MM to yyyy-MM-dd
   */
  private String convertDate(String date) {
    String[] components = date.split("\\.");
    assert components.length == 2;
    String year = "12".equals(components[1]) ? "2012" : "2013";
    return Joiner.on('-').join(year, components[1], components[0]);
  }

  /**
   * Extracts teacher name from a string with teacher position prefix
   */
  private String extractName(String teacherLine) {
    List<String> components = Arrays.asList(teacherLine.split("\\s+"));
    for (int i = 0; i < components.size(); i++) {
      if (!components.get(i).endsWith(".")) {
        return Joiner.on(' ').join(components.subList(i, components.size())); 
      }
    }
    assert false : "Не найдена ни одна полная фамилия в " + teacherLine;
    return teacherLine;
  }

  /**
   * Searches for existing auditorium and generates a command to create a new one if 
   * auditorium is not yet defined
   */
  private Auditorium importAuditorium(String line, StringBuilder commands) {
    Auditorium result = myBufferAuditoriumExtent.find(line);
    if (result == null) {
      String command = String.format("определить помещение \"%s\"", line);
      if (new AuditoriumCommandParser(myBufferAuditoriumExtent).parse(command)) {
        commands.append(command).append("\n");
        result = myBufferAuditoriumExtent.find(line);
      }
    }
    assert result != null;
    return result;
  }

  /**
   * Searches for existing topic with the given topic name and set of owners
   * and generates a command to create a new one if topic is not yet defined 
   */
  private Topic importTopic(String topicName, Set<Attendee> teachers, String groupPrefix, StringBuilder commands) {
    for (Topic t : myBufferTopicExtent.getAll()) {
      if (t.getName().equals(topicName) && Sets.newHashSet(t.getOwners()).equals(teachers)) {
        return t;
      }
    }
    String abbr = createAbbreviation(topicName) + "-";
    String suffix = groupPrefix + "-exam";
    char c = 'a';
    while (myBufferTopicExtent.find(abbr + suffix) != null) {
      suffix = groupPrefix + "-exam" + c;
      c++;
    }
    String command = String.format("определить экзамен \"%s\" \"%s\" владельцы %s", 
        abbr + suffix, topicName, createAttendeeList(teachers));
    if (new EventDeclareCommandParser(myBufferTopicExtent, myBufferAttendeeExtent).parse(command)) {
      commands.append(command).append("\n");
      return myBufferTopicExtent.find(abbr + suffix);
    }
    return null;
  }

  private static Pattern ABBR_PATTERN = Pattern.compile("^[a-zA-Zа-яА-Я].*");
  /**
   * generates abbreviation consisting of lowercased first characters of words in the given string
   */
  private String createAbbreviation(String line) {
    StringBuilder result = new StringBuilder();
    for (String word : line.split("\\s+")) {
      if (!word.isEmpty()) {
        if (ABBR_PATTERN.matcher(word).matches()) {
          result.append(word.charAt(0));
        }
      }
    }
    return result.toString().toLowerCase();
  }

  /**
   * Searches for existing attendees enumerated in the id list and generates
   * commands to create attendees which are not yet defined
   * 
   * @param isList comma-separated list of attendee IDs
   * @param type type of attendees to create
   */
  private List<Attendee> importAttendees(String idList, Attendee.Type type, StringBuilder commands) {
    List<Attendee> result = Lists.newArrayList();
    for (String groupId : idList.split("\\s*,\\s*")) {
      Attendee group = myBufferAttendeeExtent.find(groupId);
      if (group != null) {
        result.add(group);
        continue;
      }
      String typeModifier;
      switch (type) {
      case ACADEMIC_GROUP:
        typeModifier = "учебную группу";
        break;
      case TEACHER:
        typeModifier = "преподавателя";
        break;
      default:
        assert false;
        typeModifier = null;
      }
      String command = String.format("определить участника \"%s\" как %s", groupId, typeModifier);
      if (new AttendeeCommandParser(myBufferAttendeeExtent).parse(command)) {
        commands.append(command).append("\n");
      }
      group = myBufferAttendeeExtent.find(groupId);
      assert group != null;
      result.add(group);
    }
    return result;
  }
  
  /**
   * Creates a list of comma-separated quoted attendee IDs
   */
  private static String createAttendeeList(Collection<Attendee> attendees) {
    return createIDList(attendees, new Function<Attendee, String>() {
      @Override
      public String apply(Attendee a) {
        return "\"" + a.getID() + "\"";
      }
    });
  }
  
  private static <T> String createIDList(Collection<T> objects, Function<T, String> getId) {
    return Joiner.on(',').join(Collections2.transform(objects, getId));
  }
  
  static class OfficeHourGenerator {
    static String generateOficeHours(TopicExtent topicExtent) {
      Multimap<String, String> att2oh = TreeMultimap.create();
      StringBuilder buffer = new StringBuilder();
      for (Topic t : topicExtent.getAll()) {
        if (t.getType() == Topic.Type.EXAM) {
          String ohID = t.getID().replace("exam", "oh");
          buffer.append(String.format("определить консультация \"%s\" \"%s\"\n", ohID, t.getID()));
          for (Event e : t.getEvents()) {
            String attendees = LegacyExamScheduleImporter.createAttendeeList(e.getAttendees());
            att2oh.put(attendees, String.format("событие \"%s\" состоится на консультация %s в \"%s\" для %s\n", 
                ohID, getPrevWorkingDate(e.getStartDate()), e.getAuditorium().getID(), attendees));
          }
        }
      }
      for (String s : att2oh.values()) {
        buffer.append(s);
      }
      return buffer.toString();
    }
    
    private static String getPrevWorkingDate(Date startDate) {
      Calendar c = (Calendar) Calendar.getInstance(new Locale("ru", "RU")).clone();;
      c.setTime(startDate);
      do {
        c.add(Calendar.DAY_OF_YEAR, -1);
      } while (c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY);
      return String.format("%04d-%02d-%02d", c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH));
    }
  }
}
