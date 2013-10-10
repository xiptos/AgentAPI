/*
 * $Id: SnmpMacrosHandler.java 4 2004-08-03 14:20:29Z rlopes $
* Copyright (C) 2002-2004 Rui Pedro Lopes (rlopes at ipb dot pt)
*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA  02111-1307, USA.
 *
 */
package pt.ipb.agentapi.macros;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public interface SnmpMacrosHandler {

  /**
   * A container element start event handling method.
   * 
   * @param meta
   *          attributes
   */
  public void start_snmp(final Attributes meta) throws SAXException;

  /**
   * A container element end event handling method.
   */
  public void end_snmp() throws SAXException;

  /**
   * A container element start event handling method.
   * 
   * @param meta
   *          attributes
   */
  public void start_trap(final Attributes meta) throws SAXException;

  /**
   * A container element end event handling method.
   */
  public void end_trap() throws SAXException;

  /**
   * A container element start event handling method.
   * 
   * @param meta
   *          attributes
   */
  public void start_inform(final Attributes meta) throws SAXException;

  /**
   * A container element end event handling method.
   */
  public void end_inform() throws SAXException;

  /**
   * A container element start event handling method.
   * 
   * @param meta
   *          attributes
   */
  public void start_getNext(final Attributes meta) throws SAXException;

  /**
   * A container element end event handling method.
   */
  public void end_getNext() throws SAXException;

  /**
   * A container element start event handling method.
   * 
   * @param meta
   *          attributes
   */
  public void start_getBulk(final Attributes meta) throws SAXException;

  /**
   * A container element end event handling method.
   */
  public void end_getBulk() throws SAXException;

  /**
   * A container element start event handling method.
   * 
   * @param meta
   *          attributes
   */
  public void start_get(final Attributes meta) throws SAXException;

  /**
   * A container element end event handling method.
   */
  public void end_get() throws SAXException;

  /**
   * An empty element event handling method.
   * 
   * @param meta
   *          value or null
   */
  public void handle_mib(final Attributes meta) throws SAXException;

  /**
   * A container element start event handling method.
   * 
   * @param meta
   *          attributes
   */
  public void start_task(final Attributes meta) throws SAXException;

  /**
   * A container element end event handling method.
   */
  public void end_task() throws SAXException;

  /**
   * An empty element event handling method.
   * 
   * @param meta
   *          value or null
   */
  public void handle_property(final Attributes meta) throws SAXException;

  /**
   * An empty element event handling method.
   * 
   * @param meta
   *          value or null
   */
  public void handle_varBind(final Attributes meta) throws SAXException;

  /**
   * A container element start event handling method.
   * 
   * @param meta
   *          attributes
   */
  public void start_set(final Attributes meta) throws SAXException;

  /**
   * A container element end event handling method.
   */
  public void end_set() throws SAXException;

  /**
   * A container element start event handling method.
   * 
   * @param meta
   *          attributes
   */
  public void start_response(final Attributes meta) throws SAXException;

  /**
   * A container element end event handling method.
   */
  public void end_response() throws SAXException;

  /**
   * An empty element event handling method.
   * 
   * @param meta
   *          value or null
   */
  public void handle_runTask(final Attributes meta) throws SAXException;

}

