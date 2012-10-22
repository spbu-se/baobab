package ru.spbu.math.baobab.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import ru.spbu.math.baobab.model.EvenOddWeek;
import ru.spbu.math.baobab.model.TimeInstant;
import ru.spbu.math.baobab.model.TimeSlot;
import ru.spbu.math.baobab.model.TimeSlotExtent;

/**
 * Implementation of TimeSlotExtent interface
 * 
 * @author dageev
 */

public class TimeSlotExtentImpl implements TimeSlotExtent{
  private Collection<TimeSlot> mTimeSlot = new ArrayList();

    @Override
	public Collection<TimeSlot> getAll() {
	  return mTimeSlot;
	}

	@Override
	public List<TimeSlot> findByWeekDay(int day) {
	  List<TimeSlot> pList = new ArrayList<TimeSlot>();
	  for(TimeSlot i : mTimeSlot){
		if(i.getDayOfWeek() == day)
	      pList.add(i);  
	  }
	  Collections.sort(pList, new Comparator<TimeSlot>() {
		public int compare(TimeSlot o1, TimeSlot o2) {
		  TimeInstant TimeStart1 = o1.getStart();
		  TimeInstant TimeStart2 = o2.getStart();
		  int val1 = TimeStart1.getHour()*60 + TimeStart1.getMinute();
		  int val2 = TimeStart2.getHour()*60 + TimeStart2.getMinute();
		  return (val1<val2 ? -1 : (val1==val2 ? 0 : 1));
	    }
	  });
	  return pList;
	}

	@Override
	public List<TimeSlot> findByDate(Date date) {
	  List<TimeSlot> pList = new ArrayList<TimeSlot>();
	  int day = date.getDay();
	  int EvenorOdd = date.getDate() / 14;
	  for(TimeSlot i : mTimeSlot){
		if(i.getDayOfWeek() == day){
	      boolean flag = true;
		  if ( i.getEvenOddWeek() == EvenOddWeek.ODD && EvenorOdd == 1)
		    flag = false;
		  if ( i.getEvenOddWeek() == EvenOddWeek.EVEN && EvenorOdd == 0)
			flag = false;
		  if (flag) pList.add(i);
		}
	  }
	  Collections.sort(pList, new Comparator<TimeSlot>() {
	    public int compare(TimeSlot o1, TimeSlot o2) {
		  TimeInstant TimeStart1 = o1.getStart();
		  TimeInstant TimeStart2 = o2.getStart();
		  int val1 = TimeStart1.getHour()*60 + TimeStart1.getMinute();
		  int val2 = TimeStart2.getHour()*60 + TimeStart2.getMinute();
		  return (val1<val2 ? -1 : (val1==val2 ? 0 : 1));
		}
	  });
	  return pList;
	}

	@Override
	public TimeSlot create(String name, TimeInstant start, TimeInstant finish,
			               int day, EvenOddWeek flashing){
	  boolean Err=false;
	  for(TimeSlot i : mTimeSlot){
	    if (i.getName() == name) Err = false;
	  }
	  if (Err) 
		throw new IllegalStateException("The TimeSlot with this name is already exist");
	  TimeSlot timeslot = new TimeSlotImpl(name, start, finish, day, flashing, this);
      mTimeSlot.add(timeslot);
      return timeslot;
		
	}
}
