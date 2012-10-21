package ru.spbu.math.baobab.server;

import java.util.ArrayList;
import java.util.Collection;

import ru.spbu.math.baobab.model.Attendee;
import ru.spbu.math.baobab.model.AttendeeExtent;

/**
 * Implementation of Attendee interface
 * 
 * @author agudulin
 */
public class AttendeeImpl implements Attendee {
  private String myName;
  private final Type myType;
  private String myId;
  private final Collection<Attendee> myMembers = new ArrayList<Attendee>();
  private final AttendeeExtent myExtent;
  
  public AttendeeImpl(String id, String name, Type type, AttendeeExtent extent) {
    myExtent = extent;
    this.setID(id);
    myName = name;
    myType = type;
  }

  public String getName() {
    return myName;
  }
  
  public String getID() {
    return myId;
  }
  
  public void setID(String id) {
    if (myExtent.find(id) != null) {
      throw new IllegalArgumentException("Attendee with the given ID already exists");
    }
    myId = id;
  }
  
  public Type getType() {
    return myType;
  }
  
  public boolean isGroup() {
    return (myType == Type.ACADEMIC_GROUP || myType == Type.FREE_FORM_GROUP);
  }
  
  public Collection<Attendee> getGroupMembers() {
    if (!this.isGroup()) {
      return null;
    }
    return myMembers;
  }
  
  public void addGroupMember(Attendee member) {
    if (!this.isGroup()) {
      throw new IllegalStateException("The attendee is not a group.");
    }
    this.getGroupMembers().add(member);
  }
}
