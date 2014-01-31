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


import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class TextValue implements Serializable {

  private static final long serialVersionUID = -2896313508645333080L;
  
  private final String s;

  public TextValue(String s) {
    this.s = s;
  }


  @Override
  public String toString() {
    return s;
  }


  public String toSqlLiteral() {
    StringBuffer buffer = new StringBuffer(s.length() + 4);
    buffer.append('\'');
    int n0 = 0;
    int n = s.indexOf('\'');
    while (n != -1) {
      buffer.append(s.substring(n0, n + 1));
      buffer.append('\'');
      n0 = n + 1;
      n = s.indexOf('\'', n0);
    }
    buffer.append(s.substring(n0));
    buffer.append('\'');
    return buffer.toString();
  }


  public void setInStmt(PreparedStatement stmt, int[] index) {
    try {
      stmt.setString(index[0]++, s);
    } catch (SQLException ex) {
      throw new RuntimeException(ex);
    }
  }


  public String asPriorValue () {
  	return toString();
  }
  

  @Override
  public boolean equals(Object other) {
  	if (this == other) {
  		return true;
  	}
    if (other == null) {
      return false;
    }
    if (!(other instanceof TextValue)) {
    	return false;
    }
    return this.s.equals(((TextValue)other).s);
  }


  public int length () {
  	return s.length();
  }
  
}
