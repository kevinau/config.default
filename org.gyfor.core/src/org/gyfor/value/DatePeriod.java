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
package org.gyfor.value;


import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.List;

import org.gyfor.time.DayDate;


/** <p>Validate a DatePeriod rule.  A DatePeriod rule consists of one or
 *  more segments separated by comma (,).  Each segment consists of
 *  a adjustment amount followed by zero, 1 or 2 alignment amounts.</p>
 *  <p>Possible adjustment amounts are as follows:</p> 
 *  <table>
 *    <tr>
 *      <th>Adjustment amount</th>
 *      <th>Description</th>
 *    </tr>
 *    <tr>
 *      <td>&plusmn;nM</td>
 *      <td>Add/subtract n months.  This adjustment amount must be followed
 *          by an alignment amount to determine how days of the month
 *          are handled.  If n is omitted, 1 is assumed.</td>
 *    </tr>
 *    <tr>
 *      <td>&plusmn;nQ</td>
 *      <td>Add/subtract n quarters.  Equivalent to adding/subtracting
 *          3 &times; n months.  This adjustment amount must be followed
 *          by an alignment amount to determine how days of the month
 *          are handled.</td>
 *    </tr>
 *    <tr>
 *      <td>&plusmn;nY</td>
 *      <td>Add/subtract n years.  Equivalent to adding/subtracting
 *          12 &times; n months.  This adjustment amount must be followed
 *          by an alignment amount to determine how days of the month
 *          are handled.</td>
 *    </tr>
 *    <tr>
 *      <td>&plusmn;nD<br />
 *          &plusmn;n</td>
 *      <td>Add/subtract n days.  If n is omitted, 1 is assumed.</td>
 *    </tr>
 *    <tr>
 *      <td>&plusmn;nW</td>
 *      <td>Add/subtract n weeks.  Equivalent to adding/subtracting
 *          7 &times; n days.</td>
 *    </tr>
 *  </table>
 *  <p>Possible alignment amounts are as follows:</p> 
 *  <table>
 *    <tr>
 *      <th>Alignment amount</th>
 *      <th>Description</th>
 *    </tr>
 *    <tr>
 *      <td>&plusmn;n</td>
 *      <td>Use the day of month counting from the beginning (+) or end 
 *          (-) of the month.  Thus, &gt;5 is the 5<sup>th</sup> of the
 *          month; &lt;1 is the last day of the month.  n cannot be
 *          ommitted.  n cannot be zero.</td>
 *    </tr>
 *    <tr>
 *      <td>&plusmn;nXX</td>
 *      <td>Align on a weekday, counting forward 
 *          (+) or backward (-), where XX is an unambiguous
 *          abbreviation of the week day.  In English, XX can be M, 
 *          TU, W, TH, F, SA, SU.  n is the occurance.  1 means the
 *          next, 2 means the second, -1 means the previous, and
 *          so on.  If n is omitted, 1 is assumed.  n cannot be zero.</td>
 *    </tr>
 *  </table>
 */
public class DatePeriod {
  private static String[] weekdays = new DateFormatSymbols().getWeekdays();
  
  
  private class CharIterator {
    private static final char DONE = '\uFFFF';
    
    private final char[] sx;
    private int index;
    private char current = 0;
    
    
    private CharIterator (String s) {
      this.sx = s.toCharArray();
      index = 0;
    }
    
    private char next() {
      if (index < sx.length) {
        current = sx[index++];
      } else {
        current = DONE;
      }
      return current;
    }
    
    //private char current() {
    //  return current;
    //}
    
    private boolean hasNext () {
      return index < sx.length;
    }
    
    private int getIndex () {
      return index;
    }
    
    private String getSource () {
      return new String(sx);
    }
    
  }
  
  
  public static class DatePeriodException extends RuntimeException {
    private static final long serialVersionUID = 759368230280554746L;

    private String msg;
  	private boolean completeable;
  	
    public DatePeriodException (String msg, CharIterator iter, boolean completeable) {
      super (msg + " at position " + iter.getIndex() + " of " + iter.getSource());
      this.msg = msg;
      this.completeable = completeable;
    }
    
    public String getUserMessage () {
    	return msg;
    }
    
    public boolean isCompleteable () {
    	return completeable;
    }
  }
    
    
  private static class Alignment {
    private int alignQnty;
    private char alignType;
    private int weekday;
    
    private Alignment (CharIterator iter) {
      char c = iter.current;
      char sign;
      if (c == '+') {
        sign = '+';
        c = iter.next();
      } else if (c == '-') {
        sign = '-'; 
        c = iter.next();
      } else {
        sign = '+';
      }
      if (Character.isDigit(c)) {
        alignQnty = 0;
        while (Character.isDigit(c)) {
          alignQnty = alignQnty * 10 + Character.getNumericValue(c);
          c = iter.next();
        }
      } else {
        alignQnty = 1;
      }
      if (sign == '-') {
        alignQnty = -alignQnty;
      }
      StringBuffer wdx = new StringBuffer();
      while (Character.isLetter(c)) {
        wdx.append(c);
        c = iter.next();
      }
      if (wdx.length() == 0) {
        alignType = 'M';
      } else {
        alignType = 'W';
        String wd = wdx.toString();
        for (int i = 1; i <= weekdays.length; i++) {
          if (weekdays[i].startsWith(wd)) {
            weekday = i;
            break;
          }
        }
      }
    }
    
    
    private void adjust (DayDate date, char adjType) {
      if (alignType == 'M') {
        if (adjType == 'M' || adjType == 'Q' || adjType == 'Y') {
          if (alignQnty < 0) {
            date.setDay(date.daysInMonth() + alignQnty + 1);
          } if (alignQnty > 0) {
            date.setDay(alignQnty);
          }
        } else {
          date.addDay(alignQnty);
        }          
      } else {
        if (adjType == 'M' || adjType == 'Q' || adjType == 'Y') {
          if (alignQnty < 0) {
            /* Set date to be the last day of the month. */
            date.addDay(date.daysInMonth() + alignQnty);
          }
        }
        if (alignQnty < 0) {  
          int n = weekday - date.getWeekday();
          if (n > 0) {
            n -= 7;
          }
          n += (alignQnty + 1) * 7;
          date.addDay(n);
        } else {
          int n = weekday - date.getWeekday();
          if (n < 0) {
            n += 7;
          }
          n += (alignQnty - 1) * 7;
          date.addDay(n);
        }
      }
    }
  }
  
  private static class Segment {
    private int adjQnty;
    private char adjType;
    private List<List<Alignment>> groups = new ArrayList<List<Alignment>>(3);
    
    
    private Segment (CharIterator iter) throws DatePeriodException {
      char sign = iter.current;
      if (sign != '+' && sign != '-') {
        throw new DatePeriodException("expecting + or -", iter, false);
      }
      adjQnty = 0;
      char c = iter.next();
      if (Character.isDigit(c)) {
        while (Character.isDigit(c)) {
          adjQnty = adjQnty * 10 + Character.getNumericValue(c);
          c = iter.next();
        }
      } else {
        adjQnty = 1;
      }
      if (sign == '-') {
        adjQnty = -adjQnty;
      }
      switch (c) {
      case 'M' :
      case 'Q' :
      case 'Y' :
      case 'D' :
      case 'W' :
        adjType = c;
        c = iter.next();
        break;
      case '+' :
      case '-' :
      case '(' :
      case CharIterator.DONE :
        adjType = 'D';
        break;
      default :
        throw new DatePeriodException("expecting adjustment type (M,Q,Y,D,W)", iter, false);
      }
      while (c == '(') {
        c = iter.next();
        List<Alignment> alignments = new ArrayList<Alignment>(3);
				Alignment alignment = new Alignment(iter);
				alignments.add(alignment);
				c = iter.current;
				while (c == ',') {
          c = iter.next();
          alignment = new Alignment(iter);
          alignments.add(alignment);
          c = iter.current;
        }
        groups.add(alignments);
        if (c != ')') {
          throw new DatePeriodException("expecting end of alignment ')'", iter, true);
        }
        c = iter.next();
      }
    } 
    
    
    private static int computeDifference (DayDate date, List<Alignment> alignments, char adjType2) {
      DayDate date1 = (DayDate)date.clone();
      for (Alignment alignment : alignments) {
        alignment.adjust(date1, adjType2);
        adjType2 = 'D';
      }
      int n = date.less(date1);
      return Math.abs(n);
    }
    
          
    private void adjust (DayDate date) {
      List<Alignment> applicableAlignments = null;
      boolean adjustmentRequired = true;
      
      if (groups.size() == 1) {
        applicableAlignments = groups.get(0);
      } else if (groups.isEmpty()) {
        applicableAlignments = new ArrayList<Alignment>(2);
      } else {
        long minDiff = Long.MAX_VALUE;
        int index = 0;
        for (int i = 0; i < groups.size(); i++) {
          List<Alignment> alignments = groups.get(i);
          long diff = computeDifference(date, alignments, adjType);
          if (diff < minDiff) {
            index = i;
            minDiff = diff;
          }
        }
        /* Get next index. */
        index++;
        if (index < groups.size()) {
          adjustmentRequired = false;
        } else {
          index = 0;
        }
        applicableAlignments = groups.get(index);
      }
      
      if (adjustmentRequired) {
        switch (adjType) {
        case 'M' : 
          date.addMonthDay(adjQnty, 0);
          break;
        case 'Q' :
          date.addMonthDay(adjQnty * 3, 0);
          break;
        case 'Y' :
          date.addMonthDay(adjQnty * 12, 0);
          break;
        case 'D' :
          date.addDay(adjQnty);
          break;
        case 'W' :
          date.addDay(adjQnty * 7);
          break;
        }
      }
      char adjType2 = adjType;
      for (Alignment alignment : applicableAlignments) {
        alignment.adjust(date, adjType2);
        adjType2 = 'D';
      }
    }   
  }
  
  private String rule;
  private List<Segment> segments;
  
  
  public DatePeriod (String rule) throws DatePeriodException {
    this.rule = rule;
    this.segments = new ArrayList<Segment>();
    CharIterator iter = new CharIterator(rule);
    iter.next();
    Segment segment = new Segment(iter);
    segments.add(segment);
    while (iter.hasNext()) {
      segment = new Segment(iter);
      segments.add(segment);
    }
  }
  

  public String getRule() {
    return rule;
  }
  
  
  public DayDate adjust (DayDate d) {
    DayDate d1 = (DayDate)d.clone();
    for (Segment segment : segments) {
      segment.adjust(d1);
    }
    return d1;
  }  




//  public static void main (String[] args) throws DatePeriodException {
////    DateFormat df = new SimpleDateFormat("EEE yyyy-MM-dd");
////    
//    DayDate startDate = new DayDate(2005, 2, 1);
////    Object[] testData = new Object[] {
////      "+M(1)", new DayDate(2005, 2, 1),
////			"+1M(1)", new DayDate(2005, 2, 1),
////      "+3M(1)", new DayDate(2005, 4, 1),
////      "+1Q(1)", new DayDate(2005, 4, 1),
////      "+1Y(1)", new DayDate(2006, 1, 1),
////      "+1Y(-1)", new DayDate(2006, 1, 31),
////			"+15D(15,-1)", new DayDate(2005, 2, 15),
////    };
//
//    DatePeriod p1 = new DatePeriod("-6+1M(15)(-1)");
//    DayDate d1 = startDate;
//    DayDateFormat dx = new DayDateFormat("yyyy-MM-dd EEE");
//    System.out.println(dx.format(d1));
//    for (int i = 0; i < 7; i++) {
//    	d1 = p1.adjust(d1);
//			System.out.println(dx.format(d1));
//    }
//        	
////    for (int i = 0; i < testData.length; i += 2) {
////    	DatePeriod p1 = new DatePeriod((String)testData[i]);
////      DayDate d1 = p1.adjust(startDate);
////      DayDate d2 = (DayDate)testData[i + 1];
////      System.out.println(testData[i] + ": " + df.format(d1.javaDate()) + " vs " + df.format(d2.javaDate()));
////    }
//  }
}
