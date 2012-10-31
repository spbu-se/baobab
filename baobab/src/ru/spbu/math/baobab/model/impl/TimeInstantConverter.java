package ru.spbu.math.baobab.model.impl;

/**
 * converter for TimeInstant
 * 
 * @author vloginova
 */

import ru.spbu.math.baobab.model.TimeInstant;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeInstantConverter {
	
	private final static Pattern TIME_PATTERN = Pattern.compile("(\\d{1,2}):(\\d{2})");
	
	public static TimeInstant convertToTimeInstant(String value) throws Exception{
		Matcher commandMatch = TIME_PATTERN.matcher(value);
		if (commandMatch.lookingAt()) {
			int hour = Integer.parseInt(commandMatch.group(1));
			int minute = Integer.parseInt(commandMatch.group(2));
			return new TimeInstant(hour, minute);
		}
		throw new Exception("convertToTimeInstant: Incorrect value");
	}
}
