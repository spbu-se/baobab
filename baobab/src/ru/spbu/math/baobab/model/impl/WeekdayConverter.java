package ru.spbu.math.baobab.model.impl;

/**
 * converter for Weekday
 * 
 * @author vloginova
 */

public class WeekdayConverter {

	private static final String[] MONDAY = { "Monday", "Mo", "понедельник", "пн"};
	private static final String[] TUESDAY = { "Tuesday", "Tu", "вторник", "вт"};
	private static final String[] WEDNESDAY = { "Wednesday", "We", "среда", "ср"};
	private static final String[] THURSDAY = { "Thursday", "Th", "четверг", "чт"};
	private static final String[] FRIDAY = { "Friday", "Fr", "пятница", "пт"};
	private static final String[] SATURDAY = { "Saturday", "Sa", "суббота", "сб"};
	private static final String[] SUNDAY = { "Sunday", "Su", "воскресенье", "вс"};
	
	public static int convertToInt(String value) throws Exception{
		if (IsArrayMember(MONDAY, value)){
			return 1;
		}
		if (IsArrayMember(TUESDAY, value)){
			return 2;
		}
		if (IsArrayMember(WEDNESDAY, value)){
			return 3;
		}
		if (IsArrayMember(THURSDAY, value)){
			return 4;
		}
		if (IsArrayMember(FRIDAY, value)){
			return 5;
		}
		if (IsArrayMember(SATURDAY, value)){
			return 6;
		}
		if (IsArrayMember(SUNDAY, value)){
			return 7;
		}
		throw new Exception("convertToInt: Incorrect value");
	}
	
	private static Boolean IsArrayMember(String[] array, String value){
		for (int i = 0; i < array.length; i++){
			if (value.contentEquals(array[i])){
				return true;
			}
		}
		return false;
	}
}
