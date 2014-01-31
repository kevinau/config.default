package org.gyfor.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * A properties file that is located in one of three standard places.
 * <p>
 * The properties file is located in one of the following places:
 * <ul>
 * <li>If the Java system property "config.home" is set, use that property value
 * as the name of the configuration directory. Look in that directory for the
 * property file.</li>
 * <li>Otherwise:
 * <ul>
 * <li>If the Java system property "catalina.home" (Tomcat system property) or
 * "java.home" is set, use that property value as the name of a directory.
 * Within that directory, use "config" as the name of the configuration
 * directory. Look in that directory for the property file.</li>
 * </ul>
 * </ul>
 * In short, use whichever of the following directories exists: <code>
 * $config.home
 * $catalina.home/config
 * $java.home/config
 * </code> Once the configuration directory is determined, it is assumed the
 * configuration file exists in that directory. That is, if no configuration
 * file is found in the configuration directory, an error is reported. Other
 * configuration directories are not searched.
 * 
 * @author Kevin Holloway
 * 
 */
public class RuntimeProperties extends Properties {

  private static final long serialVersionUID = 1L;

  private final String fileName;

  /**
   * Constructs a property list that is loaded from a file located in one of
   * three places:
   * <ul>
   * <li>$config.home</li>
   * <li>$catalina.home/config</li>
   * <li>$java.home/config</li>
   * </ul>
   * 
   * @param fileName
   *          - the name of the config file. This file must exist within
   *          whatever of the above directories is chosen.
   * @throws FileNotFoundException
   *           - if no config file is found within the chosen directory.
   */
  public RuntimeProperties(String fileName) throws FileNotFoundException {
    this.fileName = fileName;

    File configDir;
    String configHome = System.getProperty("config.home");
    if (configHome != null) {
      configDir = new File(configHome);
    } else {
      configHome = System.getProperty("catalina.home");
      if (configHome == null) {
        configHome = System.getProperty("user.home");
      }
      configDir = new File(configHome, "config");
    }

    File propsFile = new File(configDir, fileName);
    if (!propsFile.exists()) {
      throw new FileNotFoundException(propsFile.getAbsolutePath());
    }
    try {
      InputStream is = new FileInputStream(propsFile);
      super.load(is);
      is.close();
    } catch (FileNotFoundException ex) {
      throw new RuntimeException(ex);
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  /**
   * Searches for the property with the specified key in this property list.
   * <p>
   * If the key is not found in this property list, an invalid argument
   * exception is thrown.
   */
  @Override
  public String getProperty(String key) {
    String property = super.getProperty(key);
    if (property == null) {
      throw new IllegalArgumentException("No '" + key + "' value in property file "
          + fileName);
    }
    return property;
  }

}
