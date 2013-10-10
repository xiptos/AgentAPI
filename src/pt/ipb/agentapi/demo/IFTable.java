/*
 * $Id: IFTable.java 3 2004-08-03 10:42:11Z rlopes $ * Copyright (C) 2002-2004 Rui Pedro Lopes (rlopes at ipb dot pt)
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

package pt.ipb.agentapi.demo;

import java.util.SortedSet;
import java.util.TreeSet;

import pt.ipb.agentapi.AbstractAgent;
import pt.ipb.agentapi.MessageException;
import pt.ipb.agentapi.TableModel;
import pt.ipb.snmp.type.smi.Counter;
import pt.ipb.snmp.type.smi.Gauge;
import pt.ipb.snmp.type.smi.Int;
import pt.ipb.snmp.type.smi.OID;
import pt.ipb.snmp.type.smi.OctetString;
import pt.ipb.snmp.type.smi.TimeTicks;
import pt.ipb.snmp.type.smi.Var;
import pt.ipb.snmp.type.tc.Str;

public class IFTable implements TableModel {
  public static final String IFTABLE = ".1.3.6.1.2.1.2.2";

  public static final String IFTABLE_ENTRY = IFTABLE + ".1";

  public static final String IFDESCR = IFTABLE_ENTRY + ".2";

  public static final String IFTYPE = IFTABLE_ENTRY + ".3";

  public static final String IFMTU = IFTABLE_ENTRY + ".4";

  public static final String IFSPEED = IFTABLE_ENTRY + ".5";

  public static final String IFPHYADDRESS = IFTABLE_ENTRY + ".6";

  public static final String IFADMINSTATUS = IFTABLE_ENTRY + ".7";

  public static final String IFOPERSTATUS = IFTABLE_ENTRY + ".8";

  public static final String IFLASTCHANGE = IFTABLE_ENTRY + ".9";

  public static final String IFINOCTETS = IFTABLE_ENTRY + ".10";

  public static final String IFINUCASTPKTS = IFTABLE_ENTRY + ".11";

  public static final String IFINNUCASTPKTS = IFTABLE_ENTRY + ".12";

  public static final String IFINDISCARDS = IFTABLE_ENTRY + ".13";

  public static final String IFINERRORS = IFTABLE_ENTRY + ".14";

  public static final String IFINUNKNOWNPROTOS = IFTABLE_ENTRY + ".15";

  public static final String IFOUTOCTETS = IFTABLE_ENTRY + ".16";

  public static final String IFOUTUCASTPKTS = IFTABLE_ENTRY + ".17";

  public static final String IFOUTNUCASTPKTS = IFTABLE_ENTRY + ".18";

  public static final String IFOUTDISCARDS = IFTABLE_ENTRY + ".19";

  public static final String IFOUTERRORS = IFTABLE_ENTRY + ".20";

  public static final String IFOUTQLEN = IFTABLE_ENTRY + ".21";

  public static final String IFSPECIFIC = IFTABLE_ENTRY + ".22";

  /**
   * This TreeSet is used to order OIDs so that it is easier to calculateNext.
   */
  TreeSet treeSet = new TreeSet();

  /**
   * An array of static info for interfaces.
   */
  Var data[] = null;

  /**
   * An example of TableModel for the MIB-II ifTable.
   */
  public IFTable() {
    data = new Var[21];
    data[0] = new Str("description").toVar();
    data[1] = new Int(1);
    data[2] = new Int(1000);
    data[3] = new Gauge(1234L);
    data[4] = new OctetString("0x00.0xE0.0x7D.0xAA.0x6A.0xB5");
    data[5] = new Int(1);
    data[6] = new Int(1);
    data[7] = new TimeTicks(1000);
    data[8] = new Counter(500);
    data[9] = new Counter(501);
    data[10] = new Counter(502);
    data[11] = new Counter(503);
    data[12] = new Counter(504);
    data[13] = new Counter(505);
    data[14] = new Counter(600);
    data[15] = new Counter(601);
    data[16] = new Counter(602);
    data[17] = new Counter(603);
    data[18] = new Counter(604);
    data[19] = new Counter(605);
    data[20] = new OID(".0.0");

    // Add the recognized OIDs to the TreeSet
    treeSet.add(new OID(IFDESCR + ".1"));
    treeSet.add(new OID(IFTYPE + ".1"));
    treeSet.add(new OID(IFMTU + ".1"));
    treeSet.add(new OID(IFSPEED + ".1"));
    treeSet.add(new OID(IFPHYADDRESS + ".1"));
    treeSet.add(new OID(IFADMINSTATUS + ".1"));
    treeSet.add(new OID(IFOPERSTATUS + ".1"));
    treeSet.add(new OID(IFLASTCHANGE + ".1"));
    treeSet.add(new OID(IFINOCTETS + ".1"));
    treeSet.add(new OID(IFINUCASTPKTS + ".1"));
    treeSet.add(new OID(IFINNUCASTPKTS + ".1"));
    treeSet.add(new OID(IFINDISCARDS + ".1"));
    treeSet.add(new OID(IFINERRORS + ".1"));
    treeSet.add(new OID(IFINUNKNOWNPROTOS + ".1"));
    treeSet.add(new OID(IFOUTOCTETS + ".1"));
    treeSet.add(new OID(IFOUTUCASTPKTS + ".1"));
    treeSet.add(new OID(IFOUTNUCASTPKTS + ".1"));
    treeSet.add(new OID(IFOUTDISCARDS + ".1"));
    treeSet.add(new OID(IFOUTERRORS + ".1"));
    treeSet.add(new OID(IFOUTQLEN + ".1"));
    treeSet.add(new OID(IFSPECIFIC + ".1"));

  }

  /**
   * The function is called to retrieve the value correspondent to the specified
   * OID.
   */
  public Var getValueAt(OID oid) throws MessageException {
    // If this TableModel does not recognize OID, then noSuchObject
    if (!treeSet.contains(oid))
      throw new MessageException(AbstractAgent.NO_SUCH_OBJECT);

    // Convert OID in array index
    int i = -1;
    if (oid.startsWith(new OID(IFDESCR)))
      i = 0;
    else if (oid.startsWith(new OID(IFTYPE)))
      i = 1;
    else if (oid.startsWith(new OID(IFMTU)))
      i = 2;
    else if (oid.startsWith(new OID(IFSPEED)))
      i = 3;
    else if (oid.startsWith(new OID(IFPHYADDRESS)))
      i = 4;
    else if (oid.startsWith(new OID(IFADMINSTATUS)))
      i = 5;
    else if (oid.startsWith(new OID(IFOPERSTATUS)))
      i = 6;
    else if (oid.startsWith(new OID(IFLASTCHANGE)))
      i = 7;
    else if (oid.startsWith(new OID(IFINOCTETS)))
      i = 8;
    else if (oid.startsWith(new OID(IFINUCASTPKTS)))
      i = 9;
    else if (oid.startsWith(new OID(IFINNUCASTPKTS)))
      i = 10;
    else if (oid.startsWith(new OID(IFINDISCARDS)))
      i = 11;
    else if (oid.startsWith(new OID(IFINERRORS)))
      i = 12;
    else if (oid.startsWith(new OID(IFINUNKNOWNPROTOS)))
      i = 13;
    else if (oid.startsWith(new OID(IFOUTOCTETS)))
      i = 14;
    else if (oid.startsWith(new OID(IFOUTUCASTPKTS)))
      i = 15;
    else if (oid.startsWith(new OID(IFOUTNUCASTPKTS)))
      i = 16;
    else if (oid.startsWith(new OID(IFOUTDISCARDS)))
      i = 17;
    else if (oid.startsWith(new OID(IFOUTERRORS)))
      i = 18;
    else if (oid.startsWith(new OID(IFOUTQLEN)))
      i = 19;
    else if (oid.startsWith(new OID(IFSPECIFIC)))
      i = 20;

    if (i < 0)
      throw new MessageException(AbstractAgent.NO_SUCH_OBJECT);
    // Return value
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
    // ifTable has a single read-write column
    if (oid.startsWith(new OID(IFADMINSTATUS))) {
      data[5] = (Int) var;
      return data[5];
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