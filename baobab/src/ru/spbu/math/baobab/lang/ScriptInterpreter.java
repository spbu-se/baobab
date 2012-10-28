package ru.spbu.math.baobab.lang;

/*Ragozina Anastasiya, Loginova Vita*/

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScriptInterpreter {

	private final static String RUSSIAN_VERSION = "Rus";

	//command names
	private final static String DEFINE_TIMESLOT_NAME = "DefineTimeslot";
	private final static String DEFINE_ATTENDEE_NAME = "DefineAttendee";

	//group names
	private final static String ID_NAME = "Id";
	private final static String TIME_NAME = "Time";
	private final static String EVEN_ODD_NAME = "EvenOdd";
	private final static String WEEKDAY_NAME = "Weekday";

	//group patterns
	private final static String ID_PATTERN = "\\w+";
	private final static String TIME_PATTERN = "\\d{1,2}:\\d{2}";
	private final static String EVEN_ODD_PATTERN_ENG = "odd|even";
	private final static String EVEN_ODD_PATTERN_RUS = "четный|нечетный";
	private final static String WEEKDAY_PATTERN_ENG = "Monday|Tuesday|Wednesday|Thursday|Friday|Saturday|Sunday|Mo|Tu|We|Th|Fr|Sa|Su";
	private final static String WEEKDAY_PATTERN_RUS = "понедельник|вторник|среда|четверг|пятница|суббота|воскресенье|пн|вт|ср|чт|пт|сб|вс";

	//command patterns: for command in Russian adds a suffix RUSSIAN_VERSION, for repeated parameters adds its order number in a raw
	private final static String DEFINE_TIMESLOT = String.format("((?<%s>define\\stimeslot)\\s(?<%s>%s)\\s(?<%s>%s)\\sto\\s(?<%s>%s)\\son\\s(?<%s>%s)\\s(?<%s>%s))",
			DEFINE_TIMESLOT_NAME, ID_NAME, ID_PATTERN, TIME_NAME, TIME_PATTERN, TIME_NAME + "2", TIME_PATTERN, EVEN_ODD_NAME, EVEN_ODD_PATTERN_ENG, WEEKDAY_NAME, WEEKDAY_PATTERN_ENG)
			+ String.format("|((?<%s>определить\\sинтервал)\\s(?<%s>%s)\\sот\\s(?<%s>%s)\\sдо\\s(?<%s>%s)\\sв\\s(?<%s>%s)\\s(?<%s>%s))",
					DEFINE_TIMESLOT_NAME + RUSSIAN_VERSION, ID_NAME + RUSSIAN_VERSION, ID_PATTERN, TIME_NAME + RUSSIAN_VERSION, 
					TIME_PATTERN, TIME_NAME + RUSSIAN_VERSION + "2", TIME_PATTERN, EVEN_ODD_NAME + RUSSIAN_VERSION, 
					EVEN_ODD_PATTERN_RUS, WEEKDAY_NAME + RUSSIAN_VERSION, WEEKDAY_PATTERN_RUS);
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
	private void execute(Matcher match) throws Exception {
		if ((match.group(DEFINE_TIMESLOT_NAME) != null) ||
				(match.group(DEFINE_TIMESLOT_NAME + RUSSIAN_VERSION) != null)){
			defineTimeSlot(match);
		} else if (match.group(DEFINE_ATTENDEE_NAME) != null) {
			defineAttendee(match);
		}else {
			throw new Exception(
					//something wrong here
					"Exception: this command doesn't correspond to a pattern");
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
