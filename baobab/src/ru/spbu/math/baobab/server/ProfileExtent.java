package ru.spbu.math.baobab.server;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;

import ru.spbu.math.baobab.model.Attendee;

/**
 * ProfileExtent is a set of all user profiles.
 * 
 * @author aoool
 */
public class ProfileExtent {

  static {
    ObjectifyService.register(Profile.class);
  }

  /**
   * Creates a new Profile.
   * 
   * @param userKey key of user for whom we need to create profile
   * @param email user's email (can be null)
   * @param academicGroup for academic group memberships for ex. students (can be null)
   * @param chair for chair memberships (ex. teacher) (can be null)
   * @return new Profile instance
   * @throws IllegalArgumentException if 1. Profile of user with the given userKey is already exist; 2. all parameters
   *           are null; 3. user with the given email is already exist
   */
  public Profile create(String userKey, String email, Attendee academicGroup, Attendee chair) {
    if ((userKey == null) && (email == null) && (academicGroup == null) && (chair == null)) {
      throw new IllegalArgumentException("All parameters were == null."); // there is no need to create empty profile
    }
    Objectify ofy = ObjectifyService.begin();
    Profile profile = ofy.query(Profile.class).filter("myEmail", email).get();
    if(profile != null) {
      throw new IllegalArgumentException("User profile with the same email is already exist.");
    }
    profile = ofy.find(Profile.class, userKey);
    if (profile != null) {
      throw new IllegalArgumentException("User profile with the given user key is already exist.");
    } else {
      profile = new Profile(userKey, email, academicGroup.getID(), chair.getID());
      ofy.put(profile);
    }
    return profile;
  }
  
  /**
   * Creates a new Profile.
   * 
   * @param userKey key of user for whom we need to create profile
   * @param email user's email (can be null)
   * @param academicGroupId for academic group memberships for ex. students (can be null)
   * @param chairId for chair memberships (ex. teacher) (can be null)
   * @return new Profile instance
   * @throws IllegalArgumentException if 1. Profile of user with the given userKey is already exist; 2. all parameters
   *           are null; 3. user with the given email is already exist
   */
  public Profile create(String userKey, String email, String academicGroupId, String chairId) {
    if ((userKey == null) && (email == null) && (academicGroupId == null) && (chairId == null)) {
      throw new IllegalArgumentException("All parameters were == null."); // there is no need to create empty profile
    }
    Objectify ofy = ObjectifyService.begin();
    Profile profile = ofy.query(Profile.class).filter("myEmail", email).get();
    if(profile != null) {
      throw new IllegalArgumentException("User profile with the same email is already exist.");
    }
    profile = ofy.find(Profile.class, userKey);
    if (profile != null) {
      throw new IllegalArgumentException("User profile with the given user key is already exist.");
    } else {
      profile = new Profile(userKey, email, academicGroupId, chairId);
      ofy.put(profile);
    }
    return profile;
  }

  /**
   * Finds Profile by user key.
   * 
   * @param userKey user key
   * @return found Profile or {@code null} if Profile of user with the given userKey doesn't exists
   */
  public Profile find(String userKey) {
    Objectify ofy = ObjectifyService.begin();
    return ofy.find(Profile.class, userKey);
  }

  /**
   * Finds Profile by email.
   * 
   * @param email user's email
   * @return found Profile or {@code null} if Profile of user with the given userKey doesn't exists
   */
  public Profile findByEmail(String email) {
    Objectify ofy = ObjectifyService.begin();
    return ofy.query(Profile.class).filter("myEmail", email).get();
  }
}
