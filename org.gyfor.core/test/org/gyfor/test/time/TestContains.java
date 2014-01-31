package org.gyfor.test.time;

import org.gyfor.time.DayInterval;
import org.gyfor.time.IntervalRange;
import org.gyfor.time.MonthInterval;
import org.gyfor.time.YearInterval;
import org.junit.Assert;
import org.junit.Test;

public class TestContains {

  @Test
  public void testDayContains () {
    DayInterval day = new DayInterval(2012, 5, 12);
    DayInterval dayNext = new DayInterval(2012, 5, 13);
    DayInterval dayPrior = new DayInterval(2012, 5, 11);
    Assert.assertTrue(day.contains(day));
    Assert.assertFalse(day.contains(dayNext));
    Assert.assertFalse(day.contains(dayPrior));
  }

  @Test
  public void testMonthContains () {
    MonthInterval month = new MonthInterval(2012, 5);
    DayInterval day = new DayInterval(2012, 5, 12);
    DayInterval dayHigh = new DayInterval(2012, 5, 31);
    DayInterval dayNext = new DayInterval(2012, 6, 1);
    DayInterval dayPrior = new DayInterval(2012, 4, 30);
    DayInterval dayLow = new DayInterval(2012, 5, 1);
    Assert.assertTrue(month.contains(day));
    Assert.assertTrue(month.contains(dayLow));
    Assert.assertTrue(month.contains(dayHigh));
    Assert.assertFalse(month.contains(dayNext));
    Assert.assertFalse(month.contains(dayPrior));
  }
  
  
  @Test
  public void testYearContains () {
    YearInterval year = new YearInterval(2012);
    DayInterval day = new DayInterval(2012, 5, 12);
    DayInterval dayHigh = new DayInterval(2012, 12, 31);
    DayInterval dayNext = new DayInterval(2013, 1, 1);
    DayInterval dayPrior = new DayInterval(2011, 12, 31);
    DayInterval dayLow = new DayInterval(2012, 1, 1);
    Assert.assertTrue(year.contains(day));
    Assert.assertTrue(year.contains(dayLow));
    Assert.assertTrue(year.contains(dayHigh));
    Assert.assertFalse(year.contains(dayNext));
    Assert.assertFalse(year.contains(dayPrior));
  }

  
  @Test
  public void testRangeContains () {
    DayInterval day1 = new DayInterval(2012, 5, 12);
    DayInterval day2 = new DayInterval(2012, 7, 22);
    DayInterval day = new DayInterval(2012, 6, 1);

    IntervalRange range = new IntervalRange(day1, day2);
    DayInterval dayNext = new DayInterval(2012, 5, 11);
    DayInterval dayLast = new DayInterval(2012, 7, 21);
    DayInterval dayPrior = new DayInterval(2012, 7, 23);
    Assert.assertTrue(range.contains(day));
    Assert.assertTrue(range.contains(day1));
    Assert.assertTrue(range.contains(dayLast));
    Assert.assertFalse(range.contains(day2));
    Assert.assertFalse(range.contains(dayNext));
    Assert.assertFalse(range.contains(dayPrior));
  }

  
  @Test (expected=IllegalArgumentException.class)
  public void testInvalidContains () {
    DayInterval day = new DayInterval(2012, 6, 5);
    MonthInterval month = new MonthInterval(2012, 6);
    day.contains(month);
  }
}
