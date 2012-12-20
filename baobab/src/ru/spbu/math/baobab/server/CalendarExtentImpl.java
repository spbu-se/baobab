package ru.spbu.math.baobab.server;

import static com.google.common.collect.Maps.newHashMap;

import java.util.Collection;
import java.util.HashMap;

import com.google.common.collect.Lists;

import ru.spbu.math.baobab.model.Auditorium;
import ru.spbu.math.baobab.model.Calendar;
import ru.spbu.math.baobab.model.CalendarExtent;

/**
 * In-memory implementation of CalendarExtent
 * 
 * @author vloginova
 */
public class CalendarExtentImpl implements CalendarExtent {
  private final HashMap<String, Calendar> myCalendars = newHashMap();

  @Override
  public Calendar create(String uid) {
    if (myCalendars.get(uid) != null) {
      throw new RuntimeException("Calendar with the given UID already exists.");
    }
    Calendar calendar = new CalendarImpl(uid);
    myCalendars.put(uid, calendar);
    return calendar;
  }

  @Override
  public Calendar find(String uid) {
    return myCalendars.get(uid);
  }
}
