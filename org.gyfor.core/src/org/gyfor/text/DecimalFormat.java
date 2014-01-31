package org.gyfor.text;

import java.math.RoundingMode;

import org.gyfor.math.Decimal;

public class DecimalFormat implements IFormat {

  private final String pattern;
  private final transient int minFraction;
  private final transient int maxFraction;
  private final transient String[] negIndicators = new String[2];
  
  
  public DecimalFormat (String pattern) {
    this.pattern = pattern;
    java.text.DecimalFormat dformat = new java.text.DecimalFormat(pattern);
    maxFraction = dformat.getMaximumFractionDigits();
    minFraction = dformat.getMinimumFractionDigits();
    negIndicators[0] = dformat.getNegativePrefix();
    negIndicators[1] = dformat.getNegativeSuffix();
  }
  

  @Override
  public String toString() {
    return pattern;
  }
  
  
  @Override
  public String toString(Object value) {
    return ((Decimal)value).toString(minFraction, maxFraction, RoundingMode.HALF_EVEN, negIndicators);
  }
  
}
