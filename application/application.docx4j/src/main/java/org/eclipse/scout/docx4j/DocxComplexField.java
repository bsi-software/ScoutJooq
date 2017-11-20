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

import java.util.ArrayList;
import java.util.List;

import org.docx4j.wml.R;

/**
 * This class holds references to complex field parts. In docx, a complex field represents dynamic data like document
 * variables, dates and times or functions.
 * <p/>
 * A complex field consists of up to three markers and up to two sections:
 * 
 * <pre>
 * &lt;w:r&gt;
 *   &lt;w:fldChar w:fldCharType=\&quot;begin\&quot;/&gt;
 * &lt;/w:r&gt;
 * &lt;!-- code paragraph entries --&gt;
 * &lt;w:r&gt;
 *   &lt;w:fldChar w:fldCharType=\&quot;separate\&quot;/&gt;
 * &lt;/w:r&gt;
 * &lt;!-- default value paragraph entries --&gt;
 * &lt;w:r&gt;
 *   &lt;w:t&gt;default value&lt;/w:t&gt;
 * &lt;/w:r&gt;
 * &lt;w:r&gt;
 *   &lt;w:fldChar w:fldCharType=\&quot;end\&quot;/&gt;
 * &lt;/w:r&gt;
 * </pre>
 * <p/>
 * <b>Note:</b> The separate complex field character and therefore the default value paragraph entries as well are
 * optional.
 * 
 * @since 1.0.0
 */
public class DocxComplexField {

  private R m_beginR;
  private R m_separateR;
  private R m_endR;
  private final List<Object> m_codeContent = new ArrayList<Object>();
  private final List<Object> m_defaultValueContent = new ArrayList<Object>();

  /**
   * @return Returns the parent run of the complex field's begin element.
   */
  public R getBeginR() {
    return m_beginR;
  }

  /**
   * Sets the parent run of the complex field's begin element.
   * 
   * @param beginR
   */
  public void setBeginR(R beginR) {
    m_beginR = beginR;
  }

  /**
   * @return Returns the parent run of the complex field's separate element or <code>null</code> if the complex field
   *         does not have a separate section at all.
   */
  public R getSeparateR() {
    return m_separateR;
  }

  /**
   * Sets the parent run of the complex field's separate element.
   * 
   * @param beginR
   */
  public void setSeparateR(R separateR) {
    m_separateR = separateR;
  }

  /**
   * @return Returns the parent run of the complex field's end element.
   */
  public R getEndR() {
    return m_endR;
  }

  /**
   * Sets the parent run of the complex field's end element.
   * 
   * @param beginR
   */
  public void setEndR(R endR) {
    m_endR = endR;
  }

  /**
   * Adds a document node that is part of the code section (i.e. the field's formula).
   * 
   * @param defaultValue
   */
  public void addCodeContent(Object code) {
    m_codeContent.add(code);
  }

  /**
   * @return Returns the document nodes that are part of the complex field's part between its start and separate
   *         elements. If the complex field does not have a separate section, the document nodes between the start and
   *         end elements are returned. The result is never <code>null</code>.
   */
  public List<Object> getCodeContent() {
    return m_codeContent;
  }

  /**
   * Adds a document node that is part of the separate section (i.e. the field's current value).
   * 
   * @param defaultValue
   */
  public void addDefaultValueContent(Object defaultValue) {
    m_defaultValueContent.add(defaultValue);
  }

  /**
   * @return Returns the document nodes that are part of the complex field's separate section. The result is never
   *         <code>null</code>.
   */
  public List<Object> getDefaultValueContent() {
    return m_defaultValueContent;
  }

  /**
   * @return Returns all document nodes being part of this complex field.
   */
  public List<Object> getFieldDocumentNodes() {
    List<Object> result = new ArrayList<Object>();
    result.add(getBeginR());
    result.addAll(getCodeContent());
    result.add(getSeparateR());
    result.addAll(getDefaultValueContent());
    result.add(getEndR());
    return result;
  }
}
