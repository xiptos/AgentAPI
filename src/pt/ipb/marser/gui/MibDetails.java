/*
 * $Id: MibDetails.java 3 2004-08-03 10:42:11Z rlopes $
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
import java.awt.CardLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import pt.ipb.marser.MibNode;

/**
 * 
 * @author rlopes
 * @version $Revision: 1.1.1.1 $
 */
public class MibDetails extends JPanel {

  JPanel cards = null;

  JPanel nodePanel = null;

  public final String NODE = new String("Node");

  public MibDetails() {
    super();
    buildDetailPanel();
  }

  void buildDetailPanel() {
    cards = new JPanel();
    cards.setLayout(new CardLayout());

    nodePanel = new JPanel();
    nodePanel.setLayout(new BorderLayout());

    cards.add(nodePanel, NODE);

    setLayout(new BorderLayout());
    add(new JScrollPane(cards));
  }

  public void show(Object object) {
    String cardName = "";

    if (object instanceof MibNode) {
      cardName = NODE;
      nodePanel.removeAll();
      nodePanel.add(new NodeTable((MibNode) object), BorderLayout.CENTER);
    }

    CardLayout cl = (CardLayout) (cards.getLayout());
    cl.show(cards, cardName);
    cards.validate();
  }
}