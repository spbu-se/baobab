package ru.spbu.math.baobab.lang;

import java.util.Arrays;
import java.util.List;
import ru.spbu.math.baobab.model.*;
import ru.spbu.math.baobab.server.TimeSlotExtentImpl;

/**
 * Script interpreter processes the lines satisfying specifications of the Baobab language and executes them
 * 
 * @author Ragozina Anastasiya, Loginova Vita
 */
public class ScriptInterpreter {
  private final TimeSlotExtent myTimeSlotExtent = new TimeSlotExtentImpl();
  private final List<Parser> parsers = Arrays.asList((Parser)new TimeSlotCommandParser(getTimeslotExtent()));

  /**
   * Receive command and process it
   * 
   * @param command
   *          some command in baobab language
   */
  public void process(String command) {
    for (Parser p : parsers) {
      if (p.parse(command)) {
        return;
      }
    }
    throw new IllegalArgumentException("Incorrect command");
  }

  public TimeSlotExtent getTimeslotExtent() {
    return myTimeSlotExtent;
  }
}
