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
public abstract class Interval implements Comparable<Interval>, Comparator<Interval>, IIntervalRange, Serializable {

  private final static long serialVersionUID = 8142027935542931993L;

  public enum Resolution {
    YEAR,
    MONTH,
    DAY,
    HOUR,
    MINUTE,
    SECOND;
    
    private boolean contains (Resolution other) {
      return this.ordinal() <= other.ordinal();
    }
    
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
  protected final static long JAVA_OFFSET = (70 * 1461 / 4) * 24L * 60L * 60L;
  protected final static int YEAR_INDEX_START = (START_YEAR * 1461) / 4 + 1;
  private final static int WEEK_START = -6;
  
  public final static LocalTime GENESIS = new LocalTime(0, 1, 1);
  
  
  /* index is the number of seconds since 1/1/1970 */
  private final long index;
  private final Resolution resolution;
  
  
  public Interval (Date date, Resolution resolution) {
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
      int yearMonth = yearMonthNumber(days);
      index = yearMonthAsJulien(yearMonth / 12, yearMonth % 12) * 24L * 60L * 60L;
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


  public Interval (int[] part) {
    long i;

    switch (part.length) {
    case 1 :
      if (part[0] < 0 || part[0] > 9999) {
        throw new IllegalArgumentException("Year not in the range 0 to 9999");
      }
      i = yearMonthAsJulien(part[0], 0) * 24L * 60L * 60L;
      resolution = Resolution.YEAR;
      break;
    case 2 :
      if (part[0] < 0 || part[0] > 9999) {
        throw new IllegalArgumentException("Year not in the range 0 to 9999");
      }
      if (part[1] < 1 || part[1] > 12) {
        throw new IllegalArgumentException("Month not in the range 1 to 12");
      }
      i = yearMonthAsJulien(part[0], part[1] - 1) * 24L * 60L * 60L;
      resolution = Resolution.MONTH;
      break;
    default :
      i = (yearMonthAsJulien(part[0], part[1] - 1) + part[2] - 1) * 24L * 60L * 60L;
      if (part.length == 3) {
        resolution = Resolution.DAY;
      } else {
        i += part[3] * 60L * 60L;
        if (part.length == 4) {
          resolution = Resolution.HOUR;
        } else {
          i += part[4] * 60L;
          if (part.length == 5) {
            resolution = Resolution.MINUTE;
          } else {
            i += part[5];
            if (part.length == 6) {
              resolution = Resolution.SECOND;
            } else {
              throw new IllegalArgumentException("Array size [" + part.length + "]");
            }
          }
        }
      }
    }
    index = i;
  }
  
  
  public Interval (Timestamp timestamp) {
    index = timestamp.getTime() / 1000L + JAVA_OFFSET;
    resolution = Resolution.SECOND;
  }
  
  
  /** Construct a LocalTime from a year.
   *  <p>
   *  The resolution of the LocalTime is YEAR. 
   */
  protected Interval (int year) {
    index = yearMonthAsJulien(year, 0) * 24L * 60L * 60L;
    resolution = Resolution.YEAR;
  }
  
  
  /** Construct a LocalTime from month and year.  The month is in the range
   *  1..12.  This is different from the Java convention of 
   *  numbering months starting from 0. 
   *  <p>
   *  The resolution of the LocalTime is MONTH. 
   */
  protected Interval (int year, int month) {
    index = yearMonthAsJulien(year, month - 1) * 24L * 60L * 60L;
    resolution = Resolution.MONTH;
  }
  
  
  /** Construct a LocalTime from day, month and year.  The month is in the range
   *  1..12.  This is different from the Java convention of 
   *  numbering months starting from 0. The day is in the range
   *  1..31.
   *  <p>
   *  The resolution of the LocalTime is DAY. 
   */
  protected Interval (int year, int month, int day) {
    index = (yearMonthAsJulien(year, month - 1) + (day - 1)) * 24L * 60L * 60L;
    resolution = Resolution.DAY;
  }
  
  
  protected Interval (Interval date) {
    this.index = date.index;
    this.resolution = date.resolution;
  }
  
  
  protected Interval (long index, Resolution resolution) {
    this.index = index;
    this.resolution = resolution;
  }
  
  
  @Override
  public boolean equals (Object other) {
    if (this == other) {
      return true;
    }
    if (other == null || !(other instanceof Interval)) {
      return false;
    }
    Interval i1 = (Interval)other;
    return index == i1.index && resolution == i1.resolution;
  }
  
  
  @Override
  public int hashCode () {
    return (int)(index & 0xFFFFFFFF);
  }
  
  
  protected long getIndex() {
    return index;
  }
  
  
  protected abstract Interval create (long index);
  
  
  public Interval startOfWeek () {
    if (resolution == Resolution.YEAR || resolution == Resolution.MONTH) {
      throw new UnsupportedOperationException();
    }
    int n = getWeekday();
    if (n == 0) {
      return this;
    } else { 
      return create(index - n);
    }
  }
  
  
  @SuppressWarnings("unchecked")
  public <T extends DayInterval> T plusDays (int days) {
    if (resolution == Resolution.YEAR || resolution == Resolution.MONTH) {
      throw new UnsupportedOperationException();
    }
    return (T)create(index + days * 24L * 60L * 60L);
  }
  
  
  @SuppressWarnings("unchecked")
  public <T extends DayInterval> T minusDays (int days) {
    if (resolution == Resolution.YEAR || resolution == Resolution.MONTH) {
      throw new UnsupportedOperationException();
    }
    return (T)create(index - days * 24L * 60L * 60L);
  }
  
  
  public long less (Interval other) {
    return index - other.index;
  }
  
  
  public boolean before (Interval other) {
    return index < other.index;
  }
  
  
  protected abstract long upperLimit ();
  
  
  public boolean after (Interval other) {
    return upperLimit() > other.upperLimit();
  }
  
  
  public int compare (Interval d1, Interval d2) {
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
    return ((int)(index / (24 * 60 * 60)) + 1) * 4 / 1461 + START_YEAR;
  }
  
  
  /** Set the index to be the start of the given 
   *  year.  For the Gregorian calendar, it will set
   *  the index to Jan 1 of the given year.
   * @param year
   */
  public LocalTime setYear (int year) {
    return new LocalTime(yearAsJulien(year));
  }
  
  
  protected static int yearAsJulien(int year) {
    return (year * 1461 + 3) / 4 - YEAR_INDEX_START;
  }
  
  
  public int getYearMonthNumber () {
    int dayIndex = (int)(index / (24L * 60L * 60L));
    return yearMonthNumber(dayIndex);
  }
  
  
  public int yearMonthNumber (int dayIndex) {
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
    return getYearMonthNumber() % 12 + 1;
  }
  
  
  private static long monthAsJulien (YearMonth yearMonth) {
    return yearMonthAsJulien(yearMonth.getYear(), yearMonth.getMonth());
  }
  
  
  protected static int yearMonthAsJulien(int year, int month) {
    int n = yearAsJulien(year);
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
  
   
  public Interval setMonthWeekday (YearMonth yearMonth, int weekday) {
    long n = monthAsJulien(yearMonth);
    int weekday1 = getWeekday();
    if (weekday < weekday1) {
      weekday += 7;
    }
    return create((n + (weekday - weekday1)) * 24 * 60 * 60);
  }
  

  /**
   * Returns a new Interval with the month and day set to the values supplied.  This method will leave the year
   * unchanged, along with any time component.
   * <p>
   * If this method is called on a year or month interval, it will return a day interval, with the month and
   * day set to the values supplied.
   * <p>
   * If this method is called on an day, hour, minute or second, it will return the same type.  The month and day
   * will be set to the values supplied and any time component will be preserved.
   * <p>
   * An exception will be thrown if:
   * <ul>
   * <li>The month is not in the range 1 to 12.</li>
   * <li>The day is not in the range 1 to n, where n is the maximum number of days in the specified month.</li>
   * <li>The month is 2 and the day is 29, and the year of the date is not a leap year.</li>
   * </ul>
   * @param month
   * @param day
   * @return
   */
  public Interval setMonthDay (int month, int day) {
    if (month < 1 || month > 12) {
      throw new IllegalArgumentException("Month not in range 1..12");
    }
    if (day < 1 || day > normalCumDays[month] - normalCumDays[month - 1]) {
      throw new IllegalArgumentException("Day not in range 1..n, where n is the number of days in the month");
    }
    int year = getYear();
    if (month == 2 && day == 29 && year % 4 != 0) {
      throw new IllegalArgumentException("29th Feb when year is not a leap year");
    }
    long time = index % (24 * 60 * 60);
    long n = yearMonthAsJulien(year, month - 1) + day - 1;
    n = n * (24 * 60 * 60) + time;
    if (resolution == Resolution.YEAR || resolution == Resolution.MONTH) {
      return new DayInterval(n);
    } else {
      return create(n);
    }
  }
  
  
  /* Returns the day number of the date.  The day number follows 
   * the normal date convention of starting with 1. */
  public int getDay () {
    int dayIndex = (int)(index / (24 * 60 * 60));
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
  
  
  public YearInterval toYearInterval() {
    switch (resolution) {
    case YEAR :
      return (YearInterval)this;
    default :
      int y = getYear();
      return new YearInterval(y);
    }
  }


  public MonthInterval toMonthInterval() {
    switch (resolution) {
    case YEAR :
      throw new UnsupportedOperationException();
    default :
      int mx = getYearMonthNumber();
      return new MonthInterval(mx / 12, mx % 12);
    }
  }


  public DayInterval toDayInterval() {
    switch (resolution) {
    case YEAR :
    case MONTH :
      throw new UnsupportedOperationException();
    case DAY :
      return (DayInterval)this;
    default :
      long i = getIndex();
      long i2 =  (i / (24 * 60 * 60)) * 24 * 60 * 60;
      return new DayInterval(i2);
    }
  }


  public int daysInMonth () {
    int dayIndex = (int)(index / (24 * 60 * 60));
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
  
  
  @SuppressWarnings("unchecked")
  public <T extends DayInterval> T addYear (int adj) {
    int n = getYearMonthNumber() + adj * 12;
    int day = getDay();
    int year = n / 12;
    int month = n % 12;
    int x = yearMonthAsJulien(year, month) + day;
    return (T)create(x * 24 * 60 * 60);
  }    
  
  
  @SuppressWarnings("unchecked")
  public <T extends DayInterval> T addMonthDay (int adj, int day) {
    if (resolution == Resolution.YEAR) {
      throw new UnsupportedOperationException();
    }
    int n = getYearMonthNumber() + adj;
    int year = n / 12;
    int month = n % 12;
    int x = yearMonthAsJulien(year, month);
    if (day > 0) {
      x += day - 1;
    } else {
      x += day;
    }
    return (T)create(x * 24L * 60L * 60L);
  }    
  
  
  @SuppressWarnings("unchecked")
  public <T extends DayInterval> T addDay (int adj) {
    return (T) create(index + adj * 24L * 60L * 60L);
  }
  
  
  /* Set the day within a date.  The day is 
   * in the range 1..31 or -1..-31.
   */
  public Interval setDay (int day) {
    if (resolution == Resolution.YEAR) {
      throw new UnsupportedOperationException();
    }
    int yearMonth = getYearMonthNumber();
    int year = yearMonth / 12;
    int month = yearMonth % 12;
    int n = yearMonthAsJulien(year, month);
    if (day > 0) {
      n += day - 1;
    } else {
      n += day;
    }
    return create(((long)n) * 24L * 60L * 60L);
  }
  
  
  public boolean isGenesis () {
    return this.equals(GENESIS);
  }


  @Override
  public int compareTo(Interval other) {
    if (index < other.index) {
      return -1;
    } else if (index > other.index) {
      return +1;
    } else {
      return 0;
    }
  }

  
  public boolean contains (Interval target) {
    if (resolution.contains(target.resolution)) {
      return index <= target.index && target.upperLimit() <= upperLimit();
    } else {
      throw new IllegalArgumentException(target.resolution + " is broader that " + resolution);
    }
  }
}
