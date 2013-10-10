/*
 * $Id: MibToXml.java 3 2004-08-03 10:42:11Z rlopes $ * Copyright (C) 2002-2004 Rui Pedro Lopes (rlopes at ipb dot pt)
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

package pt.ipb.agentapi.engine.http;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

import pt.ipb.agentapi.AbstractAgent;
import pt.ipb.agentapi.Agent;
import pt.ipb.marser.MibException;
import pt.ipb.marser.MibModule;
import pt.ipb.marser.MibNode;
import pt.ipb.marser.MibOps;
import pt.ipb.marser.Syntax;
import pt.ipb.snmp.type.smi.Counter;
import pt.ipb.snmp.type.smi.Counter64;
import pt.ipb.snmp.type.smi.Int;
import pt.ipb.snmp.type.smi.OID;
import pt.ipb.snmp.type.smi.OctetString;
import pt.ipb.snmp.type.smi.TimeTicks;
import pt.ipb.snmp.type.smi.Unsigned;
import pt.ipb.snmp.type.smi.Var;
import pt.ipb.snmp.type.smi.VarBind;
import pt.ipb.snmp.type.tc.Bits;
import pt.ipb.snmp.type.tc.DateAndTime;
import pt.ipb.snmp.type.tc.Str;

public class MibToXml {
  Properties prop = null;

  MibOps mibOps = null;

  Agent agent = null;

  boolean debug = false;

  /**
   * MIB related Properties: # Directory where MIB files are
   * mib.dir=/usr/local/mibs/ # MIB files to load (space separated file names)
   * mib.files=DISMAN-SCHEDULE-MIB # MIB processing debugging mib.debug=false #
   * true if load MIBs from compiled files (faster) mib.compiled=true # Starting
   * MIB node mib.root.node=internet
   */
  public MibToXml(Properties prop, Agent agent) throws MibException,
      IOException, FileNotFoundException {

    this.agent = agent;
    this.prop = prop;
    debug = new Boolean(prop.getProperty("mib.debug", "false")).booleanValue();
    mibOps = new MibOps();

    //Loading MIBs
    String path = prop.getProperty("mib.dir", ".");
    mibOps.setPath(new String[] { path });
    String mibs = prop.getProperty("mib.files");
    mibOps.loadMibs(mibs);
  }

  /**
   * Builds an XML document based on the given parentNode. The document contains
   * all the children of the parentNode.
   */
  public String getPage(String parentNode) {
    return getPage(parentNode, false);
  }

  /**
   * If up==true, the XML document will relate to the parent of parentNode. The
   * resulting document contains parentNode and all the objects on the same
   * hierarchical level.
   */
  public String getPage(String parentNode, boolean up) {
    return getPage(parentNode, up, null);
  }

  /**
   * If up==true, the XML document will relate to the parent of parentNode. The
   * resulting document contains parentNode and all the objects on the same
   * hierarchical level.
   */
  public String getPage(String parentNode, boolean up, String reqOid) {
    StringBuffer str = new StringBuffer();
    if (parentNode == null || parentNode.equals("")) {
      parentNode = prop.getProperty("mib.root.node", "internet");
    }

    MibNode parentMibNode = mibOps.getMibNode(parentNode);
    if (up) {
      parentMibNode = parentMibNode.getParent();
      if (parentMibNode.getParent() != null)
        parentMibNode = parentMibNode.getParent();
    }

    Vector v = new Vector();
    if (parentMibNode.isLeaf()) {
      v.addElement(parentMibNode);

    } else {
      TreeMap map = new TreeMap();

      for (Enumeration e1 = mibOps.modules(); e1.hasMoreElements();) {
        MibModule mibModule = (MibModule) e1.nextElement();

        MibNode node = mibModule.getNode(parentMibNode.getLabel());
        if (node != null) {

          for (Enumeration e = node.children(); e.hasMoreElements();) {
            MibNode n = (MibNode) e.nextElement();
            if (!map.containsKey(new OID(n.getNumberedOIDString()))) {
              map.put(new OID(n.getNumberedOIDString()), n);
            }
          }
        }
      }

      for (Iterator i1 = map.entrySet().iterator(); i1.hasNext();) {
        MibNode n = (MibNode) ((Map.Entry) i1.next()).getValue();
        v.addElement(n);
      }
    }

    str.append("<smi>\n");
    for (Enumeration e = v.elements(); e.hasMoreElements();) {
      MibNode node = (MibNode) e.nextElement();
      str.append(nodeToXml(node, reqOid));
    }
    str.append("</smi>\n");
    return str.toString();
  }

  public String nodeToXml(MibNode node, String reqOid) {
    if (node.isLeaf()) {
      return scalar(node, reqOid);
    } else if (node.isTable()) {
      return node(node);
    } else if (node.isTableEntry()) {
      return table(node);
    } else if (node.isTableColumn()) {
      return column(node);
    } else {
      return node(node);
    }
  }

  public String buildUnits(MibNode node) {
    StringBuffer str = new StringBuffer();
    if (node.getUnits() != null) {
      str.append("<units>");
      str.append(node.getUnits());
      str.append("</units>\n");
    }
    return str.toString();
  }

  public String buildDefVal(MibNode node) {
    StringBuffer str = new StringBuffer();
    if (node.getDefVal() != null) {
      str.append("<default>");
      str.append(node.getDefVal());
      str.append("</default>\n");
    }
    return str.toString();
  }

  public String buildAccess(MibNode node) {
    StringBuffer str = new StringBuffer();
    if (node.getAccessStr() != null) {
      str.append("<access>");
      str.append(node.getAccessStr());
      str.append("</access>\n");
    }
    return str.toString();
  }

  public String buildDescription(MibNode node) {
    StringBuffer str = new StringBuffer();
    if (node.getDescription() != null) {
      str.append("<description>");
      str.append(node.getDescription());
      str.append("</description>\n");
    }
    return str.toString();
  }

  public String buildSyntax(MibNode node) {
    StringBuffer str = new StringBuffer();
    Syntax syntax = node.getSyntax();
    if (syntax == null)
      return "";
    str.append("<syntax>\n");
    str.append("<type module=\"");
    if (node.getModule() != null)
      str.append(node.getModule().getName());
    str.append("\" name=\"" + syntax.getDescription() + "\"/>\n");
    str.append("<typedef optname=\"" + syntax.getDescription()
        + "\" basetype=\"" + syntax.getDescription() + "\">\n");
    str.append("<parent module=\"");
    if (node.getParent().getModule() != null)
      str.append(node.getParent().getModule().getName());
    str.append("\" name=\"" + node.getParent().getLabel() + "\"/>\n");
    /*
     * if(syntax.isEnumerated()) { int enumint[] = syntax.getEnumint(); for(int
     * i=0; i <enumint.length; i++) { str.append(" <namednumber name=\"" +
     * syntax.getLabel(enumint[i]) + "\" number=\"" + enumint[i] + "\"/>\n"); } }
     */
    str.append("</typedef>\n");
    str.append("</syntax>\n");
    return str.toString();
  }

  public String scalar(MibNode node, String reqOid) {
    StringBuffer str = new StringBuffer();
    String name = node.getLabel();
    String oid = node.getNumberedOIDString();
    String status = node.getStatus();
    str.append("<scalar name=\"" + name + "\" oid=\"" + oid + "\"");
    if (status != null)
      str.append(" status=\"" + status + "\"");
    str.append(">\n");
    str.append(buildSyntax(node));
    str.append(buildAccess(node));
    str.append(buildDefVal(node));
    str.append(buildUnits(node));
    str.append(buildDescription(node));

    str.append(buildScalarValue(node, reqOid));

    str.append("</scalar>\n");
    return str.toString();
  }

  String buildScalarValue(MibNode node, String reqOid) {
    String oid = node.getNumberedOIDString();
    Syntax syntax = node.getSyntax();
    StringBuffer str = new StringBuffer();
    str.append("<value ");
    VarBind response = null;

    if (reqOid != null) {
      OID oidObj = new OID(oid);
      OID reqOidObj = new OID(reqOid);
      if (reqOidObj.startsWith(oidObj) && node.isReadable()) {
        VarBind varBind = new VarBind(reqOid, null);
        response = agent.getOperation(varBind);
      } else {
        str.append("oid='" + oid + "'>");
      }
    } else if (!node.isTableColumn()) {
      VarBind varBind = new VarBind(oid, null);
      response = agent.getNextOperation(varBind);
      OID myreqOid = new OID(oid);
      OID responseOid = new OID(response.getOID());
      if (!responseOid.startsWith(myreqOid)) {
        response.setOID(oid);
        response.setError(true);
        response.setValue(new Int(AbstractAgent.NO_SUCH_OBJECT));
      }
    } else {
      str.append("oid='" + oid + "'>");
    }

    if (response != null) {
      str.append("oid='" + response.getOID() + "'>");
      if (response.isError()) {
        str.append(AbstractAgent.errorToString(response.getError()));
      } else {
        str.append(VarGateway.var2string(response.getValue(), syntax));
      }
    }

    str.append("</value>\n");
    return str.toString();
  }

  public String table(MibNode entry) {
    MibNode table = entry.getParent();
    StringBuffer str = new StringBuffer();
    String name = table.getLabel();
    String oid = table.getNumberedOIDString();
    String status = table.getStatus();
    str.append("<table name=\"" + name + "\" oid=\"" + oid + "\"");
    if (status != null)
      str.append(" status=\"" + status + "\"");
    str.append(">\n");
    str.append(buildDescription(table));

    if (entry.isTableEntry()) {
      String e_name = entry.getLabel();
      String e_oid = entry.getNumberedOIDString();
      String e_status = entry.getStatus();
      str.append("<row name=\"" + e_name + "\" oid=\"" + e_oid + "\"");
      if (status != null)
        str.append(" status=\"" + e_status + "\"");
      str.append(">\n");
      //str.append("<linkage implied=\"" + entry.isImplied() + "\">\n");
      //Vector indexes = entry.getIndexes(mibOps);
      for (Enumeration e = entry.indexes(); e.hasMoreElements();) {
        String idx = (String) e.nextElement();
        str.append("<index module=\"");
        if (entry.getModule() != null)
          str.append(entry.getModule().getName());
        str.append("\" name=\"" + idx + "\"/>\n");
      }
      /*
       * MibNode aug = entry.getAugments(); if(aug!=null) { str.append("
       * <augments module=\"" + aug.getModuleName() + "\" name=\"" +
       * aug.getLabel() + "\"/>\n"); } str.append(" </linkage>\n");
       */
      str.append(buildDescription(entry));
      for (Enumeration i = entry.children(); i.hasMoreElements();) {
        MibNode col = (MibNode) i.nextElement();
        str.append(column(col));
      }
      str.append("</row>\n");
      str.append(buildTableValues(entry));
    }

    str.append("</table>\n");
    return str.toString();
  }

  String buildTableValues(MibNode entry) {
    StringBuffer str = new StringBuffer();
    SnmpTable snmpTable = new SnmpTable(entry, mibOps, agent);

    int rows = snmpTable.getNRows();
    for (int i = 0; i < rows; i++) {
      VarBind[] vars = snmpTable.getRow(i);
      str.append("<valuerow>\n");
      int t = 0;
      for (Enumeration e = entry.children(); e.hasMoreElements(); t++) {
        MibNode col = (MibNode) e.nextElement();

        String name = col.getLabel();
        String oid = col.getNumberedOIDString();
        if (vars[t] != null) {
          oid = vars[t].getOID();
        }
        String status = col.getStatus();
        str.append("<cell name=\"" + name + "\" oid=\"" + oid + "\"");
        if (status != null)
          str.append(" status=\"" + status + "\"");
        str.append(">\n");
        str.append(buildSyntax(col));
        str.append(buildAccess(col));
        str.append(buildUnits(col));
        str.append(buildDescription(col));
        if (vars[t] != null) {
          Var value = vars[t].getValue();
          String val = VarGateway.var2string(value, col.getSyntax());
          str.append("<value>");
          str.append(VarGateway.var2string(value, col.getSyntax()));
          str.append("</value>\n");
        }
        str.append("</cell>\n");
      }
      str.append("</valuerow>\n");
    }
    return str.toString();
  }

  public String column(MibNode node) {
    StringBuffer str = new StringBuffer();
    String name = node.getLabel();
    String oid = node.getNumberedOIDString();
    String status = node.getStatus();
    str.append("<column name=\"" + name + "\" oid=\"" + oid + "\"");
    if (status != null)
      str.append(" status=\"" + status + "\"");
    str.append(">\n");
    str.append(buildSyntax(node));
    str.append(buildAccess(node));
    str.append(buildDefVal(node));
    str.append(buildUnits(node));
    str.append(buildDescription(node));
    str.append("</column>\n");
    return str.toString();
  }

  public String node(MibNode node) {
    StringBuffer str = new StringBuffer();
    String name = node.getLabel();
    String oid = node.getNumberedOIDString();
    String status = node.getStatus();
    str.append("<node name=\"" + name + "\" oid=\"" + oid + "\"");
    if (status != null)
      str.append(" status=\"" + status + "\"");
    str.append(">\n");
    str.append(buildDescription(node));
    str.append("</node>\n");
    return str.toString();
  }

  public void set(String oid, String value) {
    MibNode node = mibOps.getCloserNode(oid);
    String type = node.getSyntax().getDescription();
    Var v = VarGateway.string2var(value, type);
    VarBind varBind = new VarBind(oid, v);
    agent.setOperation(varBind);
  }
}

class SnmpTable {
  MibNode entry = null;

  Agent agent = null;

  MibOps ops = null;

  TreeMap columns = null;

  public SnmpTable(MibNode entry, MibOps ops, Agent agent) {
    this.entry = entry;
    this.ops = ops;
    this.agent = agent;
    this.columns = new TreeMap();

    Vector oidCols = new Vector();
    for (Enumeration i = entry.children(); i.hasMoreElements();) {
      MibNode col = (MibNode) i.nextElement();
      oidCols.addElement(new OID(col.getNumberedOIDString()));
    }

    setColumns(oidCols);

    OID entryOid = new OID(entry.getNumberedOIDString());
    OID curOid = entryOid;
    while (curOid.startsWith(entryOid)) {
      VarBind query = new VarBind(curOid.toString(), null);
      VarBind result = agent.getNextOperation(query);

      if (result.isError()) {
        if (result.getError() == AbstractAgent.END_OF_MIB_VIEW) {
          break;
        }
      }
      setValue(result);

      curOid = new OID(result.getOID());
    }
  }

  public void setColumns(Vector cols) {
    for (Enumeration e = cols.elements(); e.hasMoreElements();) {
      OID col = (OID) e.nextElement();
      columns.put(col, new TreeMap());
    }
  }

  public Vector getColumns() {
    Vector cols = new Vector();
    Set keys = columns.keySet();
    for (Iterator i = keys.iterator(); i.hasNext();) {
      OID key = (OID) i.next();
      cols.addElement(key);
    }
    return cols;
  }

  public VarBind[] getRow(int row) {
    int size = columns.size();
    VarBind[] result = new VarBind[size];

    Set cols = columns.entrySet();
    int t = 0;
    for (Iterator i = cols.iterator(); i.hasNext(); t++) {
      Map.Entry mapEntry = (Map.Entry) i.next();
      TreeMap colMap = (TreeMap) mapEntry.getValue();
      Vector values = new Vector(colMap.values());

      VarBind varBind = null;
      try {
        varBind = (VarBind) values.elementAt(row);
      } catch (Exception e) {
      }

      result[t] = varBind;
    }
    return result;
  }

  public int getNRows() {
    int size = 0;

    Set cols = columns.entrySet();
    for (Iterator i = cols.iterator(); i.hasNext();) {
      Map.Entry mapEntry = (Map.Entry) i.next();
      TreeMap colMap = (TreeMap) mapEntry.getValue();
      int partialSize = colMap.size();
      size = (size < partialSize) ? partialSize : size;
    }
    return size;
  }

  public TreeMap getColumn(String col) {
    return (TreeMap) columns.get(col);
  }

  public void setValue(VarBind varBind) {
    Set keys = columns.keySet();
    OID oid = new OID(varBind.getOID());
    for (Iterator i = keys.iterator(); i.hasNext();) {
      OID key = (OID) i.next();
      if (oid.startsWith(key)) {
        // Index extraction
        OID index = oid.subOID(key.length());
        TreeMap map = (TreeMap) columns.get(key);
        map.put(index, varBind);
      }
    }
  }

}

class VarGateway {

  public static String var2string(Var var, Syntax syntax) {
    String type = syntax.getDescription();
    String result = null;

    if (type.startsWith("DateAndTime")) {
      result = DateAndTime.toString((OctetString) var);

    } else if (type.startsWith("BITS")) {
      result = Bits.toString((OctetString) var);

    } else if (var instanceof OctetString) {
      result = Str.toString((OctetString) var);

      //} else if(syntax.isEnumerated()) {
      //return new
      // String(syntax.getLabel(Integer.parseInt(var.toString()))+"("+var.toString()+")");
    } else {
      result = var.toString();
    }
    return result;
  }

  public static Var string2var(String str, String type) {
    if (type.startsWith("DateAndTime")) {
      return new DateAndTime(str).toVar();
    } else if (type.startsWith("BITS")) {
      return new Bits(str).toVar();
    } else if (type.startsWith("INTEGER")) {
      return new Int(str);
    } else if (type.startsWith("OCTETSTRING")) {
      return new OctetString(str);
    } else if (type.startsWith("COUNTER32")) {
      return new Counter(str);
    } else if (type.startsWith("UNSIGNED32")) {
      return new Unsigned(str);
    } else if (type.startsWith("TIMETICKS")) {
      return new TimeTicks(str);
    } else if (type.startsWith("COUNTER64")) {
      return new Counter64(str);
    } else {
      return new Str(str).toVar();
    }
  }
}