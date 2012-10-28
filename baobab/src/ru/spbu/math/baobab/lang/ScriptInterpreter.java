package ru.spbu.math.baobab.lang;

/*Ragozina Anastasiya, Loginova Vita*/

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScriptInterpreter {

	private final static String DEFINE_TIMESLOT_NAME = "DefineTimeslot";
	private final static String DEFINE_ATTENDEE_NAME = "DefineAttendee";
	
	private final static String ID_NAME = "ID";
	private final static String TIME_NAME = "TIME";
	private final static String EVEN_ODD_NAME = "EVEN";
	private final static String WEEKDAY_NAME = "WEEKDAY";
	
	private final static String ID_PATTERN = "\\w+";
	private final static String TIME_PATTERN = "\\d{1,2}:\\d{2}";
	private final static String EVEN_ODD_PATTERN_ENG = "odd|even";
	private final static String EVEN_ODD_PATTERN_RUS = "четный|нечетный";
	private final static String WEEKDAY_PATTERN_ENG = "Monday|Tuesday|Wednesday|Thursday|Friday|Saturday|Sunday|Mo|Tu|We|Th|Fr|Sa|Su";
	private final static String WEEKDAY_PATTERN_RUS = "понедельник|вторник|среда|четверг|пятница|суббота|воскресенье|пн|вт|ср|чт|пт|сб|вс";
	
	private final static String DEFINE_TIMESLOT = String.format("(?<%s>define\\stimeslot)\\s(?<%s>%s)\\s(?<%s>%s)\\sto\\s\\k<%s>\\son\\s(?<%s>%s)\\s(?<%s>%s)",
			DEFINE_TIMESLOT_NAME, ID_NAME, ID_PATTERN, TIME_NAME, TIME_PATTERN, TIME_NAME, EVEN_ODD_NAME, EVEN_ODD_PATTERN_ENG, WEEKDAY_NAME, WEEKDAY_PATTERN_ENG);
	private final static String DEFINE_ATTENDEE = "";

	private static Pattern MAIN_PATTERN;

	public ScriptInterpreter() {
		MAIN_PATTERN = Pattern.compile(getMainRegex());
	}

	/* Form string which matches to all regular expressions */
	private String getMainRegex() {
		String mainRegex = "^" + DEFINE_TIMESLOT + "$";
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
