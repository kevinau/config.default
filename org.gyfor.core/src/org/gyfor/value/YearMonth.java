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
package org.gyfor.value;


/** 
 * A month within a year.  Years are 4 digits, representing the full year.
 * Months are numbered starting with 1.
 * 
 * @author Kevin Holloway
 */
public class YearMonth {

  private final int index;
  
  public YearMonth (int year, int month) {
    index = year * 12 + month - 1;
  }
  
  
  public int getYear () {
    return index / 12;
  }
  
  
  public int getMonth () {
    return index % 12 + 1;
  }
}
