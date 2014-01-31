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


import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;


public class CommaInputFile implements ICSVReader {
	private final String path;
	private final char fieldDelimiter;
	private final boolean isQuotedStrings;
	
  private LineNumberReader fd;
  private String line;
  
  
  private class Exception extends RuntimeException {

    private static final long serialVersionUID = -4792125579063048120L;

    private Exception (String message) {
  		super (path + ": " + message + (fd == null ? "" : " at line " + fd.getLineNumber()));
  	}
  }
  	
  	
  public CommaInputFile (String path) {
  	this(path, ',', true);
  }
  
  
	public CommaInputFile (String path, char fieldDelimiter) {
		this(path, fieldDelimiter, true);
	}


  public CommaInputFile (String path, char fieldDelimiter, boolean isQuotedStrings) {
  	this.path = path;
  	this.fieldDelimiter = fieldDelimiter;
  	this.isQuotedStrings = isQuotedStrings;
  	try {
      fd = new LineNumberReader(new FileReader(path));
      fd.mark(0);
  	} catch (FileNotFoundException ex) {
  		throw new RuntimeException(ex);
  	} catch (IOException ex) {
  		throw new RuntimeException(ex);
  	}
  }    


  @Override
  public void close () {
  	try {
    	fd.close();
  	} catch (IOException ex) {
  		throw new RuntimeException(ex.getMessage());
  	}
  }
  

  public void rewind () {
  	try {
    	fd.reset();
  	} catch (IOException ex) {
  		throw new Exception(ex.getMessage());
  	}
  }
  
  

  private String readLine () {
  	try {
    	String line = fd.readLine();
    	if (line != null) {
  	  	line = line.trim();
    	}
    	return line;
  	} catch (IOException ex) {
  		throw new Exception(ex.getMessage());
  	}
  }
  

  public RuntimeException createException (String message) {
  	return new Exception(message);
  }
  
  
  /* An optimised version of the read.  By specifying the maximum number
   * of fields, a String[] can be used in place of a List. */
  public String[] read (int minFlds, int maxFlds) {
    String[] flds = new String[maxFlds];
    
    line = readLine();
    /* Skip any blank lines or comment lines. */
    while (line != null && (line.length() == 0 || line.charAt(0) == '#')) {
      line = readLine();
    }
    if (line == null) {
    	return null;
    } else {
      char[] buff = line.toCharArray();
      int fldCount = 0;
      int i = 0;
      while (i < buff.length) {
      	if (isQuotedStrings && (buff[i] == '"' || buff[i] == '\'')) {
      		char quote = buff[i];
      		int start = ++i;
      		while (i < buff.length) {
      			if (buff[i] == quote && (i + 1 == buff.length || buff[i + 1] == fieldDelimiter)) {
      				break;
      			}
      			i++;
      		}
      		if (i == buff.length) {
      			throw new Exception("Un-terminated quote (" + quote + "), column " + i);
      		}
       		flds[fldCount++] = new String(buff, start, i - start);
      		/* Step over trailing quote */
      		i++;
      		/* Step over following comma */
      		if (i < buff.length) {
      			if (buff[i] == fieldDelimiter) {
      				i++;
      			} else {
      				throw new Exception("Expecting field delimiter (" + fieldDelimiter + "), column " + i);
      			}
      		}
      	} else {
      		int start = i;
      		while (i < buff.length && buff[i] != fieldDelimiter) {
      			i++;
      		}
       		flds[fldCount++] = new String(buff, start, i - start);
      		if (i < buff.length) {
      			/* Step over comma */
      			i++;	
      		}
      	}
      	if (fldCount == maxFlds) {
      		break;
      	}      		
      }
      if (fldCount < minFlds) {
      	throw new Exception("too few fields, " + minFlds + " required");
      }
      while (fldCount < maxFlds) {
      	flds[fldCount++] = "";
      }
      return flds;
    }
  }


  public String[] read (int minFlds) {
    List<String> flds = null;
    
    line = readLine();
    /* Skip any blank lines or comment lines. */
    while (line != null && (line.length() == 0 || line.charAt(0) == '#')) {
      line = readLine();
    }
    if (line == null) {
    	return null;
    } else {
    	flds = new ArrayList<String>();
      char[] buff = line.toCharArray();
      int i = 0;
      while (i < buff.length) {
      	if (isQuotedStrings && (buff[i] == '"' || buff[i] == '\'')) {
      		char quote = buff[i];
      		int start = ++i;
      		while (i < buff.length) {
      			if (buff[i] == quote && (i + 1 == buff.length || buff[i + 1] == fieldDelimiter)) {
      				break;
      			}
      			i++;
      		}
      		if (i == buff.length) {
      			throw new Exception("Un-terminated quote (" + quote + "): column " + i);
      		}
       		flds.add(new String(buff, start, i - start));
      		/* Step over trailing quote */
      		i++;
      		/* Step over following comma */
      		if (i < buff.length) {
      			if (buff[i] == fieldDelimiter) {
      				i++;
      			} else {
      				throw new Exception("Expecting field delimiter (" + fieldDelimiter + "), column " + i);
      			}
      		}
      	} else {
      		int start = i;
      		while (i < buff.length && buff[i] != fieldDelimiter) {
      			i++;
      		}
       		flds.add(new String(buff, start, i - start));
      		if (i < buff.length) {
      			/* Step over comma */
      			i++;	
      		}
      	}      		
      }
      if (flds.size() < minFlds) {
      	throw new Exception("too few fields, " + minFlds + " required");
      }
      return flds.toArray(new String[flds.size()]);
    }
  }


  @Override
  public String[] read () {
  	return read (0);
  }
  
  
  public String getRawLine () {
  	return line;
  }
  
}

