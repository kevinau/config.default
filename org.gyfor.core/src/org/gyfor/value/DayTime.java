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
package org.gyfor.value;


import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;


public class DayTime implements Comparator<DayTime>, Serializable {

  private static final long serialVersionUID = 8142027935542931993L;

  public static final DayTime ZERO = new DayTime(0);
  

  private final int daySecs;
  
  
  public DayTime () {
    this (0);
  }
  
  
  public DayTime (Date date) {
    daySecs = ((int)(date.getTime() / 1000L)) % (24 * 60 * 60);
  }
  

  public DayTime (long time) {
    daySecs = ((int)(time / 1000L)) % (24 * 60 * 60);
  }
  

  /* Construct a date from hour, minute and second.  The hour is in the range 0..23, and the
   * minute and second is in the range 0..59.  24:00:00 is also allowed.
   */
  public DayTime (int hour, int minute, int second) {
    daySecs = (hour * 24 + minute) * 60 + second;
  }
  
  
  public DayTime (int hour, int minute) {
    this (hour, minute, 0);
  }
  
  
  private DayTime (int daySecs) {
    this.daySecs = daySecs;
  }
  
  
  public static DayTime now() {
    return new DayTime(new Date());
  }
  
  
  @Override
  public Object clone () {
    return new DayTime(daySecs);
  }
  
  
  @Override
  public boolean equals (Object other) {
    if (this == other) {
      return true;
    }
    if (other == null || !(other instanceof DayTime)) {
      return false;
    }
    DayTime time1 = (DayTime)other;
    return daySecs == time1.daySecs;
  }
  
  
  @Override
  public int hashCode () {
    return daySecs;
  }
  
  
  int getDaySecs() {
    return daySecs;
  }
  
  
  public static DayTime parseIsoTime (String s) {
    /* s is assumed to be ISO time format: hh:mm:ss */
    int hour = Integer.parseInt(s.substring(0, 2));
    int minute = Integer.parseInt(s.substring(3, 5));
    int second;
    if (s.length() > 5) {
      second = Integer.parseInt(s.substring(6));
    } else {
      second = 0;
    }
    return new DayTime(hour, minute, second);
  }
  
  
  public DayTime plus (DayTime time) {
    return new DayTime(daySecs + time.daySecs);
  }
  
  
  public DayTime minus (DayTime time) {
    return new DayTime(daySecs - time.daySecs);
  }
  
  
  public boolean before (DayTime other) {
    return daySecs < other.daySecs;
  }
  
  
  public boolean after (DayTime other) {
    return daySecs > other.daySecs;
  }
  
  
  public int compare (DayTime d1, DayTime d2) {
    return d1.daySecs - d2.daySecs;
  }
  
  
  @Override
  public String toString () {
    StringBuffer buffer = new StringBuffer(8);
    int hour = getHour();
    if (hour < 10) {
      buffer.append('0');
    }
    buffer.append(hour);
    buffer.append(':');
    int minute = getMinute();
    if (minute < 10) {
      buffer.append('0');
    }
    buffer.append(minute);
    buffer.append(':');
    int second = getSecond();
    if (second < 10) {
      buffer.append('0');
    }
    buffer.append(second);
    return buffer.toString();
  }
  
  
  public Date javaDate () {
    return new Date(daySecs * 1000L);
  }
  
  
  public java.sql.Time sqlDate () {
    return new java.sql.Time(daySecs * 1000L);
  }
  
  
  public long javaTime () {
    return daySecs * 1000L;
  }
  
  
  public int getHour () {
    return daySecs / (60 * 60);
  }
  
  
  public int getMinute () {
    return (daySecs / 60) % 60;
  }


  public int getSecond () {
    return daySecs % 60;
  }
  
}
