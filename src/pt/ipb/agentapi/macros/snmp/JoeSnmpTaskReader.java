/*
 * $Id: JoeSnmpTaskReader.java 4 2004-08-03 14:20:29Z rlopes $
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

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.opennms.protocols.snmp.SnmpCounter32;
import org.opennms.protocols.snmp.SnmpCounter64;
import org.opennms.protocols.snmp.SnmpEndOfMibView;
import org.opennms.protocols.snmp.SnmpHandler;
import org.opennms.protocols.snmp.SnmpIPAddress;
import org.opennms.protocols.snmp.SnmpInt32;
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

import pt.ipb.agentapi.macros.Set;
import pt.ipb.agentapi.macros.Task;
import pt.ipb.agentapi.macros.TaskReader;
import pt.ipb.agentapi.macros.Tasks;
import pt.ipb.snmp.SnmpConstants;
import pt.ipb.snmp.SnmpProperties;
import pt.ipb.snmp.SnmpURL;
import pt.ipb.snmp.type.smi.Counter;
import pt.ipb.snmp.type.smi.Counter64;
import pt.ipb.snmp.type.smi.Int;
import pt.ipb.snmp.type.smi.IpAddress;
import pt.ipb.snmp.type.smi.OID;
import pt.ipb.snmp.type.smi.OctetString;
import pt.ipb.snmp.type.smi.Opaque;
import pt.ipb.snmp.type.smi.TimeTicks;
import pt.ipb.snmp.type.smi.Unsigned;
import pt.ipb.snmp.type.smi.Var;
import pt.ipb.snmp.type.smi.VarBind;

/**
 * This class uses the JoeSNMP library.
 */
public class JoeSnmpTaskReader implements TaskReader, SnmpHandler {

  SnmpProperties props = null;

  SnmpSession session = null;

  SnmpObjectId startAt = null;

  // The stop point
  SnmpObjectId stopAt = null;

  Task task = null;

  /**
   * This class builds a Tasks object by walking a given OID on a given SNMP
   * agent.
   * 
   * @see pt.ipb.snmp.SnmpProperties
   */
  public JoeSnmpTaskReader(SnmpProperties p, String oid) {
    this.props = p;
    this.startAt = new SnmpObjectId(oid);
  }

  /**
   * The SNMP agent port.
   * 
   * @param p
   *          The agent port
   */
  public void setPort(int p) {
    props.setPort(p);
  }

  /**
   * The SNMP agent host address.
   * 
   * @param h
   *          The agent address
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
   * Reads a Tasks object from an SNMP agent. The agent address and port, as
   * well as other SNMP related information is provided in the SnmpProperties
   * object.
   * 
   * @see pt.ipb.agentapi.macros.Tasks
   * @see #setSnmpProperties
   */
  public Tasks read() throws UnknownHostException, SocketException {
    prepareSession();
    // Set the stop point
    stopAt = new SnmpObjectId(startAt);
    int[] ids = stopAt.getIdentifiers();
    ++ids[ids.length - 1];
    stopAt.setIdentifiers(ids);

    // Send the first request
    SnmpVarBind[] vbList = { new SnmpVarBind(startAt) };
    SnmpPduRequest pdu = new SnmpPduRequest(SnmpPduPacket.GETNEXT, vbList);
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

    Tasks tasks = new Tasks();
    tasks.setSnmpProperties(props);
    if (task != null) {
      tasks.addTask(task);
    }

    return tasks;
  }

  void prepareSession() throws UnknownHostException, SocketException {
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

    if (session == null) {
      session = new SnmpSession(peer);
      session.setDefaultHandler(this);
    } else {
      session.setPeer(peer);
    }
  }

  Var toValue(SnmpSyntax syntax) {
    byte type = syntax.typeId();
    switch (type) {
    case SnmpSMI.SMI_INTEGER:
      return new Int(((SnmpInt32) syntax).getValue());
    case SnmpSMI.SMI_STRING:
      return new OctetString(((SnmpOctetString) syntax).getString());
    case SnmpSMI.SMI_OBJECTID:
      return new OID(((SnmpObjectId) syntax).toString());
    case SnmpSMI.SMI_IPADDRESS:
      return new IpAddress(((SnmpIPAddress) syntax).toString());
    case SnmpSMI.SMI_COUNTER32:
      return new Counter(((SnmpCounter32) syntax).getValue());
    case SnmpSMI.SMI_UNSIGNED32:
      return new Unsigned(((SnmpUInt32) syntax).getValue());
    case SnmpSMI.SMI_TIMETICKS:
      return new TimeTicks(((SnmpTimeTicks) syntax).getValue());
    case SnmpSMI.SMI_OPAQUE:
      return new Opaque(((SnmpOpaque) syntax).toString());
    case SnmpSMI.SMI_COUNTER64:
      return new Counter64(((SnmpCounter64) syntax).getValue().toString());
    //case SnmpSMI.SMI_GAUGE32: return ((SnmpInt32)syntax).getValue());
    default:
      return null;
    }
  }

  /*
   * byte toType(byte type) { switch(type) { case SnmpSMI.SMI_INTEGER: return
   * SnmpConstants.INTEGER; case SnmpSMI.SMI_STRING: return
   * SnmpConstants.OCTETSTRING; case SnmpSMI.SMI_OBJECTID: return
   * SnmpConstants.OID; case SnmpSMI.SMI_IPADDRESS: return
   * SnmpConstants.IPADDRESS; case SnmpSMI.SMI_COUNTER32: return
   * SnmpConstants.COUNTER32; case SnmpSMI.SMI_UNSIGNED32: return
   * SnmpConstants.UNSIGNED32; case SnmpSMI.SMI_TIMETICKS: return
   * SnmpConstants.TIMETICKS; case SnmpSMI.SMI_OPAQUE: return
   * SnmpConstants.OPAQUE; case SnmpSMI.SMI_COUNTER64: return
   * SnmpConstants.COUNTER64; //case SnmpSMI.SMI_GAUGE32: return
   * SnmpConstants.GAUGE32; default: return SnmpConstants.UNKNOWN; } }
   */

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

    if (req.getErrorStatus() != 0) {
      System.out.println("End of mib reached");
      synchronized (session) {
        session.notify();
      }
      return;
    }

    if (task == null) {
      task = new Task("JoeSnmpTask");
    }

    String dest = session.getPeer().getPeer().getHostAddress();
    int port = session.getPeer().getPort();
    SnmpURL url = new SnmpURL("snmp", dest, port);
    Set set = new Set();
    set.setDestination(url.toString());

    SnmpVarBind[] vbArray = pdu.toVarBindArray();
    for (int i = 0; i < vbArray.length; i++) {
      SnmpVarBind vb = vbArray[i];
      if (vb.getValue().typeId() == SnmpEndOfMibView.ASNTYPE
          || (stopAt != null && stopAt.compare(vb.getName()) < 0)) {
        synchronized (session) {
          session.notify();
        }
        return;
      }
      String myOid = vb.getName().toString();
      Var var = toValue(vb.getValue());
      VarBind myVb = new VarBind(myOid, var);
      set.addVarBind(myVb);
    }
    task.addOp(set);

    // make the next pdu
    SnmpVarBind vb = vbArray[vbArray.length - 1];
    SnmpVarBind[] vblist = { new SnmpVarBind(vb.getName()) };
    SnmpPduRequest newReq = new SnmpPduRequest(SnmpPduPacket.GETNEXT, vblist);
    newReq.setRequestId(SnmpPduPacket.nextSequence());

    session.send(newReq);
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
      SnmpProperties props = new SnmpProperties();
      props.setPort(10161);
      props.setWriteCommunity("private");
      props.setCommunity("public");
      props.setHost("localhost");
      JoeSnmpTaskReader jr = new JoeSnmpTaskReader(props, arg[0]);
      Tasks t = jr.read();
      System.out.println(t.toXML());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}

