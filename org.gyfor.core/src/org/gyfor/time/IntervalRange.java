package org.gyfor.time;

import java.io.Serializable;

public class IntervalRange implements IIntervalRange, Serializable {

  private static final long serialVersionUID = 1L;

  public final static IntervalRange ALL = new IntervalRange(DayInterval.START_DAY, DayInterval.END_DAY);
  
  private final Interval start;
  private final Interval end;
  
  
  public IntervalRange (Interval start, Interval end) {
    this.start = start;
    this.end = end;
  }
  
  
  public boolean contains (Interval value) {
    return !value.before(start) && value.before(end);
  }


  @Override
  public String toString() {
    return "[" + start + ".." + end + ")";
  }
}
