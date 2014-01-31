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



public class TreeValue extends CodeValue {

  private static final long serialVersionUID = -3679834723224689234L;


  public TreeValue (String code, String label, boolean obsolete) {
		super(code, label, obsolete);
	}
  
  
  public TreeValue (String code, String label) {
    this (code, label, false);
  }
  
}
