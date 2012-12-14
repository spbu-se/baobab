package ru.spbu.math.baobab.server;

import java.util.Collection;

import com.google.common.collect.Lists;

import ru.spbu.math.baobab.model.Calendar;
import ru.spbu.math.baobab.model.CalendarExtent;

/**
 * In-memory implementation of CalendarExtent
 * 
 * @author vloginova
 */
public class CalendarExtentImpl implements CalendarExtent {
  private Collection<Calendar> myCalendars = Lists.newArrayList();

  @Override
  public Calendar create(String uid) {
    Calendar calendar = new CalendarImpl(uid);
    if (myCalendars.contains(calendar)) {
      throw new RuntimeException("Calendar with the given UID already exists.");
    }
    myCalendars.add(calendar);
    return calendar;
  }

  @Override
  public Calendar find(String uid) {
    for (Calendar topic : myCalendars) {
      if (topic.getID().equals(uid)) {
        return topic;
      }
    }
    return null;
  }
}
