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
      ScriptInterpreter interpteter = new ScriptInterpreter(timeSlotExtent);
      interpteter.process("define timeslot 1st_try 12:12 to 12:40 on even Monday");
    } catch (Exception e) {
      fail("command's not correct");
    }
  }

  public void testCommandCorrectnessRus() {
    TimeSlotExtent timeSlotExtent = new TimeSlotExtentImpl();
    try {
      ScriptInterpreter interpteter = new ScriptInterpreter(timeSlotExtent);
      interpteter.process("определить интервал 1st_try от 1:00 до 3:00 в нечетный вт");
    } catch (Exception e) {
      fail("command's not correct");
    }
  }
  
  public void testIncorrectCommand() {
    boolean isExceptionCatched = false;
    TimeSlotExtent timeSlotExtent = new TimeSlotExtentImpl();
    try {
      ScriptInterpreter interpteter = new ScriptInterpreter(timeSlotExtent);
      interpteter.process("define timeslot 1st_try to 12:40 on even Monday");
    } catch (Exception e) {
      isExceptionCatched = true;
    }
    finally{
      assertTrue(isExceptionCatched);
    }
  }
}
