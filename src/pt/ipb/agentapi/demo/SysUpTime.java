/*
 * $Id: SysUpTime.java 3 2004-08-03 10:42:11Z rlopes $
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

import pt.ipb.agentapi.AgentObject;
import pt.ipb.agentapi.MessageException;
import pt.ipb.snmp.type.smi.TimeTicks;
import pt.ipb.snmp.type.smi.VarBind;

/**
 * Any SNMP object must derive AgentObject in the API.
 */
public class SysUpTime extends AgentObject {

  long initial;

  public SysUpTime(String oid) {
    super(oid);
    initial = System.currentTimeMillis();
  }

  public VarBind get(String oid) throws MessageException {
    long now = System.currentTimeMillis();
    long sysUpTime = (now - initial) / 10;
    return new VarBind(new String(getOID()), new TimeTicks(new Long(sysUpTime)
        .toString()));
  }
}