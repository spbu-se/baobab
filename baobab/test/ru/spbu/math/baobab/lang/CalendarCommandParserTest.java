package ru.spbu.math.baobab.lang;

import java.util.Collection;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;

import junit.framework.TestCase;
import ru.spbu.math.baobab.model.Calendar;
import ru.spbu.math.baobab.model.Topic;
import ru.spbu.math.baobab.server.CalendarExtentImpl;
import ru.spbu.math.baobab.server.TopicExtentImpl;

/**
 * Tests calendar command parser behavior
 * 
 * @author dbarashev
 */
public class CalendarCommandParserTest extends TestCase {
  private static final Function<Topic, String> getTopicID = new Function<Topic, String>() {
    @Override
    public String apply(Topic topic) {
      return topic.getID();
    }
  };
  
  private TopicExtentImpl myTopicExtent;
  private CalendarExtentImpl myCalendarExtent;
  
  @Override
  protected void setUp() throws Exception {
    myTopicExtent = new TopicExtentImpl();
    myCalendarExtent = new CalendarExtentImpl();
  }

  private static void assertTopicIdSet(Set<String> expected, Collection<Topic> actual) {
    assertEquals(expected, Sets.newHashSet(Collections2.transform(actual, getTopicID)));
  }
  
  public void testAddExistingTopicToExistingCalendar() {
    myTopicExtent.createTopic("exam01", Topic.Type.EXAM, "Calculus");
    myTopicExtent.createTopic("exam02", Topic.Type.EXAM, "Algebra");
    Calendar calendar = myCalendarExtent.create("расписание экзаменов зима 2012 2013");
    
    CalendarCommandParser parser = new CalendarCommandParser(myCalendarExtent, myTopicExtent);
    assertTrue(parser.parse("  добавить в календарь  \"расписание экзаменов зима 2012 2013\"  события  exam01, \"exam02\"  "));
    assertTopicIdSet(Sets.newHashSet("exam02", "exam01"), calendar.getAllTopics());
  }
  
  public void testAddExistingTopicToNotExistingCalendar() {
    myTopicExtent.createTopic("exam01", Topic.Type.EXAM, "Calculus");
    myTopicExtent.createTopic("exam02", Topic.Type.EXAM, "Algebra");
    
    CalendarCommandParser parser = new CalendarCommandParser(myCalendarExtent, myTopicExtent);
    assertTrue(parser.parse("  добавить в календарь  \"расписание экзаменов зима 2012 2013\"  события  exam01, \"exam02\"  "));
    
    Calendar calendar = myCalendarExtent.find("расписание экзаменов зима 2012 2013");
    assertNotNull(calendar);
    assertTopicIdSet(Sets.newHashSet("exam02", "exam01"), calendar.getAllTopics());    
  }
  
  public void testAddNotExistingTopic() {
    myTopicExtent.createTopic("exam01", Topic.Type.EXAM, "Calculus");
    myCalendarExtent.create("расписание экзаменов зима 2012 2013");
    
    CalendarCommandParser parser = new CalendarCommandParser(myCalendarExtent, myTopicExtent);
    try {
      parser.parse("добавить в календарь  \"расписание экзаменов зима 2012 2013\"  события  exam01, \"exam02\"  ");
      fail("Expected that exception is thrown because exam02 is undefined");
    } catch (IllegalArgumentException e) {
      // OK
    }
  }
  
  public void testSyntaxErrorNoTopicList() {
    myTopicExtent.createTopic("exam01", Topic.Type.EXAM, "Calculus");
    myTopicExtent.createTopic("exam02", Topic.Type.EXAM, "Algebra");
    myCalendarExtent.create("расписание экзаменов зима 2012 2013");
    
    CalendarCommandParser parser = new CalendarCommandParser(myCalendarExtent, myTopicExtent);
    assertFalse(parser.parse("добавить в календарь  \"расписание экзаменов зима 2012 2013\" "));
  }
}
