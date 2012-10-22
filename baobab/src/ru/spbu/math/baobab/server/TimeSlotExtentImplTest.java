package ru.spbu.math.baobab.server;


import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import ru.spbu.math.baobab.model.EvenOddWeek;
import ru.spbu.math.baobab.model.TimeInstant;
import ru.spbu.math.baobab.model.TimeSlot;
import ru.spbu.math.baobab.model.TimeSlotExtent;
import junit.framework.TestCase;

public class TimeSlotExtentImplTest extends TestCase{
  
  public void testCreate() {
    TimeSlotExtent timeSlotExtent = new TimeSlotExtentImpl();
    TimeInstant start = new TimeInstant(9,30);
    TimeInstant finish = new TimeInstant(11,5);
    timeSlotExtent.create("first double class", start, finish, 2, EvenOddWeek.ALL);
    try{ 
      TimeInstant start1 = new TimeInstant(13,40);
      TimeInstant finish1 = new TimeInstant(15,15);
      timeSlotExtent.create("first double class", start1, finish1, 5, EvenOddWeek.ODD);
      fail("Expected IllegalArgumentException");
    }
    catch (IllegalArgumentException e){
      // can't create with existing name
    }
  }
  
  public void testfindByWeekDay(){
    TimeSlotExtent timeSlotExtent = new TimeSlotExtentImpl();
    TimeInstant start = new TimeInstant(9,30);
    TimeInstant finish = new TimeInstant(11,5);
    TimeInstant start1 = new TimeInstant(13,40);
    TimeInstant finish1 = new TimeInstant(15,15);
    timeSlotExtent.create("first double class1", start, finish, 2, EvenOddWeek.ALL);
    timeSlotExtent.create("first double class2", start, finish, 3, EvenOddWeek.ALL);
    timeSlotExtent.create("first double class3", start1, finish1, 4, EvenOddWeek.ALL);
    timeSlotExtent.create("first double class4", start, finish, 4, EvenOddWeek.ALL);
    List<TimeSlot> list = timeSlotExtent.findByWeekDay(4);
    assertEquals(list.get(0).getName(), "first double class4");
    assertEquals(list.get(1).getName(), "first double class3");
  }
  
  public void testfindByDate(){
    TimeSlotExtent timeSlotExtent = new TimeSlotExtentImpl();
    TimeInstant start = new TimeInstant(9,30);
    TimeInstant finish = new TimeInstant(11,5);
    TimeInstant start1 = new TimeInstant(13,40);
    TimeInstant finish1 = new TimeInstant(15,15);
    Calendar c = new GregorianCalendar();
    c.setTimeInMillis(System.currentTimeMillis());
    Date date1 = c.getTime();
    c.add(Calendar.WEEK_OF_YEAR, -1); 
    Date date2 = c.getTime();
    int day = c.get(Calendar.DAY_OF_WEEK);
    timeSlotExtent.create("first double class2", start1, finish1, day, EvenOddWeek.ALL);
    timeSlotExtent.create("first double class3", start, finish, day, EvenOddWeek.EVEN);
    timeSlotExtent.create("first double class4", start1, finish1,day, EvenOddWeek.ODD);
    List<TimeSlot> list = timeSlotExtent.findByDate(date1);
    List<TimeSlot> list1 = timeSlotExtent.findByDate(date2);
    assertNotSame(list.get(0).getName(),list1.get(0).getName());
  
    
  }
  
 

}
