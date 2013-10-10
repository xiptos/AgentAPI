/*
 * $Id: OIDTransfers.java 3 2004-08-03 10:42:11Z rlopes $
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

package pt.ipb.marser.gui;

/**
 * 
 * @author rlopes
 * @version $Revision: 1.1.1.1 $
 */
public class OIDTransfers implements java.io.Serializable {
  String oid = null;

  String name = null;

  String type = null;

  public OIDTransfers() {
  }

  public OIDTransfers(String name, String oid) {
    setName(name);
    setOID(oid);
  }

  public OIDTransfers(String name, String oid, String type) {
    setName(name);
    setOID(oid);
    setType(type);
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setOID(String oid) {
    this.oid = oid;
  }

  public String getName() {
    return name;
  }

  public String getOID() {
    return oid;
  }

  public String getType() {
    return type;
  }

  public void setType(String t) {
    this.type = t;
  }
}