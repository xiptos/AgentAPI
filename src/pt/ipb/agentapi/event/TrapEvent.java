/*
 * $Id: TrapEvent.java 3 2004-08-03 10:42:11Z rlopes $ * Copyright (C) 2002-2004 Rui Pedro Lopes (rlopes at ipb dot pt)
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

import java.util.Vector;

public class TrapEvent extends MessageEvent {
  public final static String SNMP_TRAP_OID = ".1.3.6.1.6.3.1.1.4.1.0";

  int generic;

  int specific;

  long timestamp;

  String enterpriseOid;

  /**
   * Constructs a message with a specified source
   * 
   * @param source
   *          the source of this message
   * @param v
   *          possibly the vector with the variable bindings
   */
  public TrapEvent(Object source, Vector v) {
    super(source, v);
  }

  public TrapEvent(Object source) {
    super(source);
  }

  /**
   * Getter for property enterpriseOid.
   * 
   * @return Value of property enterpriseOid.
   */
  public java.lang.String getEnterpriseOid() {
    return enterpriseOid;
  }

  /**
   * Setter for property enterpriseOid.
   * 
   * @param enterpriseOid
   *          New value of property enterpriseOid.
   */
  public void setEnterpriseOid(java.lang.String enterpriseOid) {
    this.enterpriseOid = enterpriseOid;
  }

  /**
   * Getter for property generic.
   * 
   * @return Value of property generic.
   */
  public int getGeneric() {
    return generic;
  }

  /**
   * Setter for property generic.
   * 
   * @param generic
   *          New value of property generic.
   */
  public void setGeneric(int generic) {
    this.generic = generic;
  }

  /**
   * Getter for property specific.
   * 
   * @return Value of property specific.
   */
  public int getSpecific() {
    return specific;
  }

  /**
   * Setter for property specific.
   * 
   * @param specific
   *          New value of property specific.
   */
  public void setSpecific(int specific) {
    this.specific = specific;
  }

  /**
   * Getter for property timestamp.
   * 
   * @return Value of property timestamp.
   */
  public long getTimestamp() {
    return timestamp;
  }

  /**
   * Setter for property timestamp.
   * 
   * @param timestamp
   *          New value of property timestamp.
   */
  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }

}