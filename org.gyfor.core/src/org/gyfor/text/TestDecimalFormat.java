package org.gyfor.text;

import java.math.RoundingMode;

import org.gyfor.math.Decimal;

public class TestDecimalFormat {

  private static String[] negIndicators = { "(", ")", };

  public static void main(String[] args) {
    Decimal d0 = new Decimal("123456.789123");
    for (int i = -6; i <= 6; i++) {
      String n0 = d0.toString(i, i, RoundingMode.HALF_EVEN, negIndicators);
      System.out.println(d0 + " --> " + n0);
    }
    System.out.println();

    Decimal d1 = new Decimal("100000");
    for (int i = -6; i <= 6; i++) {
      String n1 = d1.toString(i, i, RoundingMode.HALF_EVEN, negIndicators);
      System.out.println(d1 + " --> " + n1);
    }
    System.out.println();

    Decimal d2 = Decimal.ZERO;
    for (int i = -6; i <= 6; i++) {
      String n2 = d2.toString(i, i, RoundingMode.HALF_EVEN, negIndicators);
      System.out.println(d2 + " --> " + n2);
    }
    System.out.println();

    Decimal d3 = new Decimal("0.0001");
    for (int i = -6; i <= 6; i++) {
      String n1 = d3.toString(i, i, RoundingMode.HALF_EVEN, negIndicators);
      System.out.println(d3 + " --> " + n1);
    }
    System.out.println();

  }
}
