package org.gyfor.data;

import java.io.File;

public class DataLocationConfiguration {

  private File dataDir;
  
  private String schema;
  
  
  public DataLocationConfiguration () {
    // Default values
    String home = System.getProperty("user.home");
    dataDir = new File(home, ".pennyledger");
    schema = null;
  }
  
  
  public File getDataDir() {
    return dataDir;
  }
  
  
  public String getSchema() {
    return schema;
  }

}
