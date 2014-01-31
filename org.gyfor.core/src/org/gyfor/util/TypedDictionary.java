package org.gyfor.util;

import java.io.File;
import java.util.Dictionary;

public class TypedDictionary {

  private final Dictionary<String, Object> props;
  private final String dictionaryName;
  
  public TypedDictionary (Dictionary<String, Object> props) {
    this.props = props;
    this.dictionaryName = null;
  }

  
  public TypedDictionary (Dictionary<String, Object> props, String dictionaryName) {
    this.props = props;
    this.dictionaryName = dictionaryName;
  }

  
  @SuppressWarnings("unchecked")
  public <T> T getClassInstance (String name) {
    String className = (String)props.get(name);
    if (className == null) {
      throw new RuntimeException("No '" + name + "' property" + (dictionaryName == null ? "" : " in " + dictionaryName));
    }
    T instance;
    try {
      instance = (T)Class.forName(className).newInstance();
    } catch (InstantiationException ex) {
      throw new RuntimeException(ex);
    } catch (IllegalAccessException ex) {
      throw new RuntimeException(ex);
    } catch (ClassNotFoundException ex) {
      throw new RuntimeException(ex);
    }
    return instance;
  }

  
  public File getDirectory (String name) {
    String dirName = (String)props.get(name);
    if (dirName == null) {
      throw new RuntimeException("No '" + name + "' property" + (dictionaryName == null ? "" : " in " + dictionaryName));
    }
    File dir = new File(dirName);
    if (!dir.exists() || !dir.isDirectory()) {
      throw new IllegalStateException("'" + name + "' property: " + dirName + ", does not name a directory");
    }
    return dir;
  }

  
  public boolean getBoolean (String name, boolean defaultValue) {
    Boolean value = (Boolean)props.get(name);
    if (value == null) {
      return defaultValue;
    } else {
      return value;
    }
  }

  
  public boolean getBoolean (String name) {
    Boolean value = (Boolean)props.get(name);
    if (value == null) {
      throw new RuntimeException("No '" + name + "' property" + (dictionaryName == null ? "" : " in " + dictionaryName));
    }
    return value;
  }

  
  public String getString (String name, String defaultValue) {
    String value = (String)props.get(name);
    if (value == null) {
      return defaultValue;
    } else {
      return value;
    }
  }


  public String getString (String name) {
    String value = (String)props.get(name);
    if (value == null) {
      throw new RuntimeException("No '" + name + "' property" + (dictionaryName == null ? "" : " in " + dictionaryName));
    }
    return value;
  }

}
