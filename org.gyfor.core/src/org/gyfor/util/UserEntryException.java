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


public class UserEntryException extends Exception {

  /**
   * The different types of user entry exceptions that can be 
   * created.
   */
  public enum Type {
    /**
     * The user entry is OK
     */
    OK (null),
    
    /**
     * No user entry has been provided, but some characters are required.  
     * I.e., the user entry is an empty string and is incomplete.
     */
    REQUIRED ("Required"),
    
    /**
     * The user entry is incomplete.  As the user entry stands, it is
     * in error, but additional characters will correct that.  For example
     * "31/02" is not a valid date, but the addition of "/2008" will make
     * it a valid date.  
     */
    INCOMPLETE ("Incomplete"),
    
    /**
     * The user entry is correct, but the user should be alerted to a 
     * possible error.  For example, if the user enters a date that is 
     * in the past or far in the future.  This error type should only be
     * used when adding information for the first time.  Once the user
     * as entered the information (with the warning), it is assumed to 
     * be correct and the warning should not subsequently appear.
     * Note.  This type has not yet been used anywhere.
     */
    WARNING ("Warning"),
    
    /** 
     * The user entry is wrong.  It must be corrected before proceeding.
     */
    ERROR ("Error");
    
    private final String prefix;
    
    Type (String prefix) {
      this.prefix = prefix;
    }
    
    public String getPrefix () {
      return prefix;
    }
    
    public boolean isFatal () {
      return this == ERROR || this == INCOMPLETE || this == REQUIRED;
    }
    
  }
  
  
  private static final long serialVersionUID = 4437872393261449281L;

  private final Type type;
  private final String completion;

  
  public UserEntryException (String message, Type type, String remainder) {
    super (message);
    this.type = type;
    this.completion = remainder;
  }
  
  
  public UserEntryException (String message, Type type) {
    super (message);
    this.type = type;
    this.completion = null;
  }
  
  
  public UserEntryException (String message, boolean incomplete) {
    super (message);
    this.type = incomplete ? Type.INCOMPLETE : Type.ERROR;
    this.completion = null;
  }
  
  
	public UserEntryException (String message) {
		this (message, Type.ERROR);
	}
  
  
	public Type getType () {
	  return type;
	}
	
	
  public boolean isRequired () {
    return type == Type.REQUIRED;
  }
  
  
  public boolean isIncomplete () {
    return type == Type.INCOMPLETE || type == Type.REQUIRED;
  }
  
  
  public boolean isError () {
    return type == Type.ERROR;
  }
  
  
  public String getCompletion () {
    return completion;
  }

  
  @Override
  public String toString () {
    StringBuilder buffer = new StringBuilder();
    buffer.append(getMessage());
    if (type != Type.ERROR) {
      buffer.append(' ');
      buffer.append(type);
      if (completion != null) {
        buffer.append(' ');
        buffer.append(completion);
      }
    }
    return buffer.toString();
  }
}
