package org.gyfor.time;

import java.util.Date;

public class SecondInterval extends Interval {

  private static final long serialVersionUID = 1L;


  SecondInterval (long index) {
    super (index, Resolution.DAY);
  }
  
  
  @SuppressWarnings("deprecation")
  public static Interval now() {
    Date now = new Date();
    long n = new Date().getTime() / 1000 + JAVA_OFFSET - now.getTimezoneOffset() * 60;
    return new SecondInterval(n);

  }


  protected SecondInterval create (long index) {
    return new SecondInterval(index);
  }
  
  
  protected long upperLimit () {
    return getIndex() + 1;
  }
  
}
