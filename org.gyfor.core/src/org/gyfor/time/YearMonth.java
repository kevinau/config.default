package org.gyfor.time;

public class YearMonth {
  
  private final int monthIndex;
  
  
  public YearMonth (int monthIndex) {
    this.monthIndex = monthIndex;
  }
  
  
  public YearMonth (int years, int month) {
    if (month < 0) {
      // do nothing
    } else if (month > 0) {
      month--;
    } else {
      throw new IllegalArgumentException("Month cannot be zero");
    }
    monthIndex = years * 12 + month;
  }
  
  
  public int getYear () {
    return monthIndex / 12;
  }
  
  
  public int getMonth () {
    return (monthIndex % 12) + 1;
  }
  
  
  public YearMonth addYears (int years) {
    return new YearMonth(monthIndex + years * 12);
  }
  
  
  public YearMonth addMonths (int months) {
    return new YearMonth(monthIndex + months);
  }
  
  
  public YearMonth addYearsMonth (int years, int month) {
    if (month < 0) {
      // do nothing
    } else if (month > 0) {
      month--;
    } else {
      throw new IllegalArgumentException("Month cannot be zero");
    }
    return new YearMonth(monthIndex + years * 12 + month);
  }
  
  
  public DayDate makeDate (int day) {
    return new DayDate(monthIndex / 12, monthIndex % 12 + 1, day);
  }
  
}
