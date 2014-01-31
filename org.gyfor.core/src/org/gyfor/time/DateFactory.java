package org.gyfor.time;

import java.util.Calendar;

import org.gyfor.text.DayDateFormat;
import org.gyfor.util.UserEntryException;


public abstract class DateFactory {

  private static final String REQUIRED_MESSAGE = "a date is required";
  
  public static final DayDateFormat entryDateFormat = new DayDateFormat("dd/MM/yy");
  
  //private static final DateTimeFormatter isoFormat = DateTimeFormat.forPattern("yyyy-MM-dd");
  
  public static final int OK = 0;
  public static final int REQUIRED = 1;
  public static final int INCOMPLETE = 2;
  public static final int ERROR = 3;
  
  private static String[] months = new String[] {
    "january", "february", "march", "april", "may", "june",
    "july", "august", "september", "october", "november", "december",
  };
  private static int[] normalMthDays = new int[] {
    31, 28, 31, 30, 31, 30, 
    31, 31, 30, 31, 30, 31,
  };
  private static int[] leapMthDays = new int[] {
    31, 29, 31, 30, 31, 30, 
    31, 31, 30, 31, 30, 31,
  };
  

  private static String entryDateFormat (int year, int month, int day) {
    StringBuilder buffer = new StringBuilder(8);
    if (day < 10) {
      buffer.append('0');
    }
    buffer.append(day);
    buffer.append('/');
    if (month < 10) {
      buffer.append('0');
    }
    buffer.append(month);
    buffer.append('/');
    year = year % 100;
    if (year < 10) {
      buffer.append('0');
    }
    buffer.append(year);
    return buffer.toString();
  }

  
  private static String mmyyDateFormat (int year, int month) {
    StringBuilder buffer = new StringBuilder(5);
    if (month < 10) {
      buffer.append('0');
    }
    buffer.append(month);
    buffer.append('/');
    year = year % 100;
    if (year < 10) {
      buffer.append('0');
    }
    buffer.append(year);
    return buffer.toString();
  }

  
  private static String mmDateFormat (int month) {
    StringBuilder buffer = new StringBuilder(2);
    if (month < 10) {
      buffer.append('0');
    }
    buffer.append(month);
    return buffer.toString();
  }

  
  private static String yyDateFormat (int year) {
    StringBuilder buffer = new StringBuilder(2);
    year = year % 100;
    if (year < 10) {
      buffer.append('0');
    }
    buffer.append(year);
    return buffer.toString();
  }

  
  /**
   * Parse a string that contains a date.  The string can contain a day, month and year, as follows:
   * <ul>
   * <li>If the fields are delimited by spaces or punctuation, or if the month is an alpha field:
   * <ul>
   * <li>The day can be one or two digits.</li>
   * <li>The month can be one or two digits, or alpha characters that describe a month.</li>
   * <li>The year can be two or four digits.</li>
   * </ul>
   * </li>
   * <li>If the fields are all numbers and are packed tight (no spaces):
   * <ul>
   * <li>The day must be two digits.</li>
   * <li>The month must be two digits.</li>
   * <li>The year can be two or four digits.</li>
   * </ul>
   * </li>
   * </ul>
   * <p>
   * Note.  There is a Javascript version of this method in jquery.ui.datepicker.  Any changes to this
   * method should be changed in jquery.ui.datepicker.
   * 
   * @param source
   * @param fillYMD
   * @param msg
   * @param resultYMD
   * @param completion
   * @return an integer reporting the validity of the source string
   */
  public static int validate (String source, int[] fillYMD, String[] msg, int[] resultYMD, String[] completion) {
    if (fillYMD == null) {
      Calendar today = Calendar.getInstance();
      fillYMD = new int[] {
          today.get(Calendar.YEAR),
          today.get(Calendar.MONTH) + 1,
          today.get(Calendar.DAY_OF_MONTH),
      };
    }
    int year = fillYMD[0];
    int month = fillYMD[1];
    int day = fillYMD[2];
    
    char[] x = source.toCharArray();
    if (x.length == 0) {
      completion[0] = entryDateFormat(year, month, day);
      msg[0] = REQUIRED_MESSAGE;
      return REQUIRED;
    }
    completion[0] = null;
    
    int leadingDigitCount = 0;
    for (int i = 0; i < x.length; i++) {
      if (!Character.isDigit(x[i])) {
        break;
      }
      leadingDigitCount++;
    }
    if (leadingDigitCount == x.length) {
      // all digits
      if (x.length > 8) {
        msg[0] = "invalid date: too many digits";
        return ERROR;
      }
      int n = 0;
      n = Integer.parseInt(source);
      switch (x.length) {
      case 1 :
        msg[0] = "length 1";
        return INCOMPLETE;
      case 3 :
        n /= 10;
        /* Drop through */
      case 2 :
        day = n;
        if (day < 1 || day > 31) {
          msg[0] = "invalid date: bad day (" + day + ")"; 
          return ERROR;
        }
        if (x.length == 2) {
          completion[0] = mmyyDateFormat(year, month);
        }
        msg[0] = "length 2/3";
        return INCOMPLETE;
      case 5 :
        n /= 10;
        /* Drop through */ 
      case 4 : 
        day = n / 100;
        month = n % 100;
        if (month < 1 || month > 12) {
          msg[0] = "invalid date: bad month (" + month + ")"; 
          return ERROR;
        }
        month--;
        if (day < 1 || day > leapMthDays[month]) {
          msg[0] = "invalid date: bad day (" + day + ") for month " + (month + 1); 
          return ERROR;
        }
        if (x.length == 4) {
          completion[0] = yyDateFormat(year);
        }
        msg[0] = "length 4/5";
        return INCOMPLETE;
      case 6 :
      case 8 :
        if (x.length == 6) {
          day = n / 10000;
          n %= 10000;
          month = n / 100;
          year = n % 100;
          if (year < 50) {
            year += 2000;
          } else {
            year += 1900;
          }
        } else {
          day = n / 1000000;
          n %= 1000000;
          month = n / 10000;
          year = n % 10000;
        }
        if (month < 1 || month > 12) {
          msg[0] = "invalid date: bad month (" + month + ")"; 
          return ERROR;
        }
        month--;
        if ((year & 3) == 0) {      
          if (day < 1 || day > leapMthDays[month]) {
            msg[0] = "invalid date: bad day (" + day + ") for month " + (month + 1); 
            return ERROR;
          }
        } else {
          if (day < 1 || day > normalMthDays[month]) {
            msg[0] = "invalid date: bad day (" + day + ") for month " + (month + 1); 
            return ERROR;
          }
        }
        break;
      default :
        msg[0] = "invalid date: too many digits"; 
        return ERROR;
      }
    } else if (leadingDigitCount == 4) {
      if (x.length == 10 && x[4] == '-' && x[7] == '-') {
        // Special case, ISO date
        year = 0;
        for (int i = 0; i < 4; i++) {
          year = year * 10 + Character.getNumericValue(x[i]);
        }
        month = 0;
        for (int i = 5; i < 7; i++) {
          month = month * 10 + Character.getNumericValue(x[i]);
        }
        month--;
        day = 0;
        for (int i = 8; i < 10; i++) {
          day = day * 10 + Character.getNumericValue(x[i]);
        }
      } else {
        msg[0] = "invalid date: starts with yyyy but not an ISO date";
        return ERROR;
      }
    } else {      
      /* Assuming date starts with day. */
      int i = 0;
      day = 0;
      while (Character.isDigit(x[i])) {
        day = day * 10 + Character.getNumericValue(x[i]);
        i++;
      }
      if (i > 2 || day < 1 || day > 31) {
        msg[0] = "invalid date: bad day (" + day + ")"; 
        return ERROR;
      }
      String fillChar = "";
      while (i < x.length && !Character.isLetterOrDigit(x[i])) {
        fillChar = Character.toString(x[i]);
        i++;
      }
      if (i == x.length) {
        /* No month or year. */
        completion[0] = mmDateFormat(month) + fillChar + yyDateFormat(year);
        msg[0] = "no month or year";
        return INCOMPLETE;
      }
      int monthCount;
      if (Character.isDigit(x[i])) {
        int i0 = i;
        month = 0;
        while (i < x.length && Character.isDigit(x[i])) {
          month = month * 10 + Character.getNumericValue(x[i]);
          i++;
        }
        if (i < x.length && Character.isLetter(x[i])) {
          msg[0] = "invalid characters following month: " + source.substring(i);
          return ERROR;
        }
        if (i - i0 > 2 || month < 1 || month > 12) {
          msg[0] = "invalid date: bad month (" + month + ")"; 
          return ERROR;
        }
        month--;
        monthCount = 1;
      } else {
        int i0 = i;
        while (i < x.length && Character.isLetter(x[i])) {
          i++;
        }
        month = 0;
        monthCount = 0;
        String mmm = new String(x, i0, i - i0).toLowerCase();
        for (int m = 0; m < months.length; m++) {
          if (months[m].startsWith(mmm)) {
            month = m;
            monthCount++;
          }
        }
        if (monthCount == 0) {
          /* Month not recognized */
          msg[0] = "invalid date: bad month (" + mmm + ")"; 
          return ERROR;
        }
      }
      if (day > leapMthDays[month]) {
        msg[0] = "invalid date: bad day (" + day + ") for month " + (month + 1); 
        return ERROR;
      }
      if (i == x.length) {
        if (monthCount == 1) {
          completion[0] = fillChar + yyDateFormat(year);
        }
        msg[0] = "no year";
        return INCOMPLETE;
      }
      if (monthCount > 1) {
        msg[0] = "invalid date: ambiguous month"; 
        return ERROR;
      }
      while (i < x.length && !Character.isDigit(x[i])) {
        i++;
      }
      if (i == x.length) {
        /* No year */
        completion[0] = yyDateFormat(year);
        msg[0] = "no year following delimiter";
        return INCOMPLETE;
      }
      int i0 = i;
      year = 0;
      while (i < x.length && Character.isDigit(x[i])) {
        year = year * 10 + Character.getNumericValue(x[i]);
        i++;
      }
      if (i < x.length) {
        msg[0] = "invalid date: too many characters"; 
        return ERROR;
      }
      switch (i - i0) {
      case 2 :
        /* Year in the form yy */
        if (year < 50) {
          year += 2000;
        } else {
          year += 1900;
        }
        break;
      case 4 :
        /* Year in the form yyyy */
        break;
      default :
        /* Year not the right length */
        msg[0] = "invalid date: year not 2 or 4 digits";
        return x.length - i < 2 ? INCOMPLETE : ERROR;
      }
      if (year % 4 != 0) {
        /* Not a leap year */
        if (day > normalMthDays[month]) {
          msg[0] = "invalid date: bad day (" + day + ") for month " + (month + 1); 
          return ERROR;
        }
      }
    }
    resultYMD[0] = year;
    resultYMD[1] = month + 1;
    resultYMD[2] = day;
    return OK; 
  }
  
  
  public static int[] createFromString (String source, int[] fillYMD) throws UserEntryException {
    if (source.equals("0100-01-01") || source.equals("0000-01-01")) {
      return null;
    } else {
      int[] resultYMD = new int[3];
      String[] completion = new String[1];
      String[] msg = new String[1];
      int result = validate(source, fillYMD, msg, resultYMD, completion);
      switch (result) {
      case OK :
        return resultYMD;
      case INCOMPLETE :
        throw new UserEntryException(msg[0], UserEntryException.Type.INCOMPLETE, completion[0]);
      case REQUIRED :
        throw new UserEntryException(msg[0], UserEntryException.Type.REQUIRED, completion[0]);
      default :
        throw new UserEntryException(msg[0]);
      }
    }
  }
  
  
}
