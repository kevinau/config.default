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

public enum EntityLife {

  /**
   * The entity can be referenced by other entities without restriction.
   */
  ACTIVE,
  
  /**
   * The entity may be still referenced by other entities, but it should not be referenced by new entities.
   * Existing entities cannot be changed to reference a retired entity.
   * <p>
   * A retired entity will be made history when it is not longer referenced by any other entity.
   */
  RETIRED,
  
  /**
   * The entity is not referenced by any other entity and can no longer be used.  It is kept only for
   * historical reference.
   */
  HISTORY;
  
}
