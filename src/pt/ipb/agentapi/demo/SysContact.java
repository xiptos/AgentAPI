/*
 * $Id: SysContact.java 3 2004-08-03 10:42:11Z rlopes $
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
package pt.ipb.agentapi.demo;

import pt.ipb.agentapi.AbstractAgent;
import pt.ipb.agentapi.AgentObject;
import pt.ipb.agentapi.MessageException;
import pt.ipb.agentapi.WritableAgentObject;
import pt.ipb.snmp.type.smi.Var;
import pt.ipb.snmp.type.smi.VarBind;
import pt.ipb.snmp.type.tc.Str;

/**
 * SysContact is a read-write object, so, in addition to extending AgentObject,
 * it must implement the WritableAgentObject
 */
public class SysContact extends AgentObject implements WritableAgentObject {
  Var value = new Str("").toVar();

  public SysContact(String oid) {
    super(oid);
  }

  public VarBind get(String oid) throws MessageException {
    return new VarBind(new String(getOID()), value);
  }

  public VarBind set(VarBind varBind) throws MessageException {
    Var val = varBind.getValue();
    if (val.getType() != Var.OCTETSTRING)
      throw new MessageException(AbstractAgent.WRONG_TYPE);
    value = new Str(val.toString()).toVar();
    return new VarBind(new String(getOID()), value);
  }
}

