/*
 * $Id: Tasks.java 3 2004-08-03 10:42:11Z rlopes $ * Copyright (C) 2002-2004 Rui Pedro Lopes (rlopes at ipb dot pt) *
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
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import pt.ipb.snmp.SnmpProperties;
import pt.ipb.snmp.type.smi.VarBind;

public class Tasks {
  SnmpProperties snmpProps = null;

  private Hashtable props = null;

  private Hashtable tasks = null;

  private ArrayList mibs = null;

  private ArrayList propsList = null;

  private ArrayList taskList = null;

  public Tasks() {
    props = new Hashtable();
    mibs = new ArrayList();
    propsList = new ArrayList();
    taskList = new ArrayList();
    tasks = new Hashtable();
  }

  public SnmpProperties getSnmpProperties() {
    return snmpProps;
  }

  public void setSnmpProperties(SnmpProperties p) {
    this.snmpProps = p;
  }

  public void append(Tasks t) {
    for (Iterator i = t.getMibList().iterator(); i.hasNext();) {
      Mib m = (Mib) i.next();
      addMib(m);
    }
    for (Iterator i = t.getPropertyList().iterator(); i.hasNext();) {
      Property p = (Property) i.next();
      addProperty(p);
    }
    for (Iterator i = t.getTaskList().iterator(); i.hasNext();) {
      Task p = (Task) i.next();
      if (tasks.containsValue(p)) {
        Task myP = getTask(p.getName());
        myP.append(p);
      } else {
        addTask(p);
      }
    }

  }

  public void addProperty(Property p) {
    propsList.add(p);
    props.put(p.getName(), p);
  }

  /**
   * Given a property name, returns the Property object ( <name,value>);
   */
  public Property getProperty(String name) {
    return (Property) props.get(name);
  }

  public Map getPropertyMap() {
    return props;
  }

  public List getPropertyList() {
    return propsList;
  }

  public void addMib(Mib p) {
    mibs.add(p);
  }

  public List getMibList() {
    return mibs;
  }

  public void addTask(Task p) {
    if (taskList.contains(p)) {
      int i = taskList.indexOf(p);
      taskList.set(i, p);
    } else {
      taskList.add(p);
    }
    tasks.put(p.getName(), p);
  }

  public Task getTask(String name) {
    return (Task) tasks.get(name);
  }

  public List getTaskNameList() {
    ArrayList list = new ArrayList(tasks.keySet());
    Collections.sort(list);
    return list;
  }

  public List getTaskList() {
    return taskList;
  }

  /**
   * Returns an iterator over the Op elements in this document in proper
   * sequence, including those under RunTask objects.
   */
  public Iterator iterator() {
    return new Itr(this);
  }

  /**
   * Given a certain Op object, calling this method will replace any Property
   * that it may have by the correspondent Property value.
   */
  public void resolveProperty(Op op) {
    String var = op.getDestination();
    String newVal = resolveVar(var);
    if (newVal != null)
      op.setDestination(newVal);

    for (Iterator i = op.getVarBinds().iterator(); i.hasNext();) {
      VarBind varBind = (VarBind) i.next();
      var = varBind.getName();
      newVal = resolveVar(var);
      if (newVal != null)
        varBind.setName(newVal);
      //var = varBind.getValue();
      //newVal = resolveVar(var);
      //if(newVal!=null) varBind.setValue(newVal);
      var = varBind.getOID();
      newVal = resolveVar(var);
      if (newVal != null)
        varBind.setOID(newVal);
    }
  }

  String resolveVar(String var) {
    if (var.startsWith("$")) {
      var = var.substring(1);
    }
    if (props.containsKey(var))
      return (String) props.get(var);
    return null;
  }

  public List getVarBinds() {
    ArrayList list = new ArrayList();
    for (Iterator i = taskList.iterator(); i.hasNext();) {
      Task task = (Task) i.next();
      list.addAll(task.getVarBinds());
    }
    return list;
  }

  public VarBind searchVarBind(String oid) {
    for (Iterator i = taskList.iterator(); i.hasNext();) {
      Task task = (Task) i.next();
      VarBind varBind = task.getVarBind(oid);
      if (varBind != null) {
        return varBind;
      }
    }
    return null;
  }

  public String toXML() {
    StringBuffer str = new StringBuffer();

    str.append("<" + XMLTaskReader.SNMP);
    str.append(" " + XMLTaskReader.VERSION + "=\"" + snmpProps.getVersion()
        + "\"");
    if (snmpProps.getUser() != null)
      str.append(" " + XMLTaskReader.USER + "=\"" + snmpProps.getUser() + "\"");
    if (snmpProps.getProperty(SnmpProperties.AUTHPROTO) != null)
      str.append(" " + XMLTaskReader.AUTHPROTO + "=\""
          + snmpProps.getProperty(SnmpProperties.AUTHPROTO) + "\"");
    if (snmpProps.getAuthPass() != null)
      str.append(" " + XMLTaskReader.AUTHPASS + "=\"" + snmpProps.getAuthPass()
          + "\"");
    if (snmpProps.getPrivPass() != null)
      str.append(" " + XMLTaskReader.PRIVPASS + "=\"" + snmpProps.getPrivPass()
          + "\"");
    if (snmpProps.getCommunity() != null)
      str.append(" " + XMLTaskReader.COMMUNITY + "=\""
          + snmpProps.getCommunity() + "\"");
    if (snmpProps.getWriteCommunity() != null)
      str.append(" " + XMLTaskReader.WRITECOMMUNITY + "=\""
          + snmpProps.getWriteCommunity() + "\"");
    str.append(">\n");

    for (Iterator i = mibs.iterator(); i.hasNext();) {
      Mib m = (Mib) i.next();
      str.append(m.toXML());
    }
    for (Iterator i = propsList.iterator(); i.hasNext();) {
      Property p = (Property) i.next();
      str.append(p.toXML());
    }
    for (Iterator i = taskList.iterator(); i.hasNext();) {
      Task p = (Task) i.next();
      str.append(p.toXML());
    }

    str.append("</" + XMLTaskReader.SNMP + ">\n");
    return str.toString();
  }
}

class Itr implements Iterator {
  Iterator taskIterator = null;

  Iterator curTask = null;

  public Itr(Tasks tasks) {
    this.taskIterator = tasks.getTaskList().iterator();
  }

  public boolean hasNext() {
    while (curTask == null || !curTask.hasNext()) {
      if (taskIterator.hasNext()) {
        curTask = ((Task) taskIterator.next()).iterator();
      } else {
        return false;
      }
    }
    return true;
  }

  public Object next() {
    while (curTask == null || !curTask.hasNext()) {
      if (taskIterator.hasNext()) {
        curTask = ((Task) taskIterator.next()).iterator();
      } else {
        throw new NoSuchElementException();
      }
    }
    return curTask.next();
  }

  public void remove() {
    if (curTask == null)
      throw new IllegalStateException();

    curTask.remove();
  }

}

