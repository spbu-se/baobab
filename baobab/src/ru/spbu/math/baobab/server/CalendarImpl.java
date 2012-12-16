package ru.spbu.math.baobab.server;

import java.util.Collection;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;

import ru.spbu.math.baobab.model.Calendar;
import ru.spbu.math.baobab.model.Topic;

/**
 * In-memory implementation of Calendar
 * 
 * @author vloginova
 */
public class CalendarImpl implements Calendar {
  private Collection<Topic> myTopics = Lists.newArrayList();
  private final String myID;

  public CalendarImpl(String id) {
    myID = id;
  }

  @Override
  public Collection<Topic> getAllTopics() {
    return myTopics;
  }

  @Override
  public void addTopic(Topic topic) {
    myTopics.add(topic);
  }

  @Override
  public String getID() {
    return myID;
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
    if (obj instanceof Calendar == false) {
      return false;
    }
    Calendar calendar = (Calendar) obj;
    if (this.getID().equals(calendar.getID())) {
      return true;
    }
    return false;
  }
}
