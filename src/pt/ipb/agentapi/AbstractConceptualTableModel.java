/*
 * $Id: AbstractConceptualTableModel.java 3 2004-08-03 10:42:11Z rlopes $
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

import pt.ipb.snmp.type.smi.Int;
import pt.ipb.snmp.type.smi.OID;
import pt.ipb.snmp.type.smi.Var;

/**
 * The abstract definition for the TableModel that provides a conceptual Table
 * with its contents. See RFC2878.
 */
public abstract class AbstractConceptualTableModel extends AbstractTableModel {

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
  public AbstractConceptualTableModel(String mask) {
    super(mask);
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

    ConceptualTableRow row = (ConceptualTableRow) map.get(maskedOid);
    if (row == null) {
      if (!isOIDValid(maskedOid))
        throw new MessageException(AbstractAgent.NOT_WRITABLE);

      // Create a new row
      row = new ConceptualTableRow(this, maskedOid);
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

    Var res = null;
    byte types[] = getColumnTypes();
    if (var.getType() != types[colNumber]) {
      MessageException t = new MessageException(AbstractAgent.WRONG_TYPE);
      throw t;
    }
    if (colNumber == -1) {
      MessageException t = new MessageException(AbstractAgent.NOT_WRITABLE);
      throw t;
    }

    if (allowChange(colNumber, row)) {
      try {
        // Column number corresponds to RowStatus?
        if (oid.startsWith(getRowStatusOID())) {
          byte s;
          try {
            s = new Byte(var.toString().trim()).byteValue();
          } catch (Exception e) {
            MessageException t = new MessageException(AbstractAgent.WRONG_TYPE);
            throw t;
          }

          if (s < AbstractAgent.ACTIVE || s > AbstractAgent.DESTROY) {
            MessageException t = new MessageException(
                AbstractAgent.INCONSISTENT_VALUE);
            throw t;
          }

          row.changeState(s);
          res = new Int(row.getState());

          return res;
        }

        res = row.setValueAt(colNumber, var);

      } catch (MessageException e) {
        //e.printStackTrace();
        throw e;
      }
    } else {
      MessageException t = new MessageException(
          AbstractAgent.INCONSISTENT_VALUE);
      throw t;
    }

    updateExistingOIDs(row, oid);

    return res;
  }

  /**
   * Returns the value for the cell at oid.
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

    ConceptualTableRow row = (ConceptualTableRow) map.get(maskedOid);
    if (row == null) {
      MessageException t = new MessageException(AbstractAgent.NO_SUCH_OBJECT);
      throw t;
    }

    Var var;
    // Column number corresponds to RowStatus?
    if (oid.startsWith(getRowStatusOID())) {
      int s = row.getState();
      var = new Int(s);
    } else {
      var = super.getValueAt(oid);
    }

    return var;
  }

  /**
   * Used to validate ConceptualTable rows creation. Could return:
   * INCONSISTENT_VALUE - if there is some value inconsistent with the state of
   * some other MIB object's value. <br>
   * WRONG_VALUE - if there is some wrong value in the row or if the status row
   * does not supports given value (createAndWait or notInService). <br>*
   * NOT_READY - if there are empty columns in the row. <br>
   * NO_ERROR - if everything is ok
   */
  public abstract byte validate(ConceptualTableProvider pro);

  /*
   * Allow changing colNumber column when the RowStatus is 'active'? public
   * boolean allowChange(int colNumber, ConceptualTableRow row);
   */

  public abstract OID getRowStatusOID();

}