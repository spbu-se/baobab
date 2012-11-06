package ru.spbu.math.baobab.server;

import java.util.List;
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

  public List<TableCell> getRow() {
    return myCells;
  }
}
