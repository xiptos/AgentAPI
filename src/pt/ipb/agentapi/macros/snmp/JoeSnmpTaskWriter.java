/*
 * $Id: JoeSnmpTaskWriter.java 4 2004-08-03 14:20:29Z rlopes $
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
package pt.ipb.agentapi.macros.snmp;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.opennms.protocols.snmp.SnmpCounter32;
import org.opennms.protocols.snmp.SnmpCounter64;
import org.opennms.protocols.snmp.SnmpGauge32;
import org.opennms.protocols.snmp.SnmpHandler;
import org.opennms.protocols.snmp.SnmpIPAddress;
import org.opennms.protocols.snmp.SnmpInt32;
import org.opennms.protocols.snmp.SnmpNull;
import org.opennms.protocols.snmp.SnmpObjectId;
import org.opennms.protocols.snmp.SnmpOctetString;
import org.opennms.protocols.snmp.SnmpOpaque;
import org.opennms.protocols.snmp.SnmpParameters;
import org.opennms.protocols.snmp.SnmpPduPacket;
import org.opennms.protocols.snmp.SnmpPduRequest;
import org.opennms.protocols.snmp.SnmpPeer;
import org.opennms.protocols.snmp.SnmpSMI;
import org.opennms.protocols.snmp.SnmpSession;
import org.opennms.protocols.snmp.SnmpSyntax;
import org.opennms.protocols.snmp.SnmpTimeTicks;
import org.opennms.protocols.snmp.SnmpUInt32;
import org.opennms.protocols.snmp.SnmpVarBind;

import pt.ipb.agentapi.macros.Get;
import pt.ipb.agentapi.macros.GetBulk;
import pt.ipb.agentapi.macros.GetNext;
import pt.ipb.agentapi.macros.Inform;
import pt.ipb.agentapi.macros.Op;
import pt.ipb.agentapi.macros.Response;
import pt.ipb.agentapi.macros.TaskWriter;
import pt.ipb.agentapi.macros.Tasks;
import pt.ipb.agentapi.macros.TasksResolver;
import pt.ipb.agentapi.macros.Trap;
import pt.ipb.agentapi.macros.XMLTaskReader;
import pt.ipb.snmp.SnmpConstants;
import pt.ipb.snmp.SnmpProperties;
import pt.ipb.snmp.SnmpURL;
import pt.ipb.snmp.type.smi.OID;
import pt.ipb.snmp.type.smi.Var;
import pt.ipb.snmp.type.smi.VarBind;

/**
 * This class uses the JoeSNMP library.
 */
public class JoeSnmpTaskWriter implements TaskWriter, SnmpHandler {

  SnmpProperties props = null;

  Iterator opIterator = null;

  SnmpSession session = null;

  public JoeSnmpTaskWriter() {
  }

  /**
   * This class writes a Tasks object to the given SNMP agent.
   * 
   * @see pt.ipb.snmp.SnmpProperties
   */
  public JoeSnmpTaskWriter(SnmpProperties p) {
    this.props = p;
  }

  /**
   * The destination port.
   * 
   * @param p
   *          The destination (agent) port
   */
  public void setPort(int p) {
    props.setPort(p);
  }

  /**
   * The destination host.
   * 
   * @param h
   *          The destination (agent) host address
   */
  public void setHost(String h) {
    props.setHost(h);
  }

  /**
   * Set the SnmpProperties object
   * 
   * @param p
   *          The SnmpProperties object
   */
  public void setSnmpProperties(SnmpProperties p) {
    this.props = p;
  }

  /**
   * Get the SnmpProperties object
   * 
   * @return the SnmpProperties object
   */
  public SnmpProperties getSnmpProperties() {
    return props;
  }

  /**
   * Writes the Tasks object to an SNMP agent. The agent address and port, as
   * well as other SNMP related information is provided by the SnmpProperties
   * object.
   * 
   * @param tasks
   *          The tasks to be written to the agent
   * @see pt.ipb.agentapi.macros.Tasks
   * @see #setSnmpProperties
   */
  public void write(Tasks tasks) throws UnknownHostException, SocketException,
      IOException, NoSuchElementException {
    setSnmpProperties(tasks.getSnmpProperties());
    TasksResolver tasksResolver = new TasksResolver(tasks);
    // throws NoSuchElementException, IOException
    tasksResolver.resolve();
    opIterator = tasksResolver.iterator();
    if (opIterator.hasNext()) {
      SnmpPduRequest pdu = buildPDU((Op) opIterator.next());
      prepareSession();
      try {
        synchronized (session) {
          session.send(pdu);
          // wait until being notified in the snmpReceivedPdu
          session.wait();
        }
      } catch (InterruptedException e) {
        // do nothing
      } finally {
        session.close();
      }
    }
  }

  void prepareSession() throws UnknownHostException, SocketException,
      IOException, NoSuchElementException {
    // throws UnknownHostException
    InetAddress remote = InetAddress.getByName(props.getHost());

    // Initialize the peer
    SnmpPeer peer = new SnmpPeer(remote);
    peer.setPort(props.getPort());
    peer.setTimeout(props.getTimeout() * 1000);
    peer.setRetries(props.getRetries());

    SnmpParameters parms = peer.getParameters();
    switch (props.getVersion()) {
    case SnmpConstants.SNMPv1:
      parms.setVersion(SnmpSMI.SNMPV1);
      break;
    default:
      parms.setVersion(SnmpSMI.SNMPV2);
      break;
    }

    parms.setReadCommunity(props.getCommunity());
    parms.setWriteCommunity(props.getWriteCommunity());
    //System.out.println("WriteComm: " + props.getWriteCommunity());
    //System.out.println("Comm: " + props.getCommunity());

    if (session == null) {
      session = new SnmpSession(peer);
      session.setDefaultHandler(this);
    } else {
      session.setPeer(peer);
    }

  }

  SnmpPduRequest buildPDU(Op op) throws IllegalArgumentException {
    try {
      SnmpURL url = new SnmpURL(op.getDestination());
      String host = url.getHost();
      if (host != null) {
        setHost(host);
        //System.out.println("Host: " + host);
      }
      int port = url.getPort();
      if (port != -1) {
        setPort(port);
        //System.out.println("port: " + port);
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw new IllegalArgumentException("Invalid destination string: "
          + op.getDestination());
    }
    List varBinds = op.getVarBinds();
    int type = -1;
    if (op instanceof Get) {
      type = SnmpPduPacket.GET;
    } else if (op instanceof GetNext) {
      type = SnmpPduPacket.GETNEXT;
    } else if (op instanceof GetBulk) {
      type = SnmpPduPacket.GETBULK;
    } else if (op instanceof pt.ipb.agentapi.macros.Set) {
      type = SnmpPduPacket.SET;
    } else if (op instanceof Trap) {
      type = SnmpPduPacket.V2TRAP;
    } else if (op instanceof Response) {
      type = SnmpPduPacket.RESPONSE;
    } else if (op instanceof Inform) {
      type = SnmpPduPacket.INFORM;
    }
    SnmpVarBind[] vbList = new SnmpVarBind[varBinds.size()];
    for (int i = 0; i < varBinds.size(); i++) {
      VarBind varBind = (VarBind) varBinds.get(i);
      if (varBind.getOID() == null)
        throw new IllegalArgumentException("VarBind " + i + " must have OID");
      SnmpObjectId oid = new SnmpObjectId(varBind.getOID());
      if (varBind.getValue() != null
          && varBind.getValue().getType() == SnmpConstants.UNKNOWN
          && type == SnmpPduPacket.SET)
        throw new IllegalArgumentException(
            "VarBind for Set messages must have well defined type (varbind "
                + i + ")");
      if (varBind.getValue() == null && type == SnmpPduPacket.SET)
        throw new IllegalArgumentException(
            "VarBind for Set messages must have well defined value (varbind "
                + i + ")");
      SnmpSyntax syntax = toSyntax(varBind.getValue());
      vbList[i] = new SnmpVarBind(oid, syntax);
    }
    SnmpPduRequest pdu = new SnmpPduRequest(type, vbList);
    pdu.setRequestId(SnmpPduPacket.nextSequence());

    return pdu;
  }

  SnmpSyntax toSyntax(Var value) throws IllegalArgumentException {
    byte type = value.getType();
    switch (type) {
    case SnmpConstants.INTEGER:
      return new SnmpInt32((Integer) value.toJavaValue());
    case SnmpConstants.OCTETSTRING:
      return new SnmpOctetString((byte[]) value.toJavaValue());
    case SnmpConstants.OID:
      return new SnmpObjectId(((OID) value).toString());
    //case SnmpConstants.INTEGER32: return new
    // SnmpInt32(Integer.parseInt(value));
    case SnmpConstants.IPADDRESS:
      return new SnmpIPAddress((byte[]) value.toJavaValue());
    case SnmpConstants.COUNTER32:
      return new SnmpCounter32((Long) value.toJavaValue());
    case SnmpConstants.UNSIGNED32:
      return new SnmpUInt32((Long) value.toJavaValue());
    case SnmpConstants.TIMETICKS:
      return new SnmpTimeTicks((Long) value.toJavaValue());
    case SnmpConstants.OPAQUE:
      return new SnmpOpaque((byte[]) value.toJavaValue());
    case SnmpConstants.COUNTER64:
      return new SnmpCounter64((BigInteger) value.toJavaValue());
    case SnmpConstants.GAUGE32:
      return new SnmpGauge32((Long) value.toJavaValue());
    case SnmpConstants.NULL:
      return new SnmpNull();
    default:
      return new SnmpNull();
    }
  }

  /**
   * This method is defined by the SnmpHandler interface and invoked when the
   * agent responds to the management application.
   * 
   * @param session
   *          The session receiving the pdu.
   * @param cmd
   *          The command from the pdu.
   * @param pdu
   *          The received pdu.
   */
  public void snmpReceivedPdu(SnmpSession session, int cmd, SnmpPduPacket pdu) {
    SnmpPduRequest req = null;
    if (pdu instanceof SnmpPduRequest) {
      req = (SnmpPduRequest) pdu;
    }

    if (pdu.getCommand() != SnmpPduPacket.RESPONSE) {
      System.err.println("Error: Received non-response command "
          + pdu.getCommand());
      synchronized (session) {
        session.notify();
      }
      return;
    }

    /*
     * if(req.getErrorStatus()!=0) { System.out.println("End of mib reached");
     * synchronized(session) { session.notify(); } return; }
     * 
     * SnmpVarBind vb = pdu.getVarBindAt(0); if(vb.getValue().typeId() ==
     * SnmpEndOfMibView.ASNTYPE || (m_stopAt != null &&
     * m_stopAt.compare(vb.getName()) < 0)) { System.out.println("End of mib
     * reached"); synchronized(session) { session.notify(); } return; }
     *  
     */
    SnmpVarBind vb = pdu.getVarBindAt(0);

    if (opIterator.hasNext()) {
      try {
        SnmpPduRequest newReq = buildPDU((Op) opIterator.next());
        prepareSession();
        session.send(newReq);
      } catch (Exception e) {
        e.printStackTrace();
        synchronized (session) {
          session.notify();
        }
      }
    } else {
      synchronized (session) {
        session.notify();
      }
    }

  }

  /**
   * This method is define by the SnmpHandler interface and invoked if an agent
   * fails to respond.
   * 
   * @param session
   *          The SNMP session in error.
   * @param pdu
   *          The PDU that timedout.
   *  
   */
  public void snmpTimeoutError(SnmpSession session, SnmpSyntax pdu) {
    System.err
        .println("The session timed out trying to communicate with the remote host");
    synchronized (session) {
      session.notify();
    }
  }

  /**
   * Defined by the SnmpHandler interface. Used to process internal session
   * errors.
   * 
   * @param session
   *          The SNMP session in error.
   * @param err
   *          The Error condition
   * @param pdu
   *          The pdu associated with this error condition
   *  
   */
  public void snmpInternalError(SnmpSession session, int err, SnmpSyntax pdu) {
    System.err.println("An unexpected error occured with the SNMP Session");
    System.err.println("The error code is " + err);
    synchronized (session) {
      session.notify();
    }
  }

  public static void main(String arg[]) {
    try {
      InputStream s = new FileInputStream(arg[0]);
      XMLTaskReader xml = new XMLTaskReader(s);
      SnmpProperties props = new SnmpProperties();
      props.setPort(10161);
      props.setWriteCommunity("private");
      props.setCommunity("public");
      props.setHost("localhost");
      Tasks t = xml.read();
      System.out.println(t.toXML());
      JoeSnmpTaskWriter writer = new JoeSnmpTaskWriter(props);
      writer.write(t);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}

