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

public enum Ternary {

  TRUE,
  FALSE,
  UNKNOWN;
  
  public boolean booleanValue (boolean unknown) {
    switch (this) {
    case TRUE :
      return true;
    case FALSE :
      return false;
    default :
      return unknown;
    }
  }
  
  
  public boolean booleanValue () {
    switch (this) {
    case TRUE :
      return true;
    case FALSE :
      return false;
    default :
      throw new RuntimeException("value is UNKNOWN");
    }
  }
  
  
  public static Ternary valueOf (boolean value) {
    return value ? TRUE : FALSE;
  }
}
