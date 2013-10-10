/*
 * $Id: NodeTable.java 3 2004-08-03 10:42:11Z rlopes $
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

package pt.ipb.marser.gui;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextArea;

import pt.ipb.marser.MibNode;

/**
 * 
 * @author rlopes
 * @version $Revision: 1.1.1.1 $
 */
public class NodeTable extends JPanel {

  public NodeTable(MibNode node) {

    setLayout(new BorderLayout());
    JPanel tablePanel = new JPanel();

    String[] columnNames = { "OID", "Status", "Syntax", "Units", "Access",
        "DefVal" };
    String syntaxDescr = "";
    String syntaxTypeStr = "";
    if (node.getSyntax() != null) {
      syntaxDescr = node.getSyntax().getDescription();
      syntaxTypeStr = node.getSyntax().getSnmpTypeStr();
    }
    Object[][] data = { { node.getOID(), node.getStatus(),
        new String(syntaxDescr + "[" + syntaxTypeStr + "]"), node.getUnits(),
        node.getAccessStr(), node.getDefVal() } };

    JTable table = new JTable(data, columnNames);
    table.setShowGrid(true);
    tablePanel.setLayout(new BorderLayout());
    tablePanel.add(table.getTableHeader(), BorderLayout.NORTH);
    tablePanel.add(table, BorderLayout.CENTER);

    JTextArea textArea = new JTextArea();
    textArea.setText(node.getDescription());
    add(tablePanel, BorderLayout.NORTH);
    add(textArea, BorderLayout.CENTER);
  }
}