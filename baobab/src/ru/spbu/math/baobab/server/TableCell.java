package ru.spbu.math.baobab.server;

import com.google.common.base.Objects;

/**
 * @author dageev
 */
public class TableCell {
  private final String myValue;

  public TableCell(String value) {
    myValue = value;
  }

  public String getValue() {
    return myValue;
  }

  @Override
  public int hashCode() {
    return myValue.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    TableCell other = (TableCell) obj;
    return myValue.equals(other.myValue);
  }
}
