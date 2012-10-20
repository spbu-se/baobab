package ru.spbu.math.baobab.server;

import java.util.ArrayList;
import java.util.Collection;

import ru.spbu.math.baobab.model.Attendee;
import ru.spbu.math.baobab.model.Attendee.Type;
import junit.framework.TestCase;

/**
 * Some tests for AttendeeImpl
 * 
 * @author agudulin
 */
public class AttendeeImplTest extends TestCase {
	
	public void testSetID() {
		AttendeeExtentImpl attendeeExtent = new AttendeeExtentImpl();
		Attendee group = attendeeExtent.create("123", "Test", Type.ACADEMIC_GROUP);
		Attendee student = attendeeExtent.create("1", "Test", Type.STUDENT);
		Attendee teacher = attendeeExtent.create("2", "Test", Type.TEACHER);
		group.addGroupMember(student);
		group.addGroupMember(teacher);
		try {
			teacher.setID("1");
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			// OK: can't set new ID if attendee with this ID already exists 
		}
		teacher.setID("3");
		assertEquals(teacher.getID(), "3");
	}

	public void testIsGroup() {
		AttendeeExtentImpl attendeeExtent = new AttendeeExtentImpl();
		
		Attendee teacher = attendeeExtent.create("123", "Test", Type.TEACHER);
		assertEquals(teacher.isGroup(), false);
		
		Attendee academicGroup = attendeeExtent.create("124", "Test", Type.ACADEMIC_GROUP);
		assertEquals(academicGroup.isGroup(), true);
		
		Attendee ffGroup = attendeeExtent.create("125", "Test", Type.FREE_FORM_GROUP);
		assertEquals(ffGroup.isGroup(), true);
	}

	public void testGetGroupMembers() {
		AttendeeExtentImpl attendeeExtent = new AttendeeExtentImpl();
		Attendee academicGroup = attendeeExtent.create("124", "Test", Type.ACADEMIC_GROUP);
		Attendee teacher = attendeeExtent.create("1", "Test", Type.TEACHER);
		Attendee student = attendeeExtent.create("2", "Test", Type.STUDENT);
		academicGroup.addGroupMember(teacher);
		academicGroup.addGroupMember(student);
		
		Collection<Attendee> members = new ArrayList<Attendee>();
		members.add(teacher);
		members.add(student);
		
		assertEquals(academicGroup.getGroupMembers(), members);
		assertNull(teacher.getGroupMembers());
	}

	public void testAddGroupMember() {
		AttendeeExtentImpl attendeeExtent = new AttendeeExtentImpl();
		Attendee teacher = attendeeExtent.create("1", "Test", Type.TEACHER);
		Attendee student = attendeeExtent.create("2", "Test", Type.STUDENT);
		try {
			teacher.addGroupMember(student);
			fail("Expected IllegalStateException.");
		} catch (IllegalStateException e) {
			// OK: the attendee is not a group
		}
	}

}
