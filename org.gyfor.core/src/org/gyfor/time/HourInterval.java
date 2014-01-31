package org.gyfor.time;


public class HourInterval extends Interval {
 
  private static final long serialVersionUID = 1L;

  
  HourInterval (long index) {
    super (index, Resolution.HOUR);
  }

  @Override
  protected Interval create(long index) {
    return new HourInterval(index);
  }

  @Override
  protected long upperLimit() {
    return getIndex() + 60 * 60;
  }


}
