package org.gyfor.util;

import java.io.Serializable;


public class CRC64 implements Comparable<CRC64>, Serializable {

  private static final long serialVersionUID = 1L;

  private long value;
  
  
  public CRC64 (long value) {
    this.value = value;
  }

  
  private static int deHex (char c0, char c1) {
    int i = 0;
    if (c0 <= '9') {
      i += c0 - '0';
    } else {
      i += c0 - 'a' + 10;
    }
    i <<= 4;
    if (c1 <= '9') {
      i += c1 - '0';
    } else {
      i += c1 - 'a' + 10;
    }
    return (byte)i;
  }
  
  
  public CRC64 (String s) {
    if (s.length() != 17) {
      throw new IllegalArgumentException(s);
    }
    value = 0L;
    int j = 0;
    int i = 0;
    while (j < 8) {
      char c0 = s.charAt(i++);
      if (c0 == '-') {
        c0 = s.charAt(i++);
      }
      char c1 = s.charAt(i++);
      value = (value << 8) | (deHex(c0, c1) & 0xFF);
      j++;
    }
  }

  
  public long getLong () {
    return value;
  }
  
  
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    CRC64 other = (CRC64)obj;
    return value == other.value;
  }


  @Override
  public int hashCode() {
    return (int)(((value >> 32) | value) & 0xFFFFFFFF);
  } 

  
  private static final String ZEROS = "0000000000000000";
  
  
  @Override
  public String toString() {
    String x = Long.toHexString(value);
    int n = x.length();
    x = ZEROS.substring(n) + x;
    x = x.substring(0, 4) + '-' + x.substring(4);
    return x;
  }


  @Override
  public int compareTo(CRC64 other) {
    if (value == other.value) {
      return 0;
    } else if (value < other.value) {
      return -1;
    } else {
      return +1;
    }
  }

}
