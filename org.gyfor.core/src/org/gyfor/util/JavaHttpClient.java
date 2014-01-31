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

import java.io.ByteArrayOutputStream;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.net.HttpURLConnection;
import java.net.URL;


public class JavaHttpClient {

  public static final int SC_CLIENT_CAUSED_EXCEPTION = 490;
  
  private final String serviceName;
  
  
  public JavaHttpClient () {
    this.serviceName = null;
  }
  
  public JavaHttpClient (String serviceName) {
    this.serviceName = serviceName;
  }
  
 
  public Object invoke(String methodName, Object... args) throws Exception {
    if (serviceName == null) {
      throw new RuntimeException("Service name not set");
    }
    return invokeService(serviceName, methodName, args);
  }
  
  
  public static Object invokeService(URL serverAddress, Object... args) throws Exception {
    for (Object arg : args) {
      if (!(arg instanceof Serializable)) {
        throw new NotSerializableException(arg.getClass().toString());
      }
    }
    
    String type = "application/octet-stream";

    System.out.println("JavaHttpClient: connection: " + serverAddress);
    ByteArrayOutputStream resultStream = new ByteArrayOutputStream(); 
    ObjectOutputStream objOutputStream = new ObjectOutputStream(resultStream); 
    for (Object arg : args) {
      System.out.println("JavaHttpClient: arg: " + arg);
      objOutputStream.writeObject(arg);
    }
    objOutputStream.close(); 
    byte[] byteArray = resultStream.toByteArray();
        
    // Set up out communications stuff
    HttpURLConnection connection = (HttpURLConnection)serverAddress.openConnection();
    connection.setRequestMethod("GET");
    connection.setRequestProperty("Content-Type", type);
    connection.setFixedLengthStreamingMode(byteArray.length);
    connection.setDoOutput(true);
    connection.setDoInput(true);
    connection.connect();
  
    // Write the request
    OutputStream os = connection.getOutputStream();
    os.write(byteArray);
    os.close();
  
    // Read the result from the server
    int responseCode = connection.getResponseCode();
    System.out.println("JavaHttpClient: responseCode: " + responseCode);
    if (responseCode == HttpURLConnection.HTTP_NO_CONTENT) {
      return null;
    } else if (responseCode == HttpURLConnection.HTTP_OK) {
      Object resultObject = null;
      ObjectInputStream objInputStream = new ObjectInputStream(connection.getInputStream());
      if (objInputStream.available() > 0) {
        // Some content is associated with this response
        resultObject = objInputStream.readObject();
      }
      objInputStream.close();
      return resultObject;
    } else if (responseCode == SC_CLIENT_CAUSED_EXCEPTION) {
      String exceptionName;
      String message = connection.getResponseMessage();
      System.out.println("JavaHttpClient: message: " + message);
      int n = message.indexOf(':');
      if (n == -1) {
        exceptionName = message;
        message = null;
      } else {
        exceptionName = message.substring(0, n).trim();
        message = message.substring(n + 1).trim();
      }
      System.out.println("JavaHttpClient: exception: " + exceptionName);
      @SuppressWarnings("unchecked")
      Class<Exception> exceptionClass = (Class<Exception>)Class.forName(exceptionName);
      System.out.println("JavaHttpClient: exclass: " + exceptionClass);
      System.out.println("JavaHttpClient: message: " + message);
      Exception ex;
      if (message == null) {
        ex = exceptionClass.newInstance();
      } else {
        Constructor<Exception> cons = exceptionClass.getDeclaredConstructor(String.class);
        cons.setAccessible(true);
        System.out.println("JavaHttpClient: constr: " + cons);
        ex = cons.newInstance(message);
      }
      throw ex;
    } else {
      throw new RuntimeException(responseCode + " - " + connection.getResponseMessage());
    }
  }

  
  public static Object invokeService(String pathInfo, Object... args) throws Exception {
    ServerHost server = ServerHost.getInstance();
    URL serverAddress = new URL("http", server.getHost(), server.getPort(), pathInfo);
    return invokeService(serverAddress, args);
  }

  
  public static Object invokeService(String serviceName, String methodName, Object... args) throws Exception {
    return invokeService(serviceName + "/" + methodName, args);
  }

}
