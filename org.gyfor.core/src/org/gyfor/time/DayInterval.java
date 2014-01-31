package org.gyfor.time;

import java.util.Date;

public class DayInterval extends Interval {
  
  private static final long serialVersionUID = 1L;

  public static final DayInterval START_DAY = new DayInterval(1, 1, 1);
  public static final DayInterval END_DAY = new DayInterval(2099, 12, 31);
  

  public DayInterval (int year, int month, int day) {
    super (year, month, day);
  }

  
  public DayInterval (Interval secs) {
    super (secs.getYear(), secs.getMonth(), secs.getDay());
  }
  
  
  private static int[] parseYearMonthDay (String source) {
    String[] msg = new String[1];
    int[] parts = new int[3];
    String[] completion = new String[1];
    int result = DateFactory.validate(source, null, msg, parts, completion);
    if (result == DateFactory.OK) {
      return parts;
    } else {
      throw new IllegalArgumentException(msg[0]);
    }
  }
  
  
  public DayInterval (String s) {
    super (parseYearMonthDay(s));
  }
  
  
  @SuppressWarnings("deprecation")
  public DayInterval (Date date) {
    super (((date.getTime() / 1000 + JAVA_OFFSET - date.getTimezoneOffset() * 60) / (24 * 60 * 60 )) * 24 * 60 * 60, Resolution.DAY);
  }
  
  
  public DayInterval (long index) {
    super (index, Resolution.DAY);
  }
  
  
  @SuppressWarnings("deprecation")
  public static DayInterval today() {
    Date now = new Date();
    long n = new Date().getTime() / 1000 + JAVA_OFFSET - now.getTimezoneOffset() * 60;
    // Remove any time component
    n = (n / (24 * 60 * 60)) * (24 * 60 * 60);
    return new DayInterval(n);
  }
  
  
  public static DayInterval parseISODate (String s) {
    /* s is assumed to be ISO date format: yyyy-MM-dd */
    int year = Integer.parseInt(s.substring(0, 4));
    int month = Integer.parseInt(s.substring(5, 7));
    int day = Integer.parseInt(s.substring(8));
    return new DayInterval(year, month, day);
  }
  
  
  public static DayInterval parseDate (String source) {
    String[] msg = new String[1];
    int[] parts = new int[3];
    String[] completion = new String[1];
    int result = DateFactory.validate(source, null, msg, parts, completion);
    if (result == DateFactory.OK) {
      return new DayInterval(parts[0], parts[1], parts[2]);
    } else {
      throw new IllegalArgumentException(msg[0]);
    }
  }
  
  
  protected DayInterval create (long index) {
    return new DayInterval(index);
  }
  
  
  protected long upperLimit () {
    return getIndex() + 24 * 60 * 60;
  }
  
}
