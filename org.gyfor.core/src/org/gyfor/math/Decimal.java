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

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * The <code>Decimal</code> class provides a set of decimal numbers and
 * associated arithmetic. <code>Decimal</code> numbers are those that can be
 * represented by a long integer and an integer scale of 10. That is, any number
 * that can be represented as: <blockquote>m &times; 10<sup>n</sup></blockquote>
 * where <code>m</code> is a long integer and <code>n</code> is &plusmn;18.
 * <p>
 * As it is defined, a <code>Decimal</code> contains 18 significant digits, and
 * can handle the addition/subtraction of amounts in the quintillon and
 * multiplication of amounts in the billion range. This is more than sufficient
 * for most business and financial transactions.
 * <p>
 * This class was written as a high performance alternative to the
 * <code>BigDecimal</code> class. The differences between <code>Decimal</code>
 * and <code>BigDecimal</code> are:
 * <ul>
 * <li>The <code>Decimal</code> class does not support unlimited precision
 * numbers. The numbers it does support are large, but limited in precision as
 * described above.</li>
 * <li>Exponential or engineering format is not used. <code>Decimal</code>
 * numbers are always expressed as a sign, followed by sequence of digits (0-9),
 * optionally separated by at most one decimal point.</li>
 * <li>Overflow on addition, subtraction, multiplication and division is not 
 * detected.  Care should be taken to avoid the possibility of overflow when 
 * using these methods.</li>
 * </ul>
 * As the numbers are decimal, conversion to and from <code>String</code>
 * representations is exact. <code>Decimal</code> numbers can also be converted
 * to and from integers and doubles. If converting to or from integers, the
 * conversion is exact. If converting to or from doubles, the conversion is not
 * always exact and rounding or truncation will occur.
 * <p>
 * The inspiration for this class was originally based on IBM's original version
 * of BigDecimal, with later changes to bring it more in line with the
 * <code>BigDecimal</code> class of Java 1.5. In particular, some of the method
 * descriptions have been taken directly from the JavaDoc of the
 * <code>BigDecimal</code>. Due credit is given to both IBM and Sun
 * Microsystems.
 * 
 * @see DecimalContext
 */
public class Decimal extends Number implements Comparable<Decimal> {

  private static final long serialVersionUID = 4300194441465740220L;

  /**
   * The value 0.
   * 
   * @see #ONE
   * @see #TEN
   * @see #HUNDRED
   */
  public static final Decimal ZERO = new Decimal((long) 0);

  /**
   * The value 1.
   * 
   * @see #TEN
   * @see #ZERO
   * @see #HUNDRED
   */
  public static final Decimal ONE = new Decimal((long) 1);

  /**
   * The value 10.
   * 
   * @see #ONE
   * @see #ZERO
   * @see #HUNDRED
   */
  public static final Decimal TEN = new Decimal(10);

  /**
   * The value 100.
   * <p>
   * The BigDecimal class does not support this constant.
   * 
   * @see #ONE
   * @see #ZERO
   * @see #TEN
   */
  public static final Decimal HUNDRED = new Decimal(100);

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

  /**
   * The value of the fixed decimal number, scaled by the number of decimal
   * digits.
   * <p>
   * If the number of decimal digits is 2, then the value 123 would represent
   * the decimal number 1.23.
   */
  private final long value;

  /**
   * The scale. The position of the decimal point within the value. Scale can be
   * negative.
   * <p>
   * If the value is 123, a scale of -1 represents the decimal number 1230.
   */
  private final short scale;

  /**
   * Constructs a <tt>Decimal</tt> from a character array that contains a
   * representation of a decimal number, accepting the same sequence of
   * characters as the {@link #Decimal(String)} constructor, while allowing a
   * sub-array to be specified.
   * 
   * <p>
   * Note that, if the sequence of characters is already available within a
   * character array, using this constructor is faster than converting the
   * <tt>char</tt> array to string and using the <tt>Decimal(String)</tt>
   * constructor .
   * 
   * @param in
   *          <tt>char</tt> array that is the source of characters.
   * @param offset
   *          first character in the array to inspect.
   * @param len
   *          number of characters to consider.
   * @throws NumberFormatException
   *           if <tt>in</tt> is not a valid representation of a
   *           <tt>Decimal</tt> or the defined subarray is not wholly within
   *           <tt>in</tt>.
   */
  public Decimal(char[] in, int offset, int len) {
    this(in, offset, len, null);
  }

  /**
   * Constructs a <tt>Decimal</tt> from a character array that contains a
   * representation of a decimal number, accepting the same sequence of
   * characters as the {@link #Decimal(String)} constructor, while allowing a
   * sub-array to be specified and with rounding according to the context
   * settings.
   * 
   * <p>
   * Note that, if the sequence of characters is already available within a
   * character array, using this constructor is faster than converting the
   * <tt>char</tt> array to string and using the <tt>Decimal(String)</tt>
   * constructor.
   * 
   * <p>
   * Note that, unlike BigDecimal, the context specified here determines the
   * number of decimal digits, not the precision.
   * 
   * @param in
   *          <tt>char</tt> array that is the source of characters.
   * @param offset
   *          first character in the array to inspect.
   * @param len
   *          number of characters to consider..
   * @param dc
   *          the context to use.
   * @throws ArithmeticException
   *           if the result is inexact but the rounding mode is
   *           <tt>UNNECESSARY</tt>.
   * @throws NumberFormatException
   *           if <tt>in</tt> is not a valid representation of a
   *           <tt>Decimal</tt> or the defined subarray is not wholly within
   *           <tt>in</tt>.
   */
  public Decimal(char in[], int offset, int len, DecimalContext dc) {
    if (len <= 0) {
      throw new NumberFormatException(new String(in));
    }

    // Handle and step past sign
    boolean negative = false;
    if (in[offset] == '-') {
      if (len == 1) {
        // nothing after sign
        throw new NumberFormatException(new String(in));
      }
      negative = true;
      offset++;
    } else if (in[offset] == '+') {
      if (len == 1) {
        // nothing after sign
        throw new NumberFormatException(new String(in));
      }
      offset++;
    }

    long v = 0;
    short s;
    boolean decimalSeen = false;
    int decimalOffset = 0;

    for (int i = offset; i < len; i++) {
      char c = in[i];
      if (c >= '0' && c <= '9') {
        v = v * 10 + c - '0';
      } else if (c == '.' && !decimalSeen) {
        decimalOffset = i + 1;
        decimalSeen = true;
      } else if (c == ',') {
        // Ignore comma separators
      } else {
        throw new NumberFormatException("Not expecting '" + c
            + "' at position " + i);
      }
    }
    if (negative) {
      v = -v;
    }
    if (decimalSeen) {
      s = (short)(len - decimalOffset);
    } else {
      s = 0;
    }
    if (dc != null) {
      value = dc.adjustValue(v, s);
      scale = dc.decimalDigits;
    } else {
      value = v;
      scale = s;
    }
  }

  /**
   * Constructs a <tt>Decimal</tt> from a character array that contains a
   * representation of a decimal number, accepting the same sequence of
   * characters as the {@link #Decimal(String)} constructor.
   * 
   * <p>
   * Note that, if the sequence of characters is already available as a
   * character array, using this constructor is faster than converting the
   * <tt>char</tt> array to string and using the <tt>Decimal(String)</tt>
   * constructor .
   * 
   * @param in
   *          <tt>char</tt> array that is the source of characters.
   * @throws NumberFormatException
   *           if <tt>in</tt> is not a valid representation of a
   *           <tt>Decimal</tt>.
   */
  public Decimal(char in[]) {
    this(in, 0, in.length, null);
  }

  /**
   * Constructs a <tt>Decimal</tt> from a character array that contains a
   * representation of a decimal number, accepting the same sequence of
   * characters as the {@link #Decimal(String)} constructor and with rounding
   * according to the context settings.
   * 
   * <p>
   * Note that, if the sequence of characters is already available as a
   * character array, using this constructor is faster than converting the
   * <tt>char</tt> array to string and using the <tt>Decimal(String)</tt>
   * constructor .
   * 
   * <p>
   * Note that, unlike BigDecimal, the context specified here determines the
   * number of decimal digits, not the precision.
   * 
   * @param in
   *          <tt>char</tt> array that is the source of characters.
   * @param dc
   *          the context to use.
   * @throws ArithmeticException
   *           if the result is inexact but the rounding mode is
   *           <tt>UNNECESSARY</tt>.
   * @throws NumberFormatException
   *           if <tt>in</tt> is not a valid representation of a
   *           <tt>Decimal</tt>.
   */
  public Decimal(char[] in, DecimalContext dc) {
    this(in, 0, in.length, dc);
  }

/**
   * Constructs a <tt>Decimal</tt> from a string that contains a
   * representation of a decimal number.  The string representation consists
   * of an optional sign, <tt>'+'</tt> (<tt>'&#92;u002B'</tt>) or
   * <tt>'-'</tt> (<tt>'&#92;u002D'</tt>), followed by a sequence of
   * zero or more decimal digits ("the integer"), optionally
   * followed by a fraction.
   * 
   * <p>The fraction consists of a decimal point followed by zero
   * or more decimal digits.  The string must contain at least one
   * digit in either the integer or the fraction.  The number formed
   * by the sign, the integer and the fraction is referred to as the
   * <i>significand</i>.
   *
   * <p>More formally, the strings this constructor accepts are
   * described by the following grammar:
   * <blockquote>
   * <dl>
   * <dt><i>DecimalString:</i>
   * <dd><i>Sign<sub>opt</sub> Significand</i>
   * <p>
   * <dt><i>Sign:</i>
   * <dd><tt>+</tt>
   * <dd><tt>-</tt>
   * <p>
   * <dt><i>Significand:</i>
   * <dd><i>IntegerPart</i> <tt>.</tt> <i>FractionPart<sub>opt</sub></i>
   * <dd><tt>.</tt> <i>FractionPart</i>
   * <dd><i>IntegerPart</i>
   * <p>
   * <dt><i>IntegerPart:
   * <dd>Digits</i>
   * <p>
   * <dt><i>FractionPart:
   * <dd>Digits</i>
   * <p>
   * <dt><i>SignedInteger:
   * <dd>Sign<sub>opt</sub> Digits</i>
   * <p>
   * <dt><i>Digits:
   * <dd>Digit
   * <dd>Digits Digit</i>
   * <p>
   * <dt><i>Digit:</i>
   * <dd>any character for which {@link Character#isDigit}
   * returns <tt>true</tt>, including 0, 1, 2 ...
   * </dl>
   * </blockquote>
   *
   * <p>The scale of the returned <tt>Decimal</tt> will be the
   * number of digits in the fraction, or zero if the string
   * contains no decimal point.  The value of the scale
   * must lie between <tt>Decimal.MIN_SCALE</tt> and
   * <tt>Decimal.MAX_SCALE</tt>, inclusive.
   *
   * <p>The character-to-digit mapping is provided by {@link
   * java.lang.Character#digit} set to convert to radix 10.  The
   * String may not contain any extraneous characters (whitespace,
   * for example).
   *
   * <p>The allowed string representation is the same as BigDecimal
   * except that:
   * <ul>
   * <li>No exponent is allowed</li>
   * <li>The scale is limited to <tt>Decimal.MIN_SCALE</tt> and
   * <tt>Decimal.MAX_SCALE</tt> rather than <tt>Integer.MIN_VALUE</tt> and
   * <tt>Integer.MAX_VALUE</tt></li>
   * </ul>
   * 
   * <p><b>Examples:</b><br>
   * For each string on the left, the resulting representation
   * [<tt>Integer</tt>, <tt>scale</tt>] is shown on the right.
   * <pre>
   * "0"            [0,0]
   * "0.00"         [0,2]
   * "123"          [123,0]
   * "-123"         [-123,0]
   * "12.0"         [120,1]
   * "12.3"         [123,1]
   * "0.00123"      [123,5]
   * "-0"           [0,0]
   * </pre>
   *
   * @param val String representation of a decimal number.
   *
   * @throws NumberFormatException if <tt>val</tt> is not a valid 
   *         representation of a decimal number.
   */
  public Decimal(String val) {
    this(val.toCharArray(), 0, val.length(), null);
  }

  /**
   * Constructs a <tt>Decimal</tt> from a byte array that contains a
   * representation of a decimal number, accepting the same sequence of
   * bytes as described in the {@link #Decimal(String)} constructor, 
   * while allowing a 
   * sub-array to be specified and with rounding according to the context
   * settings.
   * 
   * <p>
   * This constructor is used when a byte array is available.  It is used
   * for constructing a <tt>Decimal</tt> from a byte buffer in a high 
   * performance manner.  
   * 
   * <p>
   * This constructor is not available in the <tt>BigDecimal</tt> class.
   * 
   * @param in
   *          <tt>byte</tt> array that is the source of characters.
   * @param offset
   *          first character in the array to inspect.
   * @param len
   *          number of characters to consider.
   * @param dc
   *          the context to use.
   * @throws ArithmeticException
   *           if the result is inexact but the rounding mode is
   *           <tt>UNNECESSARY</tt>.
   * @throws NumberFormatException
   *           if <tt>in</tt> is not a valid representation of a
   *           <tt>Decimal</tt> or the defined subarray is not wholly within
   *           <tt>in</tt>.
   */
  public Decimal(byte in[], int offset, int len, DecimalContext dc) {
    if (len <= 0) {
      throw new NumberFormatException(new String(in));
    }

    // Handle and step past sign
    int offsetEnd = offset + len;
    boolean negative = false;
    if (in[offset] == '-') {
      if (len == 1) {
        // nothing after sign
        throw new NumberFormatException(new String(in));
      }
      negative = true;
      offset++;
    } else if (in[offset] == '+') {
      if (len == 1) {
        // nothing after sign
        throw new NumberFormatException(new String(in));
      }
      offset++;
    }

    long v = 0;
    short s;
    boolean decimalSeen = false;
    int decimalOffset = 0;

    for (int i = offset; i < offsetEnd; i++) {
      byte c = in[i];
      if (c >= '0' && c <= '9') {
        v = v * 10 + c - '0';
      } else if (c == '.' && !decimalSeen) {
        decimalOffset = i + 1;
        decimalSeen = true;
      } else {
        throw new NumberFormatException("Not expecting '" + c
            + "' at position " + i);
      }
    }
    if (negative) {
      v = -v;
    }
    if (decimalSeen) {
      s = (short)(offsetEnd - decimalOffset);
    } else {
      s = 0;
    }
    if (dc != null) {
      value = dc.adjustValue(v, s);
      scale = dc.decimalDigits;
    } else {
      value = v;
      scale = s;
    }
  }
  
  
  public Decimal (BigDecimal value) {
    this (value.toPlainString());
  }

  
  /**
   * Constructs a Decimal from a <tt>double</tt>, rounding as necessary to avoid
   * errors arising from not being able to exactly represent decimal numbers in
   * a <tt>double</tt>'s binary floating-point value. The scale of the returned
   * <tt>Decimal</tt> is the smallest value such that
   * <tt>(10<sup>scale</sup> &times; val)</tt> is an integer after rounding has
   * been applied.
   * <p>
   * 
   * @param val
   *          <tt>double</tt> value to be converted to <tt>Decimal</tt>.
   * @throws NumberFormatException
   *           if <tt>val</tt> is infinite or NaN.
   */
  public Decimal(double val) {
    short s = 0;
    boolean negative = false;
    if (val < 0) {
      val = -val;
      negative = true;
    }
    long v = (long) (val + 0.5);
    double tolerance = 0.0000000001;
    double error = val - v;
    while (s < 10 && (error < -tolerance || error > tolerance)) {
      s++;
      val *= 10.0;
      v = (long) (val + 0.5);
      error = val - v;
      tolerance *= 10.0;
    }
    if (negative) {
      value = -v;
    } else {
      value = v;
    }
    scale = s;
  }

  /**
   * Constructs a <tt>Decimal</tt> from a <tt>double</tt>, with rounding
   * according to the context settings. The scale of the <tt>Decimal</tt> is the
   * smallest value such that <tt>(10<sup>scale</sup> &times; val)</tt> is an
   * integer.
   * 
   * @param val
   *          <tt>double</tt> value to be converted to <tt>Decimal</tt>.
   * @param dc
   *          the context to use.
   * @throws ArithmeticException
   *           if the result is inexact but the RoundingMode is UNNECESSARY.
   * @throws NumberFormatException
   *           if <tt>val</tt> is infinite or NaN.
   */
  public Decimal(double val, DecimalContext dc) {
    if (val == 0) {
      value = 0;
      scale = dc.decimalDigits;
      return;
    }
    boolean negative = false;
    if (val < 0) {
      val = -val;
      negative = true;
    }
    long v;
    long bits = Double.doubleToLongBits(val);
    long vx = (bits & 0x000fffffffffffffL) | 0x0010000000000000L;
    int exp = ((int)(bits >>> (13 * 4)) & 0x7ff) - 1023 - (13 * 4);
    long divisor = (long)Math.pow(2, -exp);
    if (dc.decimalDigits >= 0) {
      v = DecimalContext.divideValue(vx * scalex[dc.decimalDigits], divisor, dc.roundingMode);
    } else {
      v = DecimalContext.divideValue(vx, divisor * scalex[-dc.decimalDigits], dc.roundingMode);
    }
    scale = dc.decimalDigits;
    if (negative) {
      value = -v;
    } else {
      value = v;
    }
  }

  /**
   * Constructs a <tt>Decimal</tt> from an <tt>int</tt>. The scale of the
   * <tt>Decimal</tt> is zero.
   * 
   * @param value
   *          <tt>int</tt> value to be converted to <tt>Decimal</tt>.
   */
  public Decimal(int value) {
    this.value = value;
    this.scale = 0;
  }

  /**
   * Constructs a <tt>Decimal</tt> from a <tt>long</tt>. The scale of the
   * <tt>Decimal</tt> is zero.
   * 
   * @param value
   *          <tt>long</tt> value to be converted to <tt>Decimal</tt>.
   */
  public Decimal(long value) {
    this.value = value;
    this.scale = 0;
  }

  private Decimal(long unscaledValue, int scale) {
    this.value = unscaledValue;
    this.scale = (short)scale;
  }

  /**
   * Returns a <tt>Decimal</tt> whose value is <tt>(this +
   * augend)</tt>, and whose scale is <tt>max(this.scale(),
   * augend.scale())</tt>.
   * 
   * @param augend
   *          value to be added to this <tt>Decimal</tt>.
   * @return <tt>this + augend</tt>
   */
  public Decimal add(Decimal augend) {
    // Optimisation when adding 0
    if (this.value == 0) {
      return augend;
    }
    if (augend.value == 0) {
      return this;
    }

    long v;
    int s;
    // Align left or right, depending on which scale is larger
    if (this.scale == augend.scale) {
      v = this.value + augend.value;
      s = this.scale;
    } else {
      int adj = augend.scale - this.scale;
      if (adj > 0) {
        v = this.value * scalex[adj] + augend.value;
        s = augend.scale;
      } else {
        v = this.value + augend.value * scalex[-adj];
        s = this.scale;
      }
    }
    return new Decimal(v, s);
  }

  /**
   * Returns a <tt>Decimal</tt> whose value is <tt>(this -
   * subtrahend)</tt>, and whose scale is <tt>max(this.scale(),
   * subtrahend.scale())</tt>.
   * 
   * @param subtrahend
   *          value to be subtracted from this <tt>Decimal</tt>.
   * @return <tt>this - subtrahend</tt>
   */
  public Decimal subtract(Decimal subtrahend) {
    /* Optimisation when subtracting 0 */
    if (subtrahend.value == 0) {
      return this;
    }

    long v;
    int s;
    /* Align left or right, depending on which scale is larger */
    if (this.scale == subtrahend.scale) {
      v = this.value - subtrahend.value;
      s = this.scale;
    } else {
      int adj = subtrahend.scale - this.scale;
      if (adj > 0) {
        v = this.value * scalex[adj] - subtrahend.value;
        s = subtrahend.scale;
      } else {
        v = this.value - subtrahend.value * scalex[-adj];
        s = this.scale;
      }
    }
    return new Decimal(v, s);
  }

  /**
   * Returns a <tt>Decimal</tt> whose value is <tt>(this &times;
   * multiplicand)</tt>, and whose scale is <tt>(this.scale() +
   * multiplicand.scale())</tt>.
   * 
   * @param multiplicand
   *          value to be multiplied by this <tt>Decimal</tt>.
   * @return <tt>this * multiplicand</tt>
   */
  public Decimal multiply(Decimal multiplicand) {
    long v = this.value * multiplicand.value;
    int s = this.scale + multiplicand.scale;
    // Normalise value
    if (v == 0) {
      return Decimal.ZERO;
    }
    if (s > 0) {
      while (v % 10 == 0) {
        v /= 10;
        s--;
      }
    }
    return new Decimal(v, s);
  }

  /**
   * Returns a <tt>Decimal</tt> whose value is <tt>(this &times;
   * multiplicand)</tt>, with rounding according to the context settings.
   * 
   * <p>
   * Note that, unlike BigDecimal, the context specified here determines the
   * number of decimal digits, not the precision.
   * 
   * @param multiplicand
   *          value to be multiplied by this <tt>BigDecimal</tt>.
   * @param dc
   *          the context to use.
   * @return <tt>this * multiplicand</tt>, rounded as necessary.
   * @throws ArithmeticException
   *           if the result is inexact but the rounding mode is
   *           <tt>UNNECESSARY</tt>.
   */
  public Decimal multiply(Decimal multiplicand, DecimalContext dc) {
    long v = this.value * multiplicand.value;
    int s = this.scale + multiplicand.scale;
    return new Decimal(dc.adjustValue(v, s), dc.decimalDigits);
  }

  public Decimal multiply(Decimal multiplicand, int decimalDigits) {
    long v = this.value * multiplicand.value;
    int s = this.scale + multiplicand.scale;
    return new Decimal(DecimalContext.adjustValue(v, s, decimalDigits, RoundingMode.HALF_EVEN), decimalDigits);
  }

  /**
   * Returns a <tt>Decimal</tt> whose value is <tt>(this &times;
   * multiplicand)</tt>, and whose scale is <tt>this.scale()</tt>. This method
   * is more efficient than {@link #multiply(Decimal)} when the multiplicand is
   * an integer.
   * <p>
   * This method is not available in <tt>BigDecimal</tt>.
   * 
   * @param multiplicand
   *          value to be multiplied by this <tt>Decimal</tt>.
   * @return <tt>this * multiplicand</tt>
   */
  public Decimal multiply(long multiplicand) {
    if (multiplicand == 0) {
      return Decimal.ZERO;
    }
    if (multiplicand == 1) {
      return this;
    }
    return new Decimal(value * multiplicand, scale);
  }

  /**
   * Returns a <tt>Decimal</tt> whose value is <tt>(this /
   * divisor)</tt>, and whose scale is as specified. If rounding must be
   * performed to generate a result with the specified scale, the specified
   * rounding mode is applied.
   * 
   * @param divisor
   *          value by which this <tt>Decimal</tt> is to be divided.
   * @param decimalDigits
   *          the scale to use.
   * @param roundingMode
   *          the rounding mode to use if necessary.
   * @return <tt>this / divisor</tt>
   * @throws ArithmeticException
   *           if <tt>divisor</tt> is zero,
   *           <tt>roundingMode==RoundingMode.UNNECESSARY</tt> and the specified
   *           scale is insufficient to represent the result of the division
   *           exactly.
   */
  public Decimal divide(Decimal divisor, int decimalDigits,
      RoundingMode roundingMode) {
    long leftValue = this.value;
    long rightValue = divisor.value;
    if (divisor.scale > 0) {
      leftValue *= scalex[divisor.scale];
    } else if (divisor.scale < 0) {
      rightValue *= scalex[-divisor.scale];
    }
    return divide(leftValue, this.scale, rightValue, decimalDigits,
        roundingMode);
  }

  /**
   * Returns a <tt>Decimal</tt> whose value is <tt>(this /
   * divisor)</tt>, with rounding according to the context settings.
   * 
   * The BigDecimal class does not support this method.
   * 
   * @param divisor
   *          value by which this <tt>Decimal</tt> is to be divided.
   * @param dc
   *          the context to use.
   * @return <tt>this / divisor</tt>
   * @throws ArithmeticException
   *           if <tt>divisor</tt> is zero, or if the <tt>roundingMode</tt>
   *           is <tt>RoundingMode.UNNECESSARY</tt> and the specified
   *           scale is insufficient to represent the result of the division
   *           exactly.
   */
  public Decimal divide(Decimal divisor, DecimalContext dc) {
    return divide(divisor, dc.decimalDigits, dc.roundingMode);
  }

  public Decimal divide(Decimal divisor, int decimalDigits) {
    return divide(divisor, decimalDigits, RoundingMode.HALF_EVEN);
  }


  /**
   * Returns a <code>Decimal</code> whose value is <code>this / divisor</code>,
   * with rounding according to the context settings.
   * 
   * @param divisor
   *          The integer value that is the right hand side of the division.
   * @param dc
   *          the context to use.
   * @return <tt>this / divisor</tt>
   * @throws ArithmeticException
   *           if <tt>divisor</tt> is zero, or if the <tt>roundingMode</tt>
   *           is <tt>RoundingMode.UNNECESSARY</tt> and the specified
   *           scale is insufficient to represent the result of the division
   *           exactly.
   */
  public Decimal divide(long divisor, DecimalContext dc) {
    return divide(value, scale, divisor, dc.decimalDigits, dc.roundingMode);
  }

  public Decimal divide(long divisor, RoundingMode rm) {
    return divide(value, scale, divisor, scale, rm);
  }

  public Decimal divide(long divisor) {
    return divide(value, scale, divisor, scale, RoundingMode.HALF_EVEN);
  }

  /**
   * Returns a <code>Decimal</code> whose value is <code>this / divisor</code>,
   * with rounding to the specified number of decimal digits and the rounding
   * mode.
   * 
   * @param divisor
   *          The integer value that is the right hand side of the division.
   * @param decimalDigits
   *          The number of decimal digits to round the result to.
   * @param roundingMode
   *          The rounding mode to use.
   * @return <tt>this / divisor</tt>
   * @throws ArithmeticException
   *           if <tt>divisor</tt> is zero, or if the <tt>roundingMode</tt>
   *           is <tt>RoundingMode.UNNECESSARY</tt> and the specified
   *           scale is insufficient to represent the result of the division
   *           exactly.
   */
  public Decimal divide(long divisor, int decimalDigits,
      RoundingMode roundingMode) {
    return divide(value, scale, divisor, decimalDigits, roundingMode);
  }
  

  private static Decimal divide(long leftValue, int leftScale, long rightValue,
      int decimalDigits, RoundingMode roundingMode) {
    if (rightValue == 0) {
      throw new ArithmeticException("Divide by zero");
    }
    // Scale up dividend according to the number of decimal digits we want as a
    // result.
    int adj = decimalDigits - leftScale;
    if (adj > 0) {
      leftValue *= scalex[adj];
      leftScale = decimalDigits;
    }
    long v = DecimalContext.divideValue(leftValue, rightValue, roundingMode);

    int s = leftScale;
    if (s > decimalDigits) {
      v = DecimalContext.adjustValue(v, s - decimalDigits, roundingMode);
      s = decimalDigits;
    }

    // Normalise value
    if (v == 0) {
      s = 0;
    } else if (s > 0) {
      while (v % 10 == 0) {
        v /= 10;
        s--;
      }
    }
    return new Decimal(v, s);
  }

  /**
   * Returns a plain <code>Decimal</code> whose value is the remainder of
   * <code>this/rhs</code>, using fixed point arithmetic.
   * <p>
   * 
   * @param divisor
   *          The <code>Decimal</code> for the right hand side of the remainder
   *          operation.
   * @return A <code>Decimal</code> whose value is the remainder of
   *         <code>this/rhs</code>, using fixed point arithmetic.
   * @throws ArithmeticException
   *           if <code>rhs</code> is zero.
   */
  public Decimal remainder(long divisor) {
    long v = value;
    int s = scale;
    if (s != 0) {
      if (scale > 0) {
        divisor *= scalex[s];
      } else {
        v *= scalex[-scale];
        s = 0;
      }
    }
    return new Decimal(v % divisor, s);
  }

  /**
   * Returns a <code>Decimal</code> whose value is the remainder of
   * <code>this/rhs</code>.
   * <p>
   * Implements the remainder operator (as defined in the decimal documentation,
   * see {@link Decimal class header}), and returns the result as a
   * <code>Decimal</code> object.
   * <p>
   * This is not the modulo operator -- the result may be negative.
   * 
   * @param divisor
   *          The <code>Decimal</code> for the right hand side of the remainder
   *          operation.
   * @param context
   *          The <code>DecimalContext</code> arithmetic settings.
   * @return A <code>Decimal</code> whose value is the remainder of
   *         <code>this+rhs</code>.
   * @throws ArithmeticException
   *           if <code>rhs</code> is zero.
   * @throws ArithmeticException
   *           if the integer part of the result will not fit in the number of
   *           digits specified for the context.
   */
  public Decimal remainder(long divisor, DecimalContext context) {
    long v = value;
    int s = scale;
    if (s != 0) {
      if (scale > 0) {
        divisor *= scalex[s];
      } else {
        v *= scalex[-scale];
        s = 0;
      }
    }
    return new Decimal(context.adjustValue(v % divisor, s), context.decimalDigits);
  }

  /**
   * Returns a <code>Decimal</code> whose value is the absolute value of this
   * <code>Decimal</code>, and whose scale is <code>this.scale()</code>.
   * 
   * Note that the method <code>abs(DecimalContext)</code> has not been provided
   * because its use is very rare. <code>abs().round(DecimalContext)
   * </code> can be used instead.
   * 
   * @return <code>abs(this)</code>
   */
  public Decimal abs() {
    if (value < 0) {
      return new Decimal(-value, scale);
    } else {
      return this;
    }
  }

  /**
   * Returns a <code>Decimal</code> whose value is <code>(-this)</code>, and
   * whose scale is <code>this.scale()</code>.
   * 
   * Note that the method <code>negate(DecimalContext)</code> has not been
   * provided because its use is very rare. <code>negate().round(DecimalContext)
   * </code> can be used instead.
   * 
   * @return <code>-this</code>.
   */
  public Decimal negate() {
    if (value == 0) {
      return this;
    } else {
      return new Decimal(-value, scale);
    }
  }

 /**
   * Compares this <code>Decimal</code> to another.
   * <p>
   * Implements numeric comparison, (as defined in the decimal documentation,
   * see {@link Decimal class header}), and returns a result of type
   * <code>int</code>.
   * <p>
   * The result will be:
   * <table cellpadding=2>
   * <tr>
   * <td align=right><b>-1</b></td>
   * <td>if the current object is less than the first parameter</td>
   * </tr>
   * <tr>
   * <td align=right><b>0</b></td>
   * <td>if the current object is equal to the first parameter</td>
   * </tr>
   * <tr>
   * <td align=right><b>1</b></td>
   * <td>if the current object is greater than the first parameter.</td>
   * </tr>
   * </table>
   * <p>
   * Note that, the comparison is done as if both this <code>Decimal</code> and
   * the rhs were rounded (as necessary) according to the context. This means,
   * that given a context of <code>Decimals(0, ROUND_HALF_EVEN)</code>, the 1.9
   * and 2.2 would compare equal and this method would return 0. If this is not
   * what is desired, use {@link #compareTo(Decimal)} instead.
   * 
   * @param rhs
   *          The <code>Decimal</code> for the right hand side of the
   *          comparison.
   * @param context
   *          The <code>DecimalContext</code> which determines granularity of
   *          this comparison.
   * @return An <code>int</code> whose value is -1, 0, or 1 as <code>this</code>
   *         is numerically less than, equal to, or greater than
   *         <code>rhs</code>.
   * @see #compareTo(Object)
   */
  public int compareInContext(Decimal rhs, DecimalContext context) {
    Decimal lhs = this; /* name for clarity. */

    /* Get both values according to the context. */
    long leftValue = context.adjustValue(lhs.value, lhs.scale);
    long rightValue = context.adjustValue(rhs.value, rhs.scale);
    if (leftValue == rightValue) {
      return 0;
    } else if (leftValue < rightValue) {
      return -1;
    } else {
      return +1;
    }
  }

  /**
   * Compares this <code>Decimal</code> to another. Two Decimal objects that are
   * equal in value but have a different scale (like 2.0 and 2.00) are
   * considered equal by this method.
   * <p>
   * This method is provided in preference to individual methods for each of the
   * six boolean comparison operators (<, ==, >, >=, !=, <=). The suggested
   * idiom for performing these comparisons is: (x.compareTo(y)
   * <i>&lt;op&gt;</i> 0), where <i>&lt;op&gt;</i> is one of the six comparison
   * operators.
   * 
   * @param rhs
   *          The <code>Decimal</code> for the right hand side of the
   *          comparison.
   * @return An <code>int</code> whose value is -1, 0, or 1 as <code>this</code>
   *         is numerically less than, equal to, or greater than
   *         <code>rhs</code>.
   * @see #compareTo(Object)
   */
  public int compareTo(Decimal rhs) {
    Decimal lhs = this; /* name for clarity. */

    long leftValue;
    long rightValue;
    /* Adjust left or right, depending on which scale is larger */
    if (lhs.scale == rhs.scale) {
      leftValue = lhs.value;
      rightValue = rhs.value;
    } else {
      int adj = rhs.scale - lhs.scale;
      if (adj > 0) {
        leftValue = lhs.value * scalex[adj];
        rightValue = rhs.value;
      } else {
        leftValue = lhs.value;
        rightValue = rhs.value * scalex[-adj];
      }
    }
    if (leftValue == rightValue) {
      return 0;
    } else if (leftValue < rightValue) {
      return -1;
    } else {
      return +1;
    }
  }

  /**
   * Tests if this Decimal is negative or not.
   * 
   * @return <code>true</code> if this Decimal is negative, or <code>false</code>
   *         otherwise.
   */
  public boolean isNegative() {
    return value < 0;
  }

  /**
   * Tests if this Decimal is zero or not.
   * 
   * @return <code>true</code> if this Decimal is zero, or <code>false</code>
   *         otherwise.
   */
  public boolean isZero() {
    return value == 0;
  }

  /**
   * Returns a <code>Decimal</code> whose value is the maximum of
   * <code>this</code> and <code>rhs</code>.
   * 
   * @param rhs
   *          The <code>Decimal</code> for the right hand side of the
   *          comparison.
   * @return The maximum of <code>this</code> and <code>rhs</code>.
   */
  public Decimal max(Decimal rhs) {
    if (this.compareTo(rhs) < 0) {
      return rhs;
    } else {
      return this;
    }
  }

  /**
   * Returns a <code>Decimal</code> whose value is the minimum of
   * <code>this</code> and <code>rhs</code>.
   * 
   * @param rhs
   *          The <code>Decimal</code> for the right hand side of the
   *          comparison.
   * @return The minimum of <code>this</code> and <code>rhs</code>.
   */
  public Decimal min(Decimal rhs) {
    if (this.compareTo(rhs) < 0) {
      return this;
    } else {
      return rhs;
    }
  }

  /**
   * Returns a <code>Decimal</code> whose value is <code>+this</code>.
   * <code>Decimal</code>. The scale is not changed.
   * 
   * @return <code>this</code>.
   */
  public Decimal plus() {
    return this;
  }

  /**
   * Returns a <code>Decimal</code> whose value is (<code>+this</code>), scaled
   * and rounded according to the context settings.
   * <p>
   * 
   * @param dc
   *          The <code>DecimalContext</code> settings.
   * @return <code>this</code>, scaled and rounded as necessary.
   */
  public Decimal round(DecimalContext dc) {
    if (scale <= dc.decimalDigits) {
      // No rounding necessary, so return the same object
      return this;
    } else {
      return new Decimal(dc.adjustValue(value, scale), dc.decimalDigits);
    }
  }

  /**
   * Converts this <code>Decimal</code> to a <code>double</code>. If the
   * <code>Decimal</code> is out of the possible range for a <code>double</code>
   * (64-bit signed floating point) result then an
   * <code>ArithmeticException</code> is thrown.
   * <p>
   * The double produced is identical to result of expressing the
   * <code>Decimal</code> as a <code>String</code> and then converting it using
   * the <code>Double(String)</code> constructor; this can result in values of
   * <code>Double.NEGATIVE_INFINITY</code> or
   * <code>Double.POSITIVE_INFINITY</code>.
   * 
   * @return A <code>double</code> corresponding to <code>this</code>.
   */
  @Override
  public double doubleValue() {
    double d = value;
    if (scale > 0) {
      d /= scalex[scale];
    } else if (scale < 0) {
      d *= scalex[-scale];
    }
    return d;
  }

  /**
   * Compares this <code>Decimal</code> with <code>rhs</code> for equality.
   * <p>
   * If the parameter is <code>null</code>, or is not an instance of the Decimal
   * type, or is not exactly equal to the current <code>Decimal</code> object,
   * then <i>false</i> is returned. Otherwise, <i>true</i> is returned.
   * <p>
   * "Exactly equal", here, means that the <code>String</code> representations
   * of the <code>Decimal</code> numbers are identical (they have the same
   * characters in the same sequence).
   * <p>
   * The {@link #compareInContext(Decimal, DecimalContext)} method should be
   * used for more general comparisons.
   * 
   * @param obj
   *          The <code>Object</code> for the right hand side of the comparison.
   * @return A <code>boolean</code> whose value <i>true</i> if and only if the
   *         operands have identical string representations.
   * @throws ClassCastException
   *           if <code>rhs</code> cannot be cast to a <code>Decimal</code>
   *           object.
   * @see #compareTo(Object)
   * @see #compareTo(Decimal)
   * @see #compareInContext(Decimal, DecimalContext)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      // The are equal if they are the same object
      return true;
    }
    if (obj == null || !(obj instanceof Decimal)) {
      // A Decimal is never equal to null, and never equal to something
      // that is not a Decimal
      return false;
    }

    Decimal lhs = this;
    Decimal rhs = (Decimal) obj;

    /* Check for zero cases. This simplifies normalisation later. */
    if (lhs.value == 0) {
      return rhs.value == 0;
    } else if (rhs.value == 0) {
      return false;
    }
    /* Normalise left and right sides. */
    long leftValue = lhs.value;
    int leftScale = lhs.scale;
    /* Normalise the value to remove any trailing zero digits. */
    while ((leftValue % 10) == 0) {
      leftValue = leftValue / 10;
      leftScale--;
    }
    long rightValue = rhs.value;
    int rightScale = rhs.scale;
    /* Normalise the value to remove any trailing zero digits. */
    while ((rightValue % 10) == 0) {
      rightValue = rightValue / 10;
      rightScale--;
    }
    return (leftValue == rightValue && leftScale == rightScale);
  }

  /**
   * Converts this <code>Decimal</code> to a <code>float</code>. If the
   * <code>Decimal</code> is out of the possible range for a <code>float</code>
   * (32-bit signed floating point) result then an
   * <code>ArithmeticException</code> is thrown.
   * <p>
   * The float produced is identical to result of expressing the
   * <code>Decimal</code> as a <code>String</code> and then converting it using
   * the <code>Float(String)</code> constructor; this can result in values of
   * <code>Float.NEGATIVE_INFINITY</code> or
   * <code>Float.POSITIVE_INFINITY</code>.
   * 
   * @return A <code>float</code> corresponding to <code>this</code>.
   */
  @Override
  public float floatValue() {
    float f = value;
    if (scale > 0) {
      f /= scalex[scale];
    } else if (scale < 0) {
      f *= scalex[scale];
    }
    return f;
  }

  /**
   * Returns the hashcode for this <code>Decimal</code>. This hashcode is
   * suitable for use by the <code>java.util.Hashtable</code> class.
   * <p>
   * Note that two <code>Decimal</code> objects will produce the same hashcode
   * if they have the same numeric value, regardless of the scale used.
   * 
   * @return An <code>int</code> that is the hashcode for <code>this</code>.
   */
  @Override
  public int hashCode() {
    if (value == 0) {
      return 0;
    } else {
      long v = value;
      int s = scale;
      /* Normalise the value to remove any trailing zero digits. */
      while ((v % 10) == 0) {
        v = v / 10;
        s--;
      }
      return ((int) v) * 31 + s;
    }
  }

  /**
   * Converts this <code>Decimal</code> to an <code>int</code>. If the
   * <code>Decimal</code> has a non-zero decimal part or is out of the possible
   * range for an <code>int</code> (32-bit signed integer) result then an
   * <code>ArithmeticException</code> is thrown.
   * 
   * @return An <code>int</code> equal in value to <code>this</code>.
   * @throws ArithmeticException
   *           if <code>this</code> has a non-zero decimal part, or will not fit
   *           in an <code>int</code>.
   */
  @Override
  public int intValue() {
    long v;
    if (scale > 0) {
      v = value / scalex[scale];
    } else if (scale < 0) {
      v = value * scalex[-scale];
    } else {
      v = value;
    }
    if (v < Integer.MIN_VALUE || v > Integer.MAX_VALUE) {
      throw new ArithmeticException("Value exceeds range of int");
    }
    return (int) v;
  }

  /**
   * Converts this <code>Decimal</code> to a <code>long</code>. If the
   * <code>Decimal</code> has a non-zero decimal part it is discarded.
   * 
   * @return A <code>long</code> converted from <code>this</code>, truncated if
   *         necessary.
   */
  @Override
  public long longValue() {
    long v;
    if (scale > 0) {
      v = value / scalex[scale];
    } else if (scale < 0) {
      v = value * scalex[-scale];
    } else {
      v = value;
    }
    return v;
  }

  /**
   * Returns a plain <code>Decimal</code> whose decimal point has been moved to
   * the left by a specified number of positions. The parameter, <code>n</code>,
   * specifies the number of positions to move the decimal point. That is, if
   * <code>n</code> is 0 or positive, the number returned is given by:
   * <p>
   * <code>
   * this.multiply(TEN.pow(new Decimal(-n)))
   * </code>
   * <p>
   * <code>n</code> may be negative, in which case the method returns the same
   * result as <code>movePointRight(-n)</code>.
   * 
   * @param n
   *          The <code>int</code> specifying the number of places to move the
   *          decimal point leftwards.
   * @return A <code>Decimal</code> derived from <code>this</code>, with the
   *         decimal point moved <code>n</code> places to the left.
   */

  public Decimal movePointLeft(int n) {
    // Very little point in optimising for shift of 0.
    return new Decimal(value, scale + n);
  }

  public Decimal movePointLeft(int n, DecimalContext context) {
    if (n < 0) {
      return new Decimal(value, scale + n);
    } else {
      return new Decimal(context.adjustValue(value, scale + n),
          context.decimalDigits);
    }
  }

  /**
   * Returns a plain <code>Decimal</code> whose decimal point has been moved to
   * the right by a specified number of positions. The parameter, <code>n</code>,
   * specifies the number of positions to move the decimal point. That is, if
   * <code>n</code> is 0 or positive, the number returned is given by:
   * <p>
   * <code>
   * this.multiply(TEN.pow(new Decimal(n)))
   * </code>
   * <p>
   * <code>n</code> may be negative, in which case the method returns the same
   * result as <code>movePointLeft(-n)</code>.
   * 
   * @param n
   *          The <code>int</code> specifying the number of places to move the
   *          decimal point rightwards.
   * @return A <code>Decimal</code> derived from <code>this</code>, with the
   *         decimal point moved <code>n</code> places to the right.
   */
  public Decimal movePointRight(int n) {
    return new Decimal(value, scale - n);
  }

  public Decimal movePointRight(int n, DecimalContext context) {
    if (n < 0) {
      return new Decimal(context.adjustValue(value, scale - n),
          context.decimalDigits);
    } else {
      return new Decimal(value, scale - n);
    }
  }

  /**
   * Converts this <code>Decimal</code> to a <code>short</code>. If the
   * <code>Decimal</code> has a non-zero decimal part or is out of the possible
   * range for a <code>short</code> (16-bit signed integer) result then an
   * <code>ArithmeticException</code> is thrown.
   * 
   * @return A <code>short</code> equal in value to <code>this</code>.
   * @throws ArithmeticException
   *           if <code>this</code> has a non-zero decimal part, or will not fit
   *           in a <code>short</code>.
   */
  @Override
  public short shortValue() {
    long v;
    if (scale > 0) {
      v = value / scalex[scale];
    } else if (scale < 0) {
      v = value * scalex[-scale];
    } else {
      v = value;
    }
    if (v < Short.MIN_VALUE || v > Short.MAX_VALUE) {
      throw new ArithmeticException("Value exceeds range of short");
    }
    return (short) v;
  }

  
  public Decimal setScale(int sx) {
    DecimalContext requiredScale = new DecimalContext(sx, RoundingMode.UNNECESSARY);
    long v = requiredScale.adjustValue(value, scale);
    return new Decimal(v, sx);
  }

  
  public int getScale () {
    return scale;
  }
  
  
  public long toRawLong () {
    return value;
  }
  
  
  /**
   * Returns the sign of this <code>Decimal</code>, as an <code>int</code>. This
   * returns the <i>signum</i> function value that represents the sign of this
   * <code>Decimal</code>. That is, -1 if the <code>Decimal</code> is negative,
   * 0 if it is numerically equal to zero, or 1 if it is positive.
   * 
   * @return An <code>int</code> which is -1 if the <code>Decimal</code> is
   *         negative, 0 if it is numerically equal to zero, or 1 if it is
   *         positive.
   */
  public int signum() {
    if (value == 0) {
      return 0;
    } else if (value < 0) {
      return -1;
    } else {
      return +1;
    }
  }

  
  /** 
   * Trims trailing 0's from the decimal number by adjusting the scale.
   */
  public Decimal trim () {
    return trim(0);
  }
  
  
  /** 
   * Trims trailing 0's from the decimal number by adjusting the scale.
   */
  public Decimal trim (int s1) {
    if (this.scale == s1) {
      return this;
    }
    long v = this.value;
    int s = this.scale;
    while (s < s1) {
      v = v * 10;
      s++;
    }
    /* Normalise the value to remove any trailing zero digits. */
    while (s > s1 && (v % 10) == 0) {
      v = v / 10;
      s--;
    }
    return new Decimal(v, s);
  }
  
  
  public Decimal uplift (Decimal factor, DecimalContext dc) {
    return this.divide(factor.add(Decimal.ONE), dc).subtract(this);
  }
  
  
  public Decimal upliftedBase (Decimal factor, DecimalContext dc) {
    return this.divide(factor.add(Decimal.ONE), dc);
  }
  
  
  public Decimal upliftedBase (Decimal factor, int decimalDigits) {
    return this.divide(factor.add(Decimal.ONE), decimalDigits);
  }
  
  
  public void components (long[] x) {
    if (scale <= 0) {
      x[0] = value;
      x[1] = scale;
    }
    long v = this.value;
    int s = this.scale;
    /* Normalise the value to remove any trailing zero digits. */
    while (s > 0 && (v % 10) == 0) {
      v = v / 10;
      s--;
    }
    x[0] = v;
    x[1] = s;
  }
  
  
  public String toString (int minFraction, int maxFraction, RoundingMode roundingMode, String[] negIndicators) {
    StringBuilder buffer = new StringBuilder(36);
    int scaleAdj = scale - maxFraction;
    long v = value;
    if (value < 0) {
      v = -value;
    }
    if (scaleAdj < 0) {
      /* No rounding issues */
      v *= scalex[-scaleAdj];
    } else if (scaleAdj > 0) {
      v = DecimalContext.divideValue(v, scalex[scaleAdj], roundingMode);
    }
    if (maxFraction < 0) {
      v *= scalex[-maxFraction];
    } else {
      while (minFraction < maxFraction && v % 10 == 0) {
        v /= 10;
        maxFraction--;
      }
    }
    buffer.append(v);
    while (buffer.length() < minFraction) {
      buffer.insert(0, '0');
    }
    if (maxFraction > 0) {
      int i = buffer.length() - maxFraction;
      buffer.insert(i, '.');
      if (i == 0) {
        buffer.insert(0, '0');
      }
    }
    if (value < 0) {
      buffer.insert(0, negIndicators[0]);
      buffer.append(negIndicators[1]);
    }
    return buffer.toString();
  }
  
  
  /**
   * Returns the <code>Decimal</code> as a <code>String</code>. This returns a
   * <code>String</code> that exactly represents this <code>Decimal</code>, as
   * defined in the decimal documentation (see {@link Decimal class header}).
   * <p>
   * By definition, using the {@link #Decimal(String)} constructor on the result
   * <code>String</code> will create a <code>Decimal</code> that is exactly
   * equal to the original <code>Decimal</code>.
   * 
   * @return The <code>String</code> exactly corresponding to this
   *         <code>Decimal</code>.
   */
  @Override
  public String toString() {
    StringBuffer buffer = new StringBuffer(36);
    boolean negative = false;
    long v = value;
    if (v < 0) {
      negative = true;
      v = -v;
    }
    buffer.append(v);
    if (scale < 0) {
      int s = scale;
      while (s < 0) {
        buffer.append('0');
        s++;
      }
    } else if (scale > 0) {
      if (buffer.length() <= scale) {
        while (buffer.length() < scale) {
          buffer.insert(0, '0');
        }
        buffer.insert(0, "0.");
      } else {
        buffer.insert(buffer.length() - scale, '.');
      }
    }
    if (negative) {
      buffer.insert(0, '-');
    }
    return buffer.toString();
  }

  
  /**
   * Converts a <code>double</code> to a <code>Decimal</code>. This
   * "static factory method" is provided in preference to a Decimal(double)
   * constructor because it allows for reuse of frequently used Decimal values
   * such as ZERO, ONE, etc.
   * <p>
   * If the double is a whole number, the Decimal value returned will be the
   * same as the double and the scale will be zero.
   * <p>
   * If the double has decimal digits, the Decimal value returned will be the 
   * closest decimal number.  An exact conversion may not be possible so this
   * method should be used with care.  
   * 
   * @param d
   *          The <code>double</code> to be converted.
   * @return The <code>Decimal</code> equal in value to <code>d</code>, or a
   *         close approximation of.
   * @throws NumberFormatException
   *           if the parameter is infinite or not a number.
   */
  public static Decimal valueOf(double d) {
    if (d == 0) {
      return ZERO;
    } else if (d == 1.0) {
      return ONE;
    } else if (d == 10.0) {
      return TEN;
    } else if (d == 100.0) {
      return HUNDRED;
    } else if (d == Double.NaN || d == Double.NEGATIVE_INFINITY || d == Double.POSITIVE_INFINITY) {
      throw new NumberFormatException();
    } else {
      return new Decimal(d);
    }
  }

  /**
   * Translates a <code>double</code> to a <code>long</code>. This
   * "static factory method" is provided in preference to a Decimal(long)
   * constructor because it allows for reuse of frequently used Decimal values
   * such as ZERO, ONE, etc.
   * <p>
   * The Decimal value returned has the same value as the long, with a scale of
   * zero.
   * 
   * @param lint
   *          The <code>long</code> to be translated.
   * @return The <code>Decimal</code> equal in value to <code>lint</code>.
   */
  public static Decimal valueOf(long lint) {
    if (lint == 0) {
      return ZERO;
    } else if (lint == 1) {
      return ONE;
    } else if (lint == 10) {
      return TEN;
    } else if (lint == 100) {
      return HUNDRED;
    } else {
      return new Decimal(lint, 0);
    }
  }

  
  public static Decimal parse (String s) throws NumberFormatException {
    return new Decimal(s);
  }
  
}
