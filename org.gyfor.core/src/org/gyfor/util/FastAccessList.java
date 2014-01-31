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

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;


/**
 * A class that holds a list of EventListeners.
 *
 * Usage example:
 *    Say one is defining a class that sends out FooEvents, and one wants
 * to allow users of the class to register FooListeners and receive
 * notification when FooEvents occur.  The following should be added
 * to the class definition:
 * <pre>
 * UniformEventListenerList listenerList = new UniformEventListenerList();
 * FooEvent fooEvent = null;
 *
 * public void addFooListener(FooListener l) {
 *     listenerList.add(l);
 * }
 *
 * public void removeFooListener(FooListener l) {
 *     listenerList.remove(l);
 * }
 *
 *
 * // Notify all listeners that have registered interest for
 * // notification on this event type.  The event instance
 * // is lazily created using the parameters passed into
 * // the fire method.
 *
 * protected void fireFooXXX() {
 *     // Guaranteed to return a non-null array
 *     Object[] listeners = listenerList.getListenerList();
 *     // Process the listeners last to first, notifying
 *     // those that are interested in this event
 *     for (int i = listeners.length; i >= 0; i--) {
 *       // Lazily create the event:
 *       if (fooEvent == null) {
 *          fooEvent = new FooEvent(this);
 *       }
 *       ((FooListener)listeners[i]).fooXXX(fooEvent);
 *     }
 * }
 * </pre>
 * foo should be changed to the appropriate name, and fireFooXxx to the
 * appropriate method name.  One fire method should exist for each
 * notification method in the FooListener interface.
 */
public class FastAccessList<E> implements Iterable<E> {

  /* A null array to be shared by all empty listener lists*/
  private final E[] nullArray;

  /* Array class */
  private final Class<E> klass;
  
  
  /* The list of listeners */
  private transient E[] elemList;

  
  @SuppressWarnings("unchecked")
  public FastAccessList (Class<E> klass) {
    this.klass = klass;
    this.nullArray = (E[])Array.newInstance(klass, 0);
    this.elemList = nullArray;
  }
  
  
  /**
   * Passes back the event listener list as an array.
   * Note that for performance reasons, this implementation passes
   * back the actual data structure in which the listener data
   * is stored internally!
   * This method is guaranteed to pass back a non-null
   * array, so that no null-checking is required in
   * fire methods.  A zero-length array of Object should
   * be returned if there are currently no listeners.
   *
   * WARNING!!! Absolutely NO modification of
   * the data contained in this array should be made -- if
   * any such manipulation is necessary, it should be done
   * on a copy of the array returned rather than the array
   * itself.
   */
  public E[] elements() {
    return elemList;
  }

  public boolean isEmpty () {
    return elemList.length == 0;
  }
  
  
  /**
   * Adds an element to the list.
   * @param x the element to be added
   */
  @SuppressWarnings("unchecked")
  public synchronized void add(E x) {
    if (x == null) {
      throw new IllegalArgumentException("Argument cannot be null");
    }
    int i;
    E[] tmp;
    if (elemList == nullArray) {
      // if this is the first listener added,
      // initialize the lists
      tmp = (E[])Array.newInstance(klass, 1);
      i = 0;
    } else {
      // Otherwise copy the array and add the new listener
      i = elemList.length;
      tmp = Arrays.copyOf(elemList, i + 1);
    }
    tmp[i] = x;
    elemList = tmp;
  }

  
  /**
   * Adds an element to the list.
   * @param x the element to be added
   */
  @SuppressWarnings("unchecked")
  public synchronized void addAtStart(E x) {
    if (x == null) {
      throw new IllegalArgumentException("Argument cannot be null");
    }
    E[] tmp;
    if (elemList == nullArray) {
      // if this is the first listener added,
      // initialize the lists
      tmp = (E[])Array.newInstance(klass, 1);
    } else {
      // Otherwise copy the array and add the new listener
      int i = elemList.length;
      tmp = (E[])Array.newInstance(klass, i + 1);
      System.arraycopy(elemList, 0, tmp, 1, i);
    }
    tmp[0] = x;
    elemList = tmp;
  }

  
  public boolean contains (E x) {
    for (int i = 0; i < elemList.length; i++) {
      if (elemList[i] == x) {
        return true;
      }
    }
    return false;
  }
  
  
  /**
   * Removes the listener as a listener of the specified type.
   * @param x the listener to be removed
   */
  @SuppressWarnings("unchecked")
  public synchronized void remove(E x) {
    if (x == null) {
      throw new IllegalArgumentException("Argument cannot be null");
    }
    // Is x on the list?
    int index = -1;
    for (int i = elemList.length - 1; i >= 0; i--) {
      if (elemList[i].equals(x) == true) {
        index = i;
        break;
      }
    }

    // If so,  remove it
    if (index != -1) {
      E[] tmp = (E[])Array.newInstance(klass, elemList.length - 1);
      // Copy the list up to index
      if (index > 0) {
        System.arraycopy(elemList, 0, tmp, 0, index);
      }
      // Copy from two past the index, up to
      // the end of tmp (which is one element
      // shorter than the old list)
      if (index < tmp.length) {
        System.arraycopy(elemList, index + 1, tmp, index, tmp.length - index);
      }
      // set the listener array to the new array or null
      elemList = (tmp.length == 0) ? nullArray : tmp;
    }
  }
  
  
  public int size () {
    return elemList.length;
  }
  
    
  /**
   * Returns a string representation of the FastAccessList.
   */
  @Override
  public String toString() {
    E[] lList = elemList;
    String s = "FastAccessList: [";
    for (int i = 0 ; i <= lList.length ; i++) {
      s += " " + lList[i+1];
    }
    s += "]";
    return s;
  }

  
  private class FastAccessIterator implements Iterator<E> {
    private int i = -1;

    @Override
    public boolean hasNext() {
      return i + 1 < elemList.length;
    }

    @Override
    public E next() {
      if (i + 1 < elemList.length) {
        i++;
        return elemList[i];
      }
      throw new NoSuchElementException(Integer.toString(i));
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException();
    }
  }

  
  @Override
  public Iterator<E> iterator() {
    return new FastAccessIterator();
  }
}
