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


public class CodeValue extends TextValue implements ICodeValue {

  private static final long serialVersionUID = -4174110261045376592L;

  private final String description;
  private final boolean obsolete;
  private final boolean selfDescribing; 

  
	public CodeValue (String code, String description, boolean obsolete) {
		super(code);
		this.description = description;
		this.obsolete = obsolete;
    this.selfDescribing = code.equals(description);
	}
  
  
  public CodeValue (String code, String description) {
    this (code, description, false);
  }
  
  
  public CodeValue (String code) {
    this (code, code, false);
  }
  
  
  public String getCode () {
    return super.toString();
  }
  
  
  public String getDescription () {
    return description;
  }
  
  
  public boolean isSelfDescribing () {
    return selfDescribing;
  }
  
  
  @Override
  public String asPriorValue () {
    if (selfDescribing) {
      return super.toString();
    } else {
      return super.toString() + " " + description;
    }
  }

  

  public boolean isObsolete() {
  	return obsolete;
  }

}
