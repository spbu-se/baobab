package ru.spbu.math.baobab.model.impl;

/**
 * converter for OddEvenWeek
 * 
 * @author vloginova
 */

import ru.spbu.math.baobab.model.EvenOddWeek;

public class OddEvenWeekConverter {
	private static final String[] ODD = { "odd", "нечетный" };
	private static final String[] EVEN = { "even", "четный" };

	/*
	 * converts string to EvenOddWeek
	 * 
	 * @param value string to convert
	 * @return converted EvenOddWeek
	 */
	public static EvenOddWeek convertToOddEvenWeek(String value)
			throws Exception {
		if (IsArrayMember(ODD, value)) {
			return EvenOddWeek.ODD;
		}
		if (IsArrayMember(EVEN, value)) {
			return EvenOddWeek.EVEN;
		}
		throw new Exception("convertToOddEvenWeek: Incorrect value");
	}

	/*
	 * checks is string array contains value string
	 * 
	 * @param value string for checking
	 * @param array array, that may contains value
	 * @return true if array contains value, false if not
	 */
	private static Boolean IsArrayMember(String[] array, String value) {
		for (int i = 0; i < array.length; i++) {
			if (value.contentEquals(array[i])) {
				return true;
			}
		}
		return false;
	}
}
