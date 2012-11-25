package ru.spbu.math.baobab.lang;

import java.util.Arrays;
import java.util.List;

import ru.spbu.math.baobab.server.AuditoriumExtentImpl;
import ru.spbu.math.baobab.server.TimeSlotExtentImpl;

/**
 * Script interpreter processes the lines satisfying specifications of the Baobab language and executes them
 * 
 * @author ragozina.anastasiya, vloginova
 */
public class ScriptInterpreter {
  private final List<Parser> tsParsers = Arrays.asList((Parser) new TimeSlotCommandParser(new TimeSlotExtentImpl()));
  private final List<Parser> auditoriumParsers = Arrays.asList((Parser) new AuditoriumCommandParser(new AuditoriumExtentImpl()));
  

  /**
   * Receive command and process it
   * 
   * @param command some command in baobab language
   */
  public void process(String command) {
    for (Parser p : tsParsers) {
      if (p.parse(command)) {
        return;
      }
    }
    for (Parser p : auditoriumParsers) {
      if (p.parse(command)) {
        return;
      }
    }
    throw new IllegalArgumentException("Incorrect command");
  }
}
