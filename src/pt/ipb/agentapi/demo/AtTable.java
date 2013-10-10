/*
 * $Id: AtTable.java 3 2004-08-03 10:42:11Z rlopes $
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

package pt.ipb.agentapi.demo;

import java.util.SortedSet;
import java.util.TreeSet;

import pt.ipb.agentapi.AbstractAgent;
import pt.ipb.agentapi.MessageException;
import pt.ipb.agentapi.TableModel;
import pt.ipb.snmp.type.smi.Int;
import pt.ipb.snmp.type.smi.OID;
import pt.ipb.snmp.type.smi.OctetString;
import pt.ipb.snmp.type.smi.Var;

/**
 * Contributed by Aziz Arriani (aziz.arriani@nch.it)
 */
public class AtTable implements TableModel {

  public static final String ATTABLE = ".1.3.6.1.2.1.3.1";

  public static final String ATTABLE_ENTRY = ATTABLE + ".1";

  public static final String ATPHYSADDRESS = ATTABLE_ENTRY + ".2";

  public static final String ATNETADDRESS = ATTABLE_ENTRY + ".3";

  /**
   * This TreeSet is used to order OIDs so that it is easier to calculateNext.
   */
  TreeSet treeSet = new TreeSet();

  /**
   * An array of static info for address translation.
   */
  Var data[] = null;

  /**
   * An example of TableModel for the MIB-II atTable.
   */
  public AtTable() {

    data = new Var[2];
    data[0] = new OctetString("0x00.0xE0.0x7D.0xAA.0x6A.0xB5");
    data[1] = new Int(211);

    // To add two rows
    for (int i = 1; i < 3; i++) {
      treeSet.add(new OID(ATPHYSADDRESS + "." + i));
      treeSet.add(new OID(ATNETADDRESS + "." + i));
    }
  }

  /**
   * The function is called to retrieve the value correspondent to the specified
   * OID.
   */
  public Var getValueAt(OID oid) throws MessageException {
    // If this TableModel does not recognize OID, then noSuchObject
    if (!treeSet.contains(oid)) {
      throw new MessageException(AbstractAgent.NO_SUCH_OBJECT);
    }

    // Convert OID in array index
    int i = -1;
    if (oid.startsWith(new OID(ATPHYSADDRESS)))
      i = 0;
    else if (oid.startsWith(new OID(ATNETADDRESS)))
      i = 1;

    if (i < 0) {
      System.out.print(" GetValueAt OID=" + oid + " NO_SUCH_OBJECT");
      throw new MessageException(AbstractAgent.NO_SUCH_OBJECT);
    }
    return data[i];
  }

  /**
   * If there are any read-write/read-create object, this method is called to
   * set its values.
   */
  public Var setValueAt(OID oid, Var var) throws MessageException {
    // If the OID does not exist, then it is notWritable
    if (!treeSet.contains(oid))
      throw new MessageException(AbstractAgent.NOT_WRITABLE);

    // Type checking
    if (!(var instanceof Int))
      throw new MessageException(AbstractAgent.WRONG_TYPE);
    if (oid.startsWith(new OID(ATPHYSADDRESS))) {
      data[0] = (OctetString) var;
      return data[0];
    } else {
      throw new MessageException(AbstractAgent.NOT_WRITABLE);
    }
  }

  /**
   * This method is used for walk operations. It returns the next OID to the one
   * passed as parameter.
   */
  public String calculateNext(String oid) {
    // The next OID will be greater or equal to the provided+".0"
    OID nextOID = new OID(oid + ".0");
    // Return an orderer set of OIDs greater or equal to nextOID
    SortedSet tail = treeSet.tailSet(nextOID);
    // If it is not empty, there are at least one nextOID
    if (!tail.isEmpty()) {
      // wich is the first
      String s = ((OID) tail.first()).toString();
      return s;
    }
    return null;
  }

}