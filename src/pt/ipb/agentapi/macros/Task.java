/*
 * $Id: Task.java 3 2004-08-03 10:42:11Z rlopes $ * Copyright (C) 2002-2004 Rui Pedro Lopes (rlopes at ipb dot pt) *
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
package pt.ipb.agentapi.macros;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import pt.ipb.snmp.type.smi.VarBind;

public class Task {
  String name = null;

  private ArrayList ops = null;

  public Task(String name) {
    this();
    setName(name);
  }

  public Task() {
    ops = new ArrayList();
  }

  public boolean equals(Object t) {
    Task task = (Task) t;
    return getName().equals(task.getName());
  }

  public String getName() {
    return name;
  }

  public void setName(String u) {
    this.name = u;
  }

  public void addAll(Collection p) {
    ops.addAll(p);
  }

  public void append(Task t) {
    for (Iterator i = t.getOpList().iterator(); i.hasNext();) {
      addOp((Op) i.next());
    }
  }

  public void addOp(Object p) {
    ops.add(p);
  }

  public List getOpList() {
    return ops;
  }

  public List getVarBinds() {
    ArrayList list = new ArrayList();
    for (Iterator i = getOpList().iterator(); i.hasNext();) {
      Op op = (Op) i.next();
      list.addAll(op.getVarBinds());
    }
    return list;
  }

  public Op getOp(String oid) {
    for (Iterator i = getOpList().iterator(); i.hasNext();) {
      Op op = (Op) i.next();
      VarBind varBind = op.getVarBind(oid);
      if (varBind != null)
        return op;
    }
    return null;
  }

  public VarBind getVarBind(String oid) {
    for (Iterator i = getOpList().iterator(); i.hasNext();) {
      Op op = (Op) i.next();
      VarBind varBind = op.getVarBind(new String(oid));
      if (varBind != null)
        return varBind;
    }
    return null;
  }

  public Iterator iterator() {
    return ops.iterator();
  }

  public String toXML() {
    StringBuffer str = new StringBuffer();
    str.append("  <" + XMLTaskReader.TASK);
    if (name != null)
      str.append(" " + XMLTaskReader.NAME + "=\"" + name + "\">\n");
    for (Iterator i = ops.iterator(); i.hasNext();) {
      Object p = i.next();
      if (p instanceof Op) {
        str.append(((Op) p).toXML());
      } else if (p instanceof RunTask) {
        str.append(((RunTask) p).toXML());
      }
    }
    str.append("  </" + XMLTaskReader.TASK + ">\n");
    return str.toString();
  }

}

