package org.gyfor.data;

import java.io.File;
import java.util.Map;

import org.gyfor.osgi.OSGi;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;

@Component(service=DataLocation.class, configurationPolicy=ConfigurationPolicy.REQUIRE, immediate=true)
public class DataLocation {

  private DataLocationConfiguration config;
  
  
  @Activate
  public void activate (Map<String, Object> props) {
    config = OSGi.getConfiguration2(props, DataLocationConfiguration.class);
  }
  
  
  @Deactivate
  public void deactivate (Map<String, Object> props) {
  }
  
  
  public File getDataDir () {
    return config.getDataDir();
  }
  
  
  public String getSchema () {
    return config.getSchema();
  }
  
  
  public String getTableName (String simpleName) {
    String schema = getSchema();
    if (schema == null || schema.length() == 0) {
      return simpleName;
    } else {
      return schema + "." + simpleName;
    }
  }
  
  
  public File getDataBasedDir (String dirName) {
    String prefix = "{data}/";
    
    if (dirName.startsWith(prefix)) {
      return new File(getDataDir(), dirName.substring(prefix.length()));
    } else {
      return new File(dirName);
    }
  }
  
  
  public File getDataBasedFile (String dirName, String fileName) {
    File dir = getDataBasedDir(dirName);
    return new File(dir, fileName);
  }
  
}
