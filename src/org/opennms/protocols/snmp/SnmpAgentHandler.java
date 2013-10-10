// 
//    jSNMP - SNMPv1 & v2 Compliant Libraries for Java
//    Copyright (C) 2000  PlatformWorks, Inc.
//
//    This library is free software; you can redistribute it and/or
//    modify it under the terms of the GNU Lesser General Public
//    License as published by the Free Software Foundation; either
//    version 2.1 of the License, or (at your option) any later version.
//
//    This library is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
//    Lesser General Public License for more details.
//
//    You should have received a copy of the GNU Lesser General Public
//    License along with this library; if not, write to the Free Software
//    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
//   
// For more information contact: 
//	Brian Weaver	<weave@opennms.org>
//	http://www.opennms.org/
//
//
// Tab Size = 8
//
// 9/8/00 - Bob Snider  <bsnider@seekone.com>
//      Adapted from SnmpTrapHandler

package org.opennms.protocols.snmp;

import java.net.InetAddress;

/**
 * <P>
 * The SnmpAgentHandler interface is implemented by an object that wishs to
 * receive callbacks when a SNMP trap protocol data unit is received from an
 * agent.
 * </P>
 * 
 * @author <a href="http://www.opennms.org/">OpenNMS </a>
 * @author <a href="mailto:weave@opennms.org">Brian Weaver </a>
 * @version $Revision: 1.1.1.1 $ $Date: 2002/01/14 20:01:58 $
 *  
 */
public interface SnmpAgentHandler {
  /**
   * <P>
   * This method is defined to handle SNMP requests that are received by the
   * session. The parameters allow the handler to determine the host, port, and
   * community string of the received PDU
   * </P>
   * 
   * @param session
   *          The SNMP session
   * @param manager
   *          The remote sender
   * @param port
   *          The remote senders port
   * @param community
   *          The community string
   * @param pdu
   *          The SNMP pdu
   *  
   */
  void snmpReceivedPdu(SnmpAgentSession session, SnmpInt32 version,
      InetAddress manager, int port, SnmpOctetString community,
      SnmpPduPacket pdu);

  /**
   * <P>
   * This method is invoked if an error occurs in the session. The error code
   * that represents the failure will be passed in the second parameter,
   * 'error'. The error codes can be found in the class SnmpAgentSession class.
   * </P>
   * 
   * <P>
   * If a particular PDU is part of the error condition it will be passed in the
   * third parameter, 'pdu'. The pdu will be of the type SnmpPduRequest or
   * SnmpPduTrap object. The handler should use the "instanceof" operator to
   * determine which type the object is. Also, the object may be null if the
   * error condition is not associated with a particular PDU.
   * </P>
   * 
   * @param session
   *          The SNMP Session
   * @param error
   *          The error condition value.
   * @param ref
   *          The PDU reference, or potentially null. It may also be an
   *          exception.
   * 
   *  
   */
  void SnmpAgentSessionError(SnmpAgentSession session, int error, Object ref);
}

