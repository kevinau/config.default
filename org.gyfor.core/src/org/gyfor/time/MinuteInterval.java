package org.gyfor.time;


public class MinuteInterval extends Interval {

  private static final long serialVersionUID = 1L;

  
  MinuteInterval (long index) {
    super (index, Resolution.MINUTE);
  }

  @Override
  protected Interval create(long index) {
    return new MinuteInterval(index);
  }

  @Override
  protected long upperLimit() {
    return getIndex() + 60;
  }

}
