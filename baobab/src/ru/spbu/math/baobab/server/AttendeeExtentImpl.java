package ru.spbu.math.baobab.server;

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
  private HashMap<String, Attendee> myAttendees = newHashMap();
  
  public AttendeeExtentImpl() {}
  
  public AttendeeExtentImpl(String id, String name, Type type) {
  	this.create(id, name, type);
  }
  
  @Override
  public Attendee create(String id, String name, Type type) {
    Attendee attendee = new AttendeeImpl(id, name, type, this);
    // Constructor will throw IllegalArgumentException if 
    // an attendee with current id already exists
    this.myAttendees.put(id, attendee);
    return attendee;
  }

  public Attendee find(String id) {
    if (this.myAttendees.containsKey(id)) {
    	return this.myAttendees.get(id);
    }
    return null;
  }
}
