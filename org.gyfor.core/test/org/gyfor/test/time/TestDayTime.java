package org.gyfor.test.time;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.gyfor.time.LocalTime;
import org.gyfor.time.LocalTime.Resolution;
import org.junit.Assert;
import org.junit.Test;

public class TestDayTime {

  @Test
  public void testConstructors () {
    for (int i = 0; i < 365 * 10; i++) {
      Date date = new Date(i * 24L * 60L * 60L * 1000L);
      LocalTime d2 = new LocalTime(date, Resolution.DAY);
      Date date2 = d2.javaDate();
      //System.out.println(date + " -- " + date2);
      Assert.assertEquals(date,  date2);
    }
  }
  
  @Test
  public void testWeeks () {
    Calendar c = Calendar.getInstance();
    for (int i = 0; i < 365; i++) {
      c.setTimeInMillis(i * 24L * 60L * 60L * 1000L);
      LocalTime d2 = new LocalTime(c.getTime(), Resolution.DAY);
      int w1 = c.get(Calendar.DAY_OF_WEEK) - 1;
      int w2 = d2.getWeekday();
      Assert.assertEquals(w1, w2);
    }
  }

  @Test
  public void testJavaTime () {
    Calendar c = Calendar.getInstance();
    for (int i = 0; i < 365 * 10; i++) {
      c.setTimeInMillis(i * 24L * 60L * 60L * 1000L);
      LocalTime d2 = new LocalTime(c.getTime(), Resolution.DAY);
      Assert.assertEquals(d2.javaTime(), c.getTimeInMillis());
    }
  }

  @Test
  public void testDateString () {
    Calendar c = Calendar.getInstance();
    for (int i = 0; i < 365 * 10; i++) {
      c.setTimeInMillis(i * 24L * 60L * 60L * 1000L);
      LocalTime d2 = new LocalTime(c.getTime(), Resolution.DAY);
      DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
      String d2x = format.format(c.getTime());
      String d1x = d2.toString();
      Assert.assertEquals(d2x, d1x);
    }
  }

  @Test
  public void testMonthIndex () {
    Calendar c = Calendar.getInstance();
    for (int i = 0; i < 365 * 10; i++) {
      c.setTimeInMillis(i * 24L * 60L * 60L * 1000L);
      LocalTime date2 = new LocalTime(c.getTime(), Resolution.DAY);
      int y1 = c.get(Calendar.YEAR);
      int m1 = c.get(Calendar.MONTH);
      int mindex = date2.getMonthIndex();
      
      Assert.assertEquals(y1, mindex / 12);
      Assert.assertEquals(m1, mindex % 12);
    }
  }

  @Test
  public void testYearMonthDay () {
    Calendar c = Calendar.getInstance();
    for (int i = 0; i < 365 * 10; i++) {
      c.setTimeInMillis(i * 24L * 60L * 60L * 1000L);
      LocalTime date2 = new LocalTime(c.getTime(), Resolution.DAY);
      int y1 = c.get(Calendar.YEAR);
      int y2 = date2.getYear();
      Assert.assertEquals(y1, y2);
      int m1 = c.get(Calendar.MONTH) + 1;
      int m2 = date2.getMonth();
      Assert.assertEquals(m1, m2);
      int d1 = c.get(Calendar.DAY_OF_MONTH);
      int d2 = date2.getDay();
      Assert.assertEquals(d1, d2);
      LocalTime date3 = new LocalTime(y2, m2, d2);
      Assert.assertEquals(c.getTime(),  date3.javaDate());
      int x1 = c.getActualMaximum(Calendar.DAY_OF_MONTH);
      int x2 = date2.daysInMonth();
      Assert.assertEquals(x1, x2);
    }
  }

  @Test
  public void testYearResolution () {
    Calendar c = Calendar.getInstance();
    for (int i = 0; i < 365 * 10; i++) {
      c.setTimeInMillis(i * 24L * 60L * 60L * 1000L);
      LocalTime date2 = new LocalTime(c.getTime(), Resolution.YEAR);
      int y1 = c.get(Calendar.YEAR);
      int y2 = date2.getYear();
      Assert.assertEquals(y1, y2);
      Assert.assertEquals(1, date2.getMonth());
      Assert.assertEquals(1, date2.getDay());
    }
  }

  @Test
  public void testMonthResolution () {
    Calendar c = Calendar.getInstance();
    for (int i = 0; i < 365 * 10; i++) {
      c.setTimeInMillis(i * 24L * 60L * 60L * 1000L);
      LocalTime date2 = new LocalTime(c.getTime(), Resolution.MONTH);
      int y1 = c.get(Calendar.YEAR);
      int y2 = date2.getYear();
      Assert.assertEquals(y1, y2);
      int m1 = c.get(Calendar.MONTH) + 1;
      int m2 = date2.getMonth();
      Assert.assertEquals(m1, m2);
      Assert.assertEquals(1, date2.getDay());
    }
  }

  @Test
  public void testGenesis () {
    LocalTime t0 = LocalTime.GENESIS;
    Assert.assertEquals(true, t0.isGenesis());
    LocalTime t1 = new LocalTime(0, 1, 2);
    Assert.assertEquals(-1, t0.compareTo(t1));
    t0 = LocalTime.today();
    Assert.assertEquals(false, t0.isGenesis());
  }
}
