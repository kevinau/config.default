package org.gyfor.time;

import org.threeten.bp.LocalDate;


public class LocalDateFactory {

  public static LocalDate parseDate (String source) throws IllegalArgumentException {
    String[] msg = new String[1];
    int[] resultYMD = new int[3];
    String[] completion = new String[1];
    int result = DateFactory.validate(source, null, msg, resultYMD, completion);
    if (result == DateFactory.OK) {
      return LocalDate.of(resultYMD[0], resultYMD[1], resultYMD[2]);
    } else {
      throw new IllegalArgumentException(source + ": " + msg[0]);
    }
  }

}
