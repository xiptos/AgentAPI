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
// $Id: SnmpAgentSession.java 4 2004-08-03 14:20:29Z rlopes $
//

//
// 7/16/00 - Weave
//	Fixed error in send(SnmpPeer peer, SnmpTrapPdu trap) where
//	the trap was not actually encoded and stored in the buffer
//	that was transmitted. Bug found by Naz Irizarry.
//

// 9/8/00 - Bob Snider  <bsnider@seekone.com>
//      Adapted from SnmpTrapSession

package org.opennms.protocols.snmp;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;

import org.opennms.protocols.snmp.asn1.ASN1;
import org.opennms.protocols.snmp.asn1.AsnEncoder;
import org.opennms.protocols.snmp.asn1.AsnEncodingException;

/**
 * <P>
 * The trap session is used to send and receives SNMPv1 & v2 trap messages. The
 * messages are received on the configured port, or the default(162) port and
 * then decoded using the set ASN.1 codec. When messages are sent they are
 * encoded using the passed SnmpParameters object that is part of the SnmpPeer
 * object.
 * </P>
 * 
 * <P>
 * A trap message handler must be bound to the session in order to send or
 * receive messages.
 * </P>
 * 
 * @author <a href="http://www.opennms.org">OpenNMS </a>
 * @author <a href="mailto:weave@opennms.org">Brian Weaver </a>
 * @author Sowmya
 * @version $Revision: 1.1.1.1 $ $Date: 2002/01/14 20:01:58 $
 * 
 * @see SnmpAgentHandler
 */
public final class SnmpAgentSession extends Object {
  /**
   * <P>
   * Defines a error due to a thown exception. When the SnmpAgentSessionError
   * method is invoked in the trap handler, the exception object is passed as
   * the ref parameter.
   * </P>
   * 
   * #see SnmpAgentSession#SnmpAgentSessionError
   *  
   */
  public final static int ERROR_EXCEPTION = -1;

  /**
   * <P>
   * Defines an error condition with an invalid PDU. For the moment this is not
   * actually used, but reserved for future use. When the session trap handler
   * error method is invoke the pdu in error should be passed as the ref
   * parameters
   * 
   * #see SnmpAgentSession#SnmpAgentSessionError
   */
  public final static int ERROR_INVALID_PDU = -2;

  /**
   * This is the default port where traps should be sent and received as defined
   * by the RFC.
   *  
   */
  public final static int DEFAULT_PORT = 161;

  /**
   * The default port were traps are sent and received by this session.
   */
  private int m_port;

  /**
   * The default SNMP trap callback handler. If this is not set and it is needed
   * then an SnmpHandlerNotDefinedException is thrown.
   *  
   */
  private SnmpPortal m_portal;

  /**
   * ASN.1 codec used to encode/decode snmp traps that are sent and received by
   * this session.
   */
  private AsnEncoder m_encoder;

  /**
   * The public trap handler that process received traps.
   *  
   */
  private SnmpAgentHandler m_handler;

  /**
   * If this boolean value is set then the receiver thread is terminated due to
   * an exception that was generated in either a handler or a socket error. This
   * is considered a fatal exception.
   */
  private boolean m_threadException;

  /**
   * This is the saved fatal exception that can be rethrown by the application
   */
  private Throwable m_why;

  /**
   * <P>
   * The internal trap handler class is designed to receive information from the
   * enclosed SnmpPortal class. The information is the processed and forwarded
   * when appropiate to the SnmpAgentHandler registered with the session.
   * </P>
   *  
   */
  private class AgentHandler implements SnmpPacketHandler {
    /**
     * Who to pass as the session parameter
     */
    private SnmpAgentSession m_forWhom;

    /**
     * <P>
     * Creates a in internal trap handler to be the intermediary for the
     * interface between the SnmpPortal and the TrapSession.
     * </P>
     * 
     * @param sess
     *          The trap session reference.
     *  
     */
    public AgentHandler(SnmpAgentSession sess) {
      m_forWhom = sess;
    }

    /**
     * <P>
     * Processes the default V1 & V2 messages.
     * </P>
     * 
     * @param agent
     *          The sending agent
     * @param port
     *          The remote port.
     * @param version
     *          The SNMP Version of the message.
     * @param community
     *          The community string from the message.
     * @param pduType
     *          The type of pdu
     * @param pdu
     *          The actual pdu
     * 
     * @exception SnmpPduEncodingException
     *              Thrown if the pdu fails to decode.
     */
    public void processSnmpMessage(InetAddress agent, int port,
        SnmpInt32 version, SnmpOctetString community, int pduType,
        SnmpPduPacket pdu) throws SnmpPduEncodingException {
      try {
        m_handler.snmpReceivedPdu(m_forWhom, version, agent, port, community,
            pdu);
      } catch (Exception e) {
        // discard
      }
    }

    /**
     * <P>
     * Processes V1 trap messages.
     * </P>
     * 
     * @param agent
     *          The sending agent
     * @param port
     *          The remote port.
     * @param community
     *          The community string from the message.
     * @param pdu
     *          The actual pdu
     * 
     * @exception SnmpPduEncodingException
     *              Thrown if the pdu fails to decode.
     */
    public void processSnmpTrap(InetAddress agent, int port,
        SnmpOctetString community, SnmpPduTrap pdu)
        throws SnmpPduEncodingException {
      // discard
    }

    /**
     * <P>
     * Invoked when bad datagrams are received.
     * </P>
     * 
     * @param p
     *          The datagram packet in question.
     *  
     */
    public void processBadDatagram(DatagramPacket p) {
      // do nothing - discard?
    }

    /**
     * <P>
     * Invoked when an exception occurs in the session.
     * </P>
     * 
     * @param e
     *          The exception.
     */
    public void processException(Exception e) {
      try {
        m_handler.SnmpAgentSessionError(m_forWhom, ERROR_EXCEPTION, e);
      } catch (Exception e1) {
        // discard
      }
    }
  }

  /**
   * Used to disallow the default constructor.
   * 
   * @exception java.lang.UnsupportedOperationException
   *              Thrown if the constructor is called.
   *  
   */
  private SnmpAgentSession() throws java.lang.UnsupportedOperationException {
    throw new java.lang.UnsupportedOperationException(
        "Illegal constructor call");
  }

  /**
   * The default SnmpAgentSession constructor.
   * 
   * @param handler
   *          The handler associated for message processing.
   * 
   * @exception java.net.SocketException
   *              If thrown it is from the creation of a DatagramSocket.
   * @exception java.lang.SecurityException
   *              Thrown if the security manager disallows the creation of the
   *              handler.
   */
  public SnmpAgentSession(SnmpAgentHandler handler) throws SocketException {
    m_port = DEFAULT_PORT;
    m_encoder = (new SnmpParameters()).getEncoder();
    m_threadException = false;
    m_why = null;
    m_handler = handler;
    m_portal = new SnmpPortal(new AgentHandler(this), m_encoder, m_port);
  }

  /**
   * The default SnmpAgentSession constructor that takes a packet handler as
   * parameter. Also changes the default port to listen on
   * 
   * @exception java.net.SocketException
   *              If thrown it is from the creation of a DatagramSocket.
   */
  public SnmpAgentSession(SnmpAgentHandler handler, int port)
      throws SocketException {
    m_port = port;
    m_encoder = (new SnmpParameters()).getEncoder();
    m_handler = handler;
    m_portal = new SnmpPortal(new AgentHandler(this), m_encoder, m_port);
    m_threadException = false;
    m_why = null;
  }

  /**
   * Returns the trap handler for this trap session.
   * 
   * @return The SnmpAgentHandler
   */
  public SnmpAgentHandler getHandler() {
    return m_handler;
  }

  /**
   * Sets the trap handler for the session.
   * 
   * @param hdl
   *          The new packet handler
   *  
   */
  public void setHandler(SnmpAgentHandler hdl) {
    m_handler = hdl;
  }

  /**
   * Sets the default encoder.
   * 
   * @param encoder
   *          The new encoder
   *  
   */
  public void setAsnEncoder(AsnEncoder encoder) {
    m_encoder = encoder;
    m_portal.setAsnEncoder(encoder);
  }

  /**
   * Gets the AsnEncoder for the session.
   * 
   * @return the AsnEncoder
   */
  public AsnEncoder getAsnEncoder() {
    return m_encoder;
  }

  /**
   * Used to close the session. Once called the session should be considered
   * invalid and unusable.
   *  
   */
  public void close() {
    m_portal.close();
  }

  /**
   * If an exception occurs in the SNMP receiver thread then raise() will
   * rethrow the exception.
   * 
   * @exception java.lang.Throwable
   *              The base for thrown exceptions.
   */
  public void raise() throws Throwable {
    if (m_threadException)
      throw m_why;
  }

  /**
   * Transmits the specified SnmpRequest to the SnmpPeer defined. First the
   * SnmpPdu contained within the request is encoded using the peer AsnEncoder,
   * as defined by the SnmpParameters. Once the packet is encoded it is
   * transmitted to the agent defined by SnmpPeer. If an error occurs an
   * appropiate exception is generated.
   * 
   * @param peer
   *          The remote peer to send to.
   * @param pdu
   *          The pdu to transmit
   * 
   * @exception SnmpPduEncodingException
   *              Thrown if an encoding exception occurs at the session level
   * @exception org.opennms.protocols.snmp.asn1.AsnEncodingException
   *              Thrown if an encoding exception occurs in the AsnEncoder
   *              object.
   * @exception java.io.IOException
   *              Thrown if an error occurs sending the encoded datagram
   * 
   *  
   */
  public void send(SnmpPeer peer, SnmpPduPacket pdu)
      throws SnmpPduEncodingException, AsnEncodingException,
      java.io.IOException {
    //
    // break down the pieces into usable variables
    //
    SnmpParameters parms = peer.getParameters();

    //
    // Get the encoder and start
    // the encoding process
    //
    AsnEncoder encoder = parms.getEncoder();

    //
    // get a suitable buffer (16k)
    //
    int begin = 0;
    int offset = 0;
    byte[] buf = new byte[16 * 1024];

    //
    // encode the snmp version
    //
    SnmpInt32 version = new SnmpInt32(parms.getVersion());
    offset = version.encodeASN(buf, offset, encoder);

    //
    // get the correct community string. The
    // SET command uses the write community, all
    // others use the read community
    //
    SnmpOctetString community = new SnmpOctetString(parms.getReadCommunity()
        .getBytes());

    //
    // encode the community strings
    //
    offset = community.encodeASN(buf, offset, encoder);
    offset = pdu.encodeASN(buf, offset, encoder);

    //
    // build the header, don't forget to mark the
    // pivot point
    //
    int pivot = offset;
    offset = encoder.buildHeader(buf, offset,
        (byte) (ASN1.SEQUENCE | ASN1.CONSTRUCTOR), pivot);

    //
    // rotate the buffer around the pivot point
    //
    SnmpUtil.rotate(buf, 0, pivot, offset);

    //
    // transmit the datagram
    //
    m_portal.send(peer, buf, offset);
  }

} // end of SnmpAgentSession class

