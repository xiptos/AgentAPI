/*
 * $Id: JAXEngine.java 3 2004-08-03 10:42:11Z rlopes $
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

package pt.ipb.agentapi.engine.agentx;

import java.math.BigInteger;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

import jax.AgentXConnection;
import jax.AgentXGroup;
import jax.AgentXNotification;
import jax.AgentXOID;
import jax.AgentXRegistration;
import jax.AgentXResponsePDU;
import jax.AgentXSession;
import jax.AgentXSetPhase;
import jax.AgentXVarBind;
import pt.ipb.agentapi.AbstractAgent;
import pt.ipb.agentapi.Agent;
import pt.ipb.agentapi.engine.Engine;
import pt.ipb.agentapi.event.MessageAdapter;
import pt.ipb.agentapi.event.MessageEvent;
import pt.ipb.agentapi.event.MessageListener;
import pt.ipb.snmp.type.smi.Counter;
import pt.ipb.snmp.type.smi.Counter64;
import pt.ipb.snmp.type.smi.Gauge;
import pt.ipb.snmp.type.smi.Int;
import pt.ipb.snmp.type.smi.IpAddress;
import pt.ipb.snmp.type.smi.Null;
import pt.ipb.snmp.type.smi.OID;
import pt.ipb.snmp.type.smi.OctetString;
import pt.ipb.snmp.type.smi.Opaque;
import pt.ipb.snmp.type.smi.TimeTicks;
import pt.ipb.snmp.type.smi.Var;
import pt.ipb.snmp.type.smi.VarBind;

/**
 * This class acts as a proxy AgentX/Method invocation. SNMP (Get, GetNext,
 * GetBulk, Set) ---> MessageListener SNMP (GetResp, GetNextResp, GetBulkResp,
 * SetResp) <--- MessageListener It uses the JAX
 * (http://www.ibr.cs.tu-bs.de/projects/jasmin/jax.html);
 */
public class JAXEngine implements AgentXGroup, Engine {
  public static final String HOST = "agentx.host";

  public static final String PORT = "agentx.port";

  public static final String OID = "agentx.oid";

  AgentXConnection connection;

  AgentXSession session;

  AgentXRegistration registration;

  Agent agent = null;

  OID myOID = null;

  Properties props = null;

  VarBind undoVarBind = null;

  public JAXEngine() {
  }

  public JAXEngine(Properties prop) {
    setProperties(prop);
  }

  public void setProperties(Properties p) {
    this.props = p;
  }

  public void open() {
    String host;
    int port;
    AgentXOID oid;

    host = props.getProperty(HOST, "localhost");
    try {
      port = Integer.parseInt(props.getProperty(PORT, "705"));
    } catch (NumberFormatException e) {
      port = 705;
    }
    /*
     * String oidStr = props.getProperty(OID); if(oidStr==null) throw new
     * IllegalArgumentException("Invalid OID");
     * 
     * myOID = new OID(oidStr);
     */
    oid = new AgentXOID(myOID.toArray());

    try {
      System.out.println("JAXEngine - Connecting to " + host + ":" + port);
      connection = new AgentXConnection(host, port);

      session = new AgentXSession();
      connection.openSession(session);

      registration = new AgentXRegistration(oid);
      session.register(registration);

    } catch (Exception e) {
      e.printStackTrace();
      throw new Error(e.getMessage());
    }

    session.addGroup(this);

    /*
     * try { Thread.sleep(6000); } catch (InterruptedException e) {}
     * 
     * try { notification = new PingNoResponse(pe1, po);
     * session.notify(notification); } catch (Exception e) {
     * System.err.println(e); } try { Thread.sleep(90000); } catch
     * (InterruptedException e) {} try { Thread.sleep(90000); } catch
     * (InterruptedException e) {}
     */
    System.out.println("Done!");
  }

  /**
   * Closes the AgentX session.
   */
  public void close() {
    try {
      session.unregister(registration);
      session.close(AgentXSession.REASON_SHUTDOWN);
      connection.close();
    } catch (Exception e) {
      System.err.println(e);
    }

  }

  /**
   * To register listening Agents
   */
  public void addAgent(Agent l) {
    this.agent = l;
    myOID = agent.getCommonAncestorOID();
  }

  /**
   * To remove listening Agents
   */
  public void removeAgent(Agent l) {
  }

  /**
   * Class that listens for messages from Agent. These messages should then be
   * forward to some master agent.
   */
  class AgentListener extends MessageAdapter {
    class JAXNotification extends AgentXNotification {
      Vector v;

      public JAXNotification() {
        v = new Vector();
      }

      public JAXNotification(Vector vin) {
        this.v = vin;
      }

      public Vector getVarBindList() {
        return v;
      }
    }

    public void trapMessage(MessageEvent e) {
      Vector vout = new Vector();
      Vector vin = e.getVarBindVector();
      for (Enumeration en = vin.elements(); en.hasMoreElements();) {
        VarBind v = (VarBind) en.nextElement();
        vout.add(varBind2AgentX(v));
      }
      JAXNotification not = new JAXNotification(vout);
      try {
        session.notify(not);
      } catch (java.io.IOException ex) {
        ex.printStackTrace();
      }
    }

    public void responseMessage(MessageEvent e) {
      // only for TRAPS
    }
  }

  VarBind agentx2VarBind(AgentXVarBind vb) {
    AgentXOID oid = vb.getOID();
    byte type = 0;
    Var var = null;
    switch (vb.getType()) {
    case AgentXVarBind.COUNTER32:
      var = new Counter(vb.longValue());
      break;
    case AgentXVarBind.COUNTER64:
      var = new Counter64(vb.longValue());
      break;
    case AgentXVarBind.GAUGE32:
      var = new Gauge(vb.longValue());
      break;
    case AgentXVarBind.INTEGER:
      var = new Int(vb.intValue());
      break;
    case AgentXVarBind.IPADDRESS:
      var = new IpAddress(vb.bytesValue());
      break;
    case AgentXVarBind.NULL:
      var = new Null();
      break;
    case AgentXVarBind.OBJECTIDENTIFIER:
      var = new OID(vb.AgentXOIDValue().getArray());
      break;
    case AgentXVarBind.OPAQUE:
      var = new Opaque(vb.bytesValue());
      break;
    case AgentXVarBind.OCTETSTRING:
      var = new OctetString(vb.bytesValue());
      break;
    case AgentXVarBind.TIMETICKS:
      var = new TimeTicks(vb.intValue());
      break;
    }
    return new VarBind(new OID(oid.getArray()).toString(), var);
  }

  AgentXVarBind getAgentXError(VarBind varBind) {
    OID o = new OID(varBind.getOID());
    AgentXOID oid = new AgentXOID(o.toArray());

    int myErr = varBind.getError();
    switch (myErr) {
    case AbstractAgent.NO_SUCH_OBJECT:
      return new AgentXVarBind(oid, AgentXVarBind.NOSUCHOBJECT);
    case AbstractAgent.NO_SUCH_INSTANCE:
      return new AgentXVarBind(oid, AgentXVarBind.NOSUCHINSTANCE);
    case AbstractAgent.GEN_ERR:
      return new AgentXVarBind(oid, AgentXVarBind.NOSUCHOBJECT);
    //case AbstractAgent.NO_ERROR: return new SnmpNoSuchObject();
    case AbstractAgent.TOO_BIG:
      return new AgentXVarBind(oid, AgentXVarBind.NOSUCHOBJECT);
    case AbstractAgent.END_OF_MIB_VIEW:
      return new AgentXVarBind(oid, AgentXVarBind.ENDOFMIBVIEW);
    case AbstractAgent.NO_ACCESS:
      return new AgentXVarBind(oid, AgentXVarBind.NOSUCHOBJECT);
    case AbstractAgent.NOT_WRITABLE:
      return new AgentXVarBind(oid, AgentXVarBind.NOSUCHOBJECT);
    case AbstractAgent.WRONG_TYPE:
      return new AgentXVarBind(oid, AgentXVarBind.NOSUCHOBJECT);
    case AbstractAgent.WRONG_LENGTH:
      return new AgentXVarBind(oid, AgentXVarBind.NOSUCHOBJECT);
    case AbstractAgent.WRONG_ENCODING:
      return new AgentXVarBind(oid, AgentXVarBind.NOSUCHOBJECT);
    case AbstractAgent.WRONG_VALUE:
      return new AgentXVarBind(oid, AgentXVarBind.NOSUCHOBJECT);
    case AbstractAgent.NO_CREATION:
      return new AgentXVarBind(oid, AgentXVarBind.NOSUCHOBJECT);
    case AbstractAgent.INCONSISTENT_NAME:
      return new AgentXVarBind(oid, AgentXVarBind.NOSUCHOBJECT);
    case AbstractAgent.INCONSISTENT_VALUE:
      return new AgentXVarBind(oid, AgentXVarBind.NOSUCHOBJECT);
    case AbstractAgent.RESOURCE_UNAVAILABLE:
      return new AgentXVarBind(oid, AgentXVarBind.NOSUCHOBJECT);
    case AbstractAgent.COMMIT_FAILED:
      return new AgentXVarBind(oid, AgentXVarBind.NOSUCHOBJECT);
    case AbstractAgent.UNDO_FAILED:
      return new AgentXVarBind(oid, AgentXVarBind.NOSUCHOBJECT);
    default:
      return new AgentXVarBind(oid, AgentXVarBind.NOSUCHOBJECT);
    }
  }

  AgentXVarBind varBind2AgentX(VarBind varBind) {
    OID o = new OID(varBind.getOID());
    AgentXOID oid = new AgentXOID(o.toArray());

    Var var = varBind.getValue();
    if (var == null)
      return new AgentXVarBind(oid, AgentXVarBind.NULL);

    switch (var.getType()) {
    case Var.INTEGER:
      return new AgentXVarBind(oid, AgentXVarBind.INTEGER, ((Integer) var
          .toJavaValue()).intValue());
    case Var.OCTETSTRING:
      return new AgentXVarBind(oid, AgentXVarBind.OCTETSTRING, (byte[]) var
          .toJavaValue());
    case Var.OID:
      return new AgentXVarBind(oid, AgentXVarBind.OBJECTIDENTIFIER,
          new AgentXOID(((OID) var).toArray()));
    //case Var.INTEGER32: return SnmpVar.createVariable(var.toString(),
    // SnmpAPI.INTEGER);
    case Var.IPADDRESS:
      return new AgentXVarBind(oid, AgentXVarBind.IPADDRESS, (byte[]) var
          .toJavaValue());
    case Var.COUNTER32:
      return new AgentXVarBind(oid, AgentXVarBind.COUNTER32, ((Long) var
          .toJavaValue()).longValue());
    case Var.GAUGE32:
      return new AgentXVarBind(oid, AgentXVarBind.GAUGE32, ((Long) var
          .toJavaValue()).longValue());
    case Var.UNSIGNED32:
      return new AgentXVarBind(oid, AgentXVarBind.COUNTER32, ((Long) var
          .toJavaValue()).longValue());
    case Var.TIMETICKS:
      return new AgentXVarBind(oid, AgentXVarBind.TIMETICKS, ((Long) var
          .toJavaValue()).longValue());
    case Var.OPAQUE:
      return new AgentXVarBind(oid, AgentXVarBind.OPAQUE, (byte[]) var
          .toJavaValue());
    case Var.COUNTER64:
      return new AgentXVarBind(oid, AgentXVarBind.COUNTER64, ((BigInteger) var
          .toJavaValue()).longValue());
    case Var.NULL:
      return new AgentXVarBind(oid, AgentXVarBind.NULL);
    //case Var.STRING: type = SnmpAPI.STRING; break;
    //case Var.NETWORK_ADDRESS: type = SnmpAPI.NETWORKADDRESS; break;
    //case Var.NSAP: type = SnmpAPI.NSAP; break;
    }
    return null;
  }

  public MessageListener createAgentListener() {
    return new AgentListener();
  }

  public jax.AgentXVarBind getElement(jax.AgentXOID agentXOID) {
    AgentXVarBind vb;

    OID oid = new OID(agentXOID.getArray());
    VarBind varBind = new VarBind(oid.toString(), null);
    varBind = agent.getOperation(varBind);

    if (varBind.isError())
      vb = getAgentXError(varBind);
    else
      vb = varBind2AgentX(varBind);

    return vb;
  }

  public jax.AgentXVarBind getNextElement(jax.AgentXOID agentXOID) {
    AgentXVarBind vb;

    OID oid = new OID(agentXOID.getArray());
    VarBind varBind = new VarBind(oid.toString(), null);
    varBind = agent.getNextOperation(varBind);
    if (varBind.isError())
      vb = getAgentXError(varBind);
    else
      vb = varBind2AgentX(varBind);

    return vb;
  }

  public jax.AgentXOID getOID() {
    AgentXOID oid = new AgentXOID(myOID.toArray());
    return oid;
  }

  public jax.AgentXOID getSuffix(jax.AgentXOID agentXOID) {
    OID o = new OID(agentXOID.getArray());
    OID res = o.subOID(myOID.length());
    return new AgentXOID(res.toArray());
  }

  public int setElement(jax.AgentXSetPhase phase, jax.AgentXVarBind vb) {
    switch (phase.getPhase()) {
    case AgentXSetPhase.TEST_SET:
      break;
    case AgentXSetPhase.COMMIT:
      undoVarBind = agent.getOperation(agentx2VarBind(vb));
      VarBind res = agent.setOperation(agentx2VarBind(vb));
      if (res.isError()) {
        return getError(res.getError());
      }
      break;
    case AgentXSetPhase.UNDO:
      agent.setOperation(undoVarBind);
      break;
    case AgentXSetPhase.CLEANUP:
      undoVarBind = null;
      break;
    }
    return AgentXResponsePDU.NO_ERROR;
  }

  int getError(byte error) {
    switch (error) {
    case AbstractAgent.NO_SUCH_OBJECT:
      return AgentXResponsePDU.GEN_ERR;
    case AbstractAgent.NO_SUCH_INSTANCE:
      return AgentXResponsePDU.GEN_ERR;
    case AbstractAgent.GEN_ERR:
      return AgentXResponsePDU.GEN_ERR;
    case AbstractAgent.TOO_BIG:
      return AgentXResponsePDU.GEN_ERR;
    case AbstractAgent.END_OF_MIB_VIEW:
      return AgentXResponsePDU.GEN_ERR;
    case AbstractAgent.NO_ACCESS:
      return AgentXResponsePDU.NO_ACCESS;
    case AbstractAgent.NOT_WRITABLE:
      return AgentXResponsePDU.NOT_WRITABLE;
    case AbstractAgent.WRONG_TYPE:
      return AgentXResponsePDU.WRONG_TYPE;
    case AbstractAgent.WRONG_LENGTH:
      return AgentXResponsePDU.WRONG_LENGTH;
    case AbstractAgent.WRONG_ENCODING:
      return AgentXResponsePDU.WRONG_ENCODING;
    case AbstractAgent.WRONG_VALUE:
      return AgentXResponsePDU.WRONG_VALUE;
    case AbstractAgent.NO_CREATION:
      return AgentXResponsePDU.NO_CREATION;
    case AbstractAgent.INCONSISTENT_NAME:
      return AgentXResponsePDU.INCONSISTENT_NAME;
    case AbstractAgent.INCONSISTENT_VALUE:
      return AgentXResponsePDU.INCONSISTENT_VALUE;
    case AbstractAgent.RESOURCE_UNAVAILABLE:
      return AgentXResponsePDU.RESOURCE_UNAVAILABLE;
    case AbstractAgent.COMMIT_FAILED:
      return AgentXResponsePDU.COMMIT_FAILED;
    case AbstractAgent.UNDO_FAILED:
      return AgentXResponsePDU.GEN_ERR;
    default:
      return AgentXResponsePDU.GEN_ERR;
    }
  }

  protected void finalize() throws Throwable {
    close();
    super.finalize();
  }

}