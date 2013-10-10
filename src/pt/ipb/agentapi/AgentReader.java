/*
 * $Id: AgentReader.java 3 2004-08-03 10:42:11Z rlopes $
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

package pt.ipb.agentapi;

import java.util.Collection;
import java.util.Iterator;

import pt.ipb.snmp.type.smi.OID;
import pt.ipb.snmp.type.smi.Var;

/**
 * This class is used to read the provided Agent and return a readable String
 * with the Agent information. NOT FINISHED YET...
 */
public class AgentReader {

  Agent agent = null;

  public AgentReader(Agent agent) {
    this.agent = agent;
  }

  public String read() {
    Collection contents = agent.getContents();
    if (contents == null)
      return null;

    StringBuffer buf = new StringBuffer();
    String lineSep = System.getProperty("line.separator", "\n");
    buf.append("OID;Value;Type");
    buf.append(lineSep);

    for (Iterator i = contents.iterator(); i.hasNext();) {
      AgentObject agentObject = (AgentObject) i.next();
      buf.append(encode(agentObject));
      buf.append(lineSep);
    }

    return buf.toString();
  }

  public boolean hasData() {
    Collection contents = agent.getContents();
    if (contents == null)
      return false;
    if (contents.isEmpty())
      return false;
    return true;
  }

  public String encode(AgentObject obj) {
    OID oid = obj.getOIDObject();
    Var var = obj.getVar();
    return new String(oid.toString() + ";" + var.toString() + ";"
        + var.getType());
  }
}