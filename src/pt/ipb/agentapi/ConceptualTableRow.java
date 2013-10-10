/*
 * $Id: ConceptualTableRow.java 3 2004-08-03 10:42:11Z rlopes $
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

import pt.ipb.agentapi.event.ControlEvent;
import pt.ipb.agentapi.event.ControlListener;
import pt.ipb.snmp.type.smi.Int;
import pt.ipb.snmp.type.smi.OID;
import pt.ipb.snmp.type.smi.Var;

/**
 * This classe stores the values for every ConceptualTable row. Also calls
 * ConceptualTableProvider (ControlListener) when a status change occurs. It
 * works with the AbstractConceptualTableModel.
 */
public class ConceptualTableRow extends TableRow implements ControlListener {
  RowStatusAutomata automata = null;

  AbstractConceptualTableModel model = null;

  public ConceptualTableRow(AbstractConceptualTableModel model, OID key)
      throws MessageException {
    super(key);

    this.model = model;
  }

  public void setProvider(TableProvider p) {
    super.setProvider(p);
    automata = new RowStatusAutomata(this);
    //automata.removeControlListener((ControlListener)provider);
    automata.addControlListener(this);
    // This gets added after because of the unable to destroy exception.
    // If it happens, p will launch an exception and this will not be called
    automata.addControlListener((ControlListener) p);
    automata.reset();
    storeState();
  }

  public void changeState(byte s) throws MessageException {
    automata.input(s);
    storeState();
  }

  public void storeState() {
    try {
      provider.setValueAt(((ConceptualTableProvider) provider)
          .getRowStatusIndex(), new Int(getState()));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public int getState() {
    return automata.getState();
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
  public byte validate() {
    return model.validate((ConceptualTableProvider) provider);
  }

  /**
   * Sets the value in the cell at oid. To be used for all columns except for
   * the RowStatus. In this case, use changeState().
   */
  public Var setValueAt(int colNumber, Var value) throws MessageException {

    provider.setValueAt(colNumber, value);
    automata.input(AbstractAgent.OTHER_COL);

    Var res = provider.getValueAt(colNumber);
    return res;
  }

  public void create(ControlEvent e) {
  }

  public void destroy(ControlEvent e) {
    removeFromTable();
  }

  public void suspend(ControlEvent e) {
  }

  public void activate(ControlEvent e) {
  }

}