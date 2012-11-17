package ru.spbu.math.baobab.server;

import static com.google.common.collect.Maps.newHashMap;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import ru.spbu.math.baobab.model.Auditorium;
import ru.spbu.math.baobab.model.AuditoriumExtent;

/**
 * In-memory implementation of AuditoriumExtent
 * 
 * @author aoool
 */
public class AuditoriumExtentImpl implements AuditoriumExtent {

  private final HashMap<String, Auditorium> myAuditoriums = newHashMap();

  @Override
  public Auditorium create(String id, int capacity) {
    if (myAuditoriums.get(id) != null) {
      throw new RuntimeException("Auditurium with the given id already exists.");
    }
    Auditorium auditorium = new AuditoriumImpl(id, capacity); // if capacity < 0 constructor will assign it to 0
    myAuditoriums.put(id, auditorium);
    return auditorium;
  }

  @Override
  public Collection<Auditorium> getAll() {
    return Collections.unmodifiableCollection(myAuditoriums.values());
  }

  @Override
  public Auditorium find(String id) {
    return myAuditoriums.get(id);
  }
}
