package ru.spbu.math.baobab.lang;

/*Ragozina Anastasiya, Loginova Vita*/

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScriptInterpretator {

	private final static String DEFINE_TIMESLOT = "";
	private final static String DEFINE_ATTENDEE = "";

	private final static String DEFINE_TIMESLOT_NAME = "DEFINE_TIMESLOT";
	private final static String DEFINE_ATTENDEE_NAME = "DEFINE_ATTENDEE";

	private static Pattern MAIN_PATTERN;

	public ScriptInterpretator() {
		MAIN_PATTERN = Pattern.compile(getMainRegex());
	}

	/* Form string which matches to all regular expressions */
	private String getMainRegex() {
		String mainRegex = "^" + "$";
		return mainRegex;
	}

	/*
	 * Receive command and process it
	 * 
	 * @param: String
	 */
	public void process(String command) throws Exception {
		Matcher commandMatch = MAIN_PATTERN.matcher(command);
		if (commandMatch.lookingAt()) {
			execute(commandMatch);
		} else {
			throw new Exception(
					"Exception: this command doesn't correspond to a pattern");
		}
	}

	/*
	 * Defines type of command and execute it
	 * 
	 * @param: Matcher
	 * 
	 * @return: void
	 */
	private void execute(Matcher match) {
		if (match.group(DEFINE_TIMESLOT_NAME) != null) {
			defineTimeSlot(match);
		} else if (match.group(DEFINE_ATTENDEE_NAME) != null) {
			defineAttendee(match);
		}
	}
	
	/*
	 * defines time slot
	 * 
	 * @param: Matcher
	 * 
	 * @return: void
	 */
	private void defineTimeSlot(Matcher match) {
	}

	/*
	 * define attendee
	 * 
	 * @param: Matcher
	 * 
	 * @return: void
	 */
	private void defineAttendee(Matcher match) {

	}
}
