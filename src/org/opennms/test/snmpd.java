//
// Copyright (C) 2000 Shivakumar C. Patil <shivakumar.patil@stdc.com>
//  
// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// (at your option) any later version.
// 
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software 
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
// 
// Tab Size = 8
//
// $Id: snmpd.java 3 2004-08-03 10:42:11Z rlopes $
//
// Log:
//	06/08/00 - Brian Weaver <weave@opennms.org>
//		Commented and added file to CVS
//
// Adapted from trapd 9/8/00 by Bob Snider <bsnider@seekone.com>

package org.opennms.test;

import org.opennms.protocols.snmp.SnmpAgentHandler;
import org.opennms.protocols.snmp.SnmpAgentSession;
import org.opennms.protocols.snmp.SnmpInt32;
import org.opennms.protocols.snmp.SnmpOctetString;
import org.opennms.protocols.snmp.SnmpPduPacket;
import org.opennms.protocols.snmp.SnmpPduRequest;
import org.opennms.protocols.snmp.SnmpPduTrap;
import org.opennms.protocols.snmp.SnmpPeer;
import org.opennms.protocols.snmp.SnmpVarBind;

/**
 * <P>
 * Implements a sample SNMP trap daemon that listens and prints traps received
 * from remote agents on port 162.
 * </P>
 * 
 * @author <A HREF="mailto:shivakumar.patil@stdc.com">Shivakumar C. Patil </A>
 * @author <A HREF="mailto:weave@opennms.org">Brian Weaver </A>
 * @author <A HREF="http://www.opennms.org/">OpenNMS </A>
 * @version $Revision: 1.1.1.1 $
 */
public class snmpd implements SnmpAgentHandler {
  /**
   * The main routine. All arguments are ignored. The program will terminate if
   * any error in the trap session occur. However, malformed packets will be
   * discarded in the error handling method of this class.
   * 
   * @param args
   *          The command line arguments -- IGNORED.
   */
  public static void main(String args[]) {
    try {
      SnmpAgentSession testTrapSession = new SnmpAgentSession(new snmpd());
      System.out.println("SNMP Agent Started");
      synchronized (testTrapSession) {
        testTrapSession.wait();
      }
      System.out.println("SNMP Agent Exiting");
      testTrapSession.close();
    } catch (Exception e) {
      System.out.println("Exception in main(): " + e);
      e.printStackTrace();
    }
  }

  /**
   * Receives and prints information about SNMPv2c traps.
   * 
   * @param session
   *          The Trap Session that received the PDU.
   * @param agent
   *          The address of the remote sender.
   * @param port
   *          The remote port where the pdu was transmitted from.
   * @param community
   *          The decoded community string.
   * @param pdu
   *          The decoded V2 trap pdu.
   *  
   */

  // This should probably be refactored into snmpReceivedGet, snmpReceivedSet,
  // etc.....
  public void snmpReceivedPdu(SnmpAgentSession session, SnmpInt32 version,
      java.net.InetAddress manager, int port, SnmpOctetString community,
      SnmpPduPacket pdu) {
    System.out.println("Message from manager " + manager.toString()
        + " on port " + port);
    int cmd = pdu.getCommand();
    System.out.println("PDU command......... " + cmd);
    switch (cmd) {
    case SnmpPduPacket.SET:
      System.out.println("Set Command");
      break;
    case SnmpPduPacket.GET: {
      System.out.println("Get Command");
      int k = pdu.getLength();
      System.out.println("ID = " + pdu.getRequestId() + " length = " + k);
      SnmpVarBind[] vblist = new SnmpVarBind[k];
      for (int i = 0; i < k; i++) {
        SnmpVarBind vb = pdu.getVarBindAt(i);
        vblist[i] = new SnmpVarBind(vb.getName());
        vblist[i].setValue(new SnmpInt32(i + 1)); // the i is a dummy to
        // do a count
        System.out.print("Varbind[" + i + "] := " + vb.getName().toString());
        System.out.println(" --> " + vb.getValue().toString());
      }
      SnmpPduRequest newReq = new SnmpPduRequest(SnmpPduPacket.RESPONSE, vblist);
      newReq.setRequestId(pdu.getRequestId());
      try {
        session.send(new SnmpPeer(manager, port), newReq);
      } catch (Exception e) {
        System.out.println("Error sending response " + e);
      }
    }
      break;
    case SnmpPduPacket.GETNEXT:
      System.out.println("GetNext Command");
      break;
    case SnmpPduPacket.RESPONSE:
    case SnmpPduPacket.INFORM:
    case SnmpPduPacket.V2TRAP:
    case SnmpPduPacket.REPORT:
    case SnmpPduPacket.GETBULK:
    case SnmpPduTrap.TRAP:
      System.out.println("V2 Trap PDU ID.............. " + pdu.getRequestId());
      System.out.println("V2 Trap PDU Length.......... " + pdu.getLength());

      if (pdu instanceof SnmpPduRequest) {
        System.out.println("V2 Trap PDU Error Status.... "
            + ((SnmpPduRequest) pdu).getErrorStatus());
        System.out.println("V2 Trap PDU Error Index..... "
            + ((SnmpPduRequest) pdu).getErrorIndex());
      }

      int k = pdu.getLength();
      for (int i = 0; i < k; i++) {
        SnmpVarBind vb = pdu.getVarBindAt(i);
        System.out.print("Varbind[" + i + "] := " + vb.getName().toString());
        System.out.println(" --> " + vb.getValue().toString());
      }
      System.out.println("");
      break;
    }

    //synchronized(session)
    //{
    //	session.notify();
    //}
  }

  /**
   * Process session errors.
   * 
   * @param session
   *          The trap session in error.
   * @param error
   *          The error condition.
   * @param ref
   *          The reference object, if any.
   *  
   */
  public void SnmpAgentSessionError(SnmpAgentSession session, int error,
      java.lang.Object ref) {
    System.out.println("An error occured in the trap session");
    System.out.println("Session error code = " + error);
    if (ref != null) {
      System.out.println("Session error reference: " + ref.toString());
    }

    if (error == SnmpAgentSession.ERROR_EXCEPTION) {
      synchronized (session) {
        session.notify(); // close the session
      }
    }
  }
}