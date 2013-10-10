/*
 * $Id: Table.java 3 2004-08-03 10:42:11Z rlopes $
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
 * This class is the main class for implementing SNMP tables.
 */
public class Table extends NodeAgentObject implements WritableAgentObject {

  TableModel model = null;

  /**
   * This class gets all the information from the provided TableModel.
   */
  public Table(String o) {
    this(o, null);
  }

  /**
   * This class gets all the information from the provided TableModel.
   */
  public Table(String o, TableModel model) {
    super(o);
    this.model = model;
  }

  /**
   * Returns the table model that holds the data for this table.
   */
  public TableModel getModel() {
    return model;
  }

  /**
   * Sets the table model that holds data for this table.
   */
  public void setModel(TableModel model) {
    this.model = model;
  }

  /**
   * This object has the object next to oidStr?
   */
  public boolean hasNext(String oidStr) {
    String nextStr = calculateNext(oidStr);
    if (nextStr != null)
      return true;
    return false;
  }

  /**
   * Returns the next OID to the provided one in this object context. Calls the
   * correspondent method on TableModel.
   */
  public String calculateNext(String oid) {
    if (model != null) {
      return model.calculateNext(oid);
    }
    return null;
  }

  /**
   * From NodeAgentObject...
   */
  public VarBind getNext(String oidStr) throws MessageException {
    String nextOid = calculateNext(oidStr);
    if (nextOid == null) {
      MessageException t = new MessageException(AbstractAgent.NO_SUCH_OBJECT);
      throw t;
    }

    VarBind res = get(nextOid);
    return res;
  }

  /**
   * From AgentObject...
   */
  public VarBind get(String oidStr) throws MessageException {
    if (oidStr == null) {
      MessageException t = new MessageException(AbstractAgent.NO_SUCH_OBJECT);
      throw t;
    }

    OID oid = new OID(oidStr);
    if (!oid.startsWith(getOIDObject())) {
      MessageException t = new MessageException(AbstractAgent.NO_SUCH_OBJECT);
      throw t;
    }

    if (model == null) {
      MessageException t = new MessageException(AbstractAgent.NO_SUCH_OBJECT);
      throw t;
    }

    // get row OID
    Var var = model.getValueAt(oid);

    VarBind res = new VarBind(oid.toString(), var);
    return res;
  }

  /**
   * From WritableAgentObject...
   */
  public VarBind set(VarBind varBind) throws MessageException {
    OID oid = new OID(varBind.getOID());
    if (!oid.startsWith(getOIDObject())) {
      MessageException t = new MessageException(AbstractAgent.NOT_WRITABLE);
      throw t;
    }

    if (model == null) {
      MessageException t = new MessageException(AbstractAgent.NOT_WRITABLE);
      throw t;
    }

    // get row OID
    Var var = model.setValueAt(oid, varBind.getValue());

    VarBind res = new VarBind(oid.toString(), var);
    return res;
  }

}

