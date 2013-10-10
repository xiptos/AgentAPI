/*
 * $Id: MultiAgent.java 3 2004-08-03 10:42:11Z rlopes $ * Copyright (C) 2002-2004 Rui Pedro Lopes (rlopes at ipb dot pt)
 * *
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

import java.util.Enumeration;
import java.util.Vector;

import pt.ipb.agentapi.event.EventListenerList;
import pt.ipb.agentapi.event.MessageEvent;
import pt.ipb.agentapi.event.MessageListener;
import pt.ipb.snmp.type.smi.OID;
import pt.ipb.snmp.type.smi.VarBind;

public class MultiAgent extends AbstractAgent {
  Vector agents = null;

  // The communication module
  EventListenerList listenerList = new EventListenerList();

  /**
   * This class routes the messages to specific agents. It needs a Vector with
   * all the AbstracAgent sub agents. It needs the listener for agent responses.
   */
  public MultiAgent(Vector a) {
    this.agents = a;
  }

  public MultiAgent() {
    this.agents = new Vector();
  }

  public void add(Agent a) {
    this.agents.addElement(a);
  }

  public void remove(Agent a) {
    this.agents.remove(a);
  }

  public java.util.Collection getAgents() {
    return agents;
  }

  public Enumeration agents() {
    return agents.elements();
  }

  public void addMessageListener(MessageListener l) {
    listenerList.add(MessageListener.class, l);
    for (Enumeration e = agents.elements(); e.hasMoreElements();) {
      AbstractAgent a = (AbstractAgent) e.nextElement();
      a.addMessageListener(l);
    }
  }

  public void removeMessageListener(MessageListener l) {
    listenerList.remove(MessageListener.class, l);
    for (Enumeration e = agents.elements(); e.hasMoreElements();) {
      AbstractAgent a = (AbstractAgent) e.nextElement();
      a.removeMessageListener(l);
    }
  }

  public VarBind getOperation(VarBind varBind) {
    VarBind response = null;
    for (Enumeration enum = agents.elements(); enum.hasMoreElements();) {
      AbstractAgent a = (AbstractAgent) enum.nextElement();
      response = a.getOperation(varBind);
      if (!response.isError())
        break;
    }

    return response;
  }

  public void getMessage(MessageEvent e) {
    Vector vars = e.getVarBindVector();

    for (int i = 0; i < vars.size(); i++) {
      VarBind varBind = (VarBind) vars.elementAt(i);

      VarBind response = getOperation(varBind);

      varBind.setOID(response.getOID());
      varBind.setValue(response.getValue());
      varBind.setError(response.isError());
    }

    fireResponse(e);
  }

  protected void fireResponse(MessageEvent e) {
    Object[] listeners = listenerList.getListenerList();
    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == MessageListener.class) {
        ((MessageListener) listeners[i + 1]).responseMessage(e);
      }
    }
  }

  public VarBind getNextOperation(VarBind varBind) {
    // This variable will contain the value to return or the error occurred
    VarBind response = null;

    for (Enumeration enum = agents.elements(); enum.hasMoreElements();) {
      AbstractAgent a = (AbstractAgent) enum.nextElement();
      VarBind agentResponse = a.getNextOperation(varBind);
      OID agentOID = new OID(agentResponse.getOID());

      if (response == null) {
        response = agentResponse;
      }

      if (agentResponse.isError()
          && agentResponse.getError() == AbstractAgent.END_OF_MIB_VIEW) {
        continue;
      }

      if (agentOID.compareTo(new OID(response.getOID())) < 0) {
        response = agentResponse;
      }

      if (response.isError() && !agentResponse.isError()) {
        response = agentResponse;
      }

    }
    return response;
  }

  public VarBind setOperation(VarBind varBind) {

    VarBind response = null;
    for (Enumeration enum = agents.elements(); enum.hasMoreElements();) {
      AbstractAgent a = (AbstractAgent) enum.nextElement();
      response = a.setOperation(varBind);
      if (!response.isError())
        break;
    }

    return response;
  }

  public void setMessage(MessageEvent e) {
    Vector vars = e.getVarBindVector();

    for (int i = 0; i < vars.size(); i++) {
      VarBind varBind = (VarBind) vars.elementAt(i);

      VarBind response = setOperation(varBind);

      varBind.setOID(response.getOID());
      varBind.setValue(response.getValue());
      varBind.setError(response.isError());
    }

    fireResponse(e);
  }

  /**
   * Gets the common ancestor OID of all the agent objects.
   */
  public OID getCommonAncestorOID() {
    OID ancestor = null;
    for (Enumeration enum = agents.elements(); enum.hasMoreElements();) {
      AbstractAgent a = (AbstractAgent) enum.nextElement();
      OID key = a.getCommonAncestorOID();
      if (ancestor == null)
        ancestor = key;
      while (!key.startsWith(ancestor)) {
        ancestor = ancestor.subOID(0, ancestor.length() - 1);
      }
    }
    return ancestor;
  }

}