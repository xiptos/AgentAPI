/*
 * $Id: MibNodeRenderer.java 3 2004-08-03 10:42:11Z rlopes $
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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import pt.ipb.marser.MibCompliance;
import pt.ipb.marser.MibModule;
import pt.ipb.marser.MibNode;
import pt.ipb.marser.MibOps;
import pt.ipb.marser.MibTC;
import pt.ipb.marser.MibTrap;
import pt.ipb.marser.NotificationGroup;
import pt.ipb.marser.ObjectGroup;

/**
 * 
 * @author rlopes
 * @version $Revision: 1.1.1.1 $
 */
public class MibNodeRenderer extends DefaultTreeCellRenderer {

  ImageIcon opsIcon;

  ImageIcon modIcon;

  ImageIcon nodeoIcon;

  ImageIcon nodecIcon;

  ImageIcon tableIcon;

  ImageIcon indexIcon;

  ImageIcon dupIndexIcon;

  ImageIcon leafROIcon;

  ImageIcon leafRWIcon;

  ImageIcon leafRCIcon;

  ImageIcon leafWOIcon;

  ImageIcon entryIcon;

  ImageIcon tcIcon;

  ImageIcon trapIcon;

  ImageIcon notIcon;

  ImageIcon groupIcon;

  ImageIcon complianceIcon;

  public MibNodeRenderer() {
    opsIcon = ResourceFactory.getIcon("root.gif");
    modIcon = ResourceFactory.getIcon("module.gif");
    tableIcon = ResourceFactory.getIcon("table.gif");
    indexIcon = ResourceFactory.getIcon("index.gif");
    dupIndexIcon = ResourceFactory.getIcon("dupindex.gif");
    nodecIcon = ResourceFactory.getIcon("folderc.gif");
    nodeoIcon = ResourceFactory.getIcon("foldero.gif");
    tcIcon = ResourceFactory.getIcon("tc.gif");
    trapIcon = ResourceFactory.getIcon("trap.gif");
    leafROIcon = ResourceFactory.getIcon("leaf_ro.gif");
    leafRWIcon = ResourceFactory.getIcon("leaf_rw.gif");
    leafRCIcon = ResourceFactory.getIcon("leaf_rc.gif");
    leafWOIcon = ResourceFactory.getIcon("leaf_wo.gif");
    entryIcon = ResourceFactory.getIcon("entry.gif");
    notIcon = ResourceFactory.getIcon("notification.gif");
    groupIcon = ResourceFactory.getIcon("group.gif");
    complianceIcon = ResourceFactory.getIcon("compliance.gif");
    setOpenIcon(nodeoIcon);
    setClosedIcon(nodecIcon);
  }

  public Dimension getPreferredSize() {
    Dimension superD = super.getPreferredSize();
    return new Dimension(superD.width, 14);
  }

  public Component getTreeCellRendererComponent(JTree tree, Object value,
      boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {

    super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row,
        hasFocus);

    if (value instanceof MibArray) {
      MibArray ma = (MibArray) value;
      if (ma.toString().equals("TCs")) {
        Font f = getFont();
        setFont(new Font(f.getName(), Font.PLAIN, f.getSize()));
        setIcon(tcIcon);
        setToolTipText("Textual Conventions.");

      } else if (ma.toString().equals("Traps")) {
        Font f = getFont();
        setFont(new Font(f.getName(), Font.PLAIN, f.getSize()));
        setIcon(trapIcon);
        setToolTipText("Traps.");
      }

    } else if (value instanceof MibOps) {
      Font f = getFont();
      if (f != null)
        setFont(new Font(f.getName(), Font.PLAIN, f.getSize()));
      setIcon(opsIcon);
      setToolTipText("Root node.");

    } else if (value instanceof MibModule) {
      Font f = getFont();
      setFont(new Font(f.getName(), Font.PLAIN, f.getSize()));
      setIcon(modIcon);
      setToolTipText(((MibModule) value).getName() + "MibModule.");

    } else if (value instanceof MibTrap) {
      Font f = getFont();
      setFont(new Font(f.getName(), Font.PLAIN, f.getSize()));
      setIcon(trapIcon);
      setToolTipText(((MibTrap) value).getLabel() + " Trap.");

    } else if (value instanceof MibTC) {
      Font f = getFont();
      setFont(new Font(f.getName(), Font.PLAIN, f.getSize()));
      setIcon(tcIcon);
      setToolTipText(((MibTC) value).getLabel() + " TC.");

    } else if (value instanceof ObjectGroup) {
      Font f = getFont();
      setFont(new Font(f.getName(), Font.PLAIN, f.getSize()));
      setIcon(groupIcon);
      setToolTipText(((ObjectGroup) value).getLabel() + " Object Group.");

    } else if (value instanceof NotificationGroup) {
      Font f = getFont();
      setFont(new Font(f.getName(), Font.PLAIN, f.getSize()));
      setIcon(groupIcon);
      setToolTipText(((NotificationGroup) value).getLabel()
          + " Notification Group.");

    } else if (value instanceof MibCompliance) {
      Font f = getFont();
      setFont(new Font(f.getName(), Font.PLAIN, f.getSize()));
      setIcon(complianceIcon);
      setToolTipText(((MibCompliance) value).getLabel() + " Compliance.");

    } else if (value instanceof MibNode) {
      MibNode node = (MibNode) value;
      if (node.isTable()) {
        Font f = getFont();
        setFont(new Font(f.getName(), Font.PLAIN, f.getSize()));
        setIcon(tableIcon);
        setToolTipText("A Table node.");
      } else if (node.isTableEntry()) {
        Font f = getFont();
        setFont(new Font(f.getName(), Font.PLAIN, f.getSize()));
        setIcon(entryIcon);
        setToolTipText("An Index node.");
      } else if (node.isImpliedIndex()) {
        Font f = getFont();
        setFont(new Font(f.getName(), Font.ITALIC, f.getSize()));
        setIcon(dupIndexIcon);
        setToolTipText("An Index node.");
      } else if (node.isIndex()) {
        Font f = getFont();
        setFont(new Font(f.getName(), Font.PLAIN, f.getSize()));
        setIcon(indexIcon);
        setToolTipText("An Index node.");
      } else if (node.isLeaf()) {
        Font f = getFont();
        setFont(new Font(f.getName(), Font.PLAIN, f.getSize()));
        int access = node.getAccess();
        switch (access) {
        case MibOps.READ_ONLY:
          setIcon(leafROIcon);
          setToolTipText("A Readable node.");
          break;
        case MibOps.READ_WRITE:
          setIcon(leafRWIcon);
          setToolTipText("A Readable/Writable node.");
          break;
        case MibOps.READ_CREATE:
          setIcon(leafRCIcon);
          setToolTipText("A Readable/Writable node.");
          break;
        case MibOps.WRITE_ONLY:
          setIcon(leafROIcon);
          setToolTipText("A Readable/Writable node.");
          break;
        case MibOps.ACCESSIBLE_FOR_NOTIFY:
          setIcon(notIcon);
          setToolTipText("A Notification node.");
          break;
        default:
          setIcon(null);
          setToolTipText("");
          break;
        }
      } else {
        // Default icon
        Font f = getFont();
        setFont(new Font(f.getName(), Font.PLAIN, f.getSize()));
      }

    } else {
      Font f = getFont();
      setFont(new Font(f.getName(), Font.PLAIN, f.getSize()));
      setToolTipText(null);
    }

    return this;
  }

}