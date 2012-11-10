package ru.spbu.math.baobab.lang;

import junit.framework.TestCase;
import ru.spbu.math.baobab.model.TimeSlotExtent;
import ru.spbu.math.baobab.server.TimeSlotExtentImpl;

/**
 * Tests for script interpreter
 * 
 * @author vloginova
 */
public class ScriptInterpreterTest extends TestCase {
  public void testCommandCorrectnessEng() {
    TimeSlotExtent timeSlotExtent = new TimeSlotExtentImpl();
    try {
      ScriptInterpreter interpreter = new ScriptInterpreter(timeSlotExtent);
      interpreter.process("define timeslot 1st_try 12:12 to 12:40 on even Monday");
      assertTrue(timeSlotExtent.findByWeekDay(1).size() == 1);
    } catch (Exception e) {
      fail("command's not correct");
    }
  }

  public void testCommandCorrectnessRus() {
    TimeSlotExtent timeSlotExtent = new TimeSlotExtentImpl();
    try {
      ScriptInterpreter interpreter = new ScriptInterpreter(timeSlotExtent);
      interpreter.process("определить интервал 1st_try от 1:00 до 3:00 в нечетный вт");
      assertTrue(timeSlotExtent.findByWeekDay(2).size() == 1);
    } catch (Exception e) {
      fail("command's not correct");
    }
  }

  public void testIncorrectCommand() {
    boolean isExceptionCaught = false;
    TimeSlotExtent timeSlotExtent = new TimeSlotExtentImpl();
    try {
      ScriptInterpreter interpreter = new ScriptInterpreter(timeSlotExtent);
      interpreter.process("define timeslot 1st_try to 12:40 on even Monday");
    } catch (Exception e) {
      isExceptionCaught = true;
    } finally {
      assertTrue(isExceptionCaught);
    }
  }
}
