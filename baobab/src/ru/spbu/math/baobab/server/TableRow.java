package ru.spbu.math.baobab.server;

import java.util.Collection;

import com.google.common.collect.Lists;

public class TableRow {
  private final Collection<TableCell> myTableRow;

  public TableRow() {
    myTableRow = Lists.newArrayList();
  }
}
