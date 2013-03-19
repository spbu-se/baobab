package ru.spbu.math.baobab.model;

/**
 * UserProfile instance manages personal information about single Attendee (in case attendee provides this information).
 * Signed-in users can add or change the following information: 1. Membership in academic group if user is a student; 2.
 * Department if user is a teacher; 3. Email/XMPP for notifications.
 * 
 * @author aoool
 */
public interface UserProfile {

  /**
   * Sets or changes user's membership in Academic Group.
   * 
   * @param academicGroup Attendee with a type == ACADEMIC_GROUP
   * @throws IllegalArgumentException - if attendee's type != ACADEMIC_GROUP
   */
  public void setAcademicGroup(Attendee academicGroup);

  /**
   * @return attendee with type == ACADEMIC_GROUP otherwise null
   */
  public Attendee getAcademicGroup();

  /**
   * Sets or changes user's membership in Chair.
   * 
   * @param chair Attendee with a type == CHAIR
   * @throws IllegalArgumentException - if attendee's type != CHAIR
   */
  public void setChair(Attendee chair);

  /**
   * @return attendee with type == CHAIR otherwise null
   */
  public Attendee getChair();

  /**
   * Sets or changes user's email/XMPP.
   * 
   * @param email User's email or XMPP
   * @throws IllegalArgumentException if entered email or XMPP was wrong
   */
  public void setEmail(String email);

  /**
   * @return user's email or XMPP
   */
  public String getEmail();
}
