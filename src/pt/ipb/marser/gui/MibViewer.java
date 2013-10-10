/*
 * $Id: MibViewer.java 3 2004-08-03 10:42:11Z rlopes $
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
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import pt.ipb.marser.MibException;
import pt.ipb.marser.MibOps;

/**
 * 
 * @author rlopes
 * @version $Revision: 1.1.1.1 $
 */
public class MibViewer extends JFrame {

  MibTreeModel model = null;

  MibDetails ghDetails = null;

  JPopupMenu regionPop = null;

  JPopupMenu agencyPop = null;

  JPopupMenu placePop = null;

  MibPanel tree = null;

  TreePath treePath = null;

  MibOps mibOps = null;

  JFileChooser fc = null;

  public MibViewer(String fname) throws FileNotFoundException, MibException {
    this();
    mibOps.loadMib(fname);
    refresh();
  }

  public MibViewer() {

    mibOps = new MibOps();
    model = new MibTreeModel(mibOps);

    ghDetails = new MibDetails();
    fc = new JFileChooser();

    //Create a tree that allows one selection at a time.
    tree = new MibPanel(model);

    tree.getTree().addTreeSelectionListener(new MyTreeSelectionListener());
    tree.getTree().addMouseListener(new PopupListener());

    //Add the scroll panes to a split pane.
    JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    splitPane.setLeftComponent(tree);

    splitPane.setBottomComponent(ghDetails);

    // A linha de baixo ... nao sei o que faz !!!!!
    /*
     * Dimension minimumSize = new Dimension(50, 105);
     * tree.setMinimumSize(minimumSize);
     */

    //tree.setPreferredSize(new Dimension(200,350));
    splitPane.setDividerLocation(250); //XXX: ignored in some releases
    //of Swing. bug 4101306
    //workaround for bug 4101306:
    //tree.setPreferredSize(new Dimension(100, 100));

    // new Dimension(largura, altura) ... TAMANHO DO SPLITPANE !!!!
    splitPane.setPreferredSize(new Dimension(800, 450));

    //Add the split pane to this frame.
    getContentPane().add(splitPane, BorderLayout.CENTER);

    setJMenuBar(buildMenuBar());
    setPopupMenu();

    //Enable tool tips.
    ToolTipManager.sharedInstance().registerComponent(tree);
    tree.putClientProperty("JTree.lineStyle", "Horizontal");
  }

  void setPopupMenu() {
    // PopupAction
    PopupAction popupAction = new PopupAction(this);

    // Region PopupMenu
    regionPop = new JPopupMenu();
    JMenuItem menuItem = new JMenuItem("A popup menu item");
    regionPop.add(menuItem);
    menuItem = new JMenuItem("Another popup menu item");
    regionPop.add(menuItem);

    // Agency PopupMenu
    agencyPop = new JPopupMenu();
    menuItem = new JMenuItem("new place");
    menuItem.addActionListener(popupAction);
    agencyPop.add(menuItem);
    menuItem = new JMenuItem("OLe' !!");
    agencyPop.add(menuItem);
    menuItem.addActionListener(popupAction);

    // Agency PopupMenu
    placePop = new JPopupMenu();
    menuItem = new JMenuItem("Novo Agente ...");
    menuItem.addActionListener(popupAction);
    placePop.add(menuItem);
    menuItem = new JMenuItem("remove place");
    placePop.add(menuItem);
    menuItem.addActionListener(popupAction);
  }

  JMenuBar buildMenuBar() {
    MenuActionListener actionListener = new MenuActionListener();

    JMenuBar menuBar;
    JMenu file, view;
    JMenuItem menuItem;

    //Create the menu bar.
    menuBar = new JMenuBar();

    //Build the first menu.
    file = new JMenu("Menu");
    file.setMnemonic(KeyEvent.VK_M);
    menuBar.add(file);

    //Build the view menu.
    view = new JMenu("View");
    view.setMnemonic(KeyEvent.VK_V);
    menuBar.add(view);

    //a group of JMenuItems
    //menuItem = new JMenuItem("Option", KeyEvent.VK_O);
    //menuItem.setMnemonic(KeyEvent.VK_O);
    //menuItem.setAccelerator(KeyStroke.getKeyStroke(
    //KeyEvent.VK_O, ActionEvent.ALT_MASK));
    //menuItem.getAccessibleContext().setAccessibleDescription(
    //"This doesn't really do anything");
    //menu.add(menuItem);

    menuItem = new JMenuItem("Load", KeyEvent.VK_L);
    menuItem.setMnemonic(KeyEvent.VK_L);
    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
        ActionEvent.ALT_MASK));
    menuItem.addActionListener(actionListener);
    file.add(menuItem);

    menuItem = new JMenuItem("Refresh", KeyEvent.VK_R);
    menuItem.setMnemonic(KeyEvent.VK_R);
    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5,
        ActionEvent.ALT_MASK));
    menuItem.addActionListener(actionListener);
    view.add(menuItem);

    return menuBar;
  }

  public static void main(String[] args) {

    try {
      JFrame frame = null;
      if (args.length == 1) {
        frame = new MibViewer(args[0]);
      } else {
        frame = new MibViewer();
      }

      frame.addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
          System.exit(0);
        }
      });

      frame.pack();
      frame.setVisible(true);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  class MenuActionListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      String actionCommand = e.getActionCommand();

      if (actionCommand.equals("Load")) {
        int returnVal = fc.showOpenDialog(MibViewer.this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
          File file = fc.getSelectedFile();
          try {
            mibOps.loadMib(file.getAbsolutePath());
            model.refresh();
          } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(MibViewer.this, ex.getMessage(),
                "alert", JOptionPane.ERROR_MESSAGE);
          }
        } else {
          //log.append("Open command cancelled by user." + newline);
        }

      } else if (actionCommand.equals("Refresh")) {
        System.out.println("Calling refresh...");
        model.refresh();
      } else {
      }
    }
  }

  class MyTreeSelectionListener implements TreeSelectionListener {
    public void valueChanged(TreeSelectionEvent e) {
      Object object = e.getPath().getLastPathComponent();
      //System.out.println("O objecto e do tipo: " +
      // object.getClass().getName());
      ghDetails.show(object);
    }
  }

  class PopupListener extends MouseAdapter {
    public void mousePressed(MouseEvent e) {
      maybeShowPopup(e);
    }

    public void mouseReleased(MouseEvent e) {
      maybeShowPopup(e);
    }

    private void maybeShowPopup(MouseEvent e) {
    }
  }

  public void refresh() {
    model.refresh();
  }

  public TreePath getLastPath() {
    return treePath;
  }

}