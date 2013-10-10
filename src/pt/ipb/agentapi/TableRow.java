/*
 * $Id: TableRow.java 3 2004-08-03 10:42:11Z rlopes $
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

import pt.ipb.agentapi.event.EventListenerList;
import pt.ipb.agentapi.event.TableModelEvent;
import pt.ipb.agentapi.event.TableModelListener;
import pt.ipb.snmp.type.smi.OID;
import pt.ipb.snmp.type.smi.Var;

/**
 * This classe stores the values for every Table row. It works with the Table
 * and TableModel.
 */
public class TableRow {

  TableProvider provider = null;

  OID key = null;

  EventListenerList listenerList = new EventListenerList();

  public TableRow(OID key) throws MessageException {
    this.key = key;
  }

  public OID getKey() {
    return key;
  }

  public void setProvider(TableProvider l) {
    provider = l;
  }

  public TableProvider getProvider() {
    return provider;
  }

  public void addTableModelListener(TableModelListener l) {
    listenerList.add(TableModelListener.class, l);
  }

  public void removeTableModelListener(TableModelListener l) {
    listenerList.remove(TableModelListener.class, l);
  }

  protected void fireRemove(TableModelEvent e) {
    Object[] listeners = listenerList.getListenerList();
    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == TableModelListener.class) {
        ((TableModelListener) listeners[i + 1]).remove(e);
      }
    }
  }

  public void removeFromTable() {
    fireRemove(new TableModelEvent(this));
  }

  /**
   * Returns the value for the cell at oid.
   */
  public Var getValueAt(int colNumber) {
    Var res = provider.getValueAt(colNumber);
    return res;
  }

  /**
   * Sets the value in the cell at oid.
   */
  public Var setValueAt(int colNumber, Var value) throws MessageException {
    provider.setValueAt(colNumber, value);

    Var res = provider.getValueAt(colNumber);
    return res;
  }

}