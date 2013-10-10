/*
 * $Id: MibTree.java 3 2004-08-03 10:42:11Z rlopes $
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

import java.awt.Cursor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;

import javax.swing.JTree;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import pt.ipb.marser.MibNode;
import pt.ipb.snmp.SnmpConstants;

/**
 * 
 * @author rlopes
 * @version $Revision: 1.1.1.1 $
 */
public class MibTree extends JTree implements DragSourceListener,
    DragGestureListener {

  DragSource source = null;

  Cursor drag = null;

  Cursor noDrop = null;

  public MibTree(TreeModel model) {
    super(model);
    source = DragSource.getDefaultDragSource();
    source.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_MOVE,
        this);
    setCellRenderer(new MibNodeRenderer());

    noDrop = DragSource.DefaultMoveNoDrop;
    drag = DragSource.DefaultMoveDrop;
  }

  public void dragGestureRecognized(DragGestureEvent dge) {
    TreePath path = getSelectionPath();
    if ((path == null) || (path.getPathCount() <= 1)) {
      // We can't really move the root node (or an empty selection).
      return;
    }
    Object o = path.getLastPathComponent();
    if (o instanceof MibNode) {
      MibNode node = (MibNode) o;
      // Make a version of the node that we can use in the DnD system...
      OIDTransfers trans = new OIDTransfers(node.getLabel(), node
          .getNumberedOIDString());
      if (node.getSyntax() != null) {
        byte snmpType = node.getSyntax().getSnmpType();
        trans.setType(SnmpConstants.type2string(snmpType));
      }
      Transferable t = new MibTransferable(trans);
      dge.startDrag(noDrop, t, this);
    }
  }

  public void setDragCursor(Cursor drag) {
    this.drag = drag;
  }

  public void setNoDropCursor(Cursor noDrop) {
    this.noDrop = noDrop;
  }

  /*
   * Drag Event Handlers
   */
  public void dragEnter(DragSourceDragEvent dsde) {
    dsde.getDragSourceContext().setCursor(null);
    dsde.getDragSourceContext().setCursor(drag);
  }

  public void dragExit(DragSourceEvent dse) {
    dse.getDragSourceContext().setCursor(null);
    dse.getDragSourceContext().setCursor(noDrop);
  }

  public void dragOver(DragSourceDragEvent dsde) {
    dsde.getDragSourceContext().setCursor(null);
    dsde.getDragSourceContext().setCursor(drag);
  }

  public void dropActionChanged(DragSourceDragEvent dsde) {
  }

  public void dragDropEnd(DragSourceDropEvent dsde) {
  }

}

