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
  private String mName;
  private final int mDay;
  private final EvenOddWeek mFlashing;
  private final TimeInstant mStart;
  private final TimeInstant mFinish;
  private final TimeSlotExtent mTimeSlotExtent;
  
  
  public TimeSlotImpl(String name, TimeInstant start, TimeInstant finish,
			          int day, EvenOddWeek flashing, TimeSlotExtent timeslotextent){
	mTimeSlotExtent = timeslotextent;
	mName=name;
	mDay=day; 
	mFlashing= flashing;
	mStart = start;
	mFinish= finish;
  }
  
  public String getName(){
    return mName;
  }

  public int getDayOfWeek(){
    return mDay;
  }

  public EvenOddWeek getEvenOddWeek(){
	return mFlashing;
  }
  
  public TimeInstant getStart(){
	return mStart;
  }

  public TimeInstant getFinish(){
	return mFinish;
  }
  
  public TimeSlotExtent getExtent(){
	return mTimeSlotExtent;
  }
}
