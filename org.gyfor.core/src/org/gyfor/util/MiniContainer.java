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
package org.gyfor.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class MiniContainer {

  private Map<Class<?>, Object> map = new HashMap<Class<?>, Object>();
  
  
  public void addComponent (Class<?> klass) {
    map.put(klass, null);  
  }
  
  
  public void addComponent (Class<?> klass, Object value) {
    map.put(klass, value);  
  }
  
  
  public <T> T newInstance (Class<T> klass) {
    return getComponent(klass);
  }
  
  
  private Object newInternalInstance (Class<?> klass) {
    try {
      Constructor<?>[] constructors = klass.getConstructors();
      // If there is more than one constructor, we are assuming the order 
      // does not matter.  We should sort this array by increasing number of parameters
      
      for (Constructor<?> constructor : constructors) {
        Class<?>[] argTypes = constructor.getParameterTypes();
        Object[] argValues = new Object[argTypes.length];
        int i = 0;
        for (Class<?> argType : argTypes) {
          Object value = getComponent(argType);

          if (value == null) {
            break;
          }
          argValues[i] = value;
          i++;
        }
        if (i == argValues.length) {
          Object instanceValue = constructor.newInstance(argValues);
          return instanceValue;
        }
      }
    } catch (SecurityException ex) {
      throw new RuntimeException(ex);
    } catch (IllegalArgumentException ex) {
      throw new RuntimeException(ex);
    } catch (InstantiationException ex) {
      throw new RuntimeException(ex);
    } catch (IllegalAccessException ex) {
      throw new RuntimeException(ex);
    } catch (InvocationTargetException ex) {
      throw new RuntimeException(ex);
    }
    throw new RuntimeException("No satisfied constructor for " + klass);
  }
  
  
  private boolean isApproxEqual (Class<?> target, Class<?> key) {
    if (target.equals(Object.class)) {
      return key.equals(Object.class);
    } else {
      return target.isAssignableFrom(key);
    }
  }
  
  
  @SuppressWarnings("unchecked")
  private <T> T getComponent(Class<T> klass) {
    for (Class<?> key : map.keySet()) {
      if (isApproxEqual(klass, key)) {
        Object value = map.get(key);
        if (value == null) {
          value = newInternalInstance(key);
          map.put(key, value);
          Method[] methods = key.getMethods();
          for (Method method : methods) {
            String methodName = method.getName();
            if (methodName.startsWith("set") && methodName.length() > 3 && method.getReturnType().equals(Void.TYPE) && Character.isUpperCase(methodName.charAt(3))) {
              Class<?>[] params = method.getParameterTypes();
              if (params.length == 1) {
                // This is a set method, with one parameter.  If there is a matching map entry, use that to set a parameter
                // using this method
                Object attributeValue = getComponent(params[0]);
                if (attributeValue != null) {
                  try {
                    method.invoke(value, attributeValue);
                  } catch (IllegalArgumentException ex) {
                    throw new RuntimeException(ex);
                  } catch (IllegalAccessException ex) {
                    throw new RuntimeException(ex);
                  } catch (InvocationTargetException ex) {
                    throw new RuntimeException(ex);
                  }
                }
              }
            }

          }
        }
        return (T)value;
      }
    }
    return null;
  }
}
