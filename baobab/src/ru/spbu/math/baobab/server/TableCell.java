package ru.spbu.math.baobab.server;

/**
 * Class TableCell
 * 
 * @author dageev
 */
public class TableCell {
  private String myEvent = ""; // in future it will be EVENT

  public TableCell(String Event) {
    myEvent = Event;
  }

  public void setEvent(String Event) {
    myEvent = Event;
  }

  public String getEvent() {
    return myEvent;
  }
}
