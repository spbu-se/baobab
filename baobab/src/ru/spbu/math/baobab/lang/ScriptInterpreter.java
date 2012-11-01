package ru.spbu.math.baobab.lang;

/*Ragozina Anastasiya, Loginova Vita*/

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import ru.spbu.math.baobab.model.impl.*;
import ru.spbu.math.baobab.model.*;

public class ScriptInterpreter {

	private final static String RUSSIAN_VERSION = "Rus";

	// command names
	private final static String DEFINE_TIMESLOT_NAME = "DefineTimeslot";
	private final static String DEFINE_ATTENDEE_NAME = "DefineAttendee";

	// group names
	private final static String ID_NAME = "Id";
	private final static String TIME_NAME = "Time";
	private final static String EVEN_ODD_NAME = "EvenOdd";
	private final static String WEEKDAY_NAME = "Weekday";

	// group patterns
	private final static String ID_PATTERN = "\\w+";
	private final static String TIME_PATTERN = "\\d{1,2}:\\d{2}";
	private final static String EVEN_ODD_PATTERN_ENG = "odd|even";
	private final static String EVEN_ODD_PATTERN_RUS = "четный|нечетный";
	private final static String WEEKDAY_PATTERN_ENG = "Monday|Tuesday|Wednesday|Thursday|Friday|Saturday|Sunday|Mo|Tu|We|Th|Fr|Sa|Su";
	private final static String WEEKDAY_PATTERN_RUS = "понедельник|вторник|среда|четверг|пятница|суббота|воскресенье|пн|вт|ср|чт|пт|сб|вс";

	// command patterns: for command in Russian adds a suffix RUSSIAN_VERSION,
	// for repeated parameters adds its order number in a raw;
	// for each command adds a prefix COMMAND_NAME
	private final static String DEFINE_TIMESLOT = String
			.format("((?<%s>define\\stimeslot)\\s(?<%s>%s)\\s(?<%s>%s)\\sto\\s(?<%s>%s)\\son\\s(?<%s>%s)\\s(?<%s>%s))",
					DEFINE_TIMESLOT_NAME, DEFINE_TIMESLOT_NAME + ID_NAME,
					ID_PATTERN, DEFINE_TIMESLOT_NAME + TIME_NAME, TIME_PATTERN,
					DEFINE_TIMESLOT_NAME + TIME_NAME + "2", TIME_PATTERN,
					DEFINE_TIMESLOT_NAME + EVEN_ODD_NAME, EVEN_ODD_PATTERN_ENG,
					DEFINE_TIMESLOT_NAME + WEEKDAY_NAME, WEEKDAY_PATTERN_ENG)
			+ String.format(
					"|((?<%s>определить\\sинтервал)\\s(?<%s>%s)\\sот\\s(?<%s>%s)\\sдо\\s(?<%s>%s)\\sв\\s(?<%s>%s)\\s(?<%s>%s))",
					DEFINE_TIMESLOT_NAME + RUSSIAN_VERSION,
					DEFINE_TIMESLOT_NAME + ID_NAME + RUSSIAN_VERSION,
					ID_PATTERN, DEFINE_TIMESLOT_NAME + TIME_NAME
							+ RUSSIAN_VERSION, TIME_PATTERN,
					DEFINE_TIMESLOT_NAME + TIME_NAME + RUSSIAN_VERSION + "2",
					TIME_PATTERN, DEFINE_TIMESLOT_NAME + EVEN_ODD_NAME
							+ RUSSIAN_VERSION, EVEN_ODD_PATTERN_RUS,
					DEFINE_TIMESLOT_NAME + WEEKDAY_NAME + RUSSIAN_VERSION,
					WEEKDAY_PATTERN_RUS);
	private final static String DEFINE_ATTENDEE = "";

	private static Pattern MAIN_PATTERN;

	private TimeSlotExtent timeSlotExtent;

	public ScriptInterpreter(TimeSlotExtent timeSlotExtent) throws Exception {
		MAIN_PATTERN = Pattern.compile(getMainRegex());
		if (timeSlotExtent != null) {
			this.timeSlotExtent = timeSlotExtent;
		} else {
			throw new Exception();
		}
	}

	/*
	 * Form string which matches to all regular expressions
	 * 
	 * @return main pattern string for all regexps
	 */
	private String getMainRegex() {
		String mainRegex = "^" + DEFINE_TIMESLOT + "$";
		return mainRegex;
	}

	/*
	 * Receive command and process it
	 * 
	 * @param command some command in baobab language
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
	 * @param match matcher with matched command string
	 * 
	 * @return: void
	 */
	private void execute(Matcher match) throws Exception {
		if ((match.group(DEFINE_TIMESLOT_NAME) != null)
				|| (match.group(DEFINE_TIMESLOT_NAME + RUSSIAN_VERSION) != null)) {
			defineTimeSlot(match);
		} else if (match.group(DEFINE_ATTENDEE_NAME) != null) {
			defineAttendee(match);
		} else {
			throw new Exception(
			// something wrong here
					"Exception: this command doesn't correspond to a pattern");
		}
	}

	/*
	 * defines time slot using TimeSlotExtent
	 * 
	 * @param match matcher with matched command string
	 * 
	 * @return: void
	 */
	private void defineTimeSlot(Matcher match) throws Exception {
		String id = match.group(DEFINE_TIMESLOT_NAME + ID_NAME) != null ? match
				.group(DEFINE_TIMESLOT_NAME + ID_NAME) : match
				.group(DEFINE_TIMESLOT_NAME + ID_NAME + RUSSIAN_VERSION);
		TimeInstant start = TimeInstantConverter.convertToTimeInstant(match
				.group(DEFINE_TIMESLOT_NAME + TIME_NAME) != null ? match
				.group(DEFINE_TIMESLOT_NAME + TIME_NAME) : match
				.group(DEFINE_TIMESLOT_NAME + TIME_NAME + RUSSIAN_VERSION));
		TimeInstant finish = TimeInstantConverter.convertToTimeInstant(match
				.group(DEFINE_TIMESLOT_NAME + TIME_NAME + "2") != null ? match
				.group(DEFINE_TIMESLOT_NAME + TIME_NAME + "2")
				: match.group(DEFINE_TIMESLOT_NAME + TIME_NAME
						+ RUSSIAN_VERSION + "2"));
		EvenOddWeek flashing = OddEvenWeekConverter.convertToOddEvenWeek(match
				.group(DEFINE_TIMESLOT_NAME + EVEN_ODD_NAME) != null ? match
				.group(DEFINE_TIMESLOT_NAME + EVEN_ODD_NAME) : match
				.group(DEFINE_TIMESLOT_NAME + EVEN_ODD_NAME + RUSSIAN_VERSION));
		Integer weekday = WeekdayConverter.convertToInt(match
				.group(DEFINE_TIMESLOT_NAME + WEEKDAY_NAME) != null ? match
				.group(DEFINE_TIMESLOT_NAME + WEEKDAY_NAME) : match
				.group(DEFINE_TIMESLOT_NAME + WEEKDAY_NAME + RUSSIAN_VERSION));
		timeSlotExtent.create(id, start, finish, weekday, flashing);
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
