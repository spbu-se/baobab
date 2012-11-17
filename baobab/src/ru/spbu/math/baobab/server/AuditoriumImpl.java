package ru.spbu.math.baobab.server;

import com.google.appengine.api.search.checkers.Preconditions;

import ru.spbu.math.baobab.model.Auditorium;

/**
 * In-memory implementation of Auditorium
 * 
 * @author aoool
 */
public class AuditoriumImpl implements Auditorium {

  private final String myID;
  private final int myCapacity;

  // constructor
  public AuditoriumImpl(String id, int capacity) {
    myID = id;
    Preconditions.checkArgument(capacity >= 0, "Capacity must be >= 0");
    myCapacity = capacity;
  }

  @Override
  public String getID() {
    return myID;
  }

  @Override
  public int getCapacity() {
    return myCapacity;
  }

  @Override
  public int hashCode() {
    return myID.hashCode();
  }

  @Override
  public boolean equals(Object instance) {
    if (instance instanceof Auditorium) {
      Auditorium auditorium = (Auditorium) instance;
      if (getID().equals(auditorium.getID())) {
        return true;
      }
    }
    return false;
  }
}
