/*
 * $Id: AbstractTableModel.java 3 2004-08-03 10:42:11Z rlopes $
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

import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import pt.ipb.agentapi.event.TableModelEvent;
import pt.ipb.agentapi.event.TableModelListener;
import pt.ipb.snmp.type.smi.OID;
import pt.ipb.snmp.type.smi.Var;

/**
 * The abstract definition for the TableModel that provides Table with its
 * contents.
 */
public abstract class AbstractTableModel implements TableModel,
    TableModelListener {

  TreeMap map = null;

  TreeSet treeSet = null;

  OID mask = null;

  /**
   * Abstract model for providing SNMP table values. Mask is the mask to extract
   * the row information, or:
   * 
   * <pre>
   * 
   *  
   *   
   *    z.zz.z.zz.z.zz.y.ww.ttt.tt.tt.ttt.t.ttt
   *    |              | |  |
   *    +-Table OID    | |  +-key (row)
   *                   | +-column
   *                   +-EntryOID
   *   
   *    And so, the mask will be:
   *    0.00.0.00.0.00.0.00
   *   ---------------------------------------------
   *    0.00.0.00.0.00.0.00.ttt.tt.tt.ttt.t.ttt
   *   
   *    After trim():
   *    ttt.tt.tt.ttt.t.ttt
   *    
   *   
   *  
   * </pre>
   */
  public AbstractTableModel(String mask) {
    this.map = new TreeMap();
    this.treeSet = new TreeSet();
    this.mask = new OID(mask);
  }

  /**
   * Returns the next OID to the provided one in this object context.
   */
  public String calculateNext(String oid) {
    OID nextOID = new OID(oid + ".0");

    try {
      SortedSet tail = treeSet.tailSet(nextOID);
      if (!tail.isEmpty()) {
        String s = ((OID) tail.first()).toString();
        return s;
      }
    } catch (Exception e) {
    }

    return null;
  }

  /**
   * Returns the value for the cell at oid.
   * 
   * @throws MessageException
   *           if the oid does not exist.
   */
  public Var getValueAt(OID oid) throws MessageException {
    if (oid == null) {
      MessageException t = new MessageException(AbstractAgent.NO_SUCH_OBJECT);
      throw t;
    }

    if (!treeSet.contains(oid)) {
      MessageException t = new MessageException(AbstractAgent.NO_SUCH_OBJECT);
      throw t;
    }

    // get row OID
    OID maskedOid = oid.applyMask(mask);
    maskedOid.trim();

    TableRow row = (TableRow) map.get(maskedOid);
    if (row == null) {
      MessageException t = new MessageException(AbstractAgent.NO_SUCH_OBJECT);
      throw t;
    }

    OID cols[] = getColumns();
    int colNumber = -1;
    for (int i = 0; i < cols.length; i++) {
      if (oid.startsWith(cols[i])) {
        colNumber = i;
        break;
      }
    }
    if (colNumber == -1) {
      MessageException t = new MessageException(AbstractAgent.NO_SUCH_OBJECT);
      throw t;
    }

    Var var = row.getValueAt(colNumber);

    if (var == null) {
      MessageException t = new MessageException(AbstractAgent.NO_SUCH_OBJECT);
      throw t;
    }

    return var;
  }

  /**
   * Sets the value in the cell at oid.
   * 
   * @return the final cell value.
   * @throws MessageException
   *           if the cell is not writable
   */
  public Var setValueAt(OID oid, Var var) throws MessageException {

    // get row OID
    OID maskedOid = oid.applyMask(mask);
    maskedOid.trim();

    TableRow row = (TableRow) map.get(maskedOid);
    if (row == null) {
      if (!isOIDValid(maskedOid))
        throw new MessageException(AbstractAgent.NOT_WRITABLE);
      // Create a new row
      row = new TableRow(maskedOid);
      row.addTableModelListener(this);
      TableProvider provider = createNewProvider(maskedOid);
      setDefaultValues(provider);
      row.setProvider(provider);
      map.put(maskedOid, row);
    }

    OID cols[] = getColumns();
    int colNumber = -1;
    for (int i = 0; i < cols.length; i++) {
      if (oid.startsWith(cols[i])) {
        colNumber = i;
        break;
      }
    }
    if (colNumber == -1) {
      MessageException t = new MessageException(AbstractAgent.NOT_WRITABLE);
      throw t;
    }

    Var res = null;
    byte types[] = getColumnTypes();
    if (var.getType() == types[colNumber]) {

      if (allowChange(colNumber, row)) {
        res = row.setValueAt(colNumber, var);
        if (res == null) {
          MessageException t = new MessageException(AbstractAgent.NOT_WRITABLE);
          throw t;
        }

      } else {
        MessageException t = new MessageException(
            AbstractAgent.INCONSISTENT_VALUE);
        throw t;
      }

    } else {
      MessageException t = new MessageException(AbstractAgent.WRONG_TYPE);
      throw t;
    }

    updateExistingOIDs(row, oid);

    return res;
  }

  void updateExistingOIDs(TableRow row, OID oid) {
    OID cols[] = getColumns();
    int colNumber = -1;
    for (int i = 0; i < cols.length; i++) {
      if (oid.startsWith(cols[i])) {
        colNumber = i;
        break;
      }
    }
    if (colNumber == -1)
      return;

    OID sufix = oid.subOID(cols[colNumber].length());

    for (int i = 0; i < cols.length; i++) {
      try {
        if (row.getValueAt(i) != null) {
          OID o = new OID(cols[i].toString());
          o.append(sufix);
          addOID(o);
        }
      } catch (Exception e) {
      }
    }
  }

  /**
   * Remove provided Row from this ConceptualTable.
   */
  public void remove(TableModelEvent e) {
    TableRow row = (TableRow) e.getSource();
    OID oids[] = getColumns();
    for (int i = 0; i < oids.length; i++) {
      oids[i].append(row.getKey());
      removeOID(oids[i]);
    }
    map.remove(row.getKey());
  }

  /**
   * Remove provided OID from this ConceptualTable.
   */
  public void removeOID(OID o) {
    treeSet.remove(o);
  }

  /**
   * Add provided OID to this ConceptualTable.
   */
  public void addOID(OID o) {
    treeSet.add(o);
  }

  /**
   * The provider (storage) object for column values
   */
  public abstract TableProvider createNewProvider(OID key);

  /**
   * The OIDs for the columns
   */
  public abstract OID[] getColumns();

  /**
   * Return the column types.
   */
  public abstract byte[] getColumnTypes();

  /**
   * If the table has default values...
   */
  public abstract void setDefaultValues(TableProvider pro);

  /**
   * Allow changing colNumber column?
   */
  public abstract boolean allowChange(int colNumber, TableRow row);

  /**
   * Is OID valid for creating rows?
   */
  public abstract boolean isOIDValid(OID oid);

}

