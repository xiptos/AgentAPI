/*
 * $Id: ObjectType.java 3 2004-08-03 10:42:11Z rlopes $
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

package pt.ipb.marser;

import java.util.Enumeration;
import java.util.Vector;

/**
 * 
 * @author rlopes
 * @version $Revision: 1.1.1.1 $
 */
public class ObjectType extends InfoNode {
  Syntax syntax = null;

  String units = null;

  int access = MibOps.NO_VAL;

  Vector indexes = new Vector();

  Vector impliedIndexes = new Vector();

  boolean augments = false;

  String defval = null;

  public ObjectType() {
  }

  public ObjectType(String label, int subId) {
    super(label, subId);
  }

  public void setSyntax(Syntax syntax) {
    this.syntax = syntax;
  }

  public Syntax getSyntax() {
    return syntax;
  }

  public void setUnits(String units) {
    this.units = units;
  }

  public String getUnits() {
    return units;
  }

  public void setAccess(String access) {
    this.access = MibOps.access2int(access);
  }

  public void setAccess(int access) {
    this.access = access;
  }

  public int getAccess() {
    return access;
  }

  public String getAccessStr() {
    return MibOps.access2str(access);
  }

  public void addIndex(String index) {
    addIndex(index, false);
  }

  public void addImpliedIndex(String index) {
    addIndex(index, true);
  }

  public void setIndexes(Vector v) {
    this.indexes = v;
  }

  public void addIndex(String index, boolean implied) {
    if (implied)
      impliedIndexes.addElement(index);
    else
      indexes.addElement(index);
  }

  public Enumeration indexes() {
    return indexes.elements();
  }

  public Enumeration impliedIndexes() {
    return impliedIndexes.elements();
  }

  public boolean augments() {
    return augments;
  }

  public void setAugments(boolean augments) {
    this.augments = augments;
  }

  public void setDefVal(String defval) {
    this.defval = defval;
  }

  public String getDefVal() {
    return defval;
  }

  public boolean isTableColumn() {
    if (isTableEntry())
      return false;
    MibNode p = getParent();
    while (p != null) {
      if (p.isTable())
        return true;
      p = p.getParent();
    }
    return false;
  }

  public boolean isScalar() {
    return (isLeaf() && !isTableColumn());
  }

  public boolean isTableEntry() {
    return (indexes().hasMoreElements() || impliedIndexes().hasMoreElements());
  }

  public boolean isTable() {
    for (Enumeration e = children(); e.hasMoreElements();) {
      MibNode node = (MibNode) e.nextElement();
      if (node.isTableEntry())
        return true;
    }
    return false;
  }

}