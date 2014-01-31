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


public class DateRange implements IValueRange<DayDate> {
  
    public static final DayDate MAX_VALUE = new DayDate(9999, 12, 31);
    
    public static final DayDate MIN_VALUE = new DayDate(1, 1, 1);
    
    private final DayDate beginDate;
    
    private final DayDate endDate;
    
    public DateRange (DayDate beginDate, DayDate endDate) {
        this.beginDate = beginDate;
        this.endDate = endDate;
    }
    
  
    @Override
    public int compareToRange(DayDate value) {
        if (value.before(beginDate)) {
            return -1;
        } else if (value.before(endDate)) {
            return 0;
        } else {
            return +1;
        }
    }

    @Override
    public boolean contains(DayDate obj) {
        return compareToRange(obj) == 0;
    }
    
    @Override
    public String toString () {
        return beginDate.toString() + "..." + endDate.toString();
    }


    @Override
    public DayDate getBeginValue() {
      return beginDate;
    }


    @Override
    public DayDate getEndValue() {
      return endDate;
    }


    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + beginDate.hashCode();
      result = prime * result + endDate.hashCode();
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
      DateRange other = (DateRange) obj;
      if (!beginDate.equals(other.beginDate)) {
        return false;
      }
      if (!endDate.equals(other.endDate)) {
        return false;
      }
      return true;
    }

}
