package org.gyfor.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.commons.codec.binary.Base64;

public class ObjectStringSerializer {

  public static String toString(Object config) {
    try {
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      ObjectOutputStream out = new ObjectOutputStream(bos);
      out.writeObject(config);
      out.close();
      byte[] base64 = Base64.encodeBase64(bos.toByteArray());
      return new String(base64);
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }
  
  
  @SuppressWarnings("unchecked")
  public static <T> T fromString(String s) {
    try {
      byte[] bytes = Base64.decodeBase64(s.getBytes());
      ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
      ObjectInputStream in = new ObjectInputStream(bis);
      return (T)in.readObject();
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    } catch (ClassNotFoundException ex) {
      throw new RuntimeException(ex);
    }
  }

  
  public static <T> T fromString(Object obj) {
	return fromString((String)obj);
  }

}
