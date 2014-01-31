package org.gyfor.time;


public class LocalTimeFactory {

  public static LocalTime parseTime (String source) throws IllegalArgumentException {
    String[] msg = new String[1];
    int[] resultYMD = new int[3];
    String[] completion = new String[1];
    int result = DateFactory.validate(source, null, msg, resultYMD, completion);
    if (result == DateFactory.OK) {
      return new LocalTime(resultYMD[0], resultYMD[1], resultYMD[2]);
    } else {
      throw new IllegalArgumentException(msg[0]);
    }
  }

}
