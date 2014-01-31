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
 * TODO Write a reasonably detailed description of the purpose and use of the class here.
 * @param <T> 
 * 
 */
public interface IValueRange<T> {

    /**
     * Tests whether this range contains a value or not.
     * 
     * @param value -- the value being tested
     * @return true if this range contains the parameter value, or false otherwise
     */
    public boolean contains(T value);

    /**
     * Reports where a value occurs within this range.
     * <ul>
     * <li>0 is returned if the value is within the range. That is, the value is greater than or
     * equal to the start value and less than the end value.</li>
     * <li>+1 is returned if the value is higher than the range. That is, the value is greater than
     * or equal to the end value.</li>
     * <li>-1 is returned if the value is lower than the range. That is, the value is less than the
     * start value.</li>
     * </ul>
     * 
     * @param value
     * @return the value 0 if value is within the range, +1 if it is greater than the range, and
     * -1 if it is less than the range.
     */
    public int compareToRange(T value);

    
    public T getBeginValue ();
    
    
    public T getEndValue ();
    
}
