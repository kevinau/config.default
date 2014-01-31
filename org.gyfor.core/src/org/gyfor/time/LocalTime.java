/*******************************************************************************
 * Copyright (c) 2012 Kevin Holloway (kholloway@geckosoftware.co.uk).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kevin Holloway - initial API and implementation
 *******************************************************************************/
package org.gyfor.time;


import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Comparator;
import java.util.Date;

import org.gyfor.value.YearMonth;


/**
 * A local date and time, where the time is measured in various levels of resolution down to seconds.
 * <p>
 * This class can record date and time in the following resolution:
 * <ul>
 * <li>Year</li>
 * <li>Year and month</li>
 * <li>Year, month and day</li>
 * <li>Year, month, day and hour</li>
 * <li>Year, month, day, hour and minute</li>
 * <li>Year, month, day, hour, minute and second</li>
 * </ul>
 * The constructors determine what level of resolution is required.  The resolution affects comparison and adjustment of times.
 *
 * @author Kevin
 *
 */
public class LocalTime implements Comparable<LocalTime>, Comparator<LocalTime>, Serializable {

  private final static long serialVersionUID = 8142027935542931993L;

  public enum Resolution {
    YEAR,
    MONTH,
    DAY,
    HOUR,
    MINUTE,
    SECOND;
  }
  
  private static final int[] normalCumDays = {
    0,
    31,
    31 + 28,
    31 + 28 + 31,
    31 + 28 + 31 + 30,
    31 + 28 + 31 + 30 + 31,
    31 + 28 + 31 + 30 + 31 + 30,
    31 + 28 + 31 + 30 + 31 + 30 + 31,
    31 + 28 + 31 + 30 + 31 + 30 + 31 + 31,
    31 + 28 + 31 + 30 + 31 + 30 + 31 + 31 + 30,
    31 + 28 + 31 + 30 + 31 + 30 + 31 + 31 + 30 + 31,
    31 + 28 + 31 + 30 + 31 + 30 + 31 + 31 + 30 + 31 + 30,
    31 + 28 + 31 + 30 + 31 + 30 + 31 + 31 + 30 + 31 + 30 + 31,
  };
  
  private static final int[] leapCumDays = {
    0,
    31,
    31 + 29,
    31 + 29 + 31,
    31 + 29 + 31 + 30,
    31 + 29 + 31 + 30 + 31,
    31 + 29 + 31 + 30 + 31 + 30,
    31 + 29 + 31 + 30 + 31 + 30 + 31,
    31 + 29 + 31 + 30 + 31 + 30 + 31 + 31,
    31 + 29 + 31 + 30 + 31 + 30 + 31 + 31 + 30,
    31 + 29 + 31 + 30 + 31 + 30 + 31 + 31 + 30 + 31,
    31 + 29 + 31 + 30 + 31 + 30 + 31 + 31 + 30 + 31 + 30,
    31 + 29 + 31 + 30 + 31 + 30 + 31 + 31 + 30 + 31 + 30 + 31,
  };
  
  
 
  private final static int START_YEAR = 1900;
  private final static long JAVA_OFFSET = (70 * 1461 / 4) * 24L * 60L * 60L;
  private final static int YEAR_INDEX_START = (START_YEAR * 1461) / 4 + 1;
  private final static int WEEK_START = -6;
  
  public final static LocalTime GENESIS = new LocalTime(0, 1, 1);
  
  
  /* index is the number of seconds since 1/1/1970 */
  private final long index;
  private final Resolution resolution;
  
  
  public LocalTime (Date date, Resolution resolution) {
    long secs = date.getTime() / 1000L + JAVA_OFFSET;
    switch (resolution) {
    case SECOND :
      index = secs;
      break;
    case MINUTE :
      index = (secs / 60) * 60;
      break;
    case HOUR :
      index = (secs / (60 * 60)) * 60 * 60;
      break;
    case DAY :
      index = (secs / (24 * 60 * 60)) * 24 * 60 * 60;
      break;
    case MONTH :
      int days = (int)(secs / (24L * 60L * 60L));
      int yearMonth = getMonthIndex(days);
      index = yearMonthIndex(yearMonth / 12, yearMonth % 12) * 24L * 60L * 60L;
      break;
    case YEAR :
      int days2 = (int)(secs / (24L * 60L * 60L));
      int year = (days2 + 1) * 4 / 1461;
      int startOfYear = (year * 1461 - 1) / 4;
      index = startOfYear * 24L * 60L * 60L;
      break;
    default :
      index = 0;
      break;
    }
    this.resolution = resolution;
  }
  
  
  public LocalTime (Timestamp timestamp) {
    index = timestamp.getTime() / 1000L + JAVA_OFFSET;
    resolution = Resolution.SECOND;
  }
  
  
  /** Construct a LocalTime from day, month and year.  The month is in the range
   *  1..12.  This is different from the Java convention of 
   *  numbering months starting from 0. The day is in the range
   *  1..31.
   *  <p>
   *  The resolution of the LocalTime is DAY. 
   */
  public LocalTime (int year, int month, int day) {
    index = (yearMonthIndex(year, month - 1) + (day - 1)) * 24L * 60L * 60L;
    resolution = Resolution.DAY;
  }
  
  
  /** Construct a LocalTime from month and year.  The month is in the range
   *  1..12.  This is different from the Java convention of 
   *  numbering months starting from 0. 
   *  <p>
   *  The resolution of the LocalTime is MONTH. 
   */
  public LocalTime (int year, int month) {
    index = yearMonthIndex(year, month - 1) * 24L * 60L * 60L;
    resolution = Resolution.MONTH;
  }
  
  
  /** Construct a LocalTime from a year.
   *  <p>
   *  The resolution of the LocalTime is YEAR. 
   */
  public LocalTime (int year) {
    index = yearMonthIndex(year, 0) * 24L * 60L * 60L;
    resolution = Resolution.YEAR;
  }
  
  
  public LocalTime (LocalTime date) {
    this.index = date.index;
    this.resolution = date.resolution;
  }
  
  
  private LocalTime (long index, Resolution resolution) {
    this.index = index;
    this.resolution = resolution;
  }
  
  
  public static LocalTime today() {
    return new LocalTime(new Date(), Resolution.DAY);
  }
  
  
  public static LocalTime now() {
    return new LocalTime(new Date(), Resolution.SECOND);
  }
  
  
  @Override
  public Object clone () {
    return new LocalTime(index, resolution);
  }
  
  
  @Override
  public boolean equals (Object other) {
    if (this == other) {
      return true;
    }
    if (other == null || !(other instanceof LocalTime)) {
      return false;
    }
    LocalTime day1 = (LocalTime)other;
    return index == day1.index;
  }
  
  
  @Override
  public int hashCode () {
    return (int)(index & 0xFFFFFFFF);
  }
  
  
  protected long getIndex() {
    return index;
  }
  
  
	public static LocalTime parseIsoDate (String s) {
		/* s is assumed to be ISO date format: yyyy-MM-dd */
		int year = Integer.parseInt(s.substring(0, 4));
		int month = Integer.parseInt(s.substring(5, 7));
		int day = Integer.parseInt(s.substring(8));
		return new LocalTime(year, month, day);
	}
  
  
  public LocalTime startOfWeek () {
    int n = getWeekday();
    if (n == 0) {
      return this;
    } else { 
      return new LocalTime(index - n, Resolution.DAY);
    }
  }
  
  
  public LocalTime plus (int days) {
    return new LocalTime(index + days * 24L * 60L * 60L, resolution);
  }
  
  
  public LocalTime minus (int days) {
    return new LocalTime(index - days * 24L * 60L * 60L, resolution);
  }
  
  
  public long less (LocalTime other) {
    return index - other.index;
  }
  
  
  public boolean before (LocalTime other) {
    return index < other.index;
  }
  
  
  public boolean after (LocalTime other) {
    return index > other.index;
  }
  
  
  public int compare (LocalTime d1, LocalTime d2) {
    if (d1.index < d2.index) {
      return -1;
    } else if (d1.index > d2.index) {
      return +1;
    } else {
      return 0;
    }
  }
  
  
  @Override
  public String toString () {
    if (isGenesis()) {
      return "";
    } else {
      StringBuffer buffer = new StringBuffer(10);
      int day = getDay();
      if (day < 10) {
        buffer.append('0');
      }
      buffer.append(day);
      buffer.append('/');
      int month = getMonth();
      if (month < 10) {
        buffer.append('0');
      }
      buffer.append(month);
      buffer.append('/');
      buffer.append(getYear());
      return buffer.toString();
    }
  }
  
  
  public String toISOString () {
    StringBuffer buffer = new StringBuffer(10);
    buffer.append(getYear());
    if (resolution == Resolution.YEAR) {
      return buffer.toString();
    }
    buffer.append('-');
    int month = getMonth();
    if (month < 10) {
      buffer.append('0');
    }
    buffer.append(month);
    if (resolution == Resolution.MONTH) {
      return buffer.toString();
    }
    buffer.append('-');
    int day = getDay();
    if (day < 10) {
      buffer.append('0');
    }
    buffer.append(day);
    if (resolution == Resolution.DAY) {
      return buffer.toString();
    }
    buffer.append('T');
    int hour = getHour();
    if (hour < 10) {
      buffer.append('0');
    }
    buffer.append(hour);
    if (resolution == Resolution.HOUR) {
      return buffer.toString();
    }
    buffer.append(':');
    int min = getMinute();
    if (min < 10) {
      buffer.append('0');
    }
    buffer.append(min);
    if (resolution == Resolution.MINUTE) {
      return buffer.toString();
    }
    buffer.append(':');
    int sec = getSecond();
    if (sec < 10) {
      buffer.append('0');
    }
    buffer.append(sec);
    return buffer.toString();
  }
  
  
  public Date javaDate () {
    return new Date((index - JAVA_OFFSET) * 1000L);
  }
  
  
  public java.sql.Date sqlDate () {
    return new java.sql.Date((index - JAVA_OFFSET) * 1000L);
  }
  
  
  public long javaTime () {
    return (index - JAVA_OFFSET) * 1000L;
  }
  
  
  public int getYear () {
    return ((int)(index / (24L * 60L * 60L)) + 1) * 4 / 1461 + START_YEAR;
  }
  
  
  /** Set the index to be the start of the given 
   *  year.  For the Gregorian calendar, it will set
   *  the index to Jan 1 of the given year.
   * @param year
   */
  public LocalTime setYear (int year) {
    return new LocalTime(yearIndex(year));
  }
  
  
  private static int yearIndex(int year) {
    return (year * 1461 + 3) / 4 - YEAR_INDEX_START;
  }
  
  
  public int getMonthIndex () {
    int dayIndex = (int)(index / (24L * 60L * 60L));
    return getMonthIndex(dayIndex);
  }
  
  
  public int getMonthIndex (int dayIndex) {
    int year = (dayIndex + YEAR_INDEX_START) * 4 / 1461;
    int[] cumDays;
    if ((year % 4) == 0) {
      cumDays = leapCumDays;
    } else {
      cumDays = normalCumDays;
    }

    int startOfYear = (year * 1461 + 3) / 4 - YEAR_INDEX_START;
    int dayOfYear = dayIndex - startOfYear;
    int m = 0;
    while (dayOfYear >= cumDays[m]) {
      m++;
    }
    return year * 12 + m - 1;
  }
  

  /* Returns the month of the date.  The month is in the
   * range 1..12. */
  public int getMonth () {
    return getMonthIndex() % 12 + 1;
  }
  
  
  private static long monthIndex (YearMonth yearMonth) {
    return yearMonthIndex(yearMonth.getYear(), yearMonth.getMonth());
  }
  
  
  private static int yearMonthIndex(int year, int month) {
    int n = yearIndex(year);
    if ((year % 4) == 0) {
      n += leapCumDays[month];
    } else {
      n += normalCumDays[month];
    }
    return n;
  }
  
  
//  /* Set the day, month and year.  The month is in the range
//   * 1..12.  This is different from the Java convention of 
//   * numbering months starting from 0. The day is in the range
//   * 1..31. */
//  public void setYearMonthDay (int year, int month, int day) {
//    setYear (year);
//    month--;
//    if ((year % 4) == 0) {
//      index += leapCumDays[month];
//    } else {
//      index += normalCumDays[month];
//    }
//    index += day - 1;
//  }
  
   
  public int getWeek () {
    return ((int)(index / (24L * 60L * 60L)) + WEEK_START) / 7;
  }
  

  public int getWeekday () {
    return ((int)(index / (24L * 60L * 60L)) + WEEK_START) % 7;
  }
  
  
  private static String[] shortWeekdayNames = {
    "Sun",
    "Mon",
    "Tue",
    "Wed",
    "Thu",
    "Fri",
    "Sat",
  };
  
  
  public String[] getShortWeekdayNames () { 
    return shortWeekdayNames;
  }
  

  public Date setWeek (int week) {
    return new Date(week * 7 - WEEK_START);
  }
  
   
  public Date setWeekWeekday (int week, int weekday) {
    return new Date(week * 7 - WEEK_START + weekday);
  }
  
   
  public LocalTime setMonthWeekday (YearMonth yearMonth, int weekday) {
    long n = monthIndex(yearMonth);
    int weekday1 = getWeekday();
    if (weekday < weekday1) {
      weekday += 7;
    }
    return new LocalTime(n + (weekday - weekday1) * 24L * 60L * 60L, Resolution.DAY);
  }
  
  
  /* Returns the day number of the date.  The day number follows 
   * the normal date convention of starting with 1. */
  public int getDay () {
    int dayIndex = (int)(index / (24L * 60L * 60L));
    int year = (dayIndex + YEAR_INDEX_START) * 4 / 1461;
    int[] cumDays;
    if ((year % 4) == 0) {
      cumDays = leapCumDays;
    } else {
      cumDays = normalCumDays;
    }

    int startOfYear = (year * 1461 + 3) / 4 - YEAR_INDEX_START;
    int dayOfYear = dayIndex - startOfYear;
    int m = 0;
    while (dayOfYear >= cumDays[m]) {
      m++;
    }
    return dayOfYear - cumDays[m - 1] + 1;
  }
  
  
  public int getHour () {
    long hourIndex = index / (60 * 60);
    return (int)(hourIndex % 24);
  }
  
  
  public int getMinute () {
    long minIndex = index / 60;
    return (int)(minIndex % 60);
  }
  
  
  public int getSecond () {
    return (int)(index % 60);
  }
  
  
  public int daysInMonth () {
    int dayIndex = (int)(index / (24L * 60L * 60L));
    int year = (dayIndex + YEAR_INDEX_START) * 4 / 1461;
    int[] cumDays;
    if ((year % 4) == 0) {
      cumDays = leapCumDays;
    } else {
      cumDays = normalCumDays;
    }

    int startOfYear = (year * 1461 + 3) / 4 - YEAR_INDEX_START;
    int dayOfYear = dayIndex - startOfYear;
    int m = 0;
    while (dayOfYear >= cumDays[m]) {
      m++;
    }
    return cumDays[m] - cumDays[m - 1];
  }
  
  
  public LocalTime addMonthDay (int adj, int day) {
    int n = getMonthIndex() + adj;
    int year = n / 12;
    int month = n % 12;
    int x = LocalTime.yearMonthIndex(year, month);
    if (day > 0) {
      x += day - 1;
    } else {
      x += day;
    }
    return new LocalTime(x * 24L * 60L * 60L, resolution);
  }    
  
  
  public LocalTime addDay (int adj) {
    return new LocalTime(index + adj, resolution);
  }
  
  
  /* Set the day within a date.  The day is 
   * in the range 1..31 or -1..-31.
   */
  public LocalTime setDay (int day) {
    int monthIndex = getMonthIndex();
    int year = monthIndex / 12;
    int month = monthIndex % 12;
    int n = yearMonthIndex(year, month);
    if (day > 0) {
      n += day - 1;
    } else {
      n += day;
    }
    return new LocalTime(n);
  }
  
  
  public boolean isGenesis () {
    return this.equals(GENESIS);
  }


  @Override
  public int compareTo(LocalTime other) {
    if (index < other.index) {
      return -1;
    } else if (index > other.index) {
      return +1;
    } else {
      return 0;
    }
  }

}
