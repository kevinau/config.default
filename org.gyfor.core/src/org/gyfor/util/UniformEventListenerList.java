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


import java.util.EventListener;

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
public class UniformEventListenerList {

  /* A null array to be shared by all empty listener lists*/
  private final static EventListener[] NULL_ARRAY = new EventListener[0];

  /* The list of listeners */
  private transient EventListener[] listenerList = NULL_ARRAY;

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
  public EventListener[] listeners() {
    return listenerList;
  }

  /**
   * Returns the total number of listeners for this listener list.
   */
  public int getListenerCount() {
    return listenerList.length;
  }

  
  public boolean isEmpty () {
    return listenerList.length == 0;
  }
  
  
  /**
   * Adds the listener as a listener of the specified type.
   * @param x the listener to be added
   */
  public synchronized void add(EventListener x) {
    if (x == null) {
      // In an ideal world, we would do an assertion here
      // to help developers know they are probably doing
      // something wrong
      return;
    }
    if (listenerList == NULL_ARRAY) {
      // if this is the first listener added,
      // initialize the lists
      listenerList = new EventListener[] { x };
    } else {
      // Otherwise copy the array and add the new listener
      int i = listenerList.length;
      EventListener[] tmp = new EventListener[i + 1];
      System.arraycopy(listenerList, 0, tmp, 0, i);
      tmp[i] = x;
      listenerList = tmp;
    }
  }

  
  /**
   * Adds the listener as a listener of the specified type.
   * @param x the listener to be added
   */
  public synchronized void addAtStart(EventListener x) {
    if (x == null) {
      // In an ideal world, we would do an assertion here
      // to help developers know they are probably doing
      // something wrong
      return;
    }
    if (listenerList == NULL_ARRAY) {
      // if this is the first listener added,
      // initialize the lists
      listenerList = new EventListener[] { x };
    } else {
      // Otherwise copy the array and add the new listener
      int i = listenerList.length;
      EventListener[] tmp = new EventListener[i + 1];
      System.arraycopy(listenerList, 0, tmp, 1, i);
      tmp[0] = x;
      listenerList = tmp;
    }
  }

  
  public boolean contains (EventListener x) {
    for (int i = 0; i < listenerList.length; i++) {
      if (listenerList[i] == x) {
        return true;
      }
    }
    return false;
  }
  
  
  /**
   * Removes the listener as a listener of the specified type.
   * @param x the listener to be removed
   */
  public synchronized void remove(EventListener x) {
    if (x == null) {
      // In an ideal world, we would do an assertion here
      // to help developers know they are probably doing
      // something wrong
      return;
    }
    // Is x on the list?
    int index = -1;
    for (int i = listenerList.length - 1; i >= 0; i--) {
      if (listenerList[i].equals(x) == true) {
        index = i;
        break;
      }
    }

    // If so,  remove it
    if (index != -1) {
      EventListener[] tmp = new EventListener[listenerList.length - 1];
      // Copy the list up to index
      if (index > 0) {
        System.arraycopy(listenerList, 0, tmp, 0, index);
      }
      // Copy from two past the index, up to
      // the end of tmp (which is one element
      // shorter than the old list)
      if (index < tmp.length) {
        System.arraycopy(listenerList, index + 1, tmp, index, tmp.length - index);
      }
      // set the listener array to the new array or null
      listenerList = (tmp.length == 0) ? NULL_ARRAY : tmp;
    }
  }
  
  
  public int size () {
  	return listenerList.length;
  }
  
    
  /**
   * Returns a string representation of the EventListenerList.
   */
  @Override
  public String toString() {
    EventListener[] lList = listenerList;
    String s = "EventListenerList: ";
    s += lList.length + " listeners: ";
    for (int i = 0 ; i <= lList.length ; i++) {
      s += " listener " + lList[i+1];
    }
    return s;
  }
}
