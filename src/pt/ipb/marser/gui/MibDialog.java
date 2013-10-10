/*
 * $Id: MibDialog.java 3 2004-08-03 10:42:11Z rlopes $
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
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import pt.ipb.marser.MibNode;
import pt.ipb.marser.MibOps;

/**
 * 
 * @author rlopes
 * @version $Revision: 1.1.1.1 $
 */
public class MibDialog extends JDialog {

  MibPanel mibPanel = null;

  private JOptionPane optionPane = null;

  Frame frame = null;

  int ret;

  public MibDialog(Component p) {
    super(JOptionPane.getFrameForComponent(p), true);
    frame = JOptionPane.getFrameForComponent(p);
    setTitle("Mib Tree");
    this.mibPanel = new MibPanel();

    optionPane = new JOptionPane(mibPanel);
    setContentPane(optionPane);
    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent we) {
        optionPane.setValue(new Integer(JOptionPane.CANCEL_OPTION));
      }
    });

    optionPane.addPropertyChangeListener(new PropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent e) {
        String prop = e.getPropertyName();

        if (isVisible() && (e.getSource() == optionPane)
            && (prop.equals(JOptionPane.VALUE_PROPERTY))) {
          Object value = optionPane.getValue();

          if (value == JOptionPane.UNINITIALIZED_VALUE) {
            //ignore reset
            return;
          }

          // Reset the OptionPane's value.
          // If you don't do this, then if the user
          // presses the same button next time, no
          // property change event will be fired.
          optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);

          if (value.equals(new Integer(JOptionPane.OK_OPTION))) {
            Object o = getMibPanel().getTree().getLastSelectedPathComponent();
            if (o instanceof MibNode) {
              ret = JOptionPane.OK_OPTION;
              setVisible(false);
            } else {
              JOptionPane.showMessageDialog(MibDialog.this,
                  "Sorry, you must select a MIB Node\n" + "Please, try again.",
                  "Try again", JOptionPane.ERROR_MESSAGE);
            }
          } else { // user closed dialog or clicked cancel
            setVisible(false);
            ret = JOptionPane.CANCEL_OPTION;
          }
        }
      }
    });
    pack();
  }

  public void setMibOps(MibOps mibOps) {
    mibPanel.getMibTreeModel().updateMibOps(mibOps);
  }

  public int showMibDialog() {
    super.setVisible(true);
    return ret;
  }

  public MibPanel getMibPanel() {
    return mibPanel;
  }

  public String getSelectedOID() {
    Object o = mibPanel.getTree().getLastSelectedPathComponent();
    if (o instanceof MibNode) {
      return ((MibNode) o).getNumberedOIDString();
    }
    return "";
  }

}

