package ru.spbu.math.baobab.model.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Converts "yyyy-MM-dd" to date
 * 
 * @author vloginova
 */
public class DateConverter {
  private final static String DATE_PATTERN = "yyyy-MM-dd";

  /**
   * converts string to Date
   * 
   * @param value string to convert
   * @return converted Date
   */
  public static Date convertToDate(String value) {
    SimpleDateFormat format = new SimpleDateFormat(DATE_PATTERN);
    try{
      return format.parse(value);
    }
    catch(Exception e){
    throw new IllegalArgumentException("convertToDate: Incorrect value"); 
    }
  }
}
