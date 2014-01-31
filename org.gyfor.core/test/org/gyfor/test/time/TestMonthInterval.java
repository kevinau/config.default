package org.gyfor.test.time;

import org.gyfor.time.MonthInterval;
import org.junit.Assert;
import org.junit.Test;

public class TestMonthInterval {

  @Test
  public void constructorTest () {
    MonthInterval year = new MonthInterval(2012, 5);
    String s = year.toISOString();
    Assert.assertEquals("2012-05", s);
    
    MonthInterval year2 = new MonthInterval(year);
    String s2 = year.toISOString();
    Assert.assertEquals("2012-05", s2);
  
    MonthInterval year3 = new MonthInterval("201205");
    Assert.assertEquals(year2, year3);

    MonthInterval year4 = new MonthInterval("2012-05");
    Assert.assertEquals(year2, year4);

    MonthInterval year5 = new MonthInterval("05/2012");
    Assert.assertEquals(year2, year5);

    MonthInterval year6 = new MonthInterval("5/2012");
    Assert.assertEquals(year2, year6);
  }

  @Test(expected=java.lang.IllegalArgumentException.class)
  public void badConstructorTest () {
    new MonthInterval("99999");
  }

}
