package org.gyfor.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.ValidationEventLocator;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;


public class JaxbBasedFileLoader<T> {

  private class ReportValidationEventHandler implements ValidationEventHandler {
    
    private boolean errorFound = false;
    
    public boolean handleEvent (ValidationEvent ve) {
      if (ve.getSeverity() == ValidationEvent.FATAL_ERROR || ve.getSeverity() == ValidationEvent.ERROR) {
        ValidationEventLocator locator = ve.getLocator();
        errorFound = true;
        System.out.println("Error: " + ve.getMessage() + ", line " + locator.getLineNumber() + ", column " + locator.getColumnNumber());
      }
      return true;
    }
    
    private boolean wasErrorFound () {
      return errorFound;
    }
  }
  
  
  private Unmarshaller unmarshaller;
  private ReportValidationEventHandler eventHandler;
  

  public JaxbBasedFileLoader (String schemaName, Class<T> klass) {
    try {
      SchemaFactory sf = SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
      URL schemaURL = klass.getResource(schemaName);
      if (schemaURL == null) {
        throw new RuntimeException("Cannot read schema: " + schemaName);
      }
      
      Schema schema = sf.newSchema(schemaURL);
      
      JAXBContext jc = JAXBContext.newInstance(klass);
      unmarshaller = jc.createUnmarshaller();
      unmarshaller.setSchema(schema);
      
      eventHandler = new ReportValidationEventHandler();
      unmarshaller.setEventHandler(eventHandler);
    } catch (SAXException ex) {
      throw new RuntimeException(ex);
    } catch (JAXBException ex) {
      throw new RuntimeException(ex);
    }
  }
  
  
  @SuppressWarnings("unchecked")
  public T load (InputStream inputStream) {
    T rootElem;

    // Load the report layout file
    try {
      rootElem = (T)unmarshaller.unmarshal(inputStream);
    } catch (JAXBException ex) {
      throw new RuntimeException(ex);
    }
    if (wasErrorFound()) {
      throw new RuntimeException("Error(s) found parsing inputStream");
    }
    return rootElem;
  }


  @SuppressWarnings("unchecked")
  public T load (File file) throws FileNotFoundException {
    T rootElem;

    // Load the report layout file
    try {
      InputStream inputStream = new FileInputStream(file);
      rootElem = (T)unmarshaller.unmarshal(inputStream);
    } catch (JAXBException ex) {
      throw new RuntimeException(ex);
    }
    if (wasErrorFound()) {
      throw new RuntimeException("Error(s) found parsing: " + file.getAbsolutePath());
    }
    return rootElem;
  }


  public boolean wasErrorFound () {
    return eventHandler.wasErrorFound();
  }

}
