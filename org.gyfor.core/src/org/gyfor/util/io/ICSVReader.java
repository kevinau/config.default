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
package org.gyfor.util.io;

/**
 * @author Kevin Holloway, Gecko Software
 */
public interface ICSVReader {

  /** Returns an array of object values, or null at end of file. */
  public String[] read ();
  
  /** Free up any resources. */
  public void close();
}
