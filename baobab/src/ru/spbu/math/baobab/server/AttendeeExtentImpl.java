package ru.spbu.math.baobab.server;

import java.util.Collection;
import java.util.HashMap;
import static com.google.common.collect.Maps.newHashMap;

import ru.spbu.math.baobab.model.Attendee;
import ru.spbu.math.baobab.model.Attendee.Type;
import ru.spbu.math.baobab.model.AttendeeExtent;

/**
 * Implementation of AttendeeExtent interface
 * 
 * @author agudulin
 */
public class AttendeeExtentImpl implements AttendeeExtent {
  private final HashMap<String, Attendee> myAttendees = newHashMap();
  
  @Override
  public Attendee create(String id, String name, Type type) {
    Attendee attendee = new AttendeeImpl(id, name, type, this);
    // Constructor will throw IllegalArgumentException if 
    // an attendee with current id already exists
    myAttendees.put(id, attendee);
    return attendee;
  }

  @Override
  public Attendee find(String id) {
    return myAttendees.get(id);
  }

  @Override
  public Collection<Attendee> getAll() {
    return myAttendees.values();
  }
}
