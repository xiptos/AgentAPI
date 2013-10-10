/*
 * $Id: TasksResolver.java 3 2004-08-03 10:42:11Z rlopes $
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
package pt.ipb.agentapi.macros;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import pt.ipb.snmp.type.smi.VarBind;

/**
 * Given a Tasks object, this class reads all the Op objects (including those
 * under RunTask objects) and resolves its Property objects.
 */
public class TasksResolver {
  Tasks tasks = null;

  List ops = null;

  public TasksResolver(Tasks tasks) {
    this.tasks = tasks;
  }

  /**
   * Resolve all the Op objects in the provided Tasks object as well as in the
   * underlying RunTask objects.
   * 
   * @throws IOException
   *           if the file was not found
   * @throws NoSuchElementException
   *           if the selected Task was not found in RunTask
   */
  public void resolve() throws IOException, NoSuchElementException {
    this.ops = new ArrayList();
    for (Iterator i = tasks.getTaskList().iterator(); i.hasNext();) {
      Task task = (Task) i.next();
      this.ops.addAll(resolve(tasks, task));
    }
  }

  List resolve(Tasks tasks, Task task) throws IOException,
      NoSuchElementException {
    ArrayList list = new ArrayList();
    for (Iterator i = task.iterator(); i.hasNext();) {
      Op op = (Op) i.next();
      if (op instanceof RunTask) {
        RunTask runTask = (RunTask) op;
        try {
          runTask.read();
          list.addAll(resolve(runTask.getTasks(), runTask.getSelectedTask()));
        } catch (Exception ex) {
          ex.printStackTrace();
          throw new IOException("Unrecognized file format");
        }
      } else {
        resolveProperty(tasks, op);
        list.add(op);
      }
    }
    return list;
  }

  /**
   * Returns an iterator over the already resolved Op elements in this document
   * in proper sequence, including those under RunTask objects.
   */
  public Iterator iterator() {
    return ops.iterator();
  }

  /**
   * Given a certain Op object, calling this method will replace any Property
   * that it may have by the correspondent Property value.
   */
  public void resolveProperty(Tasks tasks, Op op) {
    String var = op.getDestination();
    String newVal = resolveVar(tasks, var);
    if (newVal != null)
      op.setDestination(newVal);

    for (Iterator i = op.getVarBinds().iterator(); i.hasNext();) {
      VarBind varBind = (VarBind) i.next();
      var = varBind.getName();
      newVal = resolveVar(tasks, var);
      if (newVal != null)
        varBind.setName(newVal);
      //var = varBind.getValue();
      //newVal = resolveVar(tasks, var);
      //if(newVal!=null) varBind.setValue(newVal);
      var = varBind.getOID();
      newVal = resolveVar(tasks, var);
      if (newVal != null)
        varBind.setOID(newVal);
    }
  }

  String resolveVar(Tasks tasks, String var) {
    if (var == null)
      return null;
    if (var.startsWith("$")) {
      var = var.substring(1);
    }
    if (tasks.getPropertyMap().containsKey(var)) {
      return ((Property) tasks.getProperty(var)).getValue();
    }
    return null;
  }

}