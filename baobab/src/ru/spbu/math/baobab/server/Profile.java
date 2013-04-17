package ru.spbu.math.baobab.server;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.persistence.Id;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.annotation.Entity;
import ru.spbu.math.baobab.model.Attendee;

/**
 * Profile instance manages personal information about single Attendee (in case attendee provides this information).
 * Signed-in users can add or change the following information: 
 * 1. Membership in academic group if user is a student 
 * 2. Department if user is a teacher 
 * 3. Email/XMPP for notifications
 * @author aoool
 */
@Entity(name = "Profile")
public class Profile {

  @Id
  private String myUserKey;
  private String myAcademicGroupId;
  private String myDepartmentId;
  private String myEmail;

  public Profile() {
    // we need no-arg constructor to use objectify
  }

  public Profile(String userKey, String email, String academicGroupId, String chairId) {
    myUserKey = userKey;
    myEmail = email;
    myAcademicGroupId = academicGroupId;
    myDepartmentId = chairId;
  }

  /**
   * @return user key (it is still unique for profile if you use it like this Profile.class, userKey)
   */
  public String getUserKey() {
    return myUserKey;
  }

  /**
   * Sets or changes user's membership in Academic Group.
   * 
   * @param academicGroup Attendee with a type == ACADEMIC_GROUP
   * @throws IllegalArgumentException - if attendee's type != ACADEMIC_GROUP
   */
  public void setAcademicGroup(Attendee academicGroup) {
    myAcademicGroupId = academicGroup.getID();
    Objectify ofy = ObjectifyService.begin();
    ofy.put(this);
  }

  /**
   * @return attendee's ID whose type is ACADEMIC_GROUP otherwise null
   */
  public String getAcademicGroupId() {
    return myAcademicGroupId;
  }

  /**
   * Sets or changes user's membership in Chair.
   * 
   * @param chair Attendee with a type == CHAIR
   * @throws IllegalArgumentException - if attendee's type != CHAIR
   */
  public void setChair(Attendee chair) {
    myDepartmentId = chair.getID();
    Objectify ofy = ObjectifyService.begin();
    ofy.put(this);
  }

  /**
   * @return attendee's ID whose type is CHAIR otherwise null
   */
  public String getChairId() {
    return myDepartmentId;
  }

  /**
   * Sets or changes user's email/XMPP.
   * 
   * @param email User's email or XMPP
   * @throws IllegalArgumentException if:
   * 1. entered email or XMPP was wrong 
   * 2.entered email already in use
   */
  public void setEmail(String email) {
    if (myEmail == email) {
      return;
    }
    if (!isValidEmailAddress(email)) {
      throw new IllegalArgumentException("Wrong format of email address/XMPP was entered.");
    }
    Objectify ofy = ObjectifyService.begin();
    if (ofy.query(Profile.class).filter("myEmail", email).get() != null) {
      throw new IllegalArgumentException("Email is alredy in use.");
    }
    myEmail = email;
    ofy.put(this);
  }

  /**
   * @return user's email or XMPP
   */
  public String getEmail() {
    return myEmail;
  }

  /**
   * Validates email address.
   * 
   * @param email User's email or XMPP
   * @return true if email address is valid otherwise false
   */
  public static boolean isValidEmailAddress(String email) {
    boolean result = true;
    try {
      InternetAddress emailAddr = new InternetAddress(email);
      emailAddr.validate();
    } catch (AddressException ex) {
      result = false;
    }
    return result;
  }
}
