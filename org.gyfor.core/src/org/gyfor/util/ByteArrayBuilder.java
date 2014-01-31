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

public class ByteArrayBuilder {

  private static final int INITIAL_SIZE = 1024;
  
  byte[] buffer;
  int fillCount;
  
  
  public ByteArrayBuilder (int size) {
    buffer = new byte[size];
  }
  
  
  public ByteArrayBuilder () {
    this (INITIAL_SIZE);
  }
  
  
  public void append (byte b) {
    if (fillCount + 1 > buffer.length) {
      extendBuffer();
    }
    buffer[fillCount++] = b;
  }
  
  
  public void append (byte[] data, int offset, int length) {
    while (fillCount + length > buffer.length) {
      extendBuffer();
    }
    System.arraycopy(data, offset, buffer, fillCount, length);
    fillCount += length;
  }
  
  
  public void append (byte[] data) {
    append (data, 0, data.length);
  }
  
  
  public byte[] getBuffer () {
    return buffer;
  }
  
  
  public byte[] toByteArray () {
    if (fillCount == buffer.length) {
      return buffer;
    } else {
      byte[] bx = new byte[fillCount];
      System.arraycopy(buffer, 0, bx, 0, fillCount);
      return bx;
    }
  }
  
  
  public String toString () {
    return new String(buffer, 0, fillCount);
  }
  
  
  public int getSize () {
    return fillCount;
  }
  
  
  /**
   * Extends the buffer by doubling its size.  The buffer is extended as necessary to 
   * store all of a single FIX message.  This will happen as longer messages are 
   * received.  The buffer size is never reduced--it stays at the size necessary to
   * store the longest FIX message. 
   */
  private void extendBuffer () {
    int n = buffer.length;
    // Double the buffer size
    byte[] newBuffer = new byte[n * 2];
    System.arraycopy(buffer, 0, newBuffer, 0, n);
    buffer = newBuffer;
  }

}
