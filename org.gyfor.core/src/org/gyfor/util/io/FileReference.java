package org.gyfor.util.io;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

public class FileReference {

  private final File file;
  private final String name;
  private final String uriPath;
  
  
  public FileReference (File file, String name, String uriPath) {
    this.file = file;
    this.name = name;
    this.uriPath = uriPath;
  }
  
  
  public File getFile () {
    return file;
  }
  
  
  public String getName () {
    return name;
  }
  
  
  public URI getURI () {
    try {
      return new URI("http", null, null, -1, uriPath, null, null);
    } catch (URISyntaxException ex) {
      throw new RuntimeException(ex);
    }
  }
  
}
