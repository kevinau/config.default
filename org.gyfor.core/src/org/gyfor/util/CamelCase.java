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
package org.gyfor.util;

public class CamelCase {

  private CamelCase () {
  }
  
  
  public static String toSentence (String source) {
    char[] sx = source.toCharArray();
    int n = 0;
    StringBuilder buffer = new StringBuilder();
    boolean isWord = false; 

    while (n < sx.length) {
      int n1;
      
      if (isWord) {
        buffer.append(' ');
      }
      char c = sx[n];
      if (Character.isDigit(c)) {
        n1 = n;
        n++;
        while (n < sx.length && Character.isDigit(sx[n])) {
          n++;
        }
        buffer.append(sx, n1, n - n1);
        isWord = true;
      } else if (Character.isLetter(c)) {
        n1 = n;
        n++;
        if (n < sx.length && Character.isUpperCase(sx[n])) {
          while (n < sx.length && Character.isUpperCase(sx[n])) {
            n++;
          }
          if (n < sx.length && Character.isLowerCase(sx[n])) {
            /* The segment is followed by a lower case letter, so assume
             * the last upper case letter belongs to a new word. */
            n--;
          }
          if (n - n1 == 1) {
            buffer.append(Character.toLowerCase(c));
          } else {
            buffer.append(sx, n1, n - n1);
          }
        } else {
          sx[n1] = Character.toLowerCase(c);
          while (n < sx.length && Character.isLowerCase(sx[n])) {
            n++; 
          }
          buffer.append(sx, n1, n - n1);
        }
        isWord = true;
      }
      while (n < sx.length && !Character.isLetterOrDigit(sx[n])) {
        n++;
      }
    }
    buffer.setCharAt(0, Character.toUpperCase(buffer.charAt(0)));
    return buffer.toString();
  }

  public static String toJavaName (String source) {
    int i = source.indexOf('_');
    if (i == -1) {
      return source;
    }
    
    char[] sx = source.toCharArray();
    StringBuilder buffer = new StringBuilder();
    boolean newWord = false; 
   
    int n = 0;
    while (n < sx.length) {
      char c = sx[n];
      if (c == '_') {
        newWord = true;
      } else {
        if (newWord) {
          buffer.append(Character.toUpperCase(c));
          newWord = false;
        } else {
          buffer.append(Character.toLowerCase(c));
        }
      }
      n++;
    }
    return buffer.toString();
  }
}
