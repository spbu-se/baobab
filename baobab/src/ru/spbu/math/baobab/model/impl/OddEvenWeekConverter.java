package ru.spbu.math.baobab.model.impl;

/**
 * converter for OddEvenWeek
 * 
 * @author vloginova
 */

import ru.spbu.math.baobab.model.EvenOddWeek;
import java.util.Hashtable;

public class OddEvenWeekConverter {
	private static final String[] ODD = { "odd", "нечетный"};
	private static final String[] EVEN = { "even", "четный"};
	
	public static EvenOddWeek convertToOddEvenWeek(String value) throws Exception{
		if (IsArrayMember(ODD, value)){
			return EvenOddWeek.ODD;
		}
		if (IsArrayMember(EVEN, value)){
			return EvenOddWeek.EVEN;
		}
		throw new Exception("convertToOddEvenWeek: Incorrect value");
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
