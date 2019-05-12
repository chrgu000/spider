package com.dr.spider.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class JodaTimeUtils {


  /**
   * 时间格式 April 23, 2019 Locale.ENGLISH
   */
  public static final String DATE_FORMAT_MMM_D_YYYY = "MMM d, yyyy";

  public static final String DATE_FORMAT_YYYYMMDD_01 = "yyyy/MM/dd";

  public static String formatToString(String dateFormat) {
    DateTime dateTime = new DateTime();
    return dateTime.toString(dateFormat);
  }

  public static String formatToString(String dateFormat, Locale locale) {
    DateTime dateTime = new DateTime();
    return dateTime.toString(dateFormat, locale);
  }

  public static Date formatToDate(String dateString, String dateFormat) {
    DateTimeFormatter formatter = DateTimeFormat.forPattern(dateFormat);
    DateTime dt = formatter.parseDateTime(dateString);
    return dt.toDate();
  }

  public static Date formatToDate(String dateString, String dateFormat, Locale locale) {
    DateTimeFormatter formatter = DateTimeFormat.forPattern(dateFormat).withLocale(locale);
    DateTime dt = formatter.parseDateTime(dateString);
    return dt.toDate();
  }

  public static void main(String[] args) {
    String s = formatToString(DATE_FORMAT_MMM_D_YYYY, Locale.ENGLISH);

    Date date = formatToDate(s, DATE_FORMAT_MMM_D_YYYY, Locale.ENGLISH);

    System.out.println(date);


  }


}
