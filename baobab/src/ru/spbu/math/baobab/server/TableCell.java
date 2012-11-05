package ru.spbu.math.baobab.server;

/**
 * @author dageev
 */
public class TableCell {
  private String myEvent = ""; // in future it will be EVENT

  public TableCell(String event) {
    myEvent = event;
  }

  public void setEvent(String event) {
    myEvent = event;
  }

  public String getEvent() {
    return myEvent;
  }
}
