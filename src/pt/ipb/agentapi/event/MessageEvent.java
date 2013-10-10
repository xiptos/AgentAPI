/*
 * $Id: MessageEvent.java 3 2004-08-03 10:42:11Z rlopes $ * Copyright (C) 2002-2004 Rui Pedro Lopes (rlopes at ipb dot pt)
 * *
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

package pt.ipb.agentapi.event;

import java.util.Properties;
import java.util.Vector;

public class MessageEvent extends java.util.EventObject {
  String target = null;

  int port;

  int reqid = 0;

  Properties props = null;

  Vector varBinds = null;

  /**
   * Constructs a message with a specified source
   * 
   * @param source
   *          the source of this message
   * @param v
   *          possibly the vector with the variable bindings
   */
  public MessageEvent(Object source, Vector v) {
    super(source);
    this.varBinds = v;
  }

  public MessageEvent(Object source) {
    super(source);
  }

  public void setVarBindVector(Vector v) {
    this.varBinds = v;
  }

  public Vector getVarBindVector() {
    return varBinds;
  }

  public java.util.Enumeration varBinds() {
    return varBinds.elements();
  }

  public void setTarget(String a) {
    this.target = a;
  }

  public String getTarget() {
    return target;
  }

  public void setTargetPort(int p) {
    this.port = p;
  }

  public int getTargetPort() {
    return port;
  }

  public void setProperties(Properties p) {
    this.props = p;
  }

  public Properties getProperties() {
    return props;
  }

  public void setRequestID(int p) {
    this.reqid = p;
  }

  public int getRequestID() {
    return reqid;
  }

  public void setSource(Object s) {
    source = s;
  }

}