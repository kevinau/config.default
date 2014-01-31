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


import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class CommaOutputFile {
  public static final DateFormat dateFormatISO = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

  private PrintWriter fd;
  
  
  public CommaOutputFile (String path, boolean append) {
  	try {
      fd = new PrintWriter(new FileWriter(path, append));
  	} catch (IOException ex) {
  		throw new RuntimeException(ex.getMessage());
  	}
  }    


  public CommaOutputFile (String path) {
    this (path, false);
  }
  
  
  public void close () {
   	fd.close();
  }
  

  public void println (String line) {
    fd.println(line);
  }
  
  
  public void println (Object[] flds) {
    for (int i = 0; i < flds.length; i++) {
    	if (i > 0) {
    		fd.print(',');
    	}
    	if (flds[i] != null) {
    		if (flds[i] instanceof String) {
          String s = (String)flds[i];
          if (s.indexOf(',') == -1) {
            fd.print(s);
          } else {
            fd.print("\"" + s + "\"");
          }
    		} else if (flds[i] instanceof Date) {
    			fd.print(dateFormatISO.format((Date)flds[i]));
    		} else {
    			fd.print(flds[i].toString());
    		}
    	}
    }
    fd.println();
  }

}

