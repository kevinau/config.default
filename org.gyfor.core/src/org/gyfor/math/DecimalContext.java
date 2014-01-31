/*******************************************************************************
 * Copyright (c) 2012 Kevin Holloway (kholloway@geckosoftware.co.uk).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kevin Holloway - initial API and implementation
 *******************************************************************************/
package org.gyfor.math;

import java.math.RoundingMode;

/**
 * Immutable objects which encapsulate scale and rounding setting for 
 * the {@link Decimal} class.
 * <p>
 * This class includes the following settings:
 * <dl>
 * <dt>Scale</dt>
 * <dd>The number of digits to the right of the decimal digit.  This number may
 * may be negative, which is then the number of padding zeros to the left of the
 * decimal digit.</dd>
 * <dt>Rounding mode</dt>
 * <dd>The algorithm to be used for rounding.</dd>
 * </dl>
 * @see     Decimal
 * @author  Kevin Holloway of geckosoftware.co.uk
 */
public final class DecimalContext implements java.io.Serializable {

  private static final long serialVersionUID = 1L;

  public static DecimalContext MONEY = new DecimalContext(2, RoundingMode.HALF_EVEN);
  
  
  /**
   * The number of decimal digits (scale) to be used for an operation.  
   * This is the number of decimal digits to the right of the decimal 
   * point.  This number can be negative, which is then the of zeros
   * to the left of the decimal point.
   * <p>
   * The {@link Decimal} operator methods use this value to
   * determine the precision of results.
   * <p>
   * <code>decimalDigits</code> can be negative.
   */
  short decimalDigits;

  /**
   * The rounding algorithm to be used for an operation.
   * <p>
   * The {@link Decimal} operator methods use this value to
   * determine the algorithm to be used when non-zero digits have to
   * be discarded in order to reduce the precision of a result.
   */
  RoundingMode roundingMode;

  
  private static final int MAX_DECIMALDIGITS = 18;
  // largest value for DIGITS.  If increased,
  // the Decimal class may need update.


  /**
   * A <code>Decimals</code> object initialised to the default
   * settings for general-purpose arithmetic.  That is,
   * <code>decimalDigits=0, roundingMode=RoundingMode.HALF_UP</code>.
   */
  public static final DecimalContext DEFAULT =
    new DecimalContext(0, RoundingMode.HALF_EVEN);


  /**
   * Constructs a new <code>DecimalContext</code> with a specified
   * number of decimal digits and rounding mode HALF_EVEN.
   *
   * An <code>IllegalArgumentException</code> is thrown if the
   * number of <code>decimalDigits</code> parameter is out of range
   * (&lt;-18 or &gt;18).
   *
   * @param decimalDigits  The <code>int</code> decimalDigits setting
   *                       for this <code>MathContext</code>.
   * @throws IllegalArgumentException if the decimalDigits parameter is out of range.
   */
  public DecimalContext(int decimalDigits) {
    this(decimalDigits, RoundingMode.HALF_EVEN);
  }

  
  /**
   * Constructs a new <code>MathContext</code> with a specified number of
   * decimal digits and rounding mode.
   * 
   * An <code>IllegalArgumentException</code> is thrown if the number of
   * <code>decimalDigits</code> parameter is out of range (&lt;-18 or &gt;18).
   * 
   * @param decimalDigits
   *          The <code>int</code> decimalDigits setting for this
   *          <code>MathContext</code>.
   * @param roundingmode
   *          The rounding mode to use.
   * @throws IllegalArgumentException
   *           if the decimalDigits parameter is out of range.
   */
  public DecimalContext (int decimalDigits, RoundingMode roundingmode) {
    if (decimalDigits < -MAX_DECIMALDIGITS) {
      throw new IllegalArgumentException(
        "Decimal digits parameter < " + (-MAX_DECIMALDIGITS));
    }
    if (decimalDigits > MAX_DECIMALDIGITS) {
      throw new IllegalArgumentException(
          "Decimal digits parameter > " + MAX_DECIMALDIGITS);
    }
    this.decimalDigits = (short)decimalDigits;
    this.roundingMode = roundingmode;
  }

  
  /**
   * Returns the decimal digits setting.
   * 
   * @return an <code>int</code> which is the value of the decimal digits
   *         setting
   */
  public int getDecimalDigits() {
    return decimalDigits;
  }
  

  /**
   * Returns the roundingMode setting.
   *
   * @return a <code>RoundingMode</code> which is the value of the roundingMode
   *         setting.
   */

  public RoundingMode getRoundingMode() {
    return roundingMode;
  }


  /** 
   * Returns the <code>MathContext</code> as a readable string.  This
   * is useful for debugging.
   *
   * @return a <code>String</code> representing the context settings.
   */
  @Override
  public String toString() {
    return "DecimalContext[" + decimalDigits + "," + roundingMode + "]";
  }


  private static final long[] scalex = {
    1L, 
    10L, 
    100L, 
    1000L, 
    10000L, 
    100000L, 
    1000000L,
    10000000L,
    100000000L,
    1000000000L,
    10000000000L,
    100000000000L,
    1000000000000L,
    10000000000000L,
    100000000000000L,
    10000000000000000L,
    100000000000000000L,
    1000000000000000000L,
  };
  

  long adjustValue (long value, int scale) {
    int scaleAdj = scale - decimalDigits;
    if (scaleAdj == 0) {
      return value;
    } else if (scaleAdj <= 0) {
      /* No rounding issues */
      return value * scalex[-scaleAdj];
    } else {
      return divideValue(value, scalex[scaleAdj], roundingMode);
    }
  }


  static long adjustValue (long value, int scale, int decimalDigits, RoundingMode roundingMode) {
    int scaleAdj = scale - decimalDigits;
    if (scaleAdj == 0) {
      return value;
    } else if (scaleAdj <= 0) {
      /* No rounding issues */
      return value * scalex[-scaleAdj];
    } else {
      return divideValue(value, scalex[scaleAdj], roundingMode);
    }
  }


  static long adjustValue (long value, int scaleAdj, RoundingMode roundingMode) {
    //int scaleAdj = scale - decimalDigits;
    if (scaleAdj == 0) {
      return value;
    } else if (scaleAdj <= 0) {
      /* No rounding issues */
      return value * scalex[-scaleAdj];
    } else {
      return divideValue(value, scalex[scaleAdj], roundingMode);
    }
  }


  static long divideValue (long value, long divider, RoundingMode roundingMode) {
    long divider2 = (divider < 0 ? -divider : divider);
    long increment = 0;
    long remainder;
    int sign = value < 0 ? -1 : +1;
    switch (roundingMode) {
    case HALF_UP :
      remainder = value % divider;
      if (remainder < 0) {
        remainder = -remainder;
      }
      if (remainder * 2 >= divider2) {
        increment = sign;
      }
      break;
    case HALF_DOWN :      // 0.5000 goes down
      remainder = value % divider;
      if (remainder < 0) {
        remainder = -remainder;
      }
      if (remainder * 2 > divider2) {
        increment = sign;
      }
      break;
    case HALF_EVEN :      // 0.5000 goes down if left digit even
      remainder = value % divider;
      if (remainder < 0) {
        remainder = -remainder;
      }
      if (remainder * 2 > divider2) {
        increment = sign;
      } else if (remainder * 2 == divider2) {
        long x = value / divider;
        if ((x & 1) == 1) {
          increment = sign;
        }
      }
      break;
    case UP:              // increment if discarded non-zero
      remainder = value % divider;
      if (remainder != 0) {
        increment = sign;
      }
      break;
    case DOWN:            // never increment
      break;
    case CEILING:         // more positive
      remainder = value % divider;
      if (sign > 0 && remainder != 0) {
        increment = sign;
      }
      break;
    case FLOOR:           // more negative
      remainder = value % divider;
      if (sign < 0 && remainder != 0) {
        increment = sign;
      }
      break;
    case UNNECESSARY :
      remainder = value % divider;
      if (remainder != 0) {
        throw new java.lang.ArithmeticException("Rounding necessary");
      }
      break;
    }
    return value / divider + increment;
  }


}
