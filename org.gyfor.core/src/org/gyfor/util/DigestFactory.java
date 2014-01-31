package org.gyfor.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DigestFactory {

  private static final String ALGORITHM = "MD5";
  
  private static class DigestOutputStream extends OutputStream {

    private final MessageDigest md;
    
    private DigestOutputStream () {
      try {
        md = MessageDigest.getInstance(ALGORITHM);
      } catch (NoSuchAlgorithmException ex) {
        throw new RuntimeException(ex);
      }
    }
    
    @Override
    public void write(int b) throws IOException {
      md.update((byte)b);
    }
    
    @Override
    public void write(byte[] b) throws IOException {
      md.update(b);
    }
    
    @Override
    public void write(byte[] b, int offset, int len) throws IOException {
      md.update(b, offset, len);
    }
    
    @Override
    public void close () {
    }
    
    @Override
    public void flush() {
    }
    
    private void reset () {
      md.reset();
    }
    
    private byte[] getDigest () {
      return md.digest();
    }
  }
  
  
  public static Digest getFileDigest (File file) {
    try {
      MessageDigest md = MessageDigest.getInstance(ALGORITHM);
      FileInputStream fis = new FileInputStream(file);
      byte[] dataBytes = new byte[1024];
      int n = fis.read(dataBytes); 
      while (n != -1) {
        md.update(dataBytes, 0, n);
        n = fis.read(dataBytes);
      }
      fis.close();
      return new Digest(md.digest());
    } catch (NoSuchAlgorithmException ex) {
      throw new RuntimeException(ex);
    } catch (FileNotFoundException ex) {
      throw new RuntimeException(ex);
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }
  

  public static Digest getObjectDigest (Object obj) {
    DigestOutputStream dos = new DigestOutputStream();
    dos.reset();
    try (
        ObjectOutputStream oss = new ObjectOutputStream(dos);
        ) {
      oss.writeUnshared(obj);
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
    return new Digest(dos.getDigest());
  }


}
