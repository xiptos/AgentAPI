/*
 * $Id: AbstractAgent.java 3 2004-08-03 10:42:11Z rlopes $
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

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;

import pt.ipb.agentapi.event.BulkMessageEvent;
import pt.ipb.agentapi.event.EventListenerList;
import pt.ipb.agentapi.event.MessageEvent;
import pt.ipb.agentapi.event.MessageListener;
import pt.ipb.agentapi.event.TrapEvent;
import pt.ipb.snmp.type.smi.Int;
import pt.ipb.snmp.type.smi.OID;
import pt.ipb.snmp.type.smi.VarBind;

/**
 * This class implements MessageListener to receive messages. It is an entry
 * point to the agent objects. It is a gateway between the GET, GET-NEXT,
 * GET-BULK, SET to GET and SET recognized by the agent objects.
 * 
 * <pre>
 * 
 *  
 *   
 *              +-------------+                +-------------+
 *    ---------&gt;|             |---------------&gt;| AgentObject |
 *    GET, SET  | AbstractAg. |    GET, SET    |             |
 *    ---------&gt;| MessageList.|&lt;---------------|             |
 *    &lt;---------|             |  RESPONSE      +-------------+
 *     RESPONSE +-------------+
 *   
 *    
 *   
 *  
 * </pre>
 * 
 * The object responsible for originating messages should: <code><pre>
 * 
 *  
 *   
 *    ...
 *    AbstractAgent agent = new ...;
 *    ...
 *    addMessageListener(agent);
 *    ...
 *    
 *   
 *  
 * </pre></code>
 *  
 */
public abstract class AbstractAgent implements Agent {

  public final static byte NO_RESPONSE = -1;

  public final static byte NO_ERROR = 0;

  public final static byte TOO_BIG = 1;

  public final static byte NO_SUCH_NAME = 2;

  public final static byte BAD_VALUE = 3;

  public final static byte READ_ONLY = 4;

  public final static byte GEN_ERR = 5;

  public final static byte NO_ACCESS = 6;

  public final static byte WRONG_TYPE = 7;

  public final static byte WRONG_LENGTH = 8;

  public final static byte WRONG_ENCODING = 9;

  public final static byte WRONG_VALUE = 10;

  public final static byte NO_CREATION = 11;

  public final static byte INCONSISTENT_VALUE = 12;

  public final static byte RESOURCE_UNAVAILABLE = 13;

  public final static byte COMMIT_FAILED = 14;

  public final static byte UNDO_FAILED = 15;

  public final static byte AUTHORIZATION_ERROR = 16;

  public final static byte NOT_WRITABLE = 17;

  public final static byte INCONSISTENT_NAME = 18;

  public final static byte NO_SUCH_OBJECT = 19;

  public final static byte NO_SUCH_INSTANCE = 20;

  public final static byte END_OF_MIB_VIEW = 21;

  // Status values
  public static final byte NOT_EXISTING = -1;

  public static final byte OTHER_COL = 0;

  public static final byte ACTIVE = 1;

  public static final byte NOT_IN_SERVICE = 2;

  public static final byte NOT_READY = 3;

  public static final byte CREATE_AND_GO = 4;

  public static final byte CREATE_AND_WAIT = 5;

  public static final byte DESTROY = 6;

  // The agent message listener.
  // The agent acts as a message producer and the listener
  // acts as a consumer. So 'Agent'----->'Listener'
  EventListenerList listenerList = new EventListenerList();

  // The Object of AbstracObjects
  protected SortedMap tree;

  /**
   * Creates an empty agent. The constructor calls the method setObjects(),
   * which can be used to define the first objects.
   */
  public AbstractAgent() {
    this(Collections.synchronizedSortedMap(new TreeMap()));
  }

  /**
   * Creates an agent with the specified tree of AbstractAgents. The tree has
   * the OID as a key and the AgentObject as the object.
   */
  public AbstractAgent(SortedMap map) {
    this.tree = Collections.synchronizedSortedMap(map);
    setObjects();
  }

  /**
   * Used by child classes to specify the tree of AbstractObjects.
   */
  public void setObjects() {
  }

  /**
   * Adds an AbstractObject to this tree ordered by OID.
   */
  public Object addObject(AgentObject a) throws ClassCastException,
      NullPointerException {
    if (!exists(a.getOIDObject()))
      return tree.put(a.getOIDObject(), a);
    else
      return null;
  }

  /**
   * Removes an AbstractObject from this tree.
   */
  public Object removeObject(OID oid) throws ClassCastException,
      NullPointerException {
    return tree.remove(oid);
  }

  /**
   * Checks if the OID already exists.
   */
  public boolean exists(OID oid) throws ClassCastException,
      NullPointerException {
    return tree.containsKey(oid);
  }

  /**
   * Gets the common ancestor OID of all the agent objects.
   */
  public OID getCommonAncestorOID() {
    OID ancestor = null;
    for (Iterator i = tree.keySet().iterator(); i.hasNext();) {
      OID key = (OID) i.next();
      if (ancestor == null)
        ancestor = key;
      while (!key.startsWith(ancestor)) {
        ancestor = ancestor.subOID(0, ancestor.length() - 1);
      }
    }
    return ancestor;
  }

  /**
   * Returns the AbstractObject from this tree. The key is the OID string.
   */
  public AgentObject getAgentObject(String oid) throws ClassCastException,
      NullPointerException, NoSuchElementException {
    AgentObject obj = (AgentObject) tree.get(new OID(oid));
    if (obj == null)
      throw new NoSuchElementException();
    return obj;
  }

  /**
   * Returns the NextAbstractObject from this tree. The key is the OID string.
   */
  public AgentObject getNextAgentObject(String oid) throws ClassCastException,
      NullPointerException, NoSuchElementException {
    // From the API documentation. Do not include the equal case.
    SortedMap greaterThanMap = tree.tailMap(new OID(oid + ".0"));
    return (AgentObject) tree.get(greaterThanMap.firstKey());
  }

  /**
   * Returns the PreviousAbstractObject from this tree. The key is the OID
   * string.
   */
  public AgentObject getPreviousAgentObject(String oid)
      throws ClassCastException, NullPointerException, NoSuchElementException {
    // From the API documentation. Do not include the equal case.
    SortedMap lessThanMap = tree.headMap(new OID(oid));
    AgentObject ao = (AgentObject) tree.get(lessThanMap.lastKey());
    return ao;
  }

  /**
   * Checks if the given AbstractAgent is a NodeAbstractAgent. A
   * NodeAbstractAgent can have more AbstractAgents beneath it.
   */
  public boolean isNode(AgentObject a) {
    return (a instanceof NodeAgentObject);
  }

  ///////////////////////////////////////////////////////////
  // Message related methods
  ///////////////////////////////////////////////////////////

  /**
   * Agents are not meant to receive traps, altough this behaviour may be
   * changed in subclasses...
   */
  public void trapMessage(MessageEvent e) {
  }

  /**
   * (1) If the variable binding's name exactly matches the name of a variable
   * accessible by this request, then the variable binding's value field is set
   * to the value of the named variable.
   * <p>
   * 
   * (2) Otherwise, if the variable binding's name does not have an OBJECT
   * IDENTIFIER prefix which exactly matches the OBJECT IDENTIFIER prefix of any
   * (potential) variable accessible by this request, then its value field is
   * set to <b>`noSuchObject' </b>.
   * <p>
   * 
   * (3) Otherwise, the variable binding's value field is set to
   * <b>`noSuchInstance' </b>.
   * <p>
   * 
   * The following errors are processed by the calling object because of the
   * dependencies on the results from the other called objects. The error is
   * communicated to the calling object by throwing an MessageException with the
   * correspondent MessageException.exceptionString( ...). <br>
   * 
   * If the processing of any variable binding fails for a reason other than
   * listed above, then the Response-PDU is re-formatted with the same values in
   * its request-id and variable-bindings fields as the received GetRequest-PDU,
   * with the value of its error-status field set to <b>`genErr' </b> and the
   * value of its error-index field is set to the index of the failed variable
   * binding. <br>
   * 
   * Otherwise, the value of the Response-PDU's error-status field is set to
   * <b>`noError' </b>, and the value of its error-index field is zero. <br>
   * 
   * The generated Response-PDU is then encapsulated into a message. If the size
   * of the resultant message is less than or equal to both a local constraint
   * and the maximum message size of the originator, it is transmitted to the
   * originator of the GetRequest-PDU. <br>
   * 
   * Otherwise, an alternate Response-PDU is generated. This alternate
   * Response-PDU is formatted with the same value in its request-id field as
   * the received GetRequest-PDU, with the value of its error-status field set
   * to <b>`tooBig' </b>, the value of its error-index field set to zero, and an
   * empty variable-bindings field. This alternate Response-PDU is then
   * encapsulated into a message. If the size of the resultant message is less
   * than or equal to both a local constraint and the maximum message size of
   * the originator, it is transmitted to the originator of the GetRequest-PDU.
   * Otherwise, the snmpSilentDrops counter is incremented and the resultant
   * message is discarded. <br>
   *  
   */
  public void getMessage(MessageEvent e) {
    Vector vars = e.getVarBindVector();

    for (int i = 0; i < vars.size(); i++) {
      VarBind varBind = (VarBind) vars.elementAt(i);

      VarBind response = getOperation(varBind);

      varBind.setOID(response.getOID());
      varBind.setValue(response.getValue());
      varBind.setError(response.isError());
    }

    MessageEvent m = new MessageEvent(this, vars);
    m.setTarget(e.getTarget());
    m.setTargetPort(e.getTargetPort());
    m.setRequestID(e.getRequestID());
    m.setProperties(e.getProperties());
    fireResponse(m);
  }

  /**
   * Called by getMessage to perform the get operation on the object tree.
   */
  public VarBind getOperation(VarBind varBind) {
    VarBind response;
    try {
      // Exceptions:
      // ClassCastException - General Error
      // NullPointerException - General Error
      // NoSuchElementException - NO_SUCH_OBJECT Error

      String givenOID = varBind.getOID();
      String oid = givenOID;
      AgentObject agentObject = null;

      if (exists(new OID(givenOID))) {
        agentObject = getAgentObject(givenOID);
        return agentObject.get(givenOID);

      } else if (!isNode(getPreviousAgentObject(givenOID))) {
        throw new NoSuchElementException();

      }
      agentObject = getPreviousAgentObject(givenOID);
      response = agentObject.get(givenOID);

    } catch (MessageException ex) {
      //ex.printStackTrace();
      response = new VarBind(varBind.getOID(), new Int(ex.getError()), true);

    } catch (NoSuchElementException nse) {
      //ex.printStackTrace();
      response = new VarBind(varBind.getOID(), new Int(NO_SUCH_OBJECT), true);

    } catch (Exception exc) {
      exc.printStackTrace();
      response = new VarBind(varBind.getOID(), new Int(GEN_ERR), true);
    }
    return response;
  }

  /**
   * Called by getNextMessage to perform the get operation on the object tree.
   */
  public VarBind getNextOperation(VarBind varBind) {
    VarBind response = null;
    try {
      // Exceptions:
      // ClassCastException - General Error
      // NullPointerException - General Error
      // NoSuchElementException - END_OF_MIB_VIEW Error

      // NoSuchElementException em getPreviousAgentObject - No Error

      String givenOID = varBind.getOID();
      String oid = givenOID;
      AgentObject agentObject = null;
      boolean isNode = false;

      if (exists(new OID(givenOID))) {
        agentObject = getAgentObject(givenOID);

      } else {
        try {
          agentObject = getPreviousAgentObject(givenOID);

        } catch (Exception e) {
        }
      }

      if (isNode(agentObject))
        isNode = true;

      while (true) {
        if (!isNode) {
          agentObject = getNextAgentObject(oid);
        }
        if (isNode(agentObject)) {
          NodeAgentObject nodeAgentObject = (NodeAgentObject) agentObject;
          // Possivelmente, um erro. givenOID podera ser oid
          if (nodeAgentObject.hasNext(givenOID)) {
            //return nodeAgentObject.getNext(oid);
            // Possivelmente, um erro. givenOID podera ser oid
            return nodeAgentObject.getNext(givenOID);
          } else {
            oid = nodeAgentObject.getOID();
            //throw new NoSuchElementException();
          }

        } else {
          oid = agentObject.getOID();
          return agentObject.get(oid);
        }
        isNode = false;
      }

    } catch (MessageException ex) {
      //ex.printStackTrace();
      response = new VarBind(varBind.getOID(), new Int(ex.getError()), true);
    } catch (NoSuchElementException nse) {
      //nse.printStackTrace();
      response = new VarBind(varBind.getOID(), new Int(END_OF_MIB_VIEW), true);
    } catch (Exception exc) {
      exc.printStackTrace();
      response = new VarBind(varBind.getOID(), new Int(GEN_ERR), true);
    }
    return response;
  }

  /**
   * Called by setMessage to perform the set operation.
   */
  public VarBind setOperation(VarBind varBind) {
    VarBind response;
    try {
      // Exceptions:
      // ClassCastException - General Error
      // NullPointerException - General Error
      // NoSuchElementException - if can't be created, NOT_WRITABLE Error

      String givenOID = varBind.getOID();
      String oid = givenOID;
      AgentObject agentObject = null;

      if (exists(new OID(givenOID))) {
        agentObject = getAgentObject(givenOID);
        if (!(agentObject instanceof WritableAgentObject)) {
          response = new VarBind(varBind.getOID(), new Int(NOT_WRITABLE), true);
        } else {
          return ((WritableAgentObject) agentObject).set(new VarBind(givenOID,
              varBind.getValue()));
        }

      } else if (!isNode(getPreviousAgentObject(givenOID))) {
        throw new NoSuchElementException();

      }

      agentObject = getPreviousAgentObject(givenOID);
      if (!(agentObject instanceof WritableAgentObject)) {
        response = new VarBind(varBind.getOID(), new Int(NOT_WRITABLE), true);
      } else {
        response = ((WritableAgentObject) agentObject).set(new VarBind(
            givenOID, varBind.getValue()));
      }

      // Exceptions:
      // MessageException - the same error of the exception
    } catch (MessageException ex) {
      //ex.printStackTrace();
      response = new VarBind(varBind.getOID(), new Int(ex.getError()), true);
    } catch (NoSuchElementException nse) {
      //ex.printStackTrace();
      response = new VarBind(varBind.getOID(), new Int(NOT_WRITABLE), true);
    } catch (Exception exc) {
      exc.printStackTrace();
      response = new VarBind(varBind.getOID(), new Int(GEN_ERR), true);
    }

    return response;
  }

  /**
   * (1) The variable is located which is in the lexicographically ordered list
   * of the names of all variables which are accessible by this request and
   * whose name is the first lexicographic successor of the variable binding's
   * name in the incoming GetNextRequest-PDU. The corresponding variable
   * binding's name and value fields in the Response-PDU are set to the name and
   * value of the located variable.
   * <p>
   * 
   * (2) If the requested variable binding's name does not lexicographically
   * precede the name of any variable accessible by this request, i.e., there is
   * no lexicographic successor, then the corresponding variable binding
   * produced in the Response-PDU has its value field set to <b>`endOfMibView'
   * </b>, and its name field set to the variable binding's name in the request.
   * <p>
   * 
   * The following errors are processed by the calling object because of the
   * dependencies on the results from the other called objects. The error is
   * communicated to the calling object by throwing an MessageException with the
   * correspondent MessageException.exceptionString( ...). <br>
   * 
   * If the processing of any variable binding fails for a reason other than
   * listed above, then the Response-PDU is re-formatted with the same values in
   * its request-id and variable-bindings fields as the received
   * GetNextRequest-PDU, with the value of its error-status field set to
   * `genErr', and the value of its error-index field is set to the index of the
   * failed variable binding. <br>
   * 
   * Otherwise, the value of the Response-PDU's error-status field is set to
   * <b>`noError' </b>, and the value of its error-index field is zero. <br>
   * 
   * The generated Response-PDU is then encapsulated into a message. If the size
   * of the resultant message is less than or equal to both a local constraint
   * and the maximum message size of the originator, it is transmitted to the
   * originator of the GetNextRequest-PDU. <br>
   * 
   * Otherwise, an alternate Response-PDU is generated. This alternate
   * Response-PDU is formatted with the same values in its request-id field as
   * the received GetNextRequest-PDU, with the value of its error-status field
   * set to <b>`tooBig' </b>, the value of its error-index field set to zero,
   * and an empty variable-bindings field. This alternate Response-PDU is then
   * encapsulated into a message. If the size of the resultant message is less
   * than or equal to both a local constraint and the maximum message size of
   * the originator, it is transmitted to the originator of the
   * GetNextRequest-PDU. Otherwise, the snmpSilentDrops counter is incremented
   * and the resultant message is discarded. <br>
   *  
   */
  public void getNextMessage(MessageEvent e) {
    Vector vars = e.getVarBindVector();

    for (int i = 0; i < vars.size(); i++) {
      VarBind varBind = (VarBind) vars.elementAt(i);

      VarBind response = getNextOperation(varBind);

      varBind.setOID(response.getOID());
      varBind.setValue(response.getValue());
      varBind.setError(response.isError());
    }

    MessageEvent m = new MessageEvent(this, vars);
    m.setTarget(e.getTarget());
    m.setTargetPort(e.getTargetPort());
    m.setRequestID(e.getRequestID());
    m.setProperties(e.getProperties());
    fireResponse(m);
  }

  /**
   * A GetBulkRequest-PDU is generated and transmitted at the request of a
   * SNMPv2 application. The purpose of the GetBulkRequest-PDU is to request the
   * transfer of a potentially large amount of data, including, but not limited
   * to, the efficient and rapid retrieval of large tables.
   * 
   * Upon receipt of a GetBulkRequest-PDU, the receiving SNMPv2 entity processes
   * each variable binding in the variable-binding list to produce a
   * Response-PDU with its request-id field having the same value as in the
   * request. Processing begins by examining the values in the non-repeaters and
   * max-repetitions fields. If the value in the non-repeaters field is less
   * than zero, then the value of the field is set to zero. Similarly, if the
   * value in the max-repetitions field is less than zero, then the value of the
   * field is set to zero.
   * 
   * For the GetBulkRequest-PDU type, the successful processing of each variable
   * binding in the request generates zero or more variable bindings in the
   * Response-PDU. That is, the one-to-one mapping between the variable bindings
   * of the GetRequest-PDU, GetNextRequest- PDU, and SetRequest-PDU types and
   * the resultant Response-PDUs does not apply for the mapping between the
   * variable bindings of a GetBulkRequest-PDU and the resultant Response-PDU.
   * 
   * The values of the non-repeaters and max-repetitions fields in the request
   * specify the processing requested. One variable binding in the Response-PDU
   * is requested for the first N variable bindings in the request and M
   * variable bindings are requested for each of the R remaining variable
   * bindings in the request. Consequently, the total number of requested
   * variable bindings communicated by the request is given by N + (M * R),
   * where N is the minimum of: a) the value of the non-repeaters field in the
   * request, and b) the number of variable bindings in the request; M is the
   * value of the max-repetitions field in the request; and R is the maximum of:
   * a) number of variable bindings in the request - N, and b) zero.
   * 
   * The receiving SNMPv2 entity produces a Response-PDU with up to the total
   * number of requested variable bindings communicated by the request. The
   * request-id shall have the same value as the received GetBulkRequest-PDU.
   * 
   * If N is greater than zero, the first through the (N)-th variable bindings
   * of the Response-PDU are each produced as follows:
   * 
   * (1) The variable is located which is in the lexicographically ordered list
   * of the names of all variables which are accessible by this request and
   * whose name is the first lexicographic successor of the variable binding's
   * name in the incoming GetBulkRequest-PDU. The corresponding variable
   * binding's name and value fields in the Response-PDU are set to the name and
   * value of the located variable.
   * 
   * (2) If the requested variable binding's name does not lexicographically
   * precede the name of any variable accessible by this request, i.e., there is
   * no lexicographic successor, then the corresponding variable binding
   * produced in the Response-PDU has its value field set to `endOfMibView', and
   * its name field set to the variable binding's name in the request. If M and
   * R are non-zero, the (N + 1)-th and subsequent variable bindings of the
   * Response-PDU are each produced in a similar manner. For each iteration i,
   * such that i is greater than zero and less than or equal to M, and for each
   * repeated variable, r, such that r is greater than zero and less than or
   * equal to R, the (N + ( (i-1) * R ) + r)-th variable binding of the
   * Response-PDU is produced as follows:
   * 
   * (1) The variable which is in the lexicographically ordered list of the
   * names of all variables which are accessible by this request and whose name
   * is the (i)-th lexicographic successor of the (N + r)-th variable binding's
   * name in the incoming GetBulkRequest-PDU is located and the variable
   * binding's name and value fields are set to the name and value of the
   * located variable.
   * 
   * (2) If there is no (i)-th lexicographic successor, then the corresponding
   * variable binding produced in the Response-PDU has its value field set to
   * `endOfMibView', and its name field set to either the last lexicographic
   * successor, or if there are no lexicographic successors, to the (N + r)-th
   * variable binding's name in the request. While the maximum number of
   * variable bindings in the Response-PDU is bounded by N + (M * R), the
   * response may be generated with a lesser number of variable bindings
   * (possibly zero) for either of three reasons.
   * 
   * (1) If the size of the message encapsulating the Response-PDU containing
   * the requested number of variable bindings would be greater than either a
   * local constraint or the maximum message size of the originator, then the
   * response is generated with a lesser number of variable bindings. This
   * lesser number is the ordered set of variable bindings with some of the
   * variable bindings at the end of the set removed, such that the size of the
   * message encapsulating the Response-PDU is approximately equal to but no
   * greater than either a local constraint or the maximum message size of the
   * originator. Note that the number of variable bindings removed has no
   * relationship to the values of N, M, or R.
   * 
   * (2) The response may also be generated with a lesser number of variable
   * bindings if for some value of iteration i, such that i is greater than zero
   * and less than or equal to M, that all of the generated variable bindings
   * have the value field set to the `endOfMibView'. In this case, the variable
   * bindings may be truncated after the (N + (i * R))-th variable binding.
   * 
   * (3) In the event that the processing of a request with many repetitions
   * requires a significantly greater amount of processing time than a normal
   * request, then an agent may terminate the request with less than the full
   * number of repetitions, providing at least one repetition is completed.
   * 
   * If the processing of any variable binding fails for a reason other than
   * listed above, then the Response-PDU is re-formatted with the same values in
   * its request-id and variable-bindings fields as the received
   * GetBulkRequest-PDU, with the value of its error-status field set to
   * `genErr', and the value of its error-index field is set to the index of the
   * variable binding in the original request which corresponds to the failed
   * variable binding.
   * 
   * Otherwise, the value of the Response-PDU's error-status field is set to
   * `noError', and the value of its error-index field to zero.
   * 
   * The generated Response-PDU (possibly with an empty variable-bindings field)
   * is then encapsulated into a message. If the size of the resultant message
   * is less than or equal to both a local constraint and the maximum message
   * size of the originator, it is transmitted to the originator of the
   * GetBulkRequest-PDU. Otherwise, the snmpSilentDrops [9] counter is
   * incremented and the resultant message is discarded.
   */
  public void getBulkMessage(BulkMessageEvent e) {
    int nonRep = (e.getNonRep() < 0) ? 0 : e.getNonRep();
    int maxRep = (e.getMaxRep() < 0) ? 0 : e.getMaxRep();
    Vector vars = e.getVarBindVector();
    Vector resps = new Vector();

    for (int i = 0; i < vars.size(); i++) {
      VarBind varBind = (VarBind) vars.elementAt(i);
      if (i < nonRep) {
        // A single varBind is added
        VarBind response = getNextOperation(varBind);
        resps.addElement(response);
      } else {
        // maxRep variables are added for each request varBind
        VarBind response = varBind;
        for (int t = 0; t < maxRep; t++) {
          response = getNextOperation(response);
          resps.addElement(response);
        }
      }
    }

    MessageEvent m = new MessageEvent(this, resps);
    m.setTarget(e.getTarget());
    m.setTargetPort(e.getTargetPort());
    m.setRequestID(e.getRequestID());
    m.setProperties(e.getProperties());
    fireResponse(m);
  }

  /**
   * Agents are not supposed to receive informs, although this behaviour may be
   * changed by subclasses...
   */
  public void informMessage(MessageEvent e) {
  }

  /**
   * 
   * (1) If the variable binding's name specifies an existing or non- existent
   * variable to which this request is/would be denied access because it
   * is/would not be in the appropriate MIB view, then the value of the
   * Response-PDU's error-status field is set to <b>`noAccess' </b>, and the
   * value of its error-index field is set to the index of the failed variable
   * binding.
   * <p>
   * 
   * (2) Otherwise, if there are no variables which share the same OBJECT
   * IDENTIFIER prefix as the variable binding's name, and which are able to be
   * created or modified no matter what new value is specified, then the value
   * of the Response-PDU's error-status field is set to <b>`notWritable' </b>,
   * and the value of its error-index field i s set to the index of the failed
   * variable binding.
   * <p>
   * 
   * (3) Otherwise, if the variable binding's value field specifies, according
   * to the ASN.1 language, a type which is inconsistent with that required for
   * all variables which share the same OBJECT IDENTIFIER prefix as the variable
   * binding's name, then the value of the Response-PDU's error-status field is
   * set to <b>`wrongType' </b>, and the value of its error-index field is set
   * to the index of the failed variable binding.
   * <p>
   * 
   * (4) Otherwise, if the variable binding's value field specifies, according
   * to the ASN.1 language, a length which is inconsistent with that required
   * for all variables which share the same OBJECT IDENTIFIER prefix as the
   * variable binding's name, then the value of the Response-PDU's error-status
   * field is set to <b>`wrongLength' </b>, an d the value of its error-index
   * field is set to the index of the failed variable binding.
   * <p>
   * 
   * (5) Otherwise, if the variable binding's value field contains an ASN.1
   * encoding which is inconsistent with that field's ASN.1 tag, then the value
   * of the Response-PDU's error-status field is set to <b>`wrongEncoding' </b>,
   * and the value of its error-index field is set to the index of the failed
   * variable binding. (Note that not all implementation strategies will
   * generate this error.)
   * <p>
   * 
   * (6) Otherwise, if the variable binding's value field specifies a value
   * which could under no circumstances be assigned to the variable, then the
   * value of the Response-PDU's error-status field is set to <b>`wrongValue'
   * </b>, and the value of its error-index field is set to th e index of the
   * failed variable binding.
   * <p>
   * 
   * (7) Otherwise, if the variable binding's name specifies a variable which
   * does not exist and could not ever be created (even though some variables
   * sharing the same OBJECT IDENTIFIER prefix might under some circumstances be
   * able to be created), then the value of the Response-PDU's error-status
   * field is set to <b>`noCreation' </b>, and the value of its error-index
   * field is set to the index of the failed variable binding.
   * <p>
   * 
   * (8) Otherwise, if the variable binding's name specifies a variable which
   * does not exist but can not be created under the present circumstances (even
   * though it could be created under other circumstances), then the value of
   * the Response-PDU's error-status field is set to <b>`inconsistentName' </b>,
   * and the value of its error- index field is set to the index of the failed
   * variable binding.
   * <p>
   * 
   * (9) Otherwise, if the variable binding's name specifies a variable which
   * exists but can not be modified no matter what new value is specified, then
   * the value of the Response-PDU's error-status field is set to
   * <b>`notWritable' </b>, and the value of its error-index field i s set to
   * the index of the failed variable binding.
   * <p>
   * 
   * (10) Otherwise, if the variable binding's value field specifies a value
   * that could under other circumstances be held by the variable, but is
   * presently inconsistent or otherwise unable to be assigned to the variable,
   * then the value of the Response-PDU's error-status field is set to
   * <b>`inconsistentValue' </b>, and the value of its error-index field is set
   * to the index of the failed variable binding.
   * <p>
   * 
   * (11) When, during the above steps, the assignment of the value specified by
   * the variable binding's value field to the specified variable requires the
   * allocation of a resource which is presently unavailable, then the value of
   * the Response-PDU's error-status field is set to <b>`resourceUnavailable'
   * </b>, and the value of its error - index field is set to the index of the
   * failed variable binding.
   * <p>
   * 
   * (12) If the processing of the variable binding fails for a reason other
   * than listed above, then the value of the Response-PDU's error- status field
   * is set to <b>`genErr' </b>, and the value of its error-index field is set
   * to the index of the failed variable binding.
   * <p>
   * 
   * (13) Otherwise, the validation of the variable binding succeeds.
   * <p>
   * 
   * At the end of the first phase, if the validation of all variable bindings
   * succeeded, then the value of the Response-PDU's error-status field is set
   * to <b>`noError' </b> and the value of its error-index field is zero, and
   * processing continues as follows.
   * <p>
   * 
   * For each variable binding in the request, the named variable is created if
   * necessary, and the specified value is assigned to it. Each of these
   * variable assignments occurs as if simultaneously with respect to all other
   * assignments specified in the same request. However, if the same variable is
   * named more than once in a single request, with different associated values,
   * then the actual assignment made to that variable is
   * implementation-specific.
   * <p>
   * 
   * If any of these assignments fail (even after all the previous validations),
   * then all other assignments are undone, and the Response-PDU is modified to
   * have the value of its error-status field set to <b>`commitFailed' </b>, and
   * the value of its error-index field set t o the index of the failed variable
   * binding.
   * <p>
   * 
   * If and only if it is not possible to undo all the assignments, then the
   * Response-PDU is modified to have the value of its error-status field set to
   * <b>`undoFailed' </b>, and the value of its error-index field i s set to
   * zero. Note that implementations are strongly encouraged to take all
   * possible measures to avoid use of either <b>`commitFailed' </b> or
   * <b>`undoFailed' </b>- these two error-status codes are not to be taken as
   * license to take the easy way out in an implementation.
   * <p>
   * 
   * Finally, the generated Response-PDU is encapsulated into a message, and
   * transmitted to the originator of the SetRequest-PDU.
   */
  public void setMessage(MessageEvent e) {
    Vector vars = e.getVarBindVector();

    for (int i = 0; i < vars.size(); i++) {
      VarBind varBind = (VarBind) vars.elementAt(i);

      VarBind response = setOperation(varBind);

      varBind.setOID(response.getOID());
      varBind.setValue(response.getValue());
      varBind.setError(response.isError());
    }

    MessageEvent m = new MessageEvent(this, vars);
    m.setTarget(e.getTarget());
    m.setTargetPort(e.getTargetPort());
    m.setRequestID(e.getRequestID());
    m.setProperties(e.getProperties());
    fireResponse(m);

  }

  /**
   * Agents are not supposed to receive responses, although this behaviour may
   * be changed by subclasses...
   */
  public void responseMessage(MessageEvent e) {
  }

  /**
   * Adds the specified message listener to receive message events from this
   * object. Message events occur when this object wishes to send a message to
   * other object. If l is null, no exception is thrown and no action is
   * performed.
   * 
   * @param l
   *          the message listener
   */
  public synchronized void addMessageListener(MessageListener l) {
    listenerList.add(MessageListener.class, l);
  }

  /**
   * Removes the specified message listener so that it no longer receives
   * message events from this object. If l is null, no exception is thrown and
   * no action is performed.
   * 
   * @param l
   *          the message listener
   */
  public synchronized void removeMessageListener(MessageListener l) {
    listenerList.remove(MessageListener.class, l);
  }

  protected void fireResponse(MessageEvent e) {
    Object[] listeners = listenerList.getListenerList();
    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == MessageListener.class) {
        ((MessageListener) listeners[i + 1]).responseMessage(e);
      }
    }
  }

  protected void fireTrap(MessageEvent e) {
    Object[] listeners = listenerList.getListenerList();
    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == MessageListener.class) {
        ((MessageListener) listeners[i + 1]).trapMessage(e);
      }
    }
  }

  /**
   * Sends a trap or notification
   */
  public void sendNotification(TrapEvent e) {
    fireTrap(e);
  }

  /**
   * Returns a Collection of all the agent AbstractObjects.
   */
  public Collection getContents() {
    if (tree != null)
      return tree.values();
    return null;
  }

  public static String errorToString(byte error) {
    switch (error) {
    case NO_RESPONSE:
      return new String("No response");
    case NO_ERROR:
      return new String("No error");
    case TOO_BIG:
      return new String("Too Big");
    case NO_SUCH_NAME:
      return new String("No such name");
    case BAD_VALUE:
      return new String("Bad value");
    case READ_ONLY:
      return new String("Read only");
    case GEN_ERR:
      return new String("General error");
    case NO_ACCESS:
      return new String("No Access");
    case WRONG_TYPE:
      return new String("Wrong type");
    case WRONG_LENGTH:
      return new String("Wrong length");
    case WRONG_ENCODING:
      return new String("Wrong encoding");
    case WRONG_VALUE:
      return new String("Wrong value");
    case NO_CREATION:
      return new String("No creation");
    case INCONSISTENT_VALUE:
      return new String("Inconsistent value");
    case RESOURCE_UNAVAILABLE:
      return new String("Resource unavailable");
    case COMMIT_FAILED:
      return new String("Commit failed");
    case UNDO_FAILED:
      return new String("Undo failed");
    case AUTHORIZATION_ERROR:
      return new String("Authorization error");
    case NOT_WRITABLE:
      return new String("Not writable");
    case INCONSISTENT_NAME:
      return new String("Inconsistent name");
    case NO_SUCH_OBJECT:
      return new String("No such object");
    case NO_SUCH_INSTANCE:
      return new String("No such instance");
    case END_OF_MIB_VIEW:
      return new String("End of MIB view");
    }
    return null;

  }

}