#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
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

import javax.xml.bind.JAXBException;

import org.eclipse.scout.rt.platform.exception.ProcessingException;

/**
 * Classes implementing this interface process matching document nodes found in different parts (i.e. headers, body,
 * footers).
 * 
 * @since 1.0.0
 */
public interface IDocxNodeProcessor {

	/**
	 * Processes a docx4j document node. Implementors are required to return <code>true</code> whenever the docx4j
	 * document structure has been changed.
	 * 
	 * @param documentNode
	 *          a document node typically selected by an xpath query from different document parts (headers, body,
	 *          footers).
	 * @return Returns <code>true</code> if the docx4j document has been changed.
	 * @throws JAXBException
	 * @throws ProcessingException
	 */
	boolean process(Object documentNode) throws JAXBException, ProcessingException;
}
