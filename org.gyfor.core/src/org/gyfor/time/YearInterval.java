package org.gyfor.time;

public class YearInterval extends Interval {

  private static final long serialVersionUID = 1L;

  public YearInterval (int year) {
    super (year);
  }

  
  public YearInterval (Interval secs) {
    super (secs.getYear());
  }
  
  
  private YearInterval (long index) {
    super (index, Resolution.YEAR);
  }
  
  
  private static int[] parseYear (String s) {
    String s0 = s.trim();
    if (s0.length() != 4) {
      throw new IllegalArgumentException(s);
    }
    int year = Integer.parseInt(s0);
    return new int[] {year};
  }
  
  
  public YearInterval (String s) {
    super (parseYear(s));
  }


  @Override
  protected YearInterval create(long index) {
    return new YearInterval(index);
  }

  
  @Override
  protected long upperLimit() {
    int year = getYear();
    int x = yearMonthAsJulien(year + 1, 0);
    return (long)x * 24 * 60 * 60;
  }

}
