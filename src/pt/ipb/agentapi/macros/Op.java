/*
 * $Id: Op.java 3 2004-08-03 10:42:11Z rlopes $ * Copyright (C) 2002-2004 Rui Pedro Lopes (rlopes at ipb dot pt) *
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import pt.ipb.snmp.SnmpConstants;
import pt.ipb.snmp.type.smi.VarBind;

public class Op {
  String destination = null;

  private ArrayList vars = null;

  public Op() {
    vars = new ArrayList();
  }

  public String getDestination() {
    return destination;
  }

  public void setDestination(String u) {
    this.destination = u;
  }

  public void addVarBind(VarBind p) {
    vars.add(p);
  }

  public void setVarBinds(List l) {
    vars = new ArrayList(l);
  }

  public List getVarBinds() {
    return vars;
  }

  public VarBind getVarBind(String oid) {
    for (Iterator i = getVarBinds().iterator(); i.hasNext();) {
      VarBind varBind = (VarBind) i.next();
      if (oid.equals(varBind.getOID()))
        return varBind;
    }
    return null;
  }

  public String toXML() {
    StringBuffer str = new StringBuffer();
    if (destination != null)
      str.append(" " + XMLTaskReader.DESTINATION + "=\"" + destination
          + "\">\n");

    for (Iterator i = vars.iterator(); i.hasNext();) {
      VarBind v = (VarBind) i.next();
      str.append(varBind2xml(v));
    }
    return str.toString();
  }

  String varBind2xml(VarBind v) {
    StringBuffer str = new StringBuffer();
    str.append("      <" + XMLTaskReader.VARBIND);
    if (v.getName() != null) {
      str.append(" " + XMLTaskReader.NAME + "=\"" + v.getName() + "\"");
    }
    str.append(" " + XMLTaskReader.OID + "=\"" + v.getOID() + "\"");
    if (v.getValue() != null) {
      str.append(" " + XMLTaskReader.VALUE + "=\"" + v.getValue().toString()
          + "\"");
      if (v.getValue().getType() != SnmpConstants.UNKNOWN)
        str.append(" " + XMLTaskReader.TYPE + "=\"" + v.getValue().getTypeStr()
            + "\"");
    }
    str.append("/>\n");
    return str.toString();
  }

}

