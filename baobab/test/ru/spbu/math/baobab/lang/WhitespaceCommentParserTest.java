package ru.spbu.math.baobab.lang;

import java.util.Collections;

import junit.framework.TestCase;

/**
 * Tests parsing of whitespace and comment lines
 *  
 * @author dbarashev
 */
public class WhitespaceCommentParserTest extends TestCase {
  public void testWhitespaceAndComments() {
    ScriptInterpreter interpreter = new ScriptInterpreter(Collections.<Parser>emptyList());
    String[] commands = {"", "\r", "-- comment\r", "  -- comment", "     ", "\t   \t"};
    for (String command: commands) {
      assertTrue(command, interpreter.process(command));
    }
  }
}
