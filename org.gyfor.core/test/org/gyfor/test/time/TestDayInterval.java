package org.gyfor.test.time;

import java.util.Calendar;
import java.util.Date;

import org.gyfor.time.DayInterval;
import org.junit.Assert;
import org.junit.Test;

public class TestDayInterval {

  @Test
  public void constructorTest () {
    DayInterval year = new DayInterval(2012, 5, 2);
    String s = year.toISOString();
    Assert.assertEquals("2012-05-02", s);
    
    DayInterval year2 = new DayInterval(year);
    String s2 = year.toISOString();
    Assert.assertEquals("2012-05-02", s2);
  
    DayInterval year3 = new DayInterval("02052012");
    Assert.assertEquals(year2, year3);

    DayInterval year4 = new DayInterval("02-05-2012");
    Assert.assertEquals(year2, year4);

    DayInterval year5 = new DayInterval("02/05/2012");
    Assert.assertEquals(year2, year5);

    DayInterval year6 = new DayInterval("2/5/2012");
    Assert.assertEquals(year2, year6);
    
    DayInterval d7 = DayInterval.parseDate("2/5/2012");
    Assert.assertEquals(year2, d7);
    Assert.assertEquals(2,  d7.getDay());
    Assert.assertEquals(5,  d7.getMonth());
    Assert.assertEquals(2012,  d7.getYear());
    
    DayInterval d8 = DayInterval.today();
    Calendar c = Calendar.getInstance();
    c.setTime(new Date());
    DayInterval d8x = new DayInterval(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH));
    Assert.assertEquals(d8, d8x);
  }

  @Test(expected=java.lang.IllegalArgumentException.class)
  public void badConstructorTest () {
    new DayInterval("99999");
  }

}
