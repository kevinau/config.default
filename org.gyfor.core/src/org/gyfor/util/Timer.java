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

public class Timer {
  
  private long startTime;
  
  public Timer () {
    startTime = System.currentTimeMillis();
  }
  
  
  public void restart() {
    startTime = System.currentTimeMillis();    
  }
  
  
  public String stop () {
    long endTime = System.currentTimeMillis();
    long elapsedTime = endTime - startTime;
    int milliSecs = (int)(elapsedTime % 1000);
    int secs = (int)(elapsedTime / 1000);
    int mins = secs / 60;
    secs = secs - mins * 60;
    int hours = mins / 60;
    mins = mins - hours * 60;

    StringBuilder x = new StringBuilder();
    x.append(hours);
    x.append(':');
    if (mins < 10) {
      x.append('0');
    }
    x.append(mins);
    x.append(':');
    if (secs < 10) {
      x.append('0');
    }
    x.append(secs);
    x.append('.');
    if (milliSecs < 100) {
      x.append('0');
      if (milliSecs < 10) {
        x.append('0');
      }
    }
    x.append(milliSecs);
    return x.toString();
  }

}
