/*
 * $Id: RunTask.java 3 2004-08-03 10:42:11Z rlopes $ * Copyright (C) 2002-2004 Rui Pedro Lopes (rlopes at ipb dot pt)
 * *
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

import java.net.URL;
import java.util.List;
import java.util.NoSuchElementException;

public class RunTask extends Op {
  URL url = null;

  String name = null;

  Tasks tasks = null;

  public RunTask() {
    super();
  }

  public RunTask(URL f) {
    this();
    setURL(f);
  }

  public RunTask(String name, URL f) {
    this();
    setURL(f);
    setName(name);
  }

  public String getName() {
    return name;
  }

  public void setName(String s) {
    this.name = s;
  }

  /**
   * Selects a Task existing in the URL by number.
   */
  public void selectTask(int i) throws NoSuchElementException {
    setName((String) getTaskNameList().get(i));
    selectTask(getName());
  }

  /**
   * Selects a Task existing in the URL by name.
   */
  public void selectTask(String name) throws NoSuchElementException {
    if (tasks != null && getTaskNameList().contains(name)) {
      setName(name);
    } else {
      throw new NoSuchElementException(name);
    }
  }

  /**
   * Gets the currently selected Task name.
   */
  public String getSelectedTaskName() {
    return getSelectedTask().getName();
  }

  /**
   * Gets the Tasks object from the current URL.
   */
  public Tasks getTasks() {
    return tasks;
  }

  /**
   * Returns selected Task.
   */
  public Task getSelectedTask() {
    if (tasks != null)
      return tasks.getTask(getName());
    return null;
  }

  public URL getURL() {
    return url;
  }

  /**
   * Gets all the Tasks in the current URL.
   */
  public List getTaskNameList() {
    if (tasks != null)
      return tasks.getTaskNameList();
    return null;
  }

  /**
   * Defines the URL to read the Tasks from.
   */
  public void setURL(URL url) {
    this.url = url;
  }

  /**
   * Reads the URL.
   */
  public void read() throws Exception {
    XMLTaskReader reader = new XMLTaskReader(url.openStream());
    tasks = reader.read();
    if (getName() != null)
      selectTask(getName());
  }

  public String toXML() {
    StringBuffer str = new StringBuffer();
    str.append("    <" + XMLTaskReader.RUNTASK);
    if (getName() != null)
      str.append(" " + XMLTaskReader.NAME + "=\"" + getName() + "\"");
    if (url != null)
      str.append(" " + XMLTaskReader.DOCUMENT + "=\"" + url + "\"/>\n");
    return str.toString();
  }

}