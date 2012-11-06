package ru.spbu.math.baobab.server;

/**
 * @author dageev
 */
public class TableCell {
  private String myValue = "";

  public TableCell(String value) {
    myValue = value;
  }

  public void setValue(String value) {
    myValue = value;
  }

  public String getValue() {
    return myValue;
  }
}
