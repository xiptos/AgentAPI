/*
 * $Id: VarBind.java 3 2004-08-03 10:42:11Z rlopes $
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

package pt.ipb.snmp.type.smi;

/**
 * The OID and Var.
 * 
 * @author rlopes
 * @version $Revision: 1.1.1.1 $
 */
public class VarBind implements java.io.Serializable {

  String name = null;

  String oid = null;

  Var value = null;

  boolean error = false;

  public VarBind() {
    this(null, null, false);
  }

  public VarBind(String oid, Var value) {
    this(oid, value, false);
  }

  public VarBind(String oid, Var value, boolean error) {
    this.oid = oid;
    this.value = value;
    this.error = error;
  }

  public String getName() {
    return name;
  }

  public void setName(String n) {
    this.name = n;
  }

  public String getOID() {
    return oid;
  }

  public Var getValue() {
    return value;
  }

  public void setOID(String oid) {
    this.oid = oid;
  }

  public void setValue(Var value) {
    this.value = value;
  }

  public void setError(boolean error) {
    this.error = error;
  }

  public byte getError() {
    try {
      return Byte.parseByte(value.toString());
    } catch (Exception e) {
    }
    return -1;
  }

  public boolean isError() {
    return error;
  }

  public String toString() {
    return new String("OID: " + oid + " - Value: " + value + " - is error=="
        + error);
  }

}