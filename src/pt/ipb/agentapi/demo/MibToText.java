/*
 * MibToText.java
 *
 * Created on 10 de Julho de 2002, 20:12
 * $Id: MibToText.java 3 2004-08-03 10:42:11Z rlopes $
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

package pt.ipb.agentapi.demo;

import java.io.FileNotFoundException;
import java.util.Enumeration;

import pt.ipb.marser.MibException;
import pt.ipb.marser.MibModule;
import pt.ipb.marser.MibNode;
import pt.ipb.marser.MibOps;
import pt.ipb.marser.MibTC;
import pt.ipb.marser.MibTrap;

/**
 * 
 * @author rlopes
 */
public class MibToText {
  MibOps ops;

  /** Creates a new instance of MibToText */
  public MibToText(String fName) throws FileNotFoundException, MibException {
    this.ops = new MibOps();
    this.ops.loadMib(fName);
  }

  String children(int level, MibNode node) {
    StringBuffer str = new StringBuffer();
    for (int i = 0; i < level; i++) {
      str.append(" ");
    }
    if (node != null) {
      str.append(node.getLabel());
      str.append("(" + node.getOID() + ")\n");
      for (Enumeration e = node.children(); e.hasMoreElements();) {
        MibNode child = (MibNode) e.nextElement();
        str.append(children(level + 2, child));
      }
    }
    return str.toString();
  }

  public String toString() {
    StringBuffer str = new StringBuffer();
    for (Enumeration e = ops.modules(); e.hasMoreElements();) {
      MibModule module = (MibModule) e.nextElement();
      str.append("\nMODULE: " + module.getName() + "\n");

      MibNode root = module.getRoot();
      str.append(children(2, root));

      str.append("-- TextualConventions\n");
      for (Enumeration e2 = module.mibTCs(); e2.hasMoreElements();) {
        MibTC tc = (MibTC) e2.nextElement();
        str.append(tc.getLabel() + "\n");
      }
      str.append("-- Traps\n");
      for (Enumeration e3 = module.traps(); e3.hasMoreElements();) {
        MibTrap trap = (MibTrap) e3.nextElement();
        str.append("  " + trap.getLabel() + "\n");
      }
      str.append("END MODULE: " + module.getName() + "\n");
    }
    return str.toString();
  }

  public static void main(String args[]) {
    try {
      MibToText mtt = new MibToText(args[0]);
      System.out.println(mtt.toString());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}