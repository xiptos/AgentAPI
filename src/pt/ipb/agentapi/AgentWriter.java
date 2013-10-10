/*
 * $Id: AgentWriter.java 3 2004-08-03 10:42:11Z rlopes $
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

import java.io.BufferedReader;
import java.io.Reader;
import java.util.StringTokenizer;

import pt.ipb.snmp.type.smi.Var;
import pt.ipb.snmp.type.smi.VarBind;

/**
 * This class is used to get data from a Reader character stream and add it to
 * the specified Agent. NOT FINISHED YET!!!
 */
public class AgentWriter {

  Agent agent = null;

  public AgentWriter(Agent agent) {
    this.agent = agent;
  }

  public void write(Reader data) {
    BufferedReader reader = new BufferedReader(data);
    try {
      String line = null;
      while ((line = reader.readLine()) != null) {
        if (line.equals("OID;Value;Type"))
          continue;
        String oid = null;
        String value = null;
        String type = null;
        StringTokenizer t = new StringTokenizer(line, ";");
        if (t.countTokens() != 3)
          continue;
        oid = t.nextToken();
        value = t.nextToken();
        type = t.nextToken();
        Var var = Var.createVar(value, Byte.parseByte(type));
        VarBind varBind = new VarBind(oid, var);
        VarBind resp = agent.setOperation(varBind);
        if (resp.isError()) {
          System.out.println("An error ocurred when setting object");
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}