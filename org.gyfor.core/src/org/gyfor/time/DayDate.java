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
import java.util.Comparator;
import java.util.Date;

import org.gyfor.value.YearMonth;


public class DayDate implements Comparable<DayDate>, Comparator<DayDate>, Serializable {

  private final static long serialVersionUID = 8142027935542931993L;

  private final static int START_YEAR = 1900;
  private final static int START_OFFSET = 70 * 1461 / 4;
  private final static int INDEX_START = (START_YEAR * 1461) / 4;
  private final static int WEEK_START = -6;
  
  public final static DayDate GENESIS = new DayDate(0);
  
  
  private final int index;
  
  
  public DayDate () {
    this (0);
  }
  
  
  public DayDate (Date date) {
    index = (int)(date.getTime() / (24L * 60L * 60L * 1000L)) + START_OFFSET;
  }
  
  
  public DayDate (long time) {
    index = (int)(time / (24L * 60L * 60L * 1000L)) + START_OFFSET;
  }
  

  /* Construct a date from day, month and year.  The month is in the range
   * 1..12.  This is different from the Java convention of 
   * numbering months starting from 0. The day is in the range
   * 1..31. */
  public DayDate (int year, int month, int day) {
    index = yearMonthIndex(year, month - 1) + (day - 1);
  }
  
  
  public DayDate (DayDate date) {
    this.index = date.index;
  }
  
  
  private DayDate (int index) {
    this.index = index;
  }
  
  
  public static DayDate today() {
    return new DayDate(new Date());
  }
  
  
  @Override
  public Object clone () {
    return new DayDate(index);
  }
  
  
  @Override
  public boolean equals (Object other) {
    if (this == other) {
      return true;
    }
    if (other == null || !(other instanceof DayDate)) {
      return false;
    }
    DayDate day1 = (DayDate)other;
    return index == day1.index;
  }
  
  
  @Override
  public int hashCode () {
    return index;
  }
  
  
  int getIndex() {
    return index;
  }
  
  
	public static DayDate parseIsoDate (String s) {
		/* s is assumed to be ISO date format: yyyy-MM-dd */
		int year = Integer.parseInt(s.substring(0, 4));
		int month = Integer.parseInt(s.substring(5, 7));
		int day = Integer.parseInt(s.substring(8));
		return new DayDate(year, month, day);
	}
  
  
  public DayDate startOfWeek () {
    int n = getWeekday();
    if (n == 0) {
      return this;
    } else { 
      return new DayDate(index - n);
    }
  }
  
  
  public DayDate plus (int days) {
    return new DayDate(index + days);
  }
  
  
  public DayDate minus (int days) {
    return new DayDate(index - days);
  }
  
  
  public int less (DayDate other) {
    return index - other.index;
  }
  
  
  public boolean before (DayDate other) {
    return index < other.index;
  }
  
  
  public boolean after (DayDate other) {
    return index > other.index;
  }
  
  
  public int compare (DayDate d1, DayDate d2) {
    return d1.index - d2.index;
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
  
  
  public Date javaDate () {
    return new Date((index - START_OFFSET) * (24L * 60L * 60L * 1000L));
  }
  
  
  public java.sql.Date sqlDate () {
    return new java.sql.Date((index - START_OFFSET) * (24L * 60L * 60L * 1000L));
  }
  
  
  public long javaTime () {
    return (index - START_OFFSET) * (24L * 60L * 60L * 1000L);
  }
  
  
  public int getYear () {
    return (index + INDEX_START + 1) * 4 / 1461;
  }
  
  
  /** Set the index to be the start of the given 
   *  year.  For the Gregorian calendar, it will set
   *  the index to Jan 1 of the given year.
   * @param year
   */
  public DayDate setYear (int year) {
    return new DayDate(yearIndex(year));
  }
  
  
  private static int yearIndex(int year) {
    return (year * 1461 + 3) / 4 - INDEX_START - 1;
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
  
  public int getMonthIndex () {
    int year = (index + INDEX_START + 1) * 4 / 1461;
    int[] cumDays;
    if ((year % 4) == 0) {
      cumDays = leapCumDays;
    } else {
      cumDays = normalCumDays;
    }

    int startOfYear = (year * 1461 + 3) / 4 - INDEX_START - 1;
    int dayOfYear = index - startOfYear;
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
  
  
  private static int monthIndex (YearMonth yearMonth) {
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
    return (index + WEEK_START) / 7;
  }
  

  public int getWeekday () {
    return (index + WEEK_START) % 7;
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
  
   
  public DayDate setMonthWeekday (YearMonth yearMonth, int weekday) {
    int n = monthIndex(yearMonth);
    int weekday1 = getWeekday();
    if (weekday < weekday1) {
      weekday += 7;
    }
    return new DayDate(n + (weekday - weekday1));
  }
  
  
  /* Returns the day number of the date.  The day number follows 
   * the normal date convention of starting with 1. */
  public int getDay () {
    int year = (index + INDEX_START + 1) * 4 / 1461;
    int[] cumDays;
    if ((year % 4) == 0) {
      cumDays = leapCumDays;
    } else {
      cumDays = normalCumDays;
    }

    int startOfYear = (year * 1461 + 3) / 4 - INDEX_START - 1;
    int dayOfYear = index - startOfYear;
    int m = 0;
    while (dayOfYear >= cumDays[m]) {
      m++;
    }
    return dayOfYear - cumDays[m - 1] + 1;
  }
  
  
  public int daysInMonth () {
    int year = (index + INDEX_START + 1) * 4 / 1461;
    int[] cumDays;
    if ((year % 4) == 0) {
      cumDays = leapCumDays;
    } else {
      cumDays = normalCumDays;
    }

    int startOfYear = (year * 1461 + 3) / 4 - INDEX_START - 1;
    int dayOfYear = index - startOfYear;
    int m = 0;
    while (dayOfYear >= cumDays[m]) {
      m++;
    }
    return cumDays[m] - cumDays[m - 1];
  }
  
  
  public DayDate addMonthDay (int adj, int day) {
    int n = getMonthIndex() + adj;
    int year = n / 12;
    int month = n % 12;
    int x = DayDate.yearMonthIndex(year, month);
    if (day > 0) {
      x += day - 1;
    } else {
      x += day;
    }
    return new DayDate(x);
  }    
  
  
  public DayDate addDay (int adj) {
    return new DayDate(index + adj);
  }
  
  
  /* Set the day within a date.  The day is 
   * in the range 1..31 or -1..-31.
   */
  public DayDate setDay (int day) {
    int monthIndex = getMonthIndex();
    int year = monthIndex / 12;
    int month = monthIndex % 12;
    int n = yearMonthIndex(year, month);
    if (day > 0) {
      n += day - 1;
    } else {
      n += day;
    }
    return new DayDate(n);
  }
  
  
  public boolean isGenesis () {
    return index == 0L;
  }


  @Override
  public int compareTo(DayDate other) {
    return other.index - index;
  }

}
