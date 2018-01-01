#set( $symbol_dollar = '$' )
#set( $symbol_pound = '#' )
#set( $symbol_escape = '\' )
/*******************************************************************************
 * Copyright (c) 2013 BSI Business Systems Integration AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSI CRM Software License v1.0
 * which accompanies this distribution as bsi-v10.html
 *
 * Contributors:
 *     BSI Business Systems Integration AG - initial API and implementation
 ******************************************************************************/
package org.eclipse.scout.docx4j;

import org.docx4j.wml.P;

/**
 * Strategy for processing complex fields (i.e. document variables and other functions) of a docx document.
 * 
 * @since 1.0.0
 */
public interface IDocxComplexFieldProcessor {

  /**
   * Call back method invoked by {@link DocxAdapter${symbol_pound}processComplexFields(String, IDocxComplexFieldProcessor)}.
   * 
   * @param p
   *          the parent paragraph of the given complexField.
   * @param complexField
   *          the complex field to process.
   * @return Returns <code>true</code> if any changes were made to the document. <code>false</code> otherwise.
   */
  boolean processField(P p, DocxComplexField complexField);
}
