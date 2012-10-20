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
  private Type myType;
  private String myId;
  private Collection<Attendee> myMembers = new ArrayList<Attendee>();
	private AttendeeExtent myExtent;
  
  public AttendeeImpl(String id, String name, Type type, AttendeeExtent extent) {
  	this.myExtent = extent;
    this.setID(id);
    this.myName = name;
    this.myType = type;
  }

	public String getName() {
    return this.myName;
  }
  
  public String getID() {
    return this.myId;
  }
  
  public void setID(String id) {
  	if (this.myExtent.find(id) != null) {
  		throw new IllegalArgumentException("Attendee with the given ID already exists");
  	}
    this.myId = id;
  }
  
  public Type getType() {
    return this.myType;
  }
  
  public boolean isGroup() {
    return (this.myType == Type.ACADEMIC_GROUP || this.myType == Type.FREE_FORM_GROUP);
  }
  
  public Collection<Attendee> getGroupMembers() {
    if (!this.isGroup()) {
      return null;
    }
    return this.myMembers;
  }
  
  public void addGroupMember(Attendee member) {
    if (!this.isGroup()) {
      throw new IllegalStateException("The attendee is not a group.");
    }
		this.getGroupMembers().add(member);
  }
}
