/*
 * $Id: JoeSnmpEngine.java 3 2004-08-03 10:42:11Z rlopes $
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

package pt.ipb.agentapi.engine.snmp;

import java.math.BigInteger;
import java.net.InetAddress;
import java.util.Properties;
import java.util.Vector;

import org.opennms.protocols.snmp.SnmpAgentHandler;
import org.opennms.protocols.snmp.SnmpAgentSession;
import org.opennms.protocols.snmp.SnmpCounter32;
import org.opennms.protocols.snmp.SnmpCounter64;
import org.opennms.protocols.snmp.SnmpEndOfMibView;
import org.opennms.protocols.snmp.SnmpGauge32;
import org.opennms.protocols.snmp.SnmpHandler;
import org.opennms.protocols.snmp.SnmpIPAddress;
import org.opennms.protocols.snmp.SnmpInt32;
import org.opennms.protocols.snmp.SnmpNoSuchInstance;
import org.opennms.protocols.snmp.SnmpNoSuchObject;
import org.opennms.protocols.snmp.SnmpNull;
import org.opennms.protocols.snmp.SnmpObjectId;
import org.opennms.protocols.snmp.SnmpOctetString;
import org.opennms.protocols.snmp.SnmpOpaque;
import org.opennms.protocols.snmp.SnmpParameters;
import org.opennms.protocols.snmp.SnmpPduBulk;
import org.opennms.protocols.snmp.SnmpPduPacket;
import org.opennms.protocols.snmp.SnmpPduRequest;
import org.opennms.protocols.snmp.SnmpPduTrap;
import org.opennms.protocols.snmp.SnmpPeer;
import org.opennms.protocols.snmp.SnmpSMI;
import org.opennms.protocols.snmp.SnmpSession;
import org.opennms.protocols.snmp.SnmpSyntax;
import org.opennms.protocols.snmp.SnmpTimeTicks;
import org.opennms.protocols.snmp.SnmpUInt32;
import org.opennms.protocols.snmp.SnmpVarBind;

import pt.ipb.agentapi.AbstractAgent;
import pt.ipb.agentapi.Agent;
import pt.ipb.agentapi.engine.Engine;
import pt.ipb.agentapi.engine.EngineException;
import pt.ipb.agentapi.event.BulkMessageEvent;
import pt.ipb.agentapi.event.EventListenerList;
import pt.ipb.agentapi.event.MessageAdapter;
import pt.ipb.agentapi.event.MessageEvent;
import pt.ipb.agentapi.event.MessageListener;
import pt.ipb.agentapi.event.TrapEvent;
import pt.ipb.snmp.SnmpConstants;
import pt.ipb.snmp.SnmpProperties;
import pt.ipb.snmp.type.smi.Counter;
import pt.ipb.snmp.type.smi.Counter64;
import pt.ipb.snmp.type.smi.Int;
import pt.ipb.snmp.type.smi.IpAddress;
import pt.ipb.snmp.type.smi.Null;
import pt.ipb.snmp.type.smi.OID;
import pt.ipb.snmp.type.smi.OctetString;
import pt.ipb.snmp.type.smi.Opaque;
import pt.ipb.snmp.type.smi.TimeTicks;
import pt.ipb.snmp.type.smi.Unsigned;
import pt.ipb.snmp.type.smi.Var;
import pt.ipb.snmp.type.smi.VarBind;

/**
 * This class acts as a proxy SNMP/Method invocation. SNMP (Get, GetNext,
 * GetBulk, Set) ---> MessageListener SNMP (GetResp, GetNextResp, GetBulkResp,
 * SetResp) <--- MessageListener It uses the JoeSNMP stack
 * (http://www.opennms.com/);
 */
public class JoeSnmpEngine implements SnmpAgentHandler, Engine {
  private SnmpAgentSession m_agentSession = null;

  private SnmpSession m_session = null;

  EventListenerList listenerList = new EventListenerList();

  SnmpProperties props = null;

  public JoeSnmpEngine() {
  }

  public JoeSnmpEngine(SnmpProperties prop) {
    setProperties(prop);
  }

  public void setProperties(Properties p) {
    this.props = new SnmpProperties(p);
  }

  public void open() throws Exception {
    if (m_agentSession != null)
      close();

    System.out.println("Starting JoeSnmpEngine on port " + props.getPort());
    try {

      // Create the session object
      m_agentSession = new SnmpAgentSession(this, props.getPort());
      m_agentSession.setHandler(this);

    } catch (Exception e) {
      //e.printStackTrace();
      throw new pt.ipb.agentapi.engine.EngineException(e);
    }
    System.out.println("Done!");
  }

  /**
   * Closes the SNMP session.
   */
  public void close() {
    m_agentSession.close();
    m_agentSession = null;
  }

  public void SnmpAgentSessionError(SnmpAgentSession session, int error,
      Object ref) {
    if (error == SnmpAgentSession.ERROR_EXCEPTION) {
      synchronized (session) {
        session.notify(); // close the session
      }
    }
  }

  public void snmpReceivedPdu(SnmpAgentSession session, SnmpInt32 version,
      InetAddress manager, int port, SnmpOctetString community,
      SnmpPduPacket pdu) {
    SnmpProperties props = new SnmpProperties();
    switch (version.getValue()) {
    case SnmpSMI.SNMPV1:
      props.setVersion(SnmpConstants.SNMPv1);
      break;
    case SnmpSMI.SNMPV2:
      props.setVersion(SnmpConstants.SNMPv2c);
      break;
    }
    props.setCommunity(new String(community.getString()));
    if (!authenticate(pdu.getCommand(), new String(community.getString()))) {
      return;
    }
    Vector vars = snmp2vars(pdu.toVarBindArray());

    MessageEvent message;
    if (pdu instanceof SnmpPduBulk) {
      message = new BulkMessageEvent(this, vars);
      SnmpPduBulk bulk = (SnmpPduBulk) pdu;
      ((BulkMessageEvent) message).setMaxRep(bulk.getMaxRepititions());
      ((BulkMessageEvent) message).setNonRep(bulk.getNonRepeaters());
    } else {
      message = new MessageEvent(this, vars);
    }
    message.setTarget(manager.getHostAddress());
    message.setTargetPort(port);
    message.setRequestID(pdu.getRequestId());
    message.setProperties(props);
    relayMessage(pdu.getCommand(), message);

  }

  boolean authenticate(int command, String comm) {
    String comp;
    if (command == SnmpPduPacket.SET) {
      comp = props.getWriteCommunity();
    } else {
      comp = props.getCommunity();
    }
    return comm.equals(comp);
  }

  // To register listening Agents
  public void addAgent(Agent l) {
    listenerList.add(Agent.class, l);
  }

  public void removeAgent(Agent l) {
    listenerList.remove(Agent.class, l);
  }

  /**
   * Class that listens for messages from Agent. These messages should then be
   * forward to some SNMP Application.
   */
  class AgentListener extends MessageAdapter implements SnmpHandler {

    public void trapMessage(MessageEvent e) {
      SnmpProperties props = (SnmpProperties) e.getProperties();
      try {
        switch (props.getVersion()) {
        case SnmpConstants.SNMPv1: {
          sendV1Trap((TrapEvent) e);
          break;
        }
        case SnmpConstants.SNMPv2c: {
          sendV2Trap((TrapEvent) e);
          break;
        }
        }
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }

    public void sendV1Trap(TrapEvent e) throws EngineException {
      try {

        SnmpProperties props = (SnmpProperties) e.getProperties();
        SnmpPeer peer = new SnmpPeer(InetAddress.getByName(e.getTarget()), e
            .getTargetPort());
        peer.setRetries(props.getRetries());
        peer.setTimeout(props.getTimeout());
        SnmpParameters p = peer.getParameters();
        p.setReadCommunity(props.getCommunity());
        p.setVersion(SnmpSMI.SNMPV1);

        SnmpSession session = new SnmpSession(peer);
        session.setDefaultHandler(this);

        SnmpPduTrap trapPdu = new SnmpPduTrap();
        trapPdu.setAgentAddress(new SnmpIPAddress(peer.getPeer().getAddress()));
        trapPdu.setGeneric(e.getGeneric());
        trapPdu.setSpecific(e.getSpecific());
        trapPdu.setTimeStamp(e.getTimestamp());
        trapPdu.setEnterprise(new SnmpObjectId(e.getEnterpriseOid()));

        buildSnmpPDU(trapPdu, e.getVarBindVector());

        //trapPdu.addVarBind(new SnmpVarBind(new
        // SnmpObjectId(OID_TIMETICKS), new
        // SnmpTimeTicks(PARAM_TIMETICKS)));
        //trapPdu.addVarBind(new SnmpVarBind(new
        // SnmpObjectId(m_trapOid), new
        // SnmpOctetString(message.getBytes())));

        session.send(trapPdu);
      } catch (Exception ex) {
        throw new EngineException(ex);
      }
    }

    public void sendV2Trap(TrapEvent e) throws EngineException {
      try {
        SnmpProperties props = (SnmpProperties) e.getProperties();
        SnmpPeer peer = new SnmpPeer(InetAddress.getByName(e.getTarget()), e
            .getTargetPort());
        peer.setRetries(props.getRetries());
        peer.setTimeout(props.getTimeout());
        SnmpParameters p = peer.getParameters();
        p.setReadCommunity(props.getCommunity());
        p.setVersion(SnmpSMI.SNMPV2);

        SnmpPduRequest trapPdu = new SnmpPduRequest(SnmpPduPacket.V2TRAP);
        buildSnmpPDU(trapPdu, e.getVarBindVector());
        m_agentSession.send(peer, trapPdu);

      } catch (Exception ex) {
        throw new EngineException(ex);
      }
    }

    public void responseMessage(MessageEvent e) {
      SnmpPduRequest newReq = new SnmpPduRequest(SnmpPduPacket.RESPONSE);
      newReq.setRequestId(e.getRequestID());
      buildSnmpPDU(newReq, e.getVarBindVector());

      try {
        SnmpProperties props = (SnmpProperties) e.getProperties();
        SnmpPeer peer = new SnmpPeer(InetAddress.getByName(e.getTarget()), e
            .getTargetPort());
        SnmpParameters p = peer.getParameters();
        switch (props.getVersion()) {
        case SnmpConstants.SNMPv1:
          p.setVersion(SnmpSMI.SNMPV1);
          break;
        case SnmpConstants.SNMPv2c:
          p.setVersion(SnmpSMI.SNMPV2);
          break;
        }
        p.setReadCommunity(props.getCommunity());
        m_agentSession.send(peer, newReq);
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }

    void buildSnmpPDU(SnmpPduPacket pdu, Vector varBinds) {
      for (int i = 0; i < varBinds.size(); i++) {
        VarBind varBind = (VarBind) varBinds.elementAt(i);
        SnmpObjectId oid = new SnmpObjectId(varBind.getOID());
        try {
          SnmpSyntax snmpVar = null;
          if (varBind.isError()) {
            snmpVar = getSnmpErr(varBind);
          } else {
            snmpVar = var2snmp(varBind.getValue());
          }
          if (snmpVar != null) {
            pdu.addVarBind(new SnmpVarBind(oid, snmpVar));
          } else {
            pdu.addVarBind(new SnmpVarBind(oid));
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }

    void buildSnmpPDU(SnmpPduTrap pdu, Vector varBinds) {
      for (int i = 0; i < varBinds.size(); i++) {
        VarBind varBind = (VarBind) varBinds.elementAt(i);
        SnmpObjectId oid = new SnmpObjectId(varBind.getOID());
        try {
          SnmpSyntax snmpVar = null;
          if (varBind.isError()) {
            snmpVar = getSnmpErr(varBind);
          } else {
            snmpVar = var2snmp(varBind.getValue());
          }
          if (snmpVar != null) {
            pdu.addVarBind(new SnmpVarBind(oid, snmpVar));
          } else {
            pdu.addVarBind(new SnmpVarBind(oid));
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }

    // Dummy functions. We only send.
    public void snmpInternalError(
        org.opennms.protocols.snmp.SnmpSession snmpSession, int param,
        org.opennms.protocols.snmp.SnmpSyntax snmpSyntax) {
    }

    public void snmpReceivedPdu(
        org.opennms.protocols.snmp.SnmpSession snmpSession, int param,
        org.opennms.protocols.snmp.SnmpPduPacket snmpPduPacket) {
    }

    public void snmpTimeoutError(
        org.opennms.protocols.snmp.SnmpSession snmpSession,
        org.opennms.protocols.snmp.SnmpSyntax snmpSyntax) {
    }

  }

  /** Print octet data in a more readable form */
  String printOctets(byte[] data, int length) {
    StringBuffer s = new StringBuffer();

    int j = 0, line = 20; // we'll allow 20 bytes per line
    if (data.length < length)
      length = data.length;

    for (int i = 0; i < length; i++) {
      if (j++ > 19) {
        j = 1;
        s.append("\n");
      }
      String bs = Integer.toString(byteToInt(data[i]), 16);
      if (bs.length() < 2)
        bs = "0" + bs;
      s.append(bs + " ");
    }
    return s.toString();
  }

  static int byteToInt(byte b) {
    return (int) b & 0xFF;
  }

  Vector snmp2vars(SnmpVarBind[] snmpv) {
    Vector v = new Vector();
    for (int i = 0; i < snmpv.length; i++) {
      SnmpVarBind snmpVarBind = snmpv[i];
      String oid = snmpVarBind.getName().toString();
      SnmpSyntax snmpSyntax = snmpVarBind.getValue();
      byte snmpType = snmpSyntax.typeId();

      byte type = 0;
      Var var = null;
      try {
        switch (snmpType) {
        //case SnmpSMI.BITSTRING: var = new
        // OctetString(snmpSyntax.toBytes()); break;
        case SnmpSMI.SMI_COUNTER32:
          var = new Counter(((SnmpCounter32) snmpSyntax).getValue());
          break;
        case SnmpSMI.SMI_COUNTER64:
          var = new Counter64(((SnmpCounter64) snmpSyntax).getValue()
              .toString());
          break;
        //case SnmpSMI.SMI_GAUGE32: var = new
        // Gauge(((SnmpGauge32)snmpSyntax).getValue()); break;
        case SnmpSMI.SMI_INTEGER:
          var = new Int(((SnmpInt32) snmpSyntax).getValue());
          break;
        case SnmpSMI.SMI_IPADDRESS:
          var = new IpAddress(((SnmpIPAddress) snmpSyntax).toString());
          break;
        case SnmpSMI.SMI_NULL:
          var = new Null();
          break;
        case SnmpSMI.SMI_OBJECTID:
          var = new OID(((SnmpObjectId) snmpSyntax).toString());
          break;
        case SnmpSMI.SMI_OPAQUE:
          var = new Opaque(((SnmpOpaque) snmpSyntax).toString());
          break;
        case SnmpSMI.SMI_STRING:
          var = new OctetString(((SnmpOctetString) snmpSyntax).getString());
          break;
        case SnmpSMI.SMI_TIMETICKS:
          var = new TimeTicks(((SnmpTimeTicks) snmpSyntax).getValue());
          break;
        case SnmpSMI.SMI_UNSIGNED32:
          var = new Unsigned(((SnmpUInt32) snmpSyntax).getValue());
          break;
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
      if (var != null) {
        v.addElement(new VarBind(oid, var));
      }

    }
    return v;
  }

  void relayMessage(int command, MessageEvent message) {
    switch (command) {
    case SnmpPduPacket.GET:
      fireGet(message);
      break;
    case SnmpPduPacket.GETBULK:
      fireGetBulk((BulkMessageEvent) message);
      break;
    case SnmpPduPacket.GETNEXT:
      fireGetNext(message);
      break;
    case SnmpPduPacket.INFORM:
      fireInform(message);
      break;
    case SnmpPduPacket.REPORT:
      //fireReport(message);
      break;
    case SnmpPduPacket.SET:
      fireSet(message);
      break;
    case SnmpPduTrap.TRAP:
      fireTrap(message);
      break;
    case SnmpPduPacket.V2TRAP:
      fireTrap(message);
      break;
    }
  }

  SnmpSyntax getSnmpErr(VarBind varBind) {
    int myErr = varBind.getError();
    switch (myErr) {
    case AbstractAgent.NO_SUCH_OBJECT:
      return new SnmpNoSuchObject();
    case AbstractAgent.NO_SUCH_INSTANCE:
      return new SnmpNoSuchInstance();
    case AbstractAgent.GEN_ERR:
      return new SnmpNoSuchObject();
    //case AbstractAgent.NO_ERROR: return new SnmpNoSuchObject();
    case AbstractAgent.TOO_BIG:
      return new SnmpNoSuchObject();
    case AbstractAgent.END_OF_MIB_VIEW:
      return new SnmpEndOfMibView();
    case AbstractAgent.NO_ACCESS:
      return new SnmpNoSuchObject();
    case AbstractAgent.NOT_WRITABLE:
      return new SnmpNoSuchObject();
    case AbstractAgent.WRONG_TYPE:
      return new SnmpNoSuchObject();
    case AbstractAgent.WRONG_LENGTH:
      return new SnmpNoSuchObject();
    case AbstractAgent.WRONG_ENCODING:
      return new SnmpNoSuchObject();
    case AbstractAgent.WRONG_VALUE:
      return new SnmpNoSuchObject();
    case AbstractAgent.NO_CREATION:
      return new SnmpNoSuchObject();
    case AbstractAgent.INCONSISTENT_NAME:
      return new SnmpNoSuchObject();
    case AbstractAgent.INCONSISTENT_VALUE:
      return new SnmpNoSuchObject();
    case AbstractAgent.RESOURCE_UNAVAILABLE:
      return new SnmpNoSuchObject();
    case AbstractAgent.COMMIT_FAILED:
      return new SnmpNoSuchObject();
    case AbstractAgent.UNDO_FAILED:
      return new SnmpNoSuchObject();
    default:
      return new SnmpNoSuchObject();
    }
  }

  SnmpSyntax var2snmp(Var var) {
    if (var == null)
      return null;
    switch (var.getType()) {
    case Var.INTEGER:
      return new SnmpInt32(((Integer) var.toJavaValue()).intValue());
    case Var.OCTETSTRING:
      return new SnmpOctetString((byte[]) var.toJavaValue());
    case Var.OID:
      return new SnmpObjectId(var.toString());
    //case Var.INTEGER32: return SnmpVar.createVariable(var.toString(),
    // SnmpAPI.INTEGER);
    case Var.IPADDRESS:
      return new SnmpIPAddress((byte[]) var.toJavaValue());
    case Var.COUNTER32:
      return new SnmpCounter32(((Long) var.toJavaValue()).intValue());
    case Var.GAUGE32:
      return new SnmpGauge32(((Long) var.toJavaValue()).intValue());
    case Var.UNSIGNED32:
      return new SnmpUInt32(((Long) var.toJavaValue()).longValue());
    case Var.TIMETICKS:
      return new SnmpTimeTicks(((Long) var.toJavaValue()).longValue());
    case Var.OPAQUE:
      return new SnmpOpaque((byte[]) var.toJavaValue());
    case Var.COUNTER64:
      return new SnmpCounter64((BigInteger) var.toJavaValue());
    case Var.NULL:
      return new SnmpNull();
    //case Var.STRING: type = SnmpAPI.STRING; break;
    //case Var.NETWORK_ADDRESS: type = SnmpAPI.NETWORKADDRESS; break;
    //case Var.NSAP: type = SnmpAPI.NSAP; break;
    }
    return null;
  }

  public MessageListener createAgentListener() {
    return new AgentListener();
  }

  protected void fireGet(MessageEvent e) {
    Object[] listeners = listenerList.getListenerList();
    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == Agent.class) {
        ((Agent) listeners[i + 1]).getMessage(e);
      }
    }
  }

  protected void fireGetNext(MessageEvent e) {
    Object[] listeners = listenerList.getListenerList();
    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == Agent.class) {
        ((Agent) listeners[i + 1]).getNextMessage(e);
      }
    }
  }

  protected void fireGetBulk(BulkMessageEvent e) {
    Object[] listeners = listenerList.getListenerList();
    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == Agent.class) {
        ((Agent) listeners[i + 1]).getBulkMessage(e);
      }
    }
  }

  protected void fireSet(MessageEvent e) {
    Object[] listeners = listenerList.getListenerList();
    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == Agent.class) {
        ((Agent) listeners[i + 1]).setMessage(e);
      }
    }
  }

  protected void fireInform(MessageEvent e) {
    Object[] listeners = listenerList.getListenerList();
    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == Agent.class) {
        ((Agent) listeners[i + 1]).informMessage(e);
      }
    }
  }

  protected void fireTrap(MessageEvent e) {
    Object[] listeners = listenerList.getListenerList();
    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == Agent.class) {
        ((Agent) listeners[i + 1]).trapMessage(e);
      }
    }
  }

  protected void fireResponse(MessageEvent e) {
    Object[] listeners = listenerList.getListenerList();
    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == Agent.class) {
        ((Agent) listeners[i + 1]).responseMessage(e);
      }
    }
  }

  protected void finalize() throws Throwable {
    close();
    super.finalize();
  }

}

