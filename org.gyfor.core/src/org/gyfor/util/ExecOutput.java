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

import java.io.Serializable;

public class ExecOutput implements Serializable {

  private static final long serialVersionUID = 1L;

  private final int exitValue;
  
  private final String output;
  
  private final String error;

  public ExecOutput(int exitValue, String output, String error) {
    this.exitValue = exitValue;
    this.output = output;
    this.error = error;
  }

  public int getExitValue() {
    return exitValue;
  }

  public String getOutput() {
    return output;
  }

  public String getError() {
    return error;
  }
  
}
