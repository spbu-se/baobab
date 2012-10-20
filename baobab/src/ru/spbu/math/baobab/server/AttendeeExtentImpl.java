package ru.spbu.math.baobab.server;

import java.util.Collection;

import ru.spbu.math.baobab.model.Attendee;
import ru.spbu.math.baobab.model.Attendee.Type;
import ru.spbu.math.baobab.model.AttendeeExtent;

/**
 * Implementation of AttendeeExtent interface
 * 
 * @author sagod
 */
public class AttendeeExtentImpl implements AttendeeExtent {
	private Collection<Attendee> setOfAttendees;
	
	public Attendee create(String id, String name, Type type) {
		Attendee attendee = new AttendeeImpl(id, name, type);
		// Constructor will throw IllegalArgumentException if 
		// an attendee with current id already exists
		this.setOfAttendees.add(attendee);
		return attendee;
	}

	public Attendee find(String id) {
		for (Attendee member: this.setOfAttendees) {
			if (member.getID() == id) {
				return member;
			}
		}
		return null;
	}
}
