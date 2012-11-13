package ru.spbu.math.baobab.server;

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
    if (capacity < 0) {
      capacity = 0;
    }
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

}
