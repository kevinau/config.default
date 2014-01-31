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
import org.threeten.bp.LocalDate;


public class LocalDateRange implements IValueRange<LocalDate> {
  
    public static final LocalDate MAX_VALUE = LocalDate.MAX;
    
    public static final LocalDate MIN_VALUE = LocalDate.MIN;
    
    private final LocalDate beginDate;
    
    private final LocalDate endDate;
    
    public LocalDateRange (LocalDate beginDate, LocalDate endDate) {
        this.beginDate = beginDate;
        this.endDate = endDate;
    }
    
  
    @Override
    public int compareToRange(LocalDate value) {
        if (value.isBefore(beginDate)) {
            return -1;
        } else if (value.isBefore(endDate)) {
            return 0;
        } else {
            return +1;
        }
    }

    @Override
    public boolean contains(LocalDate obj) {
        return compareToRange(obj) == 0;
    }
    
    @Override
    public String toString () {
        return beginDate.toString() + "..." + endDate.toString();
    }


    @Override
    public LocalDate getBeginValue() {
      return beginDate;
    }


    @Override
    public LocalDate getEndValue() {
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
      LocalDateRange other = (LocalDateRange) obj;
      if (!beginDate.equals(other.beginDate)) {
        return false;
      }
      if (!endDate.equals(other.endDate)) {
        return false;
      }
      return true;
    }

}
