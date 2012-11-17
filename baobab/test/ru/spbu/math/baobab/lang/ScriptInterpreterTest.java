package ru.spbu.math.baobab.lang;

import junit.framework.TestCase;

/**
 * Tests for script interpreter
 * 
 * @author vloginova
 */
public class ScriptInterpreterTest extends TestCase {
  public void testCommandCorrectnessEng() {
    ScriptInterpreter interpreter = new ScriptInterpreter();
    interpreter.process("define timeslot 1st_try 12:12 to 12:40 on even Monday");
    assertTrue(interpreter.getTimeslotExtent().findByWeekDay(1).size() == 1);
  }

  public void testCommandCorrectnessRus() {
    ScriptInterpreter interpreter = new ScriptInterpreter();
    interpreter.process("определить интервал 1st_try от 1:00 до 3:00 в нечетный вт");
    assertTrue(interpreter.getTimeslotExtent().findByWeekDay(2).size() == 1);
  }

  public void testIncorrectCommand() {
    boolean isExceptionCaught = false;
    try {
      ScriptInterpreter interpreter = new ScriptInterpreter();
      interpreter.process("define timeslot 1st_try to 12:40 on even Monday");
    } catch (Exception e) {
      isExceptionCaught = true;
    } finally {
      assertTrue(isExceptionCaught);
    }
  }
}
