package ru.spbu.math.baobab.server;

public class TableCell {

  private String myEvent = new String(); // in future it will be EVENT

  public TableCell() {
  }

  public TableCell(String Event) {
    myEvent = Event;
  }

  public void SetEvent(String Event) {
    myEvent = Event;
  }

  public String GetEvent() {
    return myEvent;
  }
}
