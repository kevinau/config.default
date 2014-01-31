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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class PasswordValue implements Serializable {

  private static final long serialVersionUID = 7369016814827844772L;

  private final String seed;
  private final byte[] encrypted;

  
	public PasswordValue (String seed, String password) {
	  this.seed = seed;
		this.encrypted = toHash(password);
	}
 
  
  @Override
  public String toString() {
  	return "";
  }
  
  
  public String getSeed () {
    return seed;
  }
  
  
//	private static final char[] hexChar = {'0', '1', '2', '3', '4', '5', '6', '7',
//																	       '8', '9', 'a', 'b', 'c', 'd', 'e', 'f',
//	};
//  
//
//	private static char[] toHex(byte[] value) {
//		char[] buff = new char[value.length * 2];
//		int j = 0;
//		for (int i = 0; i < value.length; i++) {
//			buff[j++] = hexChar[value[i] & 0xf];
//			buff[j++] = hexChar[(value[i] >> 4) & 0xf];
//		}
//		return buff;
//	}


	public byte[] toHash (String password) {
		MessageDigest algorithm = null;
		try {
			algorithm = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException ex) {
			throw new RuntimeException(ex);
		}
		algorithm.reset();
		String x = password + seed;
		algorithm.update(x.getBytes());
		byte[] hash = algorithm.digest();
		return hash;
	}
  
  
  @Override
  public boolean equals (Object obj) {
  	if (obj == null) {
  		return false;
  	}
  	if (!(obj instanceof PasswordValue)) {
  	  return false;
  	}
  	PasswordValue obj2 = (PasswordValue)obj;
    if (encrypted.length == obj2.encrypted.length) {
      for (int i = 0; i < encrypted.length; i++) {
        if (encrypted[i] != obj2.encrypted[i]) {
          return false;
        }
      }
      return true;
    } else {
      return false;
    }
  }
  
  
	public boolean matches (String password) {
	  byte[] hash = toHash(password);
    if (encrypted.length == hash.length) {
      for (int i = 0; i < encrypted.length; i++) {
        if (encrypted[i] != hash[i]) {
          return false;
        }
      }
      return true;
    } else {
      return false;
    }
	}


//	public void setInStmt (PreparedStatement stmt, int[] index) {
//		char[] hashValue = toHash(password);
//    try {
// 		  stmt.setString (index[0]++, new String(hashValue));
//    } catch (SQLException ex) {
//      throw new RuntimeException(ex);
//    }
//	}
//	
//	
//	public String asPriorValue () {
//		return null;
//	}

}

