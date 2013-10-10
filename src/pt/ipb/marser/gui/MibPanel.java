/*
 * $Id: MibPanel.java 3 2004-08-03 10:42:11Z rlopes $
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
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeSelectionModel;

import pt.ipb.marser.MibModule;
import pt.ipb.marser.MibNode;
import pt.ipb.marser.MibOps;
import pt.ipb.marser.MibTC;
import pt.ipb.marser.MibTrap;

/**
 * 
 * @author rlopes
 * @version $Revision: 1.2 $
 */
public class MibPanel extends JPanel implements TreeSelectionListener {

  MibTreeModel model = null;

  MibTree tree = null;

  Action refreshAction = null;

  Action openAction = null;

  Action closeAction = null;

  JFileChooser fileChooser = null;

  public MibPanel(MibOps o) {
    this(new MibTreeModel(o));
  }

  public MibPanel() {
    this(new MibTreeModel(new MibOps()));
  }

  public MibPanel(MibTreeModel model) {
    createActions();
    setLayout(new BorderLayout());
    this.model = model;
    tree = new MibTree(model);
    tree.setCellRenderer(new MibNodeRenderer());

    tree.getSelectionModel().setSelectionMode(
        TreeSelectionModel.SINGLE_TREE_SELECTION);

    JScrollPane scrollPane = new JScrollPane(tree);
    add(scrollPane, BorderLayout.CENTER);
    add(createToolBar(), BorderLayout.NORTH);

    fileChooser = new JFileChooser();

    //Listen for when the selection changes.
    tree.addTreeSelectionListener(this);

  }

  public MibTreeModel getMibTreeModel() {
    return model;
  }

  public void valueChanged(TreeSelectionEvent e) {
    closeAction.setEnabled(!tree.isSelectionEmpty());
  }

  public MibTree getTree() {
    return tree;
  }

  protected void createActions() {
    refreshAction = new AbstractAction("Refresh", getRefreshIcon()) {
      public void actionPerformed(ActionEvent e) {
        model.refresh();
      }
    };
    refreshAction.putValue(Action.SHORT_DESCRIPTION, "Refresh MIB tree");

    openAction = new AbstractAction("Open", getOpenIcon()) {
      public void actionPerformed(ActionEvent e) {
        int r = fileChooser.showOpenDialog(MibPanel.this);
        if (r == JFileChooser.APPROVE_OPTION) {
          File file = fileChooser.getSelectedFile();
          try {
            model.getMibOps().loadMib(file.getPath());
            model.refresh();
          } catch (Exception ex) {
            JOptionPane.showMessageDialog(JOptionPane
                .getFrameForComponent(MibPanel.this), "MIB file problem:\n"
                + ex.getMessage(), "alert", JOptionPane.ERROR_MESSAGE);
          }
        }
      }
    };
    openAction.putValue(Action.SHORT_DESCRIPTION, "Open MIB");

    closeAction = new AbstractAction("Close", getCloseIcon()) {
      public void actionPerformed(ActionEvent e) {
        if (!tree.isSelectionEmpty()) {
          Object o = tree.getLastSelectedPathComponent();
          if (o instanceof MibNode) {
            MibNode n = (MibNode) tree.getLastSelectedPathComponent();
            System.out.println(n.getModule());
            model.getMibOps().remove(n.getModule().getName());
            model.refresh();

          } else if (o instanceof MibModule) {
            MibModule n = (MibModule) tree.getLastSelectedPathComponent();
            model.getMibOps().remove(n.getName());
            model.refresh();

          } else if (o instanceof MibTC) {
            MibTC n = (MibTC) tree.getLastSelectedPathComponent();
            model.getMibOps().removeModuleContainingTC(n);
            model.refresh();

          } else if (o instanceof MibTrap) {
            MibTrap n = (MibTrap) tree.getLastSelectedPathComponent();
            model.getMibOps().remove(n.getModule().getName());
            model.refresh();

          } else if (o instanceof MibArray) {
            MibArray n = (MibArray) tree.getLastSelectedPathComponent();
            if (n.size() > 0) {
              Object o1 = n.elementAt(0);
              if (o1 instanceof MibTC) {
                MibTC n1 = (MibTC) o1;
                model.getMibOps().removeModuleContainingTC(n1);
                model.refresh();

              } else if (o1 instanceof MibTrap) {
                MibTrap n1 = (MibTrap) o1;
                model.getMibOps().remove(n1.getModule().getName());
                model.refresh();
              }
            }
          }
        }
      }
    };
    closeAction.putValue(Action.SHORT_DESCRIPTION, "Close MIB");
    closeAction.setEnabled(false);
  }

  protected JToolBar createToolBar() {
    JToolBar toolBar = new JToolBar();
    toolBar.setFloatable(false);

    JButton button = toolBar.add(openAction);
    button.setText(""); //an icon-only button

    button = toolBar.add(closeAction);
    button.setText(""); //an icon-only button

    button = toolBar.add(refreshAction);
    button.setText(""); //an icon-only button
    return toolBar;
  }

  public ImageIcon getOpenIcon() {
    return ResourceFactory.getIcon("fileopen.png");
  }

  public ImageIcon getCloseIcon() {
    return ResourceFactory.getIcon("fileclose.png");
  }

  public ImageIcon getRefreshIcon() {
    return ResourceFactory.getIcon("refresh.png");
  }

}