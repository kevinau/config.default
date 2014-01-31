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

import java.net.URL;


public class ServerHost {

  private static final int DEFAULT_PORT = 7070;
  
  
  private String host = null;
  private int port = -1;
  
  
  // Private constructor prevents instantiation from other classes
  private ServerHost() {
  }

  
  /**
   * SingletonHolder is loaded on the first execution of ServerAddress.getInstance() 
   * or the first access to SingletonHolder.INSTANCE, not before.
   */
  private static class SingletonHolder { 
    public static final ServerHost INSTANCE = new ServerHost();
  }

  
  public static ServerHost getInstance() {
    return SingletonHolder.INSTANCE;
  }

  
  public void setLocalHost () {
    setHost("localhost");
    setPort(DEFAULT_PORT);
  }
  
  
  public void setLocalHost (int port) {
    setHost("localhost");
    setPort(port);
  }
  
  
  public void setHostAndPort (URL url) {
    setHost(url.getHost());
    setPort(url.getPort());
  }
  
  
  public void setHostAndPort (String arg) {
    int n = arg.lastIndexOf(':');
    String host;
    int port;
    if (n == -1) {
      host = arg;
      port = DEFAULT_PORT;
    } else {
      host = arg.substring(0, n);
      port = Integer.parseInt(arg.substring(n + 1));
    }
    setHost(host);
    setPort(port);
  }
  
  
//  public void setServer (Applet applet) {
//    URL appletURL = applet.getDocumentBase();
//    String host = appletURL.getHost();
//    if (host == null) {
//      throw new IllegalStateException("Applet has not been initialized");
//    }
//    setHost (host);
//    setPort (appletURL.getPort());
//  }
 
  
  public void setHost (String host) {
    if (host == null) {
      throw new IllegalArgumentException("Host cannot be null");
    }
    
    if (this.host == null) {
      this.host = host;
    } else if (!this.host.equals(host)) {
      throw new IllegalStateException("Host " + this.host + " vs " + host);
    }
  }
  
  
  public void setPort (int port) {
    if (port <= 0 || port > 0xFFFF) {
      throw new IllegalArgumentException("Port number out of range");
    }
    if (this.port == -1) {
      this.port = port;
    } else if (this.port != port) {
      throw new IllegalStateException("Port " + this.port + " vs " + port);
    }
  }
  
  
  public String getHost () {
    if (host == null) {
      throw new IllegalStateException("Host has not been set");
    }
    return host;
  }
  
  
  public int getPort () {
    if (port == -1) {
      throw new IllegalStateException("Port has not been set");
    }
    return port;
  }


  @Override
  public String toString() {
    return "ServerHost [host=" + host + ", port=" + port + "]";
  }

}
