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
package org.gyfor.time;

import org.gyfor.value.IValueRange;


public class LocalTimeRange implements IValueRange<LocalTime> {
  
    public static final LocalTime MAX_VALUE = new LocalTime(9999, 12, 31);
    
    public static final LocalTime MIN_VALUE = LocalTime.GENESIS;
    
    private final LocalTime beginTime;
    
    private final LocalTime endTime;
    
    public LocalTimeRange (LocalTime beginTime, LocalTime endTime) {
        this.beginTime = beginTime;
        this.endTime = endTime;
    }
    
  
    @Override
    public int compareToRange(LocalTime value) {
        if (value.before(beginTime)) {
            return -1;
        } else if (value.before(endTime)) {
            return 0;
        } else {
            return +1;
        }
    }

    @Override
    public boolean contains(LocalTime obj) {
        return compareToRange(obj) == 0;
    }
    
    @Override
    public String toString () {
        return beginTime.toString() + "..." + endTime.toString();
    }


    @Override
    public LocalTime getBeginValue() {
      return beginTime;
    }


    @Override
    public LocalTime getEndValue() {
      return endTime;
    }


    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + beginTime.hashCode();
      result = prime * result + endTime.hashCode();
      return result;
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
      LocalTimeRange other = (LocalTimeRange) obj;
      if (!beginTime.equals(other.beginTime)) {
        return false;
      }
      if (!endTime.equals(other.endTime)) {
        return false;
      }
      return true;
    }

}
