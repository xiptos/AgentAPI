/*
 * $Id: AgentObject.java 3 2004-08-03 10:42:11Z rlopes $
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

import pt.ipb.snmp.type.smi.OID;
import pt.ipb.snmp.type.smi.Var;
import pt.ipb.snmp.type.smi.VarBind;

/**
 * Any Agent Object must inherit this class.
 */
public abstract class AgentObject {
  protected Agent agent = null;

  protected OID oid;

  protected Var var;

  /**
   * Each AgentObject must have an OID.
   */
  public AgentObject(String oid) {
    this.oid = new OID(oid);
  }

  /**
   * Each AgentObject must have an OID.
   */
  public AgentObject(OID oid) {
    this.oid = oid;
  }

  /**
   * Called by AbstractAgent for retrieveing this object value.
   */
  public abstract VarBind get(String oid) throws MessageException;

  /**
   * Set a reference to this object agent.
   */
  public void setAgent(Agent a) {
    this.agent = a;
  }

  /**
   * Retrieve this object OID as a String.
   */
  public String getOID() {
    return getOIDObject().toString();
  }

  /**
   * Retrieve this object OID as an OID.
   */
  public OID getOIDObject() {
    return oid;
  }

  /**
   * Set this object OID.
   */
  public void setOIDObject(OID oid) {
    this.oid = oid;
  }

  /**
   * Return this object Var object.
   */
  public Var getVar() {
    return var;
  }

  /**
   * Set this object Var object.
   */
  public void setVar(Var var) throws MessageException {
    this.var = var;
  }

}