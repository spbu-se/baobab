package ru.spbu.math.baobab.model;

import java.util.Collection;

/**
 * Attendee is someone who attends events. It might be a student or a teacher, but it might also
 * be a group of students who are supposed to attend classes altogether.
 * 
 * @author dbarashev
 */
public interface Attendee {
  public enum Type {
    STUDENT, TEACHER, ACADEMIC_GROUP, CHAIR, FREE_FORM_GROUP
  }
  
  /**
   * @return attendee public name, e.g. "Mikhailo Lomonosov" for persons or "541" for academic groups
   */
  String getName();
  
  /**
   * @return for physical persons it is person login ID, as defined by authentication service;
   *         for academic groups it is a group name with group graduation year;
   *         for chairs it is a chair acronym;
   *         free-form groups define their IDs arbitrarily
   */
  String getID();
  
  /**
   * Changes attendee ID. 
   * @param id new ID
   * @throws IllegalArgumentException if attendee with the given ID already exists
   */
  void setID(String id);
  
  /**
   * @return attendee type
   */
  Type getType();
  
  /**
   * @return {@code true} if this is a group member
   */
  boolean isGroup();
  
  /**
   * @return group members if this attendee is a group, {@code null} otherwise
   */
  Collection<Attendee> getGroupMembers();
  
  /**
   * Adds a new member to a group. Throws {@link IllegalStateException} if {@code this} attendee is not a group.
   *  
   * @param member new group member
   */
  void addGroupMember(Attendee member);
}
