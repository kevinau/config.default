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
package org.gyfor.text;

import java.text.DateFormatSymbols;

import org.gyfor.time.DayDate;


public class DayDateFormat implements IFormat {
	private final String format;
	private DateFormatSymbols symbols = null;
	
	
	public static final DayDateFormat ISO = new DayDateFormat("yyyy-MM-dd");
	

	public DayDateFormat (String format) {
		this.format = format;
	}
	

	@Override
	public String toString (Object date) {
		if (symbols == null) {
			symbols = new DateFormatSymbols();
		}
		StringBuffer buffer = new StringBuffer();
		char[] fx = format.toCharArray();
		String nx = null;
		boolean priorQuote = false;
		
		int i = 0;
		while (i < fx.length) {
			int runLength = 1;
			if (Character.isLetter(fx[i])) {
				int j = i + 1;
				while (j < fx.length && fx[j] == fx[i]) {
					j++;
				}
				runLength = j - i;
			}
			switch (fx[i]) {
			case 'y' :
			  int year = ((DayDate)date).getYear();
				if (runLength <= 2) {
				    nx = Integer.toString(year % 100);
				} else {
					nx = Integer.toString(year);
				}
				i += runLength;
				priorQuote = false;
				break;
			case 'M' :
			  int month = ((DayDate)date).getMonth() - 1;
			  switch (runLength) {
				case 1 :
				case 2 :
					nx = Integer.toString(month + 1);
					break;
				case 3 :
					buffer.append(symbols.getShortMonths()[month]);
					break;
				default :
					buffer.append(symbols.getMonths()[month]);
				    break;
			    }
				i += runLength;
				priorQuote = false;
				break;
			case 'd' :
			  int day = ((DayDate)date).getDay();
				nx = Integer.toString(day);
				i += runLength;
				priorQuote = false;
				break;
			case 'E' :
			  int weekday = ((DayDate)date).getWeekday();
				if (runLength >= 4) {
					buffer.append(symbols.getWeekdays()[weekday + 1]);
				} else {
					buffer.append(symbols.getShortWeekdays()[weekday + 1]);
				}
				i += runLength;
				priorQuote = false;
				break;
			case '\'' :
				if (priorQuote) {
					buffer.append('\'');
				}
				i++;
				int j = i;
				while (j < fx.length && fx[j] != '\'') {
					j++;
				}
				if (j == i) {
					buffer.append('\'');
					priorQuote = false;
				} else {
					buffer.append(fx, i, j - i);
					priorQuote = true;
				}
				i = j + 1;
				break;
			default :
				buffer.append(fx[i]);
			    i++;
				priorQuote = false;
			}
			if (nx != null) {
				while (runLength > nx.length()) {
					buffer.append('0');
					runLength--;
				}
				buffer.append(nx);
				nx = null;
			}
		}
		return buffer.toString();
	}
	
	
  @Override
  public String toString () {
    return format;
  }


//	public static void main (String[] args) {
//		String[] fxx = new String[] {
//				"yy",
//				"yyyy",
//				"M",
//				"MM",
//				"MMM",
//				"MMMM",
//				"d",
//				"dd",
//				"EEE",
//				"EEEE",
//				"'abc'",
//				"''",
//				"''''",
//				"''''''",
//				"'o''clock'",
//				"'''clock'",
//		};
//		
//	    Calendar calendar = new GregorianCalendar();
//	    int day = calendar.get(Calendar.DAY_OF_MONTH);
//	    int month = calendar.get(Calendar.MONTH);
//	    int year = calendar.get(Calendar.YEAR);
//	
//		for (int f = 0; f < fxx.length; f++) {
//				String fx = fxx[f];
//				DayDateFormat format1 = new DayDateFormat(fx);
//				DateFormat format0 = new SimpleDateFormat(fx);
//		
//				DayDate d1 = new DayDate(year, month, day);
//			//String x1 = format1.format(year, month, day, weekday);
//				String x1 = format1.format(d1);
//				String x0 = format0.format(calendar.getTime());
//				System.out.println(fx + ": " + x0 + "  " + x1);
//		}
//		DayDateFormat fx = new DayDateFormat("yyyy-MM-dd yyyy-MMM-dd EEE");
//		DayDate d1 = new DayDate();
//		for (int f = 0; f < 40; f++) {
//				String x0 = fx.format(d1);
//				System.out.println(x0);
//				d1 = d1.plus(1);
//		}
//	}
}
