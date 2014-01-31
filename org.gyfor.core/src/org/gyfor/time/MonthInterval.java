package org.gyfor.time;

public class MonthInterval extends Interval {
  
  private static final long serialVersionUID = 1L;

  public MonthInterval (int year, int month) {
    super (year, month);
  }

  
  public MonthInterval (Interval secs) {
    super (secs.getYear(), secs.getMonth());
  }
  
  
  public MonthInterval (long index) {
    super (index, Resolution.MONTH);
  }
  
  
  private static int[] parseYearMonth (String s) {
    char[] cx = s.trim().toCharArray();
    int i = 0;
    int n = 0;
    while (i < cx.length && Character.isDigit(cx[i])) {
      n = n * 10 + cx[i] - '0';
      i++;
    }

    int year = 0;
    int month = 0;
    if (i == cx.length) {
      year = n / 100;
      month = n % 100;
    } else {
      if (i == 4) {
        year = n;
        i++;
        while (i < cx.length) {
          month = month * 10 + cx[i] - '0';
          i++;
        }
      } else {
        month = n;
        i++;
        while (i < cx.length) {
          year = year * 10 + cx[i] - '0';
          i++;
        }
      }
    }
    return new int[] {year, month};
  }
  
  
  public MonthInterval (String s) {
    super (parseYearMonth(s));
  }


  @Override
  protected MonthInterval create(long index) {
    return new MonthInterval(index);
  }


  @Override
  protected long upperLimit() {
    int n = getYearMonthNumber() + 1;
    int year = n / 12;
    int month = n % 12;
    int x = yearMonthAsJulien(year, month);
    return (long)x * 24 * 60 * 60;
  }
  
}
