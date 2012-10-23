package ru.spbu.math.baobab.server;

import ru.spbu.math.baobab.model.EvenOddWeek;
import ru.spbu.math.baobab.model.TimeInstant;
import ru.spbu.math.baobab.model.TimeSlot;
import ru.spbu.math.baobab.model.TimeSlotExtent;
/**
 * Implementation of TimeSlot interface
 * 
 * @author dageev
 */
public class TimeSlotImpl implements TimeSlot {
  private final String myName;
  private final int myDay;
  private final EvenOddWeek myFlashing;
  private final TimeInstant myStart;
  private final TimeInstant myFinish;
  private final TimeSlotExtent myTimeSlotExtent;
  
  
  public TimeSlotImpl(String name, TimeInstant start, TimeInstant finish,
      int day, EvenOddWeek flashing, TimeSlotExtent timeslotextent){
    myTimeSlotExtent = timeslotextent;
    myName=name;
    myDay=day; 
    myFlashing= flashing;
    myStart = start;
    myFinish= finish;
  }
  
  @Override
  public String getName(){
    return myName;
  }

  @Override
  public int getDayOfWeek(){
    return myDay;
  }

  @Override
  public EvenOddWeek getEvenOddWeek(){
    return myFlashing;
  }
  
  @Override
  public TimeInstant getStart(){
    return myStart;
  }
  
  @Override
  public TimeInstant getFinish(){
    return myFinish;
  }
  
  @Override
  public TimeSlotExtent getExtent(){
    return myTimeSlotExtent;
  }
}
