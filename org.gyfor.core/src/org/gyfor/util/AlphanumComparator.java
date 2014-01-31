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


import java.util.Comparator;


public class AlphanumComparator<E> implements Comparator<E> {

  private static int digitLength(char[] cx, int offset) {
    int i = offset;
    while (i < cx.length) {
      if (!Character.isDigit(cx[i])) {
        return i - offset;
      }
      i++;
    }
    return cx.length - offset;
  }

  private static int alphaLength(char[] cx, int offset) {
    int i = offset;
    while (i < cx.length) {
      if (Character.isDigit(cx[i])) {
        return i - offset;
      }
      i++;
    }
    return cx.length - offset;
  }

  private int compare (char[] cx1, int offset1, char[] cx2, int offset2, int n) {
    for (int i = 0; i < n; i++) {
      int cx = cx1[i + offset1] - cx2[i + offset2];
      if (cx != 0) {
        return cx;
      }
    }
    return 0;
  }
  
  
  public int compare(E s1, E s2) {
    char[] cx1 = s1.toString().toCharArray();
    char[] cx2 = s2.toString().toCharArray();
    int i1 = 0;
    int i2 = 0;
    boolean alphaSegment = true;

    while (i1 < cx1.length && i2 < cx2.length) {
      if (alphaSegment) {
        int n1 = alphaLength(cx1, i1);
        int n2 = alphaLength(cx2, i2);
        int n = Math.min(n1, n2);
        int x = compare(cx1, i1, cx2, i2, n);
        if (x == 0) {
          n = n1 - n2;
          if (n == 0) {
            i1 += n1;
            i2 += n2;
          } else {
            return n;
          }
        } else {
          return x;
        }
      } else {
        int n1 = digitLength(cx1, i1);
        int n2 = digitLength(cx2, i2);
        if (n1 == n2) {
          int x = compare(cx1, i1, cx2, i2, n1);
          if (x != 0) {
            return x;
          } else {
            i1 += n1;
            i2 += n2;
          }
        } else {
          return n1 - n2;
        }
      }
      alphaSegment = !alphaSegment;
    }
    return 0;
  }

}
