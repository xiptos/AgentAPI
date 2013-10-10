/*
 * $Id: Agent.java 3 2004-08-03 10:42:11Z rlopes $
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

import pt.ipb.agentapi.event.MessageListener;
import pt.ipb.snmp.type.smi.OID;
import pt.ipb.snmp.type.smi.VarBind;

/**
 * Interface implemented by AbstractAgent.
 */
public interface Agent extends java.io.Serializable, MessageListener {

  public VarBind getOperation(VarBind varBind);

  public VarBind getNextOperation(VarBind varBind);

  public VarBind setOperation(VarBind varBind);

  public Object removeObject(OID oid) throws ClassCastException,
      NullPointerException;

  public Object addObject(AgentObject oid) throws ClassCastException,
      NullPointerException;

  public AgentObject getAgentObject(String oid) throws ClassCastException,
      NullPointerException;

  public boolean exists(OID oid) throws ClassCastException,
      NullPointerException;

  public OID getCommonAncestorOID();

  public Collection getContents();

  public void addMessageListener(MessageListener l);

  public void removeMessageListener(MessageListener l);

}