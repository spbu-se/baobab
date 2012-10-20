package ru.spbu.math.baobab.server;

import java.util.Collection;
import ru.spbu.math.baobab.model.Attendee;

/**
 * Implementation of Attendee interface
 * 
 * @author sagod
 */
public class AttendeeImpl implements Attendee {
	public String name;
	private Type type;
	private String id;
	private Collection<Attendee> members;
	
	public AttendeeImpl(String id, String name, Type type) {
		this.setID(id);
		this.name = name;
		this.type = type;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getID() {
		return this.id;
	}
	
	public void setID(String id) {
		if (this.id == id) {
			throw new IllegalArgumentException("Attendee with the given ID already exists");
		}
		this.id = id;
	}
	
	public Type getType() {
		return this.type;
	}
	
	public boolean isGroup() {
		return (this.type == Type.ACADEMIC_GROUP || this.type == Type.FREE_FORM_GROUP);
	}
	
	public Collection<Attendee> getGroupMembers() {
		if (!this.isGroup()) {
			return null;
		}
		return this.members;
	}
	
	public void addGroupMember(Attendee member) {
		if (!this.isGroup()) {
			throw new IllegalStateException("The attendee is not a group.");
		}
		this.members.add(member);
	}
}
