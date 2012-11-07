package ru.spbu.math.baobab.server;

import java.util.List;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;

/**
 * @author dageev
 */
public class TableRow {
  private final List<TableCell> myCells = Lists.newArrayList();

  public TableRow() {
  }

  public void addCell(TableCell cell) {
    myCells.add(cell);
  }

  public List<TableCell> getCells() {
    return myCells;
  }

  @Override
  public int hashCode() {
    return com.google.common.base.Objects.hashCode(myCells);
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
    TableRow other = (TableRow) obj;
    return Objects.equal(myCells, other.myCells);
  }
}
