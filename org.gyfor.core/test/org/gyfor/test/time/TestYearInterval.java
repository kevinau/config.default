package org.gyfor.test.time;

import org.gyfor.time.DayInterval;
import org.gyfor.time.Interval;
import org.gyfor.time.YearInterval;
import org.junit.Assert;
import org.junit.Test;

public class TestYearInterval {

  @Test
  public void constructorTest () {
    YearInterval year = new YearInterval(2012);
    String s = year.toISOString();
    Assert.assertEquals("2012", s);
    
    YearInterval year2 = new YearInterval(year);
    String s2 = year.toISOString();
    Assert.assertEquals("2012", s2);
  
    YearInterval year3 = new YearInterval("2012");
    Assert.assertEquals(year2, year3);
  }

  @Test(expected=java.lang.IllegalArgumentException.class)
  public void badConstructorTest () {
    new YearInterval("99999");
  }

  @Test
  public void setMonthDay () {
    YearInterval year = new YearInterval(2012);
    Assert.assertEquals(2012, year.getYear());
    Interval date = year.setMonthDay(1, 1);
    Assert.assertEquals(true, date instanceof DayInterval);
    Assert.assertEquals(2012, date.getYear());
    Assert.assertEquals(1, date.getMonth());
    Assert.assertEquals(1, date.getDay());
  }

}
