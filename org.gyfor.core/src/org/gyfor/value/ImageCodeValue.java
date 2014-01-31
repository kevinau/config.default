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
package org.gyfor.value;



public class ImageCodeValue extends CodeValue {

  private static final long serialVersionUID = 2216265814652748937L;

  private final Class<?> imageClass;
  private final String imageFileName;

//  protected static Image getImage (Class<?> imageClass, String imageFileName) {
//    this.imageClass = imageClass;
//    this.imageFileName = imageFileName;
//		ImageData source = new ImageData(klass.getResourceAsStream(imageFileName));
//		ImageData mask = source.getTransparencyMask();
//		return new Image(null, source, mask);
//  }
  

  public ImageCodeValue (String code, Class<?> imageClass, String imageFileName) {
    this (code, code, imageClass, imageFileName);
  }
  
  
  public ImageCodeValue (String code, String description, Class<?> imageClass, String imageFileName) {
    super (code, description);
    this.imageClass = imageClass;
    this.imageFileName = imageFileName;
  }
  
  
	public Class<?> getImageClass() {
		return imageClass;
	}
  
  
  public String getImageFileName() {
    return imageFileName;
  }
}
