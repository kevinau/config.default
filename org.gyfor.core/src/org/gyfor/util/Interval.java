package org.gyfor.util;

public class Interval<T extends Comparable<?>> {

  private final Comparable<T> startValue;
  private final Comparable<T> endValue;
  private final boolean startClosed;
  private final boolean endClosed;
  
  
  @SuppressWarnings("unchecked")
  public Interval (T startValue, T endValue, String type) {
    switch (type) {
    case "()" :
      startClosed = true;
      endClosed = true;
      break;
    case "(]" :
      startClosed = true;
      endClosed = false;
      break;
    case "[)" :
      startClosed = false;
      endClosed = true;
      break;
    case "[]" :
      startClosed = false;
      endClosed = false;
      break;
    default :
      throw new IllegalArgumentException("Interval type must be one of: () (] [) or []");
    }
    this.startValue = (Comparable<T>)startValue;
    this.endValue = (Comparable<T>)endValue;
  }
  
  
  public boolean contains (T value) {
    int n1 = startValue.compareTo(value);
    if (n1 < 0 || (n1 == 0 && !startClosed)) {
      return false;
    }
    int n2 = endValue.compareTo(value);
    if (n2 > 0 || (n2 == 0 && !endClosed)) {
      return false;
    }
    return true;
  }
}
