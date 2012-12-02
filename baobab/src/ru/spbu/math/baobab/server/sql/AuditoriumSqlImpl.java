package ru.spbu.math.baobab.server.sql;

import com.google.common.base.Objects;

import ru.spbu.math.baobab.model.Auditorium;

/**
 * SQL-based implementation of Auditorium
 * 
 * @author vloginova
 */
public class AuditoriumSqlImpl implements Auditorium {

  private final String myID;
  private final int myCapacity;

  public AuditoriumSqlImpl(String id, int capacity) {
    myID = id;
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
    return Objects.hashCode(myID);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof Auditorium == false) {
      return false;
    }
    Auditorium auditorium = (Auditorium) obj;
    if (this.getID().equals(auditorium.getID())) {
      return true;
    }
    return false;
  }
}
